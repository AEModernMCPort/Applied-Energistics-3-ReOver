package appeng.tools.definitions;

import appeng.api.bootstrap.DefinitionFactory;
import appeng.api.definitions.IItemDefinition;
import appeng.core.lib.definitions.Definitions;
import appeng.tools.api.definitions.IToolsItemDefinitions;
import net.minecraft.item.Item;

public class ToolsItemDefinitions extends Definitions<Item, IItemDefinition<Item>> implements IToolsItemDefinitions {

	public ToolsItemDefinitions(DefinitionFactory registry){
		init(/*registry.buildDefaultItemBlocks()*/); //Just in case
	}

}
