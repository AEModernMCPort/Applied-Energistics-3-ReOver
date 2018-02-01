package appeng.core.me.api.network;

import appeng.api.uuid.AEUUID;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.util.UUID;

@Immutable
public final class NetBlockUUID extends AEUUID {

	private NetBlockUUID(@Nonnull UUID uuid){
		super(uuid);
	}

	public NetBlockUUID(){
		this(UUID.randomUUID());
	}

	@Nonnull
	public static NetBlockUUID fromNBT(@Nonnull NBTTagCompound nbt){
		return new NetBlockUUID(NBTUtil.getUUIDFromTag(nbt));
	}

}
