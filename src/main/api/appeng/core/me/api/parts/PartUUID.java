package appeng.core.me.api.parts;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.util.UUID;

@Immutable
public final class PartUUID {

	private final UUID uuid;

	private PartUUID(@Nonnull UUID uuid){
		this.uuid = uuid;
	}

	public PartUUID(){
		this(UUID.randomUUID());
	}

	@Nonnull
	public NBTTagCompound serializeNBT(){
		return NBTUtil.createUUIDTag(uuid);
	}

	@Nonnull
	public static PartUUID createPartUUID(@Nonnull NBTTagCompound nbt){
		return new PartUUID(NBTUtil.getUUIDFromTag(nbt));
	}

	@Override
	public boolean equals(Object o){
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;
		return uuid.equals(((PartUUID) o).uuid);
	}

	@Override
	public int hashCode(){
		return uuid.hashCode();
	}

	@Override
	public String toString(){
		return "PartUUID{" + uuid + '}';
	}

}
