package appeng.core.skyfall.definitions;

import appeng.api.bootstrap.DefinitionFactory;
import appeng.core.core.api.definition.IItemDefinition;
import appeng.core.lib.definitions.Definitions;
import appeng.core.skyfall.api.definitions.ISkyfallItemDefinitions;
import net.minecraft.item.Item;

public class SkyfallItemDefinitions extends Definitions<Item, IItemDefinition<Item>> implements ISkyfallItemDefinitions {

	public SkyfallItemDefinitions(DefinitionFactory registry){

	}

	private DefinitionFactory.InputHandler<Item, Item> ih(Item item){
		return new DefinitionFactory.InputHandler<Item, Item>(item) {};
	}
}
