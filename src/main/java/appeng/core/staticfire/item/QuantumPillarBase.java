package appeng.core.staticfire.item;

import net.minecraft.item.Item;

public class QuantumPillarBase extends Item implements StaticFireItemBase{
    final String REGISTRY_NAME = "quantum_pillar_base";

    public QuantumPillarBase() {
        setUnlocalizedName("test");
    }

    public String getRegistryNameSF() {
        return REGISTRY_NAME;
    }
}
