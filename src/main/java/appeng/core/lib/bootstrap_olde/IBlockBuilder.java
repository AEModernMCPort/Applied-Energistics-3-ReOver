package appeng.core.lib.bootstrap_olde;

import appeng.api.bootstrap.DefinitionBuilder;
import appeng.api.definitions.IBlockDefinition;
import net.minecraft.block.Block;

public interface IBlockBuilder<B extends Block, BB extends IBlockBuilder<B, BB>>
		extends DefinitionBuilder<B, IBlockDefinition<B>, BB> {

	BB rendering(BlockRenderingCustomizer callback);

	BB createDefaultItemBlock();

}
