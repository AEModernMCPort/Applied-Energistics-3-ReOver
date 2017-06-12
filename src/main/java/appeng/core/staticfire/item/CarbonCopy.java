package appeng.core.staticfire.item;

import net.minecraft.item.Item;

public class CarbonCopy extends Item implements StaticFireItemBase{
    final String REGISTRY_NAME = "carbon_copy_module";

    public CarbonCopy() {
        setUnlocalizedName("test");
    }

    @Override
    public String getRegistryNameSF() {
        return REGISTRY_NAME;
    }
}
