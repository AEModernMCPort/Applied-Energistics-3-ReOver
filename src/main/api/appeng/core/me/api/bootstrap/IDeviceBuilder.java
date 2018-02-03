package appeng.core.me.api.bootstrap;

import appeng.api.bootstrap.IDefinitionBuilder;
import appeng.core.me.api.definition.IDeviceDefinition;
import appeng.core.me.api.network.DeviceUUID;
import appeng.core.me.api.network.NetDevice;
import appeng.core.me.api.network.PhysicalDevice;
import appeng.core.me.api.network.device.DeviceRegistryEntry;
import net.minecraft.nbt.NBTTagCompound;

import java.util.function.BiFunction;

public interface IDeviceBuilder<N extends NetDevice<N, P>, P extends PhysicalDevice<N, P>, DD extends IDeviceBuilder<N, P, DD>> extends IDefinitionBuilder<DeviceRegistryEntry<N, P>, IDeviceDefinition<N, P>, DD> {

	DD deserializer(BiFunction<DeviceUUID, NBTTagCompound, N> deserializer);

}
