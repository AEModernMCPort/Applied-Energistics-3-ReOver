package appeng.core.core.bootstrap.component;

import appeng.api.bootstrap.IDefinitionBuilder;
import appeng.core.core.api.definition.IItemDefinition;
import net.minecraftforge.oredict.OreDictionary;

public class RegisterToOredictComponent<I extends net.minecraft.item.Item> implements IDefinitionBuilder.DefinitionInitializationComponent.Init<I, IItemDefinition<I>> {

	protected String[] names;

	public RegisterToOredictComponent(String... names){
		this.names = names;
	}

	@Override
	public void init(IItemDefinition<I> def){
		def.maybeStack(1).ifPresent(stack -> {for(String name : names) OreDictionary.registerOre(name, stack);});
	}


}
