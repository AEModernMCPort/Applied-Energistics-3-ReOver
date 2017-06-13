package appeng.core.api.bootstrap;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;

import javax.annotation.Nonnull;

@FunctionalInterface
public interface BlockItemCustomizer<I extends ItemBlock> {

	@Nonnull
	I createItem(Block block);

	@Nonnull
	default IItemBuilder<I, ?> customize(@Nonnull IItemBuilder<I, ?> builder){
		return builder;
	}

}
