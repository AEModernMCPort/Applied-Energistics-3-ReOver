package appeng.core.me.api.network.device;

import appeng.core.me.api.network.NetDevice;
import appeng.core.me.api.network.PhysicalDevice;
import net.minecraftforge.registries.IForgeRegistryEntry;

public interface DeviceRegistryEntry<N extends NetDevice<N, P>, P extends PhysicalDevice<N, P>> extends DeviceLoader<N, P>, IForgeRegistryEntry<DeviceRegistryEntry<N, P>> {

}
