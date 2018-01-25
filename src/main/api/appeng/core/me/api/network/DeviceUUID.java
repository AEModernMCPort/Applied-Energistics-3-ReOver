package appeng.core.me.api.network;

import appeng.api.uuid.AEUUID;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.util.UUID;

@Immutable
public final class DeviceUUID extends AEUUID {

	private DeviceUUID(@Nonnull UUID uuid){
		super(uuid);
	}

	public DeviceUUID(){
		this(UUID.randomUUID());
	}

	@Nonnull
	public static DeviceUUID createPartUUID(@Nonnull NBTTagCompound nbt){
		return new DeviceUUID(NBTUtil.getUUIDFromTag(nbt));
	}

}
