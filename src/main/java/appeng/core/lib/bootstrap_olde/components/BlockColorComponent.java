package appeng.core.lib.bootstrap_olde.components;

import appeng.api.bootstrap.InitializationComponent;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.IBlockColor;

public class BlockColorComponent implements InitializationComponent.Init {

	private final Block block;

	private final IBlockColor blockColor;

	public BlockColorComponent(Block block, IBlockColor blockColor){
		this.block = block;
		this.blockColor = blockColor;
	}

	@Override
	public void init(){
		Minecraft.getMinecraft().getBlockColors().registerBlockColorHandler(blockColor, block);
	}

}
