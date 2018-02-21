package appeng.core.me.network.device;

import appeng.core.me.api.network.DeviceUUID;
import appeng.core.me.api.network.NetBlock;
import appeng.core.me.api.network.PhysicalDevice;
import appeng.core.me.api.network.device.DeviceLoader;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.BiFunction;

public class NetDeviceBaseLoader<N extends NetDeviceBase<N, P>, P extends PhysicalDevice<N, P>> implements DeviceLoader<N, P> {

	private final BiFunction<DeviceUUID, NetBlock, N> creator;

	public NetDeviceBaseLoader(BiFunction<DeviceUUID, NetBlock, N> creator){
		this.creator = creator;
	}

	@Override
	public N deserializeNBT(@Nonnull DeviceUUID uuid, @Nullable NetBlock netBlock, @Nonnull NBTTagCompound nbt){
		N device = creator.apply(uuid, netBlock);
		device.deserializeNBT(nbt);
		return device;
	}

}
