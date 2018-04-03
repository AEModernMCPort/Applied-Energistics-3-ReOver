package appeng.core.me.api.network;

import appeng.core.me.api.network.block.Connection;
import appeng.core.me.api.network.device.DeviceRegistryEntry;
import appeng.core.me.api.network.event.EventBusOwner;
import appeng.core.me.api.network.event.NCEventBus;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Optional;

/**
 * Device entity existing on the network thread.<br>
 * Interacts <b>only</b> on the network thread with the netwok.<br>
 * Manages <b>all</b> scheduled tasks, as network counterpart is the one managing all scheduled tasks for both counterparts.<br><br>
 * <p>
 * If implements {@link ITickable}, {@linkplain ITickable#update() update()} will be called from (one of) network thread(s) on each update cycle (<u>on inconsistent time intervals</u>). A device must schedule tasks for other devices instead of direct interaction (possible concurrency).
 * <br><br>
 * It is not recommended to implement on any already existing structures with other purposes.
 *
 * @param <N> Network counterpart type
 * @param <P> In-world counterpart type
 * @author Elix_x
 */
public interface NetDevice<N extends NetDevice<N, P>, P extends PhysicalDevice<N, P>> extends ICapabilityProvider, EventBusOwner<N, NetDevice.NetDeviceEvent<N, P>> {

	@Nonnull
	DeviceUUID getUUID();

	Optional<P> getPhysicalCounterpart();

	@Nonnull
	Optional<NetBlock> getNetBlock();

	void switchNetBlock(@Nullable NetBlock block);
	default boolean hasNetwork(){
		return getNetBlock().map(NetBlock::hasNetwork).orElse(false);
	}

	/*
	 * Connection
	 */

	/**
	 * Returns device's connection requirement for given connection type.<br>
	 * Mutable, but the device must notify owning block whenever its' requirements change.
	 *
	 * @param connection connection
	 * @param <Param>    connection parameter type
	 * @return requirements for given connection
	 */
	<Param extends Comparable<Param>> Param getConnectionRequirement(Connection<Param, ?> connection);

	/**
	 * Called whenever paths to this device have been recomputed, giving you all the connection types that can be fulfilled.<br>
	 * Returning true will fulfill all requirements that can be, and consume them from the best paths, possibly limiting other devices.<br>
	 * Returning false will not consume your requirements from the network.
	 *
	 * @param connectionsFulfilled all connections that can be fullfilled
	 * @return whether to fulfill and consume your requirements
	 */
	boolean fulfill(Collection<Connection> connectionsFulfilled);

	/**
	 * Checks whether the device is satisfied with connections it has gotten on previous {@linkplain #fulfill(Collection) contract agreement}.
	 * <br>
	 * Called whenever there's a possibility to fulfill all of devices connected dreams. Return false to ditch current contract and get more satisfaction than before*.
	 * <br><br>
	 * <i><sub>*may not actually result in more satisfaction - terms and conditions apply</sub></i>.
	 *
	 * @return false if you want MORE
	 */
	boolean satisfied();

	/*
	 * IO
	 */

	DeviceRegistryEntry<N, P> getRegistryEntry();

	NBTTagCompound serializeNBT();

	interface NetDeviceEvent<N extends NetDevice<N, P>, P extends PhysicalDevice<N, P>> extends NCEventBus.Event<N, NetDevice.NetDeviceEvent<N, P>> {

	}

}
