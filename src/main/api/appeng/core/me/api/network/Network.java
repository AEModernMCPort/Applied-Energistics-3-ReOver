package appeng.core.me.api.network;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;

/**
 * Network executing all operations
 */
public interface Network extends INBTSerializable<NBTTagCompound> {

	@Nonnull NetworkUUID getUUID();

	@Nonnull <N extends NetDevice<N, P> & ITickable, P extends PhysicalDevice<N, P>> NetworkThread getDeviceThread(N device);
	@Nonnull Collection<NetworkThread> getThreads();

	@Nullable NetBlock getBlock(NetBlockUUID uuid);
	@Nonnull Collection<NetBlock> getBlocks();

	interface NetworkThread extends IThreadListener {

		Thread getThread();

		<N extends NetDevice<N, P> & ITickable, P extends PhysicalDevice<N, P>> Collection<N> getManagedDevices();

		//Impl

		@Override
		default boolean isCallingFromMinecraftThread(){
			return false;
		}

	}

}
