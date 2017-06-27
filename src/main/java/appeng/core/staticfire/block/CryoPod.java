package appeng.core.staticfire.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public class CryoPod  extends Block implements StaticFireBlockBase{

    final String REGISTRY_NAME = "cryo";

    public CryoPod() {
        super(Material.ROCK);
    }

    @Override
    public String getRegistryNameSF() {
        return REGISTRY_NAME;
    }
}
