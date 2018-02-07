package appeng.core.core.definition;

import appeng.api.definition.IDefinition;
import appeng.core.core.api.definition.IItemSubDefinition;
import appeng.api.item.IStateItem;
import appeng.api.item.IStateItemState;
import appeng.core.lib.definition.Definition;
import net.minecraft.item.Item;

//FOR DEFINITIVE* REMOVAL IN MC 1.13
@Deprecated
public class ItemSubDefinition<S extends IStateItemState<I>, I extends Item & IStateItem<I>> extends Definition<S> implements IItemSubDefinition<S, I> {

	private final ItemDefinition<I> parent;
	private final I item;

	public ItemSubDefinition(S t, ItemDefinition<I> parent){
		super(null, t);
		this.parent = parent;
		this.item = this.parent.maybe().get();
	}

	@Override
	public <PD extends IDefinition<I>> PD parent(){
		return (PD) parent;
	}

	@Override
	public boolean hasProperty(ISubDefinitionProperty<?> property){
		return getProperty(property.getName()) != null;
	}

	@Override
	public <V> ISubDefinitionProperty<V> getProperty(String name){
		return new PropertyWrapper(item.getProperty(name));
	}

	@Override
	public <V> IItemSubDefinition<S, I> withProperty(ISubDefinitionProperty<V> property, V value){
		return withProperty(property.getName(), value);
	}

	@Override
	public <V> IItemSubDefinition<S, I> withProperty(String property, V value){
		if(getProperty(property) == null || !getProperty(property).isValid(value)){
			return this;
		} else {
			return new ItemSubDefinition(maybe().get().withProperty(item.getProperty(property), value), parent);
		}
	}

	public static class PropertyWrapper<V> implements ISubDefinitionProperty<V> {

		public final IStateItemState.Property<V> property;

		public PropertyWrapper(IStateItemState.Property<V> property){
			this.property = property;
		}

		@Override
		public String getName(){
			return property.getName();
		}

		@Override
		public boolean isValid(V value){
			return property.isValid(value);
		}

	}

}
