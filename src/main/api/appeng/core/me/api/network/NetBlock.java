package appeng.core.me.api.network;

import appeng.core.me.api.network.device.BRINMDevice;
import appeng.core.me.api.network.event.EventBusOwner;
import appeng.core.me.api.network.event.NCEventBus;
import appeng.core.me.api.parts.GlobalWorldVoxelPosition;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Optional;

/**
 * Network devices block is a distinct collection of physically inter-connected devices.
 *
 * @author Elix_x
 */
public interface NetBlock extends EventBusOwner<NetBlock, NetBlock.NetBlockEvent>, INBTSerializable<NBTTagCompound> {

	/*
	 * Info
	 */

	@Nonnull NetBlockUUID getUUID();

	@Nonnull GlobalWorldVoxelPosition getPosition();

	/*
	 * Network
	 */

	@Nonnull Optional<Network> getNetwork();
	void switchNetwork(@Nullable Network network);
	default boolean hasNetwork(){
		return getNetwork().isPresent();
	}

	/*
	 * Destruction
	 */

	void destroyBlock();

	/*
	 * Devices
	 */

	@Nonnull <N extends NetDevice<N, P>, P extends PhysicalDevice<N, P>> Optional<N> getDevice(DeviceUUID device);
	@Nonnull <N extends NetDevice<N, P>, P extends PhysicalDevice<N, P>> Collection<N> getDevices();

	<N extends NetDevice<N, P>, P extends PhysicalDevice<N, P>> void removeDestroyedDevice(@Nonnull N device);

	@Nonnull <N extends BRINMDevice<N, P>, P extends PhysicalDevice<N, P>> N getMaster();

	/*
	 * IO
	 */

	default ResourceLocation getLoader(){
		return NBDIO.DEFAULTLOADER;
	}

	interface NetBlockEvent extends NCEventBus.Event<NetBlock, NetBlockEvent> {

	}

}
