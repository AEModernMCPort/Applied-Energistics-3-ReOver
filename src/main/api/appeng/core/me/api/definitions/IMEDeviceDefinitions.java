package appeng.core.me.api.definitions;

import appeng.api.definitions.IDefinitions;
import appeng.core.me.api.definition.IDeviceDefinition;
import appeng.core.me.api.network.NetDevice;
import appeng.core.me.api.network.PhysicalDevice;
import appeng.core.me.api.network.device.DeviceRegistryEntry;

public interface IMEDeviceDefinitions<N extends NetDevice<N, P>, P extends PhysicalDevice<N, P>> extends IDefinitions<DeviceRegistryEntry<N, P>, IDeviceDefinition<N, P>> {}
