package appeng.api.uuid;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.util.UUID;

@Immutable
public class AEUUID {

	protected final UUID uuid;

	protected AEUUID(@Nonnull UUID uuid){
		this.uuid = uuid;
	}

	@Nonnull
	public NBTTagCompound serializeNBT(){
		return NBTUtil.createUUIDTag(uuid);
	}

	@Override
	public boolean equals(Object o){
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;
		return uuid.equals(((AEUUID) o).uuid);
	}

	@Override
	public int hashCode(){
		return uuid.hashCode();
	}

	@Override
	public String toString(){
		return getClass().getSimpleName() + "{" + uuid + '}';
	}

}
