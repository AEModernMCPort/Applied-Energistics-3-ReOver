package appeng.core.me.bootstrap;

import appeng.core.me.api.network.DeviceUUID;
import appeng.core.me.api.network.NetDevice;
import appeng.core.me.api.network.PhysicalDevice;
import appeng.core.me.api.network.device.DeviceRegistryEntry;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.registries.IForgeRegistryEntry;

import java.util.function.BiFunction;

public class DeviceRegistryEntryImpl<N extends NetDevice<N, P>, P extends PhysicalDevice<N, P>> extends IForgeRegistryEntry.Impl<DeviceRegistryEntry<N, P>> implements DeviceRegistryEntry<N, P> {

	protected BiFunction<DeviceUUID, NBTTagCompound, N> deserializer;

	public DeviceRegistryEntryImpl(BiFunction<DeviceUUID, NBTTagCompound, N> deserializer){
		this.deserializer = deserializer;
	}

	@Override
	public N deserializeNBT(DeviceUUID uuid, NBTTagCompound nbt){
		return deserializer.apply(uuid, nbt);
	}

}
