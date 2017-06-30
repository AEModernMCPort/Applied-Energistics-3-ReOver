package appeng.core.api.bootstrap;

import appeng.api.definitions.IBlockDefinition;
import appeng.core.item.ItemBlockGood;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;

import javax.annotation.Nonnull;

@FunctionalInterface
public interface BlockItemCustomizer<B extends Block, I extends ItemBlock> {

	@Nonnull
	I createItem(IBlockDefinition<B> block);

	@Nonnull
	default IItemBuilder<I, ?> customize(@Nonnull IItemBuilder<I, ?> builder, @Nonnull IBlockDefinition<B> block){
		return builder;
	}

	@FunctionalInterface
	interface UseDefaultItemCustomize<B extends Block> extends BlockItemCustomizer<B, ItemBlock> {

		@Nonnull
		@Override
		default ItemBlock createItem(IBlockDefinition<B> block){
			//TODO do something. This can't be in API now.
			return new ItemBlockGood(block.maybe().get());
		}

		@Nonnull
		@Override
		IItemBuilder<ItemBlock, ?> customize(@Nonnull IItemBuilder<ItemBlock, ?> builder, @Nonnull IBlockDefinition<B> block);

	}

}
