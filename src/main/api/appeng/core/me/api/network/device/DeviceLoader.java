package appeng.core.me.api.network.device;

import appeng.core.me.api.network.DeviceUUID;
import appeng.core.me.api.network.NetBlock;
import appeng.core.me.api.network.NetDevice;
import appeng.core.me.api.network.PhysicalDevice;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface DeviceLoader<N extends NetDevice<N, P>, P extends PhysicalDevice<N, P>> {

	N deserializeNBT(@Nonnull DeviceUUID uuid, @Nullable NetBlock netBlock, @Nonnull NBTTagCompound nbt);

}
