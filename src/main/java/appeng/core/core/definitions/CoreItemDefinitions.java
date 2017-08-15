package appeng.core.core.definitions;

import appeng.api.bootstrap.DefinitionFactory;
import appeng.core.core.api.definition.IItemDefinition;
import appeng.core.core.api.definitions.ICoreItemDefinitions;
import appeng.core.lib.definitions.Definitions;
import net.minecraft.item.Item;

public class CoreItemDefinitions extends Definitions<Item, IItemDefinition<Item>> implements ICoreItemDefinitions {

	public CoreItemDefinitions(DefinitionFactory registry){

	}

	private DefinitionFactory.InputHandler<Item, Item> ih(Item item){
		return new DefinitionFactory.InputHandler<Item, Item>(item) {};
	}
}
