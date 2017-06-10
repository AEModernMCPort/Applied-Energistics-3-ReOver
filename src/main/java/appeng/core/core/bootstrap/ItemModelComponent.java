package appeng.core.core.bootstrap;

import appeng.api.bootstrap.IDefinitionBuilder;
import appeng.api.definitions.IItemDefinition;
import appeng.core.api.bootstrap.IItemBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;

import javax.annotation.Nonnull;
import java.util.Map;

/**
 * @author Fredi100
 */
public class ItemModelComponent implements IDefinitionBuilder.DefinitionInitializationComponent<Item, IItemDefinition<Item>>{


    private final Map<Integer, ModelResourceLocation> modelsByMeta;

    public ItemModelComponent(@Nonnull Map<Integer, ModelResourceLocation> modelsByMeta){
        this.modelsByMeta = modelsByMeta;
    }

    @Override
    public void init(IItemDefinition<Item> def) {
        ItemModelMesher itemMesher = Minecraft.getMinecraft().getRenderItem().getItemModelMesher();

        modelsByMeta.forEach((meta, model) -> {
            itemMesher.register(def.maybe().get(), meta, model);
        });
    }
}
