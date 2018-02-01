package appeng.core.me.network;

import appeng.core.me.api.network.NetBlock;
import appeng.core.me.api.network.NetBlockUUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class NetworkBlocksManager {

	protected Map<NetBlockUUID, NetBlockImpl> netBlocks = new HashMap<>();

	@Nullable
	public NetBlock getBlock(NetBlockUUID uuid){
		return netBlocks.get(uuid);
	}

	@Nonnull
	public Collection<NetBlockImpl> getBlocks(){
		return netBlocks.values();
	}

}
