package appeng.core.me.network;

import appeng.core.me.api.network.NetBlock;
import appeng.core.me.api.network.NetBlockUUID;
import appeng.core.me.api.network.Network;
import appeng.core.me.api.network.NetworkUUID;
import appeng.core.me.api.network.event.NCEventBus;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityDispatcher;
import net.minecraftforge.event.AttachCapabilitiesEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;

public class NetworkImpl implements Network {

	public NetworkImpl(NetworkUUID uuid){
		this.uuid = uuid;
		this.threadsManager = new NetworkThreadsManager(this);
		this.blocksManager = new NetworkBlocksManager(this);
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
	 * Threads
	 */

	protected NetworkThreadsManager threadsManager;

	@Nonnull
	@Override
	public NetworkThread requestThread(Runnable operation){
		return threadsManager.requestThread(operation);
	}

	@Nonnull
	@Override
	public Collection<? extends NetworkThread> getThreads(){
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
	public Collection<? extends NetBlock> getBlocks(){
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
		nbt.setTag("blocks", blocksManager.serializeNBT());
		nbt.setTag("capabilities", capabilities.serializeNBT());
		return nbt;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt){
		blocksManager.deserializeNBT(nbt.getCompoundTag("blocks"));
		capabilities.deserializeNBT(nbt.getCompoundTag("capabilities"));
	}

	public static NetworkImpl createFromNBT(@Nonnull NetworkUUID uuid, @Nonnull NBTTagCompound nbt){
		NetworkImpl network = new NetworkImpl(uuid);
		network.deserializeNBT(nbt);
		return network;
	}

}
