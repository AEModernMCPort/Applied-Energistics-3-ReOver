package appeng.core.me.definitions;

import appeng.api.bootstrap.DefinitionFactory;
import appeng.core.lib.definitions.Definitions;
import appeng.core.me.api.definition.IDeviceDefinition;
import appeng.core.me.api.definitions.IMEDeviceDefinitions;
import appeng.core.me.api.network.NetDevice;
import appeng.core.me.api.network.PhysicalDevice;
import appeng.core.me.api.network.device.DeviceRegistryEntry;

public class MEDeviceDefinitions<N extends NetDevice<N, P>, P extends PhysicalDevice<N, P>> extends Definitions<DeviceRegistryEntry<N, P>, IDeviceDefinition<N, P>> implements IMEDeviceDefinitions<N, P> {

	public MEDeviceDefinitions(DefinitionFactory registry){

	}

	private DefinitionFactory.InputHandler<DeviceRegistryEntry, Void> ih(){
		return new DefinitionFactory.InputHandler<DeviceRegistryEntry, Void>(null) {};
	}

}
