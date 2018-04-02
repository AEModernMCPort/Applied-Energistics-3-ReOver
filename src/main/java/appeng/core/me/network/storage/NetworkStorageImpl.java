package appeng.core.me.network.storage;

import appeng.core.me.api.network.Network;
import appeng.core.me.api.network.storage.caps.NetworkStorageSpace;
import appeng.core.me.network.storage.caps.NetworkStorageCaps;

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

	public NetworkStorageSpace getNSS(){
		return network.getCapability(NetworkStorageCaps.nss, null);
	}

}
