package appeng.core.me.network;

import appeng.core.me.api.network.NetBlock;
import appeng.core.me.api.network.NetBlockUUID;
import appeng.core.me.api.network.Network;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class NetworkBlocksManager implements INBTSerializable<NBTTagCompound> {

	protected final Network network;
	protected Map<NetBlockUUID, NetBlockImpl> netBlocks = new HashMap<>();

	public NetworkBlocksManager(Network network){
		this.network = network;
	}

	@Nullable
	public NetBlock getBlock(NetBlockUUID uuid){
		return netBlocks.get(uuid);
	}

	@Nonnull
	public Collection<NetBlockImpl> getBlocks(){
		return netBlocks.values();
	}

	/*
	 * IO
	 */

	@Override
	public NBTTagCompound serializeNBT(){
		NBTTagCompound nbt = new NBTTagCompound();
		NBTTagList blocks = new NBTTagList();
		this.netBlocks.entrySet().forEach(e -> {
			NBTTagCompound next = new NBTTagCompound();
			next.setTag("uuid", e.getKey().serializeNBT());
			next.setTag("block", e.getValue().serializeNBT());
			blocks.appendTag(next);
		});
		nbt.setTag("blocks", blocks);
		return nbt;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt){
		this.netBlocks.clear();
		NBTTagList blocks = (NBTTagList) nbt.getTag("blocks");
		blocks.forEach(nbtBase -> {
			NBTTagCompound next = (NBTTagCompound) nbtBase;
			NetBlockUUID uuid = NetBlockUUID.fromNBT(next.getCompoundTag("uuid"));
			this.netBlocks.put(uuid, NetBlockImpl.createFromNBT(uuid, network, next.getCompoundTag("block")));
		});
	}

}
