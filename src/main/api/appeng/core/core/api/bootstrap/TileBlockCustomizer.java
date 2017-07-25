package appeng.core.core.api.bootstrap;

import appeng.api.entry.TileRegistryEntry;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.tileentity.TileEntity;

import javax.annotation.Nonnull;

@FunctionalInterface
public interface TileBlockCustomizer<T extends TileEntity, B extends Block & ITileEntityProvider> {

	@Nonnull
	B createBlock(TileRegistryEntry<T> tile);

	@Nonnull
	default IBlockBuilder<B, ?> customize(@Nonnull IBlockBuilder<B, ?> builder){
		return builder;
	}

}
