package appeng.core.me.network;

import appeng.core.me.api.network.*;
import appeng.core.me.api.network.device.BRINMDevice;
import appeng.core.me.api.network.event.NCEventBus;
import appeng.core.me.api.parts.GlobalWorldVoxelPosition;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class NetBlockImpl implements NetBlock {

	public NetBlockImpl(NetBlockUUID uuid, GlobalWorldVoxelPosition position, Network network){
		this.uuid = uuid;
		this.position = position;
		this.network = network;
	}

	/*
	 * Info
	 */

	protected NetBlockUUID uuid;
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

	protected Map<DeviceUUID, NetDevice> devices = new HashMap<>();

	@Nonnull
	@Override
	public <N extends NetDevice<N, P>, P extends PhysicalDevice<N, P>> Optional<N> getDevice(DeviceUUID device){
		return Optional.ofNullable((N) devices.get(device));
	}

	@Nonnull
	@Override
	public <N extends NetDevice<N, P>, P extends PhysicalDevice<N, P>> Collection<N> getDevices(){
		return (Collection<N>) devices.values();
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
		return nbt;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt){

	}

}
