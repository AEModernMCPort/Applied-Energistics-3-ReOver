package appeng.core.me.api.bootstrap;

import appeng.api.bootstrap.IDefinitionBuilder;
import appeng.core.me.api.definition.IDeviceDefinition;
import appeng.core.me.api.network.NetDevice;
import appeng.core.me.api.network.PhysicalDevice;
import appeng.core.me.api.network.device.DeviceLoader;
import appeng.core.me.api.network.device.DeviceRegistryEntry;

import java.util.function.Function;

public interface IDeviceBuilder<N extends NetDevice<N, P>, P extends PhysicalDevice<N, P>, DD extends IDeviceBuilder<N, P, DD>> extends IDefinitionBuilder<DeviceRegistryEntry<N, P>, IDeviceDefinition<N, P>, DD> {

	DD deserializer(Function<DeviceRegistryEntry<N, P>, DeviceLoader<N, P>> deserializer);

}
