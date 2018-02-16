package appeng.core.me.api.network;

import appeng.core.me.api.network.block.ConnectUUID;
import appeng.core.me.api.network.block.ConnectionPassthrough;
import appeng.core.me.api.network.block.DeviceColor;
import appeng.core.me.api.parts.VoxelPosition;

/**
 * Device entity in-world.<br>
 * Interacts <b>only</b> on the world thread with the world.<br>
 * Does <b>not</b> manage any scheduled tasks, as network counterpart is the one managing all scheduled tasks for both counterparts.
 * <br><br>
 * Implement on already existing in-world structures.
 *
 * @param <N> Network counterpart type
 * @param <P> In-world counterpart type
 *
 * @author Elix_x
 */
public interface PhysicalDevice<N extends NetDevice<N, P>, P extends PhysicalDevice<N, P>> {

	VoxelPosition getPosition();

	N getNetworkCounterpart();

	/**
	 * Persistent, serialized, immutable, server-only UUID used for connection path finding <i>to/from this component</i> inside network blocks.<br><br>
	 * If your device both uses and pass the connection through, {@linkplain PhysicalDevice#getUUIDForConnection()} and {@linkplain ConnectionPassthrough#getUUIDForConnectionPassthrough()} must return <b>different UUIDs.</b>
	 * @return UUID for connection <i>to/from this device</i>
	 */
	ConnectUUID getUUIDForConnection();

	DeviceColor getColor();

}
