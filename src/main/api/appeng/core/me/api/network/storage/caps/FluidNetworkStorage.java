package appeng.core.me.api.network.storage.caps;

import appeng.core.me.api.network.storage.atomic.SubtypedAtomicNetworkStorage;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

/*
 * Not only FluidStack implements both hashCode and equals, but it also does not include amount in them. Thank you.
 * TODO 1.13 Check that it is still the case
 */
public interface FluidNetworkStorage extends SubtypedAtomicNetworkStorage<FluidStack, Fluid> {}
