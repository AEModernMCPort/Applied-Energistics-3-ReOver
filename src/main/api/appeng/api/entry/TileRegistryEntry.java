package appeng.api.entry;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

public interface TileRegistryEntry<T extends TileEntity> {

	ResourceLocation getRegistryName();

	Class<T> getTileClass();

}
