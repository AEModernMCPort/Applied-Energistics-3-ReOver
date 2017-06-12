package appeng.core.core.client.bootstrap;

import appeng.api.bootstrap.IDefinitionBuilder;
import appeng.api.definitions.IBlockDefinition;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.IBlockColor;
import scala.collection.parallel.ParIterableLike;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * @author Fredi100
 */
public class BlockColorComponent implements IDefinitionBuilder.DefinitionInitializationComponent<Block, IBlockDefinition<Block>> {

	private final Supplier<Optional<IBlockColor>> blockColor;

	public BlockColorComponent(Supplier<Optional<IBlockColor>> blockColor){
		this.blockColor = blockColor;
	}

	@Override
	public void init(IBlockDefinition<Block> def){
		System.out.println(this);
		blockColor.get().ifPresent(color -> Minecraft.getMinecraft().getBlockColors().registerBlockColorHandler(color, def.maybe().get()));
	}
}