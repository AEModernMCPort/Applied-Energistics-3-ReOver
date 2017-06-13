package appeng.debug.definitions;

import appeng.api.bootstrap.DefinitionFactory;
import appeng.api.definitions.IItemDefinition;
import appeng.core.AppEng;
import appeng.core.api.bootstrap.IItemBuilder;
import appeng.core.lib.definitions.Definitions;
import appeng.debug.item.TestItem;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

public class DebugItemDefinitions extends Definitions<Item, IItemDefinition<Item>> {

	public DebugItemDefinitions(DefinitionFactory registry){
		registry.<Item, IItemDefinition<Item>, IItemBuilder<Item, ?>, Item>definitionBuilder(new ResourceLocation(AppEng.MODID, "test_item_model_default"), ih(new TestItem())).defaultModel().build();
	}

	private DefinitionFactory.InputHandler<Item, Item> ih(Item item){
		return new DefinitionFactory.InputHandler<Item, Item>(item) {};
	}

}
