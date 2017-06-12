package appeng.core.core.bootstrap;

import appeng.api.bootstrap.IDefinitionBuilder;
import appeng.api.definitions.IBlockDefinition;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.IBlockColor;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * @author Fredi100
 */
public class BlockColorComponent implements IDefinitionBuilder.DefinitionInitializationComponent<Block, IBlockDefinition<Block>> {

	private final Optional<IBlockColor> blockColor;

	public BlockColorComponent(Optional<IBlockColor> blockColor){
		this.blockColor = blockColor;
	}

	@Override
	public void init(IBlockDefinition<Block> def){
		System.out.println(this);
		Minecraft.getMinecraft().getBlockColors().registerBlockColorHandler(blockColor.get(), def.maybe().get());
	}
}