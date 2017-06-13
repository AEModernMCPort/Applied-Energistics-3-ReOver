package appeng.core.core.bootstrap;

import appeng.api.definitions.IDefinition;
import appeng.api.definitions.sub.IBlockSubDefinition;
import appeng.core.lib.definitions.BlockDefinition;
import appeng.core.lib.definitions.Definition;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;

public class BlockSubDefinition<S extends IBlockState, B extends Block> extends Definition<S> implements IBlockSubDefinition<S, B> {

	private final BlockDefinition<B> parent;
	private final BlockStateContainer stateContainer;

	public BlockSubDefinition(S t, BlockDefinition<B> parent){
		super(null, t);
		this.parent = parent;
		this.stateContainer = this.parent.maybe().get().getBlockState();
	}

	@Override
	public <PD extends IDefinition<B>> PD parent(){
		return (PD) parent;
	}

	@Override
	public boolean hasProperty(ISubDefinitionProperty<?> property){
		return getProperty(property.getName()) != null;
	}

	@Override
	public <V> ISubDefinitionProperty<V> getProperty(String name){
		return new IPropertyWrapper(stateContainer.getProperty(name));
	}

	@Override
	public <V> IBlockSubDefinition<S, B> withProperty(ISubDefinitionProperty<V> property, V value){
		return withProperty(property.getName(), value);
	}

	@Override
	public <V> IBlockSubDefinition<S, B> withProperty(String property, V value){
		if(getProperty(property) == null || !getProperty(property).isValid(value)){
			return this;
		} else {
			return new BlockSubDefinition(maybe().get().withProperty((IProperty) stateContainer.getProperty(property), (Comparable) value), parent);
		}
	}

	public static class IPropertyWrapper<V extends Comparable<V>> implements ISubDefinitionProperty<V> {

		public final IProperty<V> property;

		public IPropertyWrapper(IProperty<V> property){
			this.property = property;
		}

		@Override
		public String getName(){
			return property.getName();
		}

		@Override
		public boolean isValid(V value){
			return property.getAllowedValues().contains(value);
		}

	}

}
