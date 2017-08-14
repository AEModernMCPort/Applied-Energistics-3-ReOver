package appeng.core.core.definitions;

import appeng.api.bootstrap.DefinitionFactory;
import appeng.core.core.api.definition.IItemDefinition;
import appeng.core.AppEng;
import appeng.core.core.api.bootstrap.IItemBuilder;
import appeng.core.core.api.definitions.ICoreItemDefinitions;
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

	public CoreItemDefinitions(DefinitionFactory registry){
		this.material = registry
				.<Item, IItemDefinition<Item>, IItemBuilder<Item, ?>, Item>definitionBuilder(new ResourceLocation(AppEng.MODID, "material"), ih(new ItemMaterial())).setFeature(null)
				.initializationComponent(Side.CLIENT, new ItemMeshDefinitionComponent<Item>(() -> Optional.of(stack -> new ModelResourceLocation(((ItemMaterial) stack.getItem()).getMaterial(stack).getModel(), "inventory"))))
				.build();
	}

	private DefinitionFactory.InputHandler<Item, Item> ih(Item item){
		return new DefinitionFactory.InputHandler<Item, Item>(item) {};
	}
}
