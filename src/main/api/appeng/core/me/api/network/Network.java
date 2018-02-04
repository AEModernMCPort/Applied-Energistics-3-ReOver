package appeng.core.me.api.network;

import appeng.core.me.api.network.event.EventBusOwner;
import appeng.core.me.api.network.event.NCEventBus;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
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

	@Nonnull
	void requestThread(NetworkThreadInfo thread);

	void startThreads();
	Runnable suspendThreads();

	/*
	 * Blocks
	 */

	@Nullable NetBlock getBlock(NetBlockUUID uuid);
	@Nonnull Collection<? extends NetBlock> getBlocks();

	/*
	 * IO
	 */

	default ResourceLocation getLoader(){
		return NBDIO.DEFAULTLOADER;
	}

	interface NetworkThreadInfo extends Runnable {

		/**
		 * Called whenever the operation of this thread should be suspended. When the method returns, no operation is performed by the thread and underlying data is safely serializable, until resume is called.<br>
		 * Returned {@linkplain Runnable runnable} will be called to resume thread operation
		 *
		 * @return runnable to resume thread operation
		 */
		Runnable suspend();

	}

	interface NetworkEvent extends NCEventBus.Event<Network, Network.NetworkEvent> {

	}

}
