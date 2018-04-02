package appeng.core.me.network.storage;

import appeng.core.me.api.network.Network;
import appeng.core.me.api.network.storage.caps.NetworkStorageSpace;
import appeng.core.me.network.storage.caps.NetworkStorageCaps;
import net.minecraft.util.ResourceLocation;

public abstract class NetworkStorageImpl {

	protected Network network;

	public NetworkStorageImpl(){
	}

	public NetworkStorageImpl(Network network){
		this.network = network;
	}

	public Network getNetwork(){
		return network;
	}

	public void setNetwork(Network network){
		this.network = network;
	}

	public abstract ResourceLocation getNSSID();

	public NetworkStorageSpace getNSS(){
		return network.getCapability(NetworkStorageCaps.nss, null);
	}

}
