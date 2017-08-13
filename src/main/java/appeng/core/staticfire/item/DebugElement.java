package appeng.core.staticfire.item;

import net.minecraft.item.Item;

/**
 * Created by Peter on 2017. 08. 11..
 */
public class DebugElement extends Item implements StaticFireItemBase{

    final String REGISTRY_NAME = "DebugElement";

    public DebugElement() {
        setUnlocalizedName("DebugElement");

        // Cool looking thing
        //ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(this.REGISTRY_NAME.toString()));
    }

    @Override
    public String getRegistryNameSF() {
        return REGISTRY_NAME;
    }
}