package appeng.core.lib.bootstrap_olde.components;

import appeng.api.bootstrap.InitializationComponent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.item.Item;

import javax.annotation.Nonnull;

/**
 * Registers a custom item mesh definition that can be used to dynamically determine the item model based on
 * item stack properties.
 */
public class ItemMeshDefinitionComponent implements InitializationComponent.Init {

	private final Item item;

	private final ItemMeshDefinition meshDefinition;

	public ItemMeshDefinitionComponent(@Nonnull Item item, @Nonnull ItemMeshDefinition meshDefinition){
		this.item = item;
		this.meshDefinition = meshDefinition;
	}

	@Override
	public void init(){
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(item, meshDefinition);
	}
}
