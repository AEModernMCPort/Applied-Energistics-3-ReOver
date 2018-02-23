package appeng.core.me.api.network;

import appeng.api.AEModInfo;
import appeng.core.me.network.connect.ConnectionsParams;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.BiFunction;

/**
 * Network, Net blocks and devices IO.<br>
 * Use methods specified here for IO operations on networks, net blocks and network devices.
 *
 * @author Elix_x
 */
public interface NBDIO {

	ResourceLocation DEFAULTLOADER = new ResourceLocation(AEModInfo.MODID, "default");

	/*
	 * Network
	 */

	<N extends Network> void registerNetworkLoader(@Nonnull ResourceLocation id, @Nonnull BiFunction<NetworkUUID, NBTTagCompound, N> loader);

	@Nonnull
	NBTTagCompound serializeNetworkWithArgs(@Nonnull Network network);

	@Nonnull
	<N extends Network> Pair<NetworkUUID, N> deserializeNetworkWithArgs(@Nonnull NBTTagCompound nbt);

	/*
	 * Block
	 */

	<NB extends NetBlock> void registerNetBlockLoader(ResourceLocation id, NetBlockLoader<NB> loader);

	@Nonnull
	NBTTagCompound serializeNetBlockWithArgs(@Nonnull NetBlock block);

	@Nonnull
	<NB extends NetBlock> Pair<NetBlockUUID, NB> deserializeNetBlockWithArgs(Network network, NBTTagCompound nbt);

	interface NetBlockLoader<NB extends NetBlock> {

		NB load(@Nonnull NetBlockUUID uuid, @Nullable Network network, @Nonnull NBTTagCompound nbt);

	}

	/*
	 * Device
	 */

	@Nonnull
	<N extends NetDevice<N, P>, P extends PhysicalDevice<N, P>> NBTTagCompound serializeDeviceWithArgs(N device);

	@Nonnull
	<N extends NetDevice<N, P>, P extends PhysicalDevice<N, P>> Pair<DeviceUUID, N> deserializeDeviceWithArgs(NetBlock block, NBTTagCompound nbt);

	/*
	 * Connections params
	 */

	NBTTagCompound serializeConnectionsParams(ConnectionsParams<?> cps);

	ConnectionsParams<?> deserializeConnectionsParams(NBTTagCompound nbt);

}
