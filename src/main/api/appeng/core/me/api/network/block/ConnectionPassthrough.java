package appeng.core.me.api.network.block;

import appeng.core.me.api.network.NetBlock;
import appeng.core.me.api.network.NetDevice;
import appeng.core.me.api.parts.PartColor;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.Predicate;

public interface ConnectionPassthrough extends Predicate<Connection> {

	/**
	 * Persistent, serialized, immutable, server-only UUID used for connection path finding <i>through this component</i> inside network blocks.<br><br>
	 * If your device both uses and pass the connection through, {@linkplain ConnectionPassthrough#getUUIDForConnectionPassthrough()} and {@linkplain NetDevice#getUUIDForConnection()} must return <b>different UUIDs.</b>
	 *
	 * @return UUID for connection <i>through this component</i>
	 */
	ConnectUUID getUUIDForConnectionPassthrough();

	/**
	 * Persistent, immutable, server-only color used for connectivity checks
	 *
	 * @return color of this passthrough
	 */
	PartColor getColor();

	/**
	 * Returns passthrough's connection parameters for given connection type.<br>
	 * Immutable, persistent.
	 *
	 * @param connection connection
	 * @param <Param>    connection parameter type
	 * @return requirements for given connection
	 */
	<Param extends Comparable<Param>> Param getPassthroughConnectionParameter(Connection<Param, ?> connection);

	@Nonnull
	Optional<NetBlock> getAssignedNetBlock();

	void assignNetBlock(@Nullable NetBlock netBlock);

}
