package appeng.api.entry;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import java.util.function.BiFunction;

public interface TileRegistryEntry<T extends TileEntity> extends BiFunction<World, Integer, T> {

	ResourceLocation getRegistryName();

	Class<T> getTileClass();

}
