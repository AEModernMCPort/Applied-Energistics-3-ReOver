package appeng.core.lib.recipe.ingredient;

import appeng.api.recipe.IGIngredient;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidRegistry;

/**
 * @author Fredi100
 **/
@FunctionalInterface
public interface FluidStackIngredient extends IGIngredient<FluidStack> {

	@Override
	default boolean test(FluidStack fluidStack){
		return fluidStack.amount == 0 ? acceptsEmpty() : testFluid(fluidStack.getFluid());
	}

	default boolean acceptsEmpty(){ return false; }

	boolean testFluid(Fluid fluid);

	class SimplyFluid implements FluidStackIngredient {

		private final Fluid fluid;

		public SimplyFluid(Fluid fluid){ this.fluid = fluid; }

		@Override
		public boolean testFluid(Fluid fluid){ return fluid == this.fluid; }

	}

	class EquivalentStack implements IGIngredient<FluidStack> {

		private final FluidStack fluidStack;

		public EquivalentStack(FluidStack fluidStack){
			this.fluidStack = fluidStack;
			this.fluidStack.amount = 1;
		}

		@Override
		public boolean test(FluidStack fluidStack){
			FluidStack toCompareTo = fluidStack.copy();
			toCompareTo.amount = 1;
			return FluidStack.areFluidStackTagsEqual(toCompareTo, this.fluidStack);
		}
	}

	class FluidDictionaryEntry implements IGIngredient<FluidStack> {

		private final String fluidDict;

		public FluidDictionaryEntry(String fluidDict){
			this.fluidDict = fluidDict;
		}

		@Override
		public boolean test(FluidStack fluidStack){
			return FluidRegistry.getFluidName(fluidStack).equals(fluidDict);
		}
	}
}
