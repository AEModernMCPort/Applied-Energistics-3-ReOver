package appeng.core.core.api.definition;

import appeng.api.definition.IDefinition;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.biome.Biome;
import org.apache.commons.lang3.tuple.ImmutablePair;

public interface IBiomeDefinition<B extends Biome> extends IDefinition<B> {

	/**
	 * Compare biome in world with this.
	 *
	 * @param world world with biome to check
	 * @param pos   of biome tockeck
	 * @return whether the biome at the location is the same
	 */
	default boolean isSameAs(IBlockAccess world, BlockPos pos){
		return isSameAs(new ImmutablePair(world, pos));
	}
}
