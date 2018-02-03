package appeng.core.me.api.network.device;

import appeng.core.me.api.network.DeviceUUID;
import appeng.core.me.api.network.NetDevice;
import appeng.core.me.api.network.PhysicalDevice;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.registries.IForgeRegistryEntry;

public interface DeviceRegistryEntry<N extends NetDevice<N, P>, P extends PhysicalDevice<N, P>> extends IForgeRegistryEntry<DeviceRegistryEntry<N, P>> {

	N deserializeNBT(DeviceUUID uuid, NBTTagCompound nbt);

}
