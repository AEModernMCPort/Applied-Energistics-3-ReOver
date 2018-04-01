package appeng.core.me.network.block;

import appeng.core.lib.pos.Ref2WorldCapability;
import appeng.core.me.AppEngME;
import appeng.core.me.api.network.*;
import appeng.core.me.api.network.block.ConnectionPassthrough;
import appeng.core.me.api.network.device.BRINMDevice;
import appeng.core.me.api.network.event.NCEventBus;
import appeng.core.me.api.parts.GlobalWorldVoxelPosition;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;
import java.util.stream.Stream;

public class NetBlockImpl implements NetBlock {

	public NetBlockImpl(NetBlockUUID uuid, Network network){
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

	protected NetDevice root;

	@Override
	public NetDevice getRoot(){
		return root;
	}

	public <N extends NetDevice<N, P>, P extends PhysicalDevice<N, P>> void init(World world, P pblockRoot){
		AppEngME.INSTANCE.getGlobalNBDManager().registerFreeBlock(this);

		root = pblockRoot.getNetworkCounterpart();
		position = new GlobalWorldVoxelPosition(Ref2WorldCapability.getCapability(world.isRemote).getReference(world), pblockRoot.getPosition());
		devicesManager.recalculateAll(world, pblockRoot);
	}

	@Override
	public void destroyBlock(){
		devicesManager.onBlockDestroyed();
		if(network != null) network.removeDestroyedBlock(this);
		else AppEngME.INSTANCE.getGlobalNBDManager().removeFreeBlock(this);
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
		if(this.network != null && network == null) AppEngME.INSTANCE.getGlobalNBDManager().registerFreeBlock(this);
		if(this.network == null && network != null) AppEngME.INSTANCE.getGlobalNBDManager().removeFreeBlock(this);
		this.network = network;
		//TODO Notify devices
	}

	/*
	 * Devices
	 */

	public NetBlockDevicesManager devicesManager = new NetBlockDevicesManager(this);

	@Nonnull
	@Override
	public <N extends NetDevice<N, P>, P extends PhysicalDevice<N, P>> Optional<N> getDevice(@Nonnull DeviceUUID device){
		return devicesManager.getDevice(device);
	}

	@Nonnull
	@Override
	public <N extends NetDevice<N, P>, P extends PhysicalDevice<N, P>> Stream<N> getDevices(){
		return devicesManager.getDevices();
	}

	@Override
	public <N extends NetDevice<N, P>, P extends PhysicalDevice<N, P>> void deviceCreatedAdjacentToAssigned(@Nonnull World world, @Nonnull P device){
		devicesManager.deviceCreated(world, device);
	}

	@Override
	public <N extends NetDevice<N, P>, P extends PhysicalDevice<N, P>> void removeDestroyedDevice(@Nonnull N device){
		devicesManager.removeDestroyedDevice(device);
	}

	@Nonnull
	@Override
	public <N extends BRINMDevice<N, P>, P extends PhysicalDevice<N, P>> N getMaster(){
		//TODO Masters
		return null;
	}

	/*
	 * Passthrough
	 */

	@Override
	public void assignedPassthroughLoaded(ConnectionPassthrough passthrough){
		devicesManager.notifyPassthroughLoaded(passthrough);
	}

	@Override
	public void passthroughCreatedAdjacentToAssigned(World world, ConnectionPassthrough passthroughCreated){
		devicesManager.passthroughCreated(world, passthroughCreated);
	}

	@Override
	public void assignedPassthroughDestroed(ConnectionPassthrough passthrough){
		devicesManager.passthroughDestroyed(passthrough);
	}

	/*
	 * Callbacks
	 */

	void deviceJoinedNetBlock(NetDevice device){
		if(network != null) network.deviceJoinedNetwork(device);
	}

	void deviceLeftNetBlock(NetDevice device){
		if(network != null) network.deviceLeftNetwork(device);
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
		nbt.setTag("devices", devicesManager.serializeNBT());
		nbt.setTag("rduuid", root.getUUID().serializeNBT());
		return nbt;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt){
		position = GlobalWorldVoxelPosition.fromNBT(nbt.getCompoundTag("pos"));
		devicesManager.deserializeNBT(nbt.getCompoundTag("devices"));
		root = devicesManager.getDevice(DeviceUUID.fromNBT(nbt.getCompoundTag("rduuid"))).get();
	}

	public static NetBlockImpl createFromNBT(@Nonnull NetBlockUUID uuid, @Nullable Network network, @Nonnull NBTTagCompound nbt){
		NetBlockImpl netBlock = new NetBlockImpl(uuid, network);
		netBlock.deserializeNBT(nbt);
		return netBlock;
	}

}
