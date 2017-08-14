package appeng.api.entry;

import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import java.util.function.BiFunction;

public interface TileRegistryEntry<T extends TileEntity> extends BiFunction<World, IBlockState, T> {

	ResourceLocation getRegistryName();

	Class<T> getTileClass();

}
