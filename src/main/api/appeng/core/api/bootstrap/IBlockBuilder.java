package appeng.core.api.bootstrap;

import appeng.api.bootstrap.IDefinitionBuilder;
import appeng.api.definitions.IBlockDefinition;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;

public interface IBlockBuilder<B extends Block, BB extends IBlockBuilder<B, BB>> extends IDefinitionBuilder<B, IBlockDefinition<B>, BB> {

	//TODO 1.11.2-ReOver - Be back?
	//BB rendering(BlockRenderingCustomizer callback);

	default BB createDefaultItemBlock(){
		return createItemBlock(ItemBlock::new);
	}

	BB createItemBlock(ItemBlockCustomizer ib);

}
