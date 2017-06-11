package appeng.core.core.bootstrap;

import appeng.api.bootstrap.IDefinitionBuilder;
import appeng.api.definitions.IItemDefinition;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.item.Item;

import javax.annotation.Nonnull;

/**
 * @author Fredi100
 */
public class ItemMeshDefinitionComponent implements IDefinitionBuilder.DefinitionInitializationComponent<Item, IItemDefinition<Item>> {

	private final ItemMeshDefinition meshDefinition;

	public ItemMeshDefinitionComponent(@Nonnull ItemMeshDefinition meshDefinition){
		this.meshDefinition = meshDefinition;
	}

	@Override
	public void init(IItemDefinition<Item> def){
		System.out.println("Initializing ItemMeshDefinitionComponent");
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(def.maybe().get(), meshDefinition);
	}
}
