package appeng.core.core.definitions;

import appeng.api.bootstrap.DefinitionFactory;
import appeng.api.definitions.IItemDefinition;
import appeng.core.AppEng;
import appeng.core.api.definitions.ICoreItemDefinitions;
import appeng.core.core.bootstrap.ItemDefinitionBuilder;
import appeng.core.item.ItemMaterial;
import appeng.core.lib.definitions.Definitions;
import appeng.core.lib.definitions.ItemDefinition;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

public class CoreItemDefinitions extends Definitions<Item, IItemDefinition<Item>> implements ICoreItemDefinitions {

	private final IItemDefinition material;

	public CoreItemDefinitions(DefinitionFactory registry){
		this.material = registry.<Item, ItemDefinition<Item>, ItemDefinitionBuilder<Item>, Item>definitionBuilder(new ResourceLocation(AppEng.MODID, "material"), ih(new ItemMaterial())).build();

		init(/*registry.buildDefaultItemBlocks()*/);
	}

	private DefinitionFactory.InputHandler<Item, Item> ih(Item item){
		return new DefinitionFactory.InputHandler<Item, Item>(item) {};
	}

}
