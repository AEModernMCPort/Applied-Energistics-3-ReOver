package appeng.debug.definitions;

import appeng.api.bootstrap.DefinitionFactory;
import appeng.api.definitions.IItemDefinition;
import appeng.core.lib.definitions.Definitions;
import net.minecraft.item.Item;

public class DebugItemDefinitions extends Definitions<Item, IItemDefinition<Item>> {

	public DebugItemDefinitions(DefinitionFactory registry){

	private DefinitionFactory.InputHandler<Item, Item> ih(Item item){
		return new DefinitionFactory.InputHandler<Item, Item>(item) {};
	}

}
