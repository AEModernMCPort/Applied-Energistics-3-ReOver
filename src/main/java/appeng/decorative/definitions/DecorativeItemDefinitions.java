package appeng.decorative.definitions;

import appeng.api.bootstrap.DefinitionFactory;
import appeng.core.core.api.definition.IItemDefinition;
import appeng.core.lib.definitions.Definitions;
import appeng.decorative.api.definitions.IDecorativeItemDefinitions;
import net.minecraft.item.Item;

public class DecorativeItemDefinitions extends Definitions<Item, IItemDefinition<Item>> implements IDecorativeItemDefinitions {

	public DecorativeItemDefinitions(DefinitionFactory registry){

	}

	private DefinitionFactory.InputHandler<Item, Item> ih(Item item){
		return new DefinitionFactory.InputHandler<Item, Item>(item) {};
	}

}
