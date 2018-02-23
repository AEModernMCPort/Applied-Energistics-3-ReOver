package appeng.core.me.network;

import appeng.core.me.AppEngME;
import appeng.core.me.api.network.NetBlock;
import appeng.core.me.api.network.NetBlockUUID;
import appeng.core.me.api.network.Network;
import appeng.core.me.api.network.NetworkUUID;
import appeng.core.me.api.network.event.NCEventBus;
import appeng.core.me.network.event.EventBusImpl;
import appeng.core.me.parts.part.device.Controller;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityDispatcher;
import net.minecraftforge.event.AttachCapabilitiesEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Optional;

public class NetworkImpl implements Network {

	public NetworkImpl(NetworkUUID uuid){
		this.uuid = uuid;
		this.threadsManager = new NetworkThreadsManager(this);
		this.blocksManager = new NetworkBlocksManager(this);
		this.eventBus = new EventBusImpl<>(this);
		initCapabilities();
	}

	/*
	 * UUID
	 */

	protected final NetworkUUID uuid;

	@Nonnull
	@Override
	public NetworkUUID getUUID(){
		return uuid;
	}

	/*
	 * Init
	 */

	public void initialize(Controller.Network controller, World world, Controller.Physical pcontroller){
		AppEngME.INSTANCE.getGlobalNBDManager().networkCreated(this);
		blocksManager.initialize(controller, world, pcontroller);
	}

	@Override
	public void destroyNetwork(){
		blocksManager.destroy();
		AppEngME.INSTANCE.getGlobalNBDManager().networkDestroyed(uuid);
	}

	/*
	 * Threads
	 */

	protected NetworkThreadsManager threadsManager;

	@Nonnull
	@Override
	public void requestThread(NetworkThreadInfo operation){
		threadsManager.requestThread(operation);
	}

	@Override
	public void startThreads(){
		threadsManager.startThreads();
	}

	@Override
	public Runnable suspendThreads(){
		return threadsManager.suspendThreads();
	}

	/*
	 * Blocks
	 */

	protected NetworkBlocksManager blocksManager;

	@Nullable
	@Override
	public Optional<NetBlock> getBlock(NetBlockUUID uuid){
		return Optional.ofNullable(blocksManager.getBlock(uuid));
	}

	@Nonnull
	@Override
	public Collection<? extends NetBlock> getBlocks(){
		return blocksManager.getBlocks();
	}

	@Override
	public void removeDestroyedBlock(NetBlock netBlock){
		blocksManager.removeDestroyedBlock(netBlock);
	}

	/*
	 * Events
	 */

	protected NCEventBus<Network, NetworkEvent> eventBus;

	@Nonnull
	@Override
	public NCEventBus<Network, NetworkEvent> getEventBus(){
		return eventBus;
	}

	/*
	 * Caps
	 */

	protected CapabilityDispatcher capabilities;

	protected void initCapabilities(){
		AttachCapabilitiesEvent<Network> event = new AttachCapabilitiesEvent<>(Network.class, this);
		if(event.getCapabilities().size() > 0) capabilities = new CapabilityDispatcher(event.getCapabilities(), null);
	}

	@Override
	public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing){
		return capabilities != null && capabilities.hasCapability(capability, facing);
	}

	@Nullable
	@Override
	public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing){
		return capabilities == null ? null : capabilities.getCapability(capability, facing);
	}

	/*
	 * IO
	 */

	@Override
	public NBTTagCompound serializeNBT(){
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setTag("blocks", blocksManager.serializeNBT());
		if(capabilities != null) nbt.setTag("capabilities", capabilities.serializeNBT());
		return nbt;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt){
		blocksManager.deserializeNBT(nbt.getCompoundTag("blocks"));
		if(capabilities != null && nbt.hasKey("capabilities")) capabilities.deserializeNBT(nbt.getCompoundTag("capabilities"));
	}

	public static NetworkImpl createFromNBT(@Nonnull NetworkUUID uuid, @Nonnull NBTTagCompound nbt){
		NetworkImpl network = new NetworkImpl(uuid);
		network.deserializeNBT(nbt);
		return network;
	}

}
