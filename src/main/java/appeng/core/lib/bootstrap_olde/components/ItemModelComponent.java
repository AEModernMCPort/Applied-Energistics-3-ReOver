package appeng.core.lib.bootstrap_olde.components;

import appeng.api.bootstrap.InitializationComponent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;

import javax.annotation.Nonnull;
import java.util.Map;

/**
 * Registers the models that should by used for an item, including the ability to
 * distinguish by meta.
 */
public class ItemModelComponent implements InitializationComponent.Init {

	private final Item item;

	private final Map<Integer, ModelResourceLocation> modelsByMeta;

	public ItemModelComponent(@Nonnull Item item, @Nonnull Map<Integer, ModelResourceLocation> modelsByMeta){
		this.item = item;
		this.modelsByMeta = modelsByMeta;
	}

	@Override
	public void init(){
		ItemModelMesher itemMesher = Minecraft.getMinecraft().getRenderItem().getItemModelMesher();

		modelsByMeta.forEach((meta, model) -> {
			itemMesher.register(item, meta, model);
		});
	}

}
