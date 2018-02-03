package appeng.core.me.definition;

import appeng.core.lib.definition.Definition;
import appeng.core.me.api.definition.IDeviceDefinition;
import appeng.core.me.api.definition.IPartDefinition;
import appeng.core.me.api.network.NetDevice;
import appeng.core.me.api.network.PhysicalDevice;
import appeng.core.me.api.network.device.DeviceRegistryEntry;
import appeng.core.me.api.parts.part.Part;
import net.minecraft.util.ResourceLocation;

public class DeviceDefinition<N extends NetDevice<N, P>, P extends PhysicalDevice<N, P>> extends Definition<DeviceRegistryEntry<N, P>> implements IDeviceDefinition<N, P> {

	public DeviceDefinition(ResourceLocation identifier, DeviceRegistryEntry<N, P> reg){
		super(identifier, reg);
	}
}
