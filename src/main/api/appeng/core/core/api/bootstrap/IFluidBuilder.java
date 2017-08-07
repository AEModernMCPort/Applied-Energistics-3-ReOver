package appeng.core.core.api.bootstrap;

import appeng.api.bootstrap.IDefinitionBuilder;
import appeng.core.core.api.definition.IBlockDefinition;
import appeng.core.core.api.definition.IFluidDefinition;
import net.minecraft.block.Block;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.IFluidBlock;

import java.util.function.Function;

public interface IFluidBuilder<F extends Fluid, FF extends IFluidBuilder<F, FF>> extends IDefinitionBuilder<F, IFluidDefinition<F>, FF>{

	<B extends Block & IFluidBlock> FF setBlock(Function<IFluidDefinition<F>, IBlockDefinition<B>> block);

	<B extends Block & IFluidBlock> FF createBlock(FluidBlockCustomizer<F, B> customizer);

}
