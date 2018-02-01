package appeng.core.me.api.network;

import appeng.core.me.api.network.event.EventBusOwner;
import appeng.core.me.api.network.event.NCEventBus;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;

/**
 * Network executing all operations
 */
public interface Network extends ICapabilityProvider, EventBusOwner<Network, Network.NetworkEvent>, INBTSerializable<NBTTagCompound> {

	/*
	 * UUID
	 */

	@Nonnull NetworkUUID getUUID();

	/*
	 * Threads
	 */

	@Nonnull NetworkThread requestThread(Runnable operation);
	@Nonnull Collection<? extends NetworkThread> getThreads();

	/*
	 * Blocks
	 */

	@Nullable NetBlock getBlock(NetBlockUUID uuid);
	@Nonnull Collection<? extends NetBlock> getBlocks();

	interface NetworkThread {

		Thread getThread();

	}

	interface NetworkEvent extends NCEventBus.Event<Network, Network.NetworkEvent> {

	}

}
