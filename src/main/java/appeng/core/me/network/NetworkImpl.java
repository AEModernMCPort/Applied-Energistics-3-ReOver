package appeng.core.me.network;

import appeng.core.me.api.network.*;
import appeng.core.me.api.network.event.NCEventBus;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityDispatcher;
import net.minecraftforge.event.AttachCapabilitiesEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;

public class NetworkImpl implements Network {

	public NetworkImpl(NetworkUUID uuid){
		this.uuid = uuid;
		this.threadsManager = new NetworkThreadsManager();
		this.blocksManager = new NetworkBlocksManager();
	}

	/*
	 * UUID
	 */

	protected NetworkUUID uuid;

	@Nonnull
	@Override
	public NetworkUUID getUUID(){
		return uuid;
	}

	/*
	 * Threads
	 */

	protected NetworkThreadsManager threadsManager;

	@Nonnull
	@Override
	public <N extends NetDevice<N, P> & ITickable, P extends PhysicalDevice<N, P>> NetworkThread getDeviceThread(N device){
		return threadsManager.getDeviceThread(device);
	}

	@Nonnull
	@Override
	public Collection<NetworkThread> getThreads(){
		return threadsManager.getThreads();
	}

	/*
	 * Blocks
	 */

	protected NetworkBlocksManager blocksManager;

	@Nullable
	@Override
	public NetBlock getBlock(NetBlockUUID uuid){
		return blocksManager.getBlock(uuid);
	}

	@Nonnull
	@Override
	public Collection<NetBlock> getBlocks(){
		return blocksManager.getBlocks();
	}

	/*
	 * Events
	 */

	@Nonnull
	@Override
	public NCEventBus<Network, NetworkEvent> getEventBus(){
		//TODO Implement events
		return null;
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
		return capabilities == null && capabilities.hasCapability(capability, facing);
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
		return nbt;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt){

	}
}
