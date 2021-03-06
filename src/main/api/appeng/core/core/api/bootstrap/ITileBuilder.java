package appeng.core.core.api.bootstrap;

import appeng.api.bootstrap.IDefinitionBuilder;
import appeng.core.core.api.definition.IBlockDefinition;
import appeng.core.core.api.definition.ITileDefinition;
import appeng.api.entry.TileRegistryEntry;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;

import javax.annotation.Nonnull;
import java.util.function.Function;

public interface ITileBuilder<T extends TileEntity, TT extends ITileBuilder<T, TT>> extends IDefinitionBuilder<TileRegistryEntry<T>, ITileDefinition<T>, TT> {

	<B extends Block> TT setBlock(@Nonnull Function<ITileDefinition<T>, IBlockDefinition<B>> block);

	<B extends Block> TT createBlock(@Nonnull TileBlockCustomizer<T, B> customizer);

	TT createDefaultBlock(Material material);

	TT createDefaultBlockWithItem(Material material);

}
