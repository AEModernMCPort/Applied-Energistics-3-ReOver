package appeng.core.staticfire.item;

import net.minecraft.item.Item;

public class SkyStoneIngot extends Item implements StaticFireItemBase{

    final String REGISTRY_NAME = "SkyStoneIngot";

    public SkyStoneIngot() {
        setUnlocalizedName("SkyStoneIngot");

        // Cool looking thing
        //ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(this.REGISTRY_NAME.toString()));
    }

    @Override
    public String getRegistryNameSF() {
        return REGISTRY_NAME;
    }
}