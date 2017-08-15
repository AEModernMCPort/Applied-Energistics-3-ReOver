package appeng.core.core.definitions;

import appeng.api.bootstrap.DefinitionFactory;
import appeng.core.AppEng;
import appeng.core.core.api.bootstrap.IItemBuilder;
import appeng.core.core.api.definition.IItemDefinition;
import appeng.core.core.api.definitions.ICoreItemDefinitions;
import appeng.core.core.bootstrap.component.RegisterToOredictComponent;
import appeng.core.core.client.bootstrap.ItemMeshDefinitionComponent;
import appeng.core.item.ItemMaterial;
import appeng.core.lib.definitions.Definitions;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;

import java.util.Optional;

public class CoreItemDefinitions extends Definitions<Item, IItemDefinition<Item>> implements ICoreItemDefinitions {

	private final IItemDefinition material;

	private final IItemDefinition certusQuartz;
	private final IItemDefinition certusRedstone;
	private final IItemDefinition certusSulfur;
	private final IItemDefinition incertus;

	private final IItemDefinition supersolidCertus;

	public CoreItemDefinitions(DefinitionFactory registry){
		this.material = registry
				.<Item, IItemDefinition<Item>, IItemBuilder<Item, ?>, Item>definitionBuilder(new ResourceLocation(AppEng.MODID, "material"), ih(new ItemMaterial())).setFeature(null)
				.initializationComponent(Side.CLIENT, new ItemMeshDefinitionComponent<Item>(() -> Optional.of(stack -> new ModelResourceLocation(((ItemMaterial) stack.getItem()).getMaterial(stack).getModel(), "inventory"))))
				.build();

		certusQuartz = registry.<Item, IItemDefinition<Item>, IItemBuilder<Item, ?>, Item>definitionBuilder(new ResourceLocation(AppEng.MODID, "certus_quartz"), ih(new Item())).initializationComponent(null, new RegisterToOredictComponent.Item<>("certusQuartz")).build();
		certusRedstone = registry.<Item, IItemDefinition<Item>, IItemBuilder<Item, ?>, Item>definitionBuilder(new ResourceLocation(AppEng.MODID, "certus_redstone"), ih(new Item())).initializationComponent(null, new RegisterToOredictComponent.Item<>("certusRedstone")).build();
		certusSulfur = registry.<Item, IItemDefinition<Item>, IItemBuilder<Item, ?>, Item>definitionBuilder(new ResourceLocation(AppEng.MODID, "certus_sulfur"), ih(new Item())).initializationComponent(null, new RegisterToOredictComponent.Item<>("certusSulfur")).build();
		incertus = registry.<Item, IItemDefinition<Item>, IItemBuilder<Item, ?>, Item>definitionBuilder(new ResourceLocation(AppEng.MODID, "incertus"), ih(new Item())).initializationComponent(null, new RegisterToOredictComponent.Item<>("incertus")).build();

		supersolidCertus = registry.<Item, IItemDefinition<Item>, IItemBuilder<Item, ?>, Item>definitionBuilder(new ResourceLocation(AppEng.MODID, "supersolid_certus"), ih(new Item())).initializationComponent(null, new RegisterToOredictComponent.Item<>("supersolidCertus")).build();
	}

	private DefinitionFactory.InputHandler<Item, Item> ih(Item item){
		return new DefinitionFactory.InputHandler<Item, Item>(item) {};
	}
}
