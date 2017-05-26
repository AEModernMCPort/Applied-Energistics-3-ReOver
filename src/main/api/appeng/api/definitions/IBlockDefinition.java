
package appeng.api.definitions;


import java.util.Optional;

import org.apache.commons.lang3.tuple.ImmutablePair;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;


public interface IBlockDefinition<B extends Block> extends IDefinition<B>
{

	/**
	 * @return the {@link ItemBlock} implementation if applicable
	 */
	<I extends ItemBlock> Optional<IItemDefinition<I>> maybeItem();

	/**
	 * Compare block in world with this.
	 *
	 * @param world of block
	 * @param pos of block
	 *
	 * @return whether the block at the location is the same
	 */
	default boolean isSameAs( IBlockAccess world, BlockPos pos )
	{
		return isSameAs( new ImmutablePair( world, pos ) );
	}
}
