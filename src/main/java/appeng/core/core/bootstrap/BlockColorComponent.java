package appeng.core.core.bootstrap;

import appeng.api.bootstrap.IDefinitionBuilder;
import appeng.api.definitions.IBlockDefinition;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.IBlockColor;

/**
 * @author Fredi100
 */
public class BlockColorComponent implements IDefinitionBuilder.DefinitionInitializationComponent<Block, IBlockDefinition<Block>> {

	private final IBlockColor blockColor;

	public BlockColorComponent(IBlockColor blockColor){
		this.blockColor = blockColor;
	}

	@Override
	public void init(IBlockDefinition<Block> def){
		System.out.println("Initializing BlockColorComponent");
		Minecraft.getMinecraft().getBlockColors().registerBlockColorHandler(blockColor, def.maybe().get());
	}
}