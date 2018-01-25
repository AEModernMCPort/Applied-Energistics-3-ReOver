package appeng.core.me.api.network.device;

import appeng.core.me.api.network.NetBlock;
import appeng.core.me.api.network.NetDevice;
import appeng.core.me.api.network.PhysicalDevice;

import java.util.Collection;

public interface BRINMDevice<N extends BRINMDevice<N, P>, P extends PhysicalDevice<N, P>> extends NetDevice<N, P> {

	Collection<NetBlock> getDependentBlocks();

}
