package appeng.core.me.network;

import appeng.api.bootstrap.InitializationComponent;
import appeng.core.me.AppEngME;
import appeng.core.me.api.network.*;
import appeng.core.me.network.block.NetBlockImpl;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

public class NBDIOImpl implements NBDIO, InitializationComponent {

	/*
	 * Init
	 */

	@Override
	public void init(){
		registerNetworkLoader(DEFAULTLOADER, NetworkImpl::createFromNBT);
		registerNetBlockLoader(DEFAULTLOADER, NetBlockImpl::createFromNBT);
	}

	/*
	 * Network
	 */

	protected Map<ResourceLocation, BiFunction<NetworkUUID, NBTTagCompound, ? extends Network>> networkLoaders = new HashMap<>();

	@Override
	public <N extends Network> void registerNetworkLoader(@Nonnull ResourceLocation id, @Nonnull BiFunction<NetworkUUID, NBTTagCompound, N> loader){
		networkLoaders.put(id, loader);
	}

	protected <N extends Network> BiFunction<NetworkUUID, NBTTagCompound, N> getNetworkLoader(ResourceLocation id){
		return (BiFunction) networkLoaders.get(id != null && networkLoaders.containsKey(id) ? id : DEFAULTLOADER);
	}

	@Nonnull
	@Override
	public NBTTagCompound serializeNetworkWithArgs(@Nonnull Network network){
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setTag("uuid", network.getUUID().serializeNBT());
		nbt.setString("loader", network.getLoader().toString());
		nbt.setTag("network", network.serializeNBT());
		return nbt;
	}

	@Nonnull
	@Override
	public <N extends Network> Pair<NetworkUUID, N> deserializeNetworkWithArgs(@Nonnull NBTTagCompound nbt){
		NetworkUUID uuid = NetworkUUID.fromNBT(nbt.getCompoundTag("uuid"));
		N network = this.<N>getNetworkLoader(new ResourceLocation(nbt.getString("loader"))).apply(uuid, nbt.getCompoundTag("network"));
		return new ImmutablePair<>(uuid, network);
	}

	/*
	 * Net block
	 */

	protected Map<ResourceLocation, NetBlockLoader> netBlockLoaders = new HashMap<>();

	@Override
	public <NB extends NetBlock> void registerNetBlockLoader(ResourceLocation id, NetBlockLoader<NB> loader){
		netBlockLoaders.put(id, loader);
	}

	protected <NB extends NetBlock> NetBlockLoader<NB> getNetBlockLoader(ResourceLocation id){
		return netBlockLoaders.get(id != null && netBlockLoaders.containsKey(id) ? id : DEFAULTLOADER);
	}

	@Nonnull
	@Override
	public NBTTagCompound serializeNetBlockWithArgs(@Nonnull NetBlock block){
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setTag("uuid", block.getUUID().serializeNBT());
		nbt.setString("loader", block.getLoader().toString());
		nbt.setTag("block", block.serializeNBT());
		return nbt;
	}

	@Nonnull
	@Override
	public <NB extends NetBlock> Pair<NetBlockUUID, NB> deserializeNetBlockWithArgs(Network network, NBTTagCompound nbt){
		NetBlockUUID uuid = NetBlockUUID.fromNBT(nbt.getCompoundTag("uuid"));
		NB block = this.<NB>getNetBlockLoader(new ResourceLocation(nbt.getString("loader"))).load(uuid, network, nbt.getCompoundTag("block"));
		return new ImmutablePair<>(uuid, block);
	}

	/*
	 * Device
	 */

	@Nonnull
	@Override
	public <N extends NetDevice<N, P>, P extends PhysicalDevice<N, P>> NBTTagCompound serializeDeviceWithArgs(N device){
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setTag("uuid", device.getUUID().serializeNBT());
		nbt.setString("id", device.getRegistryEntry().getRegistryName().toString());
		nbt.setTag("device", device.serializeNBT());
		return nbt;
	}

	@Nonnull
	@Override
	public <N extends NetDevice<N, P>, P extends PhysicalDevice<N, P>> Pair<DeviceUUID, N> deserializeDeviceWithArgs(NetBlock block, NBTTagCompound nbt){
		DeviceUUID uuid = DeviceUUID.fromNBT(nbt.getCompoundTag("uuid"));
		N device = AppEngME.INSTANCE.<N, P>getDeviceRegistry().getValue(new ResourceLocation(nbt.getString("id"))).deserializeNBT(uuid, block, nbt.getCompoundTag("device"));
		return new ImmutablePair<>(uuid, device);
	}
}
