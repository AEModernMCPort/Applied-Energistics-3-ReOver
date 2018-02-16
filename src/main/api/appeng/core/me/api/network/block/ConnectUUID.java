package appeng.core.me.api.network.block;

import appeng.api.uuid.AEUUID;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.util.UUID;

@Immutable
public final class ConnectUUID extends AEUUID {

	private ConnectUUID(@Nonnull UUID uuid){
		super(uuid);
	}

	public ConnectUUID(){
		this(UUID.randomUUID());
	}

	@Nonnull
	public static ConnectUUID fromNBT(@Nonnull NBTTagCompound nbt){
		return new ConnectUUID(NBTUtil.getUUIDFromTag(nbt));
	}

}
