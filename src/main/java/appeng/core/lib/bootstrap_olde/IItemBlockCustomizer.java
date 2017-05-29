package appeng.core.lib.bootstrap_olde;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;

import javax.annotation.Nonnull;

public interface IItemBlockCustomizer<I extends ItemBlock> {

	@Nonnull
	I createItemBlock(Block block);

	/**
	 * @param builder the to-be-completed definition
	 * @return the completed definition
	 */
	@Nonnull
	default ItemDefinitionBuilder<I> customize(@Nonnull ItemDefinitionBuilder<I> builder){
		return builder;
	}

}
