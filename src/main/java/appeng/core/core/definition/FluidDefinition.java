package appeng.core.core.definition;

import appeng.core.core.api.definition.IFluidDefinition;
import appeng.core.lib.definition.Definition;
import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.IFluidBlock;

import java.util.Optional;

public class FluidDefinition<F extends Fluid> extends Definition<F> implements IFluidDefinition<F> {

	public FluidDefinition(ResourceLocation identifier, F f){
		super(identifier, f);
	}

	@Override
	public <B extends Block & IFluidBlock> Optional<B> maybeBlock(){
		return (Optional) maybe().map(Fluid::getBlock);
	}
}
