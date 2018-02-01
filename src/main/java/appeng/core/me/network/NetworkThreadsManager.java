package appeng.core.me.network;

import appeng.core.me.api.network.NetDevice;
import appeng.core.me.api.network.Network;
import appeng.core.me.api.network.PhysicalDevice;
import net.minecraft.util.ITickable;

import javax.annotation.Nonnull;
import java.util.Collection;

public class NetworkThreadsManager {

	@Nonnull
	public <N extends NetDevice<N, P> & ITickable, P extends PhysicalDevice<N, P>> Network.NetworkThread getDeviceThread(N device){
		return null;
	}

	@Nonnull
	public Collection<Network.NetworkThread> getThreads(){
		return null;
	}

}
