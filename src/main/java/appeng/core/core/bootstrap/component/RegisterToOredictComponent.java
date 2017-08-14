package appeng.core.core.bootstrap.component;

import appeng.api.bootstrap.IDefinitionBuilder;
import appeng.core.core.api.definition.IItemDefinition;
import appeng.core.core.api.definition.IMaterialDefinition;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import java.util.Optional;

public class RegisterToOredictComponent {

	protected String[] names;

	public RegisterToOredictComponent(String... names){
		this.names = names;
	}

	protected void init(Optional<ItemStack> ostack){
		ostack.ifPresent(stack -> {for(String name : names) OreDictionary.registerOre(name, stack);});
	}

	public static class Item<I extends net.minecraft.item.Item> extends RegisterToOredictComponent implements IDefinitionBuilder.DefinitionInitializationComponent.Init<I, IItemDefinition<I>>{

		public Item(String... names){
			super(names);
		}

		@Override
		public void init(IItemDefinition<I> def){
			init(def.maybeStack(1));
		}

	}

	public static class Material<M extends appeng.core.core.api.material.Material> extends RegisterToOredictComponent implements IDefinitionBuilder.DefinitionInitializationComponent.Init<M, IMaterialDefinition<M>> {

		public Material(String... names){
			super(names);
		}

		@Override
		public void init(IMaterialDefinition<M> def){
			init(def.maybeStack(1));
		}
	}

}
