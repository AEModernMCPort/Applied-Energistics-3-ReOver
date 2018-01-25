package appeng.core.me.api.parts.container;

import appeng.api.uuid.AEUUID;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.util.UUID;

@Immutable
public final class PartUUID extends AEUUID {

	private PartUUID(@Nonnull UUID uuid){
		super(uuid);
	}

	public PartUUID(){
		this(UUID.randomUUID());
	}

	@Nonnull
	public static PartUUID createPartUUID(@Nonnull NBTTagCompound nbt){
		return new PartUUID(NBTUtil.getUUIDFromTag(nbt));
	}

}
