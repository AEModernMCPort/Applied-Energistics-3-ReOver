package appeng.core.me.bootstrap;

import appeng.core.me.api.network.DeviceUUID;
import appeng.core.me.api.network.NetBlock;
import appeng.core.me.api.network.NetDevice;
import appeng.core.me.api.network.PhysicalDevice;
import appeng.core.me.api.network.device.DeviceLoader;
import appeng.core.me.api.network.device.DeviceRegistryEntry;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.registries.IForgeRegistryEntry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class DeviceRegistryEntryImpl<N extends NetDevice<N, P>, P extends PhysicalDevice<N, P>> extends IForgeRegistryEntry.Impl<DeviceRegistryEntry<N, P>> implements DeviceRegistryEntry<N, P> {

	protected DeviceLoader<N, P> deserializer;

	public DeviceRegistryEntryImpl(DeviceLoader<N, P> deserializer){
		this.deserializer = deserializer;
	}

	@Override
	public N deserializeNBT(@Nonnull DeviceUUID uuid, @Nullable NetBlock netBlock, @Nonnull NBTTagCompound nbt){
		return deserializer.deserializeNBT(uuid, netBlock, nbt);
	}

}
