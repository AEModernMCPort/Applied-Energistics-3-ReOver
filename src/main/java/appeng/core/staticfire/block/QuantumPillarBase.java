package appeng.core.staticfire.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public class QuantumPillarBase extends Block implements StaticFireBlockBase {
    final String REGISTRY_NAME = "quantum_pillar_base";

    public QuantumPillarBase() {
        super(Material.PISTON);
        setUnlocalizedName("test");
    }

    public String getRegistryNameSF() {
        return REGISTRY_NAME;
    }
}
