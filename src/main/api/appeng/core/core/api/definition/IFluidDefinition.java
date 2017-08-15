package appeng.core.core.api.definition;

import appeng.api.definition.IDefinition;
import net.minecraft.block.Block;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.IFluidBlock;

import java.util.Optional;

public interface IFluidDefinition<F extends Fluid> extends IDefinition<F> {

	<B extends Block & IFluidBlock> Optional<B> maybeBlock();

}
