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

	public <N extends NetDevice<N, P>, P extends PhysicalDevice<N, P>> void init(World world, P pblockRoot){
		AppEngME.INSTANCE.getGlobalNBDManager().registerFreeBlock(this);

		root = pblockRoot.getNetworkCounterpart();
		position = new GlobalWorldVoxelPosition(Ref2WorldCapability.getCapability(world.isRemote).getReference(world), pblockRoot.getPosition());
		devicesManager.recalculateAll(world, pblockRoot);
	}

	@Override
	public void destroyBlock(){
		getDevices().forEach(d -> d.switchNetBlock(null));
		if(network != null){
			network.removeDestroyedBlock(this);
			network = null;
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

	protected NetBlockDevicesManager devicesManager = new NetBlockDevicesManager(this);

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
	public <N extends NetDevice<N, P>, P extends PhysicalDevice<N, P>> void removeDestroyedDevice(@Nonnull N device){
		devicesManager.removeDestroyedDevice(device);
	}

	@Override
	public void notifyPassthroughLoaded(ConnectionPassthrough passthrough){
		devicesManager.notifyPassthroughLoaded(passthrough);
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
		nbt.setTag("devices", devicesManager.serializeNBT());
		return nbt;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt){
		position = GlobalWorldVoxelPosition.fromNBT(nbt.getCompoundTag("pos"));
		devicesManager.deserializeNBT(nbt.getCompoundTag("devices"));
	}

	public static NetBlockImpl createFromNBT(@Nonnull NetBlockUUID uuid, @Nullable Network network, @Nonnull NBTTagCompound nbt){
		NetBlockImpl netBlock = new NetBlockImpl(uuid, network);
		netBlock.deserializeNBT(nbt);
		return netBlock;
	}

}
