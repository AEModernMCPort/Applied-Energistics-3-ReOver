package appeng.core.core.bootstrap;

import appeng.api.bootstrap.IDefinitionBuilder;
import appeng.api.definitions.IItemDefinition;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.item.Item;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

/**
 * @author Fredi100
 */
public class ItemMeshDefinitionComponent implements IDefinitionBuilder.DefinitionInitializationComponent<Item, IItemDefinition<Item>> {

	private final Supplier<ItemMeshDefinition> meshDefinition;

	public ItemMeshDefinitionComponent(@Nonnull Supplier<ItemMeshDefinition> meshDefinition){
		this.meshDefinition = meshDefinition;
	}

	@Override
	public void init(IItemDefinition<Item> def){
		System.out.println("Initializing ItemMeshDefinitionComponent");
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(def.maybe().get(), meshDefinition.get());
	}
}
