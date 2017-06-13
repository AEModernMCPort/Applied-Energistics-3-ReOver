package appeng.api.entry;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public interface TileRegistryEntry<T extends TileEntity> {

	ResourceLocation getRegistryName();

	Class<T> getTileClass();

	T createNewTile(World world, int meta);

}
