
package appeng.core.lib.bootstrap.components;


import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraftforge.fml.relauncher.Side;


public class BlockColorComponent implements InitComponent
{

	private final Block block;

	private final IBlockColor blockColor;

	public BlockColorComponent( Block block, IBlockColor blockColor )
	{
		this.block = block;
		this.blockColor = blockColor;
	}

	@Override
	public void init( Side side )
	{
		Minecraft.getMinecraft().getBlockColors().registerBlockColorHandler( blockColor, block );
	}

}
