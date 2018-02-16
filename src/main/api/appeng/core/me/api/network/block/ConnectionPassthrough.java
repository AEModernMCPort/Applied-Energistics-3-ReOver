package appeng.core.me.api.network.block;

import appeng.core.me.api.network.PhysicalDevice;
import net.minecraft.util.ResourceLocation;

import java.util.function.Predicate;

public interface ConnectionPassthrough extends Predicate<ResourceLocation> {

	/**
	 * Persistent, serialized, immutable, server-only UUID used for connection path finding <i>through this component</i> inside network blocks.<br><br>
	 * If your device both uses and pass the connection through, {@linkplain ConnectionPassthrough#getUUIDForConnectionPassthrough()} and {@linkplain PhysicalDevice#getUUIDForConnection()} must return <b>different UUIDs.</b>
	 *
	 * @return UUID for connection <i>through this component</i>
	 */
	ConnectUUID getUUIDForConnectionPassthrough();

	DeviceColor getColor();

}
