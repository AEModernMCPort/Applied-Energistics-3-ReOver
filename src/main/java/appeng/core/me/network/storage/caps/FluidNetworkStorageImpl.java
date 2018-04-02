package appeng.core.me.network.storage.caps;

import appeng.core.me.api.network.storage.caps.FluidNetworkStorage;
import appeng.core.me.network.storage.atomic.SubtypedAtomicNetworkStorageImpl;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

public class FluidNetworkStorageImpl extends SubtypedAtomicNetworkStorageImpl<FluidStack, Fluid> implements FluidNetworkStorage {

	public FluidNetworkStorageImpl(){
		super(FluidStack::getFluid, fluid -> {
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setString("id", fluid.getName());
			return nbt;
		}, nbt -> FluidRegistry.getFluid(nbt.getString("id")), stack -> stack.writeToNBT(new NBTTagCompound()), FluidStack::loadFluidStackFromNBT);
	}

}
