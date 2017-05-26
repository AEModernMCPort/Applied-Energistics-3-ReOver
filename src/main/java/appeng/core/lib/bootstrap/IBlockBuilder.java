
package appeng.core.lib.bootstrap;


import net.minecraft.block.Block;

import appeng.api.definitions.IBlockDefinition;


public interface IBlockBuilder<B extends Block, BB extends IBlockBuilder<B, BB>> extends IDefinitionBuilder<B, IBlockDefinition<B>, BB>
{

	BB rendering( BlockRenderingCustomizer callback );

	BB createDefaultItemBlock();

}
