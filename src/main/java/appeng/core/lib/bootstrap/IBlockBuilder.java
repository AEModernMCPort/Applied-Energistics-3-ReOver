package appeng.core.lib.bootstrap;

import appeng.api.definitions.IBlockDefinition;
import net.minecraft.block.Block;

public interface IBlockBuilder<B extends Block, BB extends IBlockBuilder<B, BB>>
		extends IDefinitionBuilder<B, IBlockDefinition<B>, BB> {

	BB rendering(BlockRenderingCustomizer callback);

	BB createDefaultItemBlock();

}
