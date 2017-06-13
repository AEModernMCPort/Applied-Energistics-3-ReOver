package appeng.core.core.client.bootstrap;

import appeng.api.bootstrap.IDefinitionBuilder;
import appeng.api.definitions.IItemDefinition;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.item.Item;

import javax.annotation.Nonnull;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * @author Fredi100
 */
public class ItemMeshDefinitionComponent implements IDefinitionBuilder.DefinitionInitializationComponent<Item, IItemDefinition<Item>> {

	private final Supplier<Optional<ItemMeshDefinition>> meshDefinition;

	public ItemMeshDefinitionComponent(@Nonnull Supplier<Optional<ItemMeshDefinition>> meshDefinition){
		this.meshDefinition = meshDefinition;
	}

	@Override
	public void init(IItemDefinition<Item> def){
		meshDefinition.get().ifPresent(itemMeshDefinition -> Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(def.maybe().get(), itemMeshDefinition));
	}
}
