package appeng.core.me.network;

import appeng.core.lib.pos.Ref2WorldCapability;
import appeng.core.me.AppEngME;
import appeng.core.me.api.network.*;
import appeng.core.me.api.network.device.BRINMDevice;
import appeng.core.me.api.network.event.NCEventBus;
import appeng.core.me.api.parts.GlobalWorldVoxelPosition;
import appeng.core.me.network.block.NetBlockConnections;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

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
	 * Init
	 */

	public <N extends NetDevice<N, P>, P extends PhysicalDevice<N, P>> void init(World world, P pblockRoot){
		AppEngME.INSTANCE.getGlobalNBDManager().registerFreeBlock(this);

		position = new GlobalWorldVoxelPosition(Ref2WorldCapability.getCapability(world.isRemote).getReference(world), pblockRoot.getPosition());
		connections.recalculateAll(world, pblockRoot);
	}

	@Override
	public void destroyBlock(){
		getDevices().forEach(d -> d.switchNetBlock(null));
		if(network != null){
			network = null;
			network.removeDestroyedBlock(this);
		}
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

	@Override
	public <N extends NetDevice<N, P>, P extends PhysicalDevice<N, P>> void removeDestroyedDevice(N device){
		devices.remove(device.getUUID());
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
