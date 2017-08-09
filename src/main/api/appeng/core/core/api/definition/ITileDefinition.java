package appeng.core.core.api.definition;

import appeng.api.definition.IDefinition;
import appeng.api.entry.TileRegistryEntry;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import org.apache.commons.lang3.tuple.ImmutablePair;

public interface ITileDefinition<TE extends TileEntity> extends IDefinition<TileRegistryEntry<TE>> {

	/**
	 * Compare tile in world with this.
	 *
	 * @param world of tile
	 * @param pos   of tile
	 * @return whether the tile at the location is the same
	 */
	default boolean isSameAs(IBlockAccess world, BlockPos pos){
		return isSameAs(new ImmutablePair(world, pos));
	}

}
