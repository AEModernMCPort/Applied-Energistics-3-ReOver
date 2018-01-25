package appeng.api.pos;

import appeng.api.uuid.AEUUID;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;

import javax.annotation.Nonnull;
import java.util.UUID;

public final class WorldReference extends AEUUID {

	private static final long MSIGBITS = 0x0000000000004000L; //Version 4
	private static final long LSIGBITS = 0x8000000000000000L; //Variant IETF

	private WorldReference(@Nonnull UUID uuid){
		super(uuid);
	}

	public WorldReference(long hash){
		this(new UUID(MSIGBITS | (((hash >> 32) & 0xFFFFFFFFL) << 16), LSIGBITS | (hash & 0xFFFFFFFFL)));
	}

	@Nonnull
	public static WorldReference fromNBT(@Nonnull NBTTagCompound nbt){
		return new WorldReference(NBTUtil.getUUIDFromTag(nbt));
	}

}
