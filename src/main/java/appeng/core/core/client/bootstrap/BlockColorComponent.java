package appeng.core.core.client.bootstrap;

import appeng.api.bootstrap.IDefinitionBuilder;
import appeng.core.core.api.definition.IBlockDefinition;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.IBlockColor;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * @author Fredi100
 */
public class BlockColorComponent<B extends Block> implements IDefinitionBuilder.DefinitionInitializationComponent<B, IBlockDefinition<B>> {

	private final Supplier<Optional<IBlockColor>> blockColor;

	public BlockColorComponent(Supplier<Optional<IBlockColor>> blockColor){
		this.blockColor = blockColor;
	}

	@Override
	public void init(IBlockDefinition<B> def){
		blockColor.get().ifPresent(color -> Minecraft.getMinecraft().getBlockColors().registerBlockColorHandler(color, def.maybe().get()));
	}
}