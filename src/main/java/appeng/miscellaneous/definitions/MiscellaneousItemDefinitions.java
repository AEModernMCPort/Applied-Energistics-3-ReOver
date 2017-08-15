package appeng.miscellaneous.definitions;

import appeng.api.bootstrap.DefinitionFactory;
import appeng.core.core.api.definition.IItemDefinition;
import appeng.core.lib.definitions.Definitions;
import appeng.miscellaneous.api.definitions.IMiscellaneousItemDefinitions;
import net.minecraft.item.Item;

public class MiscellaneousItemDefinitions extends Definitions<Item, IItemDefinition<Item>> implements IMiscellaneousItemDefinitions {

	public MiscellaneousItemDefinitions(DefinitionFactory registry){

	}

	private DefinitionFactory.InputHandler<Item, Item> ih(Item item){
		return new DefinitionFactory.InputHandler<Item, Item>(item) {};
	}

}
