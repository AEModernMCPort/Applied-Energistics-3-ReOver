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
import java.util.Optional;

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

	void start();

	/*
	 * Blocks
	 */

	@Nonnull Optional<NetBlock> getBlock(NetBlockUUID uuid);
	@Nonnull Collection<? extends NetBlock> getBlocks();

	void removeDestroyedBlock(NetBlock netBlock);

	/*
	 * Callbacks
	 */

	default void deviceJoinedNetwork(NetDevice device){}
	default void deviceLeftNetwork(NetDevice device){}

	default void netBlockLeftNetwork(NetBlock netBlock){}

	/*
	 * Destruction
	 */

	void destroyNetwork();

	/*
	 * IO
	 */

	default ResourceLocation getLoader(){
		return NBDIO.DEFAULTLOADER;
	}

	interface NetworkEvent extends NCEventBus.Event<Network, Network.NetworkEvent> {

	}

}
