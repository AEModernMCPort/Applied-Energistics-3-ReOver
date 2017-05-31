package appeng.core.api.bootstrap;

import appeng.api.bootstrap.IDefinitionBuilder;
import appeng.api.definitions.IBlockDefinition;
import net.minecraft.block.Block;

public interface IBlockBuilder<B extends Block, BB extends IBlockBuilder<B, BB>> extends IDefinitionBuilder<B, IBlockDefinition<B>, BB> {

	//TODO 1.11.2-ReOver - Be back?
	//BB rendering(BlockRenderingCustomizer callback);

	BB createDefaultItemBlock();

}
