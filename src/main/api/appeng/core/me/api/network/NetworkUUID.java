package appeng.core.me.api.network;

import appeng.api.uuid.AEUUID;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.util.UUID;

@Immutable
public final class NetworkUUID extends AEUUID {

	private NetworkUUID(@Nonnull UUID uuid){
		super(uuid);
	}

	public NetworkUUID(){
		this(UUID.randomUUID());
	}

	@Nonnull
	public static NetworkUUID createPartUUID(@Nonnull NBTTagCompound nbt){
		return new NetworkUUID(NBTUtil.getUUIDFromTag(nbt));
	}

}
