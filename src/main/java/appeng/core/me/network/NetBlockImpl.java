package appeng.core.me.network;

import appeng.core.me.api.network.*;
import appeng.core.me.api.network.device.BRINMDevice;
import appeng.core.me.api.network.event.NCEventBus;
import appeng.core.me.api.parts.GlobalWorldVoxelPosition;
import appeng.core.me.network.block.NetBlockConnections;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class NetBlockImpl implements NetBlock {

	protected NetBlockImpl(NetBlockUUID uuid, Network network){
		this.uuid = uuid;
		this.network = network;
	}

	public NetBlockImpl(NetBlockUUID uuid, GlobalWorldVoxelPosition position, Network network){
		this(uuid, network);
		this.position = position;
	}

	/*
	 * Info
	 */

	protected final NetBlockUUID uuid;
	protected GlobalWorldVoxelPosition position;

	@Nonnull
	@Override
	public NetBlockUUID getUUID(){
		return uuid;
	}

	@Nonnull
	@Override
	public GlobalWorldVoxelPosition getPosition(){
		return position;
	}

	/*
	 * Network
	 */

	protected Network network;

	@Nonnull
	@Override
	public Optional<Network> getNetwork(){
		return Optional.ofNullable(network);
	}

	@Override
	public void switchNetwork(@Nullable Network network){
		this.network = network;
		//TODO Notify devices
	}

	/*
	 * Devices
	 */

	protected NetBlockConnections connections = new NetBlockConnections(this);
	protected Map<DeviceUUID, NetDevice> devices = new HashMap<>();

	@Nonnull
	@Override
	public <N extends NetDevice<N, P>, P extends PhysicalDevice<N, P>> Optional<N> getDevice(DeviceUUID device){
		return Optional.ofNullable((N) devices.get(device));
	}

	@Nonnull
	@Override
	public <N extends NetDevice<N, P>, P extends PhysicalDevice<N, P>> Collection<N> getDevices(){
		return (Collection) devices.values();
	}

	@Nonnull
	@Override
	public <N extends BRINMDevice<N, P>, P extends PhysicalDevice<N, P>> N getMaster(){
		//TODO Masters
		return null;
	}

	/*
	 * Events
	 */

	@Nonnull
	@Override
	public NCEventBus<NetBlock, NetBlockEvent> getEventBus(){
		//TODO Events
		return null;
	}

	/*
	 * IO
	 */

	@Override
	public NBTTagCompound serializeNBT(){
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setTag("pos", position.serializeNBT());
		nbt.setTag("connections", connections.serializeNBT());
		return nbt;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt){
		position = GlobalWorldVoxelPosition.fromNBT(nbt.getCompoundTag("pos"));
		connections.deserializeNBT(nbt.getCompoundTag("connections"));
	}

	public static NetBlockImpl createFromNBT(@Nonnull NetBlockUUID uuid, @Nullable Network network, @Nonnull NBTTagCompound nbt){
		NetBlockImpl netBlock = new NetBlockImpl(uuid, network);
		netBlock.deserializeNBT(nbt);
		return netBlock;
	}

}
