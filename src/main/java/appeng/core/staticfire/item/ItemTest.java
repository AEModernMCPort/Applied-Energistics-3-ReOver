package appeng.core.staticfire.item;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;

public class ItemTest extends StaticFireItemBase{

    final String REGISTRY_NAME = "itemtest";

    public ItemTest() {
        setUnlocalizedName("test");


        // Cool looking thing
        //ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(this.REGISTRY_NAME.toString()));
    }
}
