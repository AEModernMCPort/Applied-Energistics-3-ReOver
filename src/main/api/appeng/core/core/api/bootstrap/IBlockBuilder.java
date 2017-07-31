package appeng.core.core.api.bootstrap;

import appeng.api.bootstrap.IDefinitionBuilder;
import appeng.core.core.api.definition.IBlockDefinition;
import appeng.core.core.api.definition.IItemDefinition;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;

import javax.annotation.Nonnull;
import java.util.function.Function;

public interface IBlockBuilder<B extends Block, BB extends IBlockBuilder<B, BB>> extends IDefinitionBuilder<B, IBlockDefinition<B>, BB> {

	//TODO 1.11.2-ReOver - Be back?
	//BB rendering(BlockRenderingCustomizer callback);

	<I extends ItemBlock> BB setItem(@Nonnull Function<IBlockDefinition<B>, IItemDefinition<I>> item);

	<I extends ItemBlock, C extends BlockItemCustomizer<B, I>> BB createItem(@Nonnull C ib);

	BB createDefaultItem();

}
