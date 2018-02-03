package appeng.core.me.api.definition;

import appeng.api.definition.IDefinition;
import appeng.core.me.api.network.NetDevice;
import appeng.core.me.api.network.PhysicalDevice;
import appeng.core.me.api.network.device.DeviceRegistryEntry;

public interface IDeviceDefinition<N extends NetDevice<N, P>, P extends PhysicalDevice<N, P>> extends IDefinition<DeviceRegistryEntry<N, P>> {}
