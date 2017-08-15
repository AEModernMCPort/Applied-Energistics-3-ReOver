package appeng.core.core.api.bootstrap;

import appeng.core.core.api.definition.IFluidDefinition;
import net.minecraft.block.Block;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.IFluidBlock;

import javax.annotation.Nonnull;

@FunctionalInterface
public interface FluidBlockCustomizer<F extends Fluid, B extends Block & IFluidBlock> {

	@Nonnull
	B createBlock(IFluidDefinition<F> fluid);

	@Nonnull
	default IBlockBuilder<B, ?> customize(@Nonnull IBlockBuilder<B, ?> builder){
		return builder;
	}

}
