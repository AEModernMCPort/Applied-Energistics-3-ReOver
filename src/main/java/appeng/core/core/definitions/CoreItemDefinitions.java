package appeng.core.core.definitions;

import appeng.api.bootstrap.DefinitionFactory;
import appeng.api.bootstrap.IDefinitionBuilder;
import appeng.api.definitions.IItemDefinition;
import appeng.core.AppEng;
import appeng.core.api.bootstrap.IItemBuilder;
import appeng.core.api.definitions.ICoreItemDefinitions;
import appeng.core.core.bootstrap.ItemColorComponent;
import appeng.core.core.bootstrap.ItemMeshDefinitionComponent;
import appeng.core.core.bootstrap.ItemVariantsComponent;
import appeng.core.item.DummyItem;
import appeng.core.item.ItemMaterial;
import appeng.core.lib.definitions.Definitions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;

import java.util.ArrayList;

public class CoreItemDefinitions extends Definitions<Item, IItemDefinition<Item>> implements ICoreItemDefinitions {

	private final IItemDefinition material;

	public CoreItemDefinitions(DefinitionFactory registry){
		this.material = registry.<ItemMaterial, IItemDefinition<ItemMaterial>, IItemBuilder<ItemMaterial, ?>, Item>definitionBuilder(new ResourceLocation(AppEng.MODID, "material"), ih(new ItemMaterial())).setFeature(null).<IDefinitionBuilder.DefinitionInitializationComponent.Init<ItemMaterial, IItemDefinition<ItemMaterial>>>initializationComponent(Side.CLIENT, def -> Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(def.maybe().get(), stack -> def.maybe().get().getMaterial(stack).getModel(stack))).build();
		registry.<Item, IItemDefinition<Item>, IItemBuilder<Item, ?>, Item>definitionBuilder(new ResourceLocation(AppEng.MODID,"test_item_model"), ih(new DummyItem()))
				.<IDefinitionBuilder.DefinitionInitializationComponent.Init<Item, IItemDefinition<Item>>>initializationComponent(Side.CLIENT, def -> new ItemColorComponent(() -> (stack, anInt) -> 0).init(def))
				.<IDefinitionBuilder.DefinitionInitializationComponent.Init<Item, IItemDefinition<Item>>>initializationComponent(Side.CLIENT, def -> new ItemMeshDefinitionComponent(() -> stack -> new ModelResourceLocation(".")).init(def))
				.<IDefinitionBuilder.DefinitionInitializationComponent.Init<Item, IItemDefinition<Item>>>initializationComponent(Side.CLIENT, def -> new ItemVariantsComponent(new ArrayList<>()).init(def))
				.build();
	}

	private DefinitionFactory.InputHandler<Item, Item> ih(Item item){
		return new DefinitionFactory.InputHandler<Item, Item>(item) {};
	}

}
