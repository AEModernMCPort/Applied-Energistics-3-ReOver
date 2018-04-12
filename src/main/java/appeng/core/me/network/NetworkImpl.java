package appeng.core.me.network;

import appeng.core.me.AppEngME;
import appeng.core.me.api.network.*;
import appeng.core.me.api.network.event.EventBusInitializeEvent;
import appeng.core.me.api.network.event.NCEventBus;
import appeng.core.me.network.block.NetBlockImpl;
import appeng.core.me.network.event.EventBusImpl;
import appeng.core.me.parts.part.device.Controller;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
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
		this.blocksManager = new NetworkBlocksManager(this);
		this.tasksManager = AppEngME.INSTANCE.getGlobalNBDManager().requestTasksManager(this);
		initCapabilities();
		initEvents();
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
		blocksManager.initialize(controller, world, pcontroller);
		AppEngME.INSTANCE.getGlobalNBDManager().networkCreated(this);
	}

	@Override
	public void destroyNetwork(){
		blocksManager.getBlocks().stream().flatMap(NetBlockImpl::getDevices).filter(d -> d instanceof ITickable).forEach(d -> tasksManager.removeScheduledTask((ITickable) d));
		blocksManager.destroy();
		AppEngME.INSTANCE.getGlobalNBDManager().networkDestroyed(uuid);
	}

	/*
	 * Threads
	 */

	protected final TasksManager tasksManager;

	@Override
	public void start(){
		blocksManager.getBlocks().stream().flatMap(NetBlockImpl::getDevices).filter(d -> d instanceof ITickable).forEach(d -> tasksManager.addScheduledTask((ITickable) d));
	}

	/*
	 * Callbacks
	 */

	@Override
	public void deviceJoinedNetwork(NetDevice device){
		if(device instanceof ITickable) tasksManager.addScheduledTask((ITickable) device);
	}

	@Override
	public void deviceLeftNetwork(NetDevice device){
		if(device instanceof ITickable) tasksManager.removeScheduledTask((ITickable) device);
	}

	@Override
	public void netBlockLeftNetwork(NetBlock netBlock){
		netBlock.getDevices().filter(device -> device instanceof ITickable).forEach(device -> tasksManager.removeScheduledTask((ITickable) device));
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
	 * Caps
	 */

	protected CapabilityDispatcher capabilities;

	protected void initCapabilities(){
		AttachCapabilitiesEvent<Network> event = new AttachCapabilitiesEvent<>(Network.class, this);
		MinecraftForge.EVENT_BUS.post(event);
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
	 * Events
	 */

	protected NCEventBus<Network, NetworkEvent> eventBus;

	protected void initEvents(){
		EventBusInitializeEvent<Network, NetworkEvent> event = new EventBusInitializeEvent<>(this, Network.class);
		MinecraftForge.EVENT_BUS.post(event);
		this.eventBus = new EventBusImpl<>(event);
	}

	@Nonnull
	@Override
	public NCEventBus<Network, NetworkEvent> getEventBus(){
		return eventBus;
	}

	/*
	 * IO
	 */

	@Override
	public NBTTagCompound serializeNBT(){
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setTag("blocks", blocksManager.serializeNBT());
		nbt.setTag("tasks", tasksManager.serializeNBT());
		if(capabilities != null) nbt.setTag("capabilities", capabilities.serializeNBT());
		return nbt;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt){
		blocksManager.deserializeNBT(nbt.getCompoundTag("blocks"));
		tasksManager.deserializeNBT(nbt.getCompoundTag("tasks"));
		if(capabilities != null && nbt.hasKey("capabilities")) capabilities.deserializeNBT(nbt.getCompoundTag("capabilities"));
	}

	public static NetworkImpl createFromNBT(@Nonnull NetworkUUID uuid, @Nonnull NBTTagCompound nbt){
		NetworkImpl network = new NetworkImpl(uuid);
		network.deserializeNBT(nbt);
		return network;
	}

}
