package appeng.core.me.api.network.storage.caps;

import appeng.core.me.api.network.storage.atomic.SubtypedAtomicNetworkStorage;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.GameRegistry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

public interface EntityNetworkStorage extends SubtypedAtomicNetworkStorage<EntityNetworkStorage.Entry, EntityEntry> {

	final class Entry {

		final EntityEntry entity;
		final NBTTagCompound info;

		public Entry(@Nonnull EntityEntry entity, @Nullable NBTTagCompound info){
			this.entity = entity;
			this.info = info;
		}

		public EntityEntry getEntity(){
			return entity;
		}

		public NBTTagCompound getInfo(){
			return info;
		}

		public NBTTagCompound serializeNBT(){
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setString("entity", entity.getRegistryName().toString());
			if(info != null) nbt.setTag("info", info);
			return nbt;
		}

		public static Entry deserializeNBT(NBTTagCompound nbt){
			return new Entry(GameRegistry.findRegistry(EntityEntry.class).getValue(new ResourceLocation(nbt.getString("entity"))), nbt.hasKey("info") ? nbt.getCompoundTag("info") : null);
		}

		@Override
		public boolean equals(Object o){
			if(this == o) return true;
			if(o == null || getClass() != o.getClass()) return false;
			Entry entry = (Entry) o;
			return Objects.equals(entity, entry.entity) && Objects.equals(info, entry.info);
		}

		@Override
		public int hashCode(){
			return Objects.hash(entity, info);
		}

		@Override
		public String toString(){
			return entity + "{info=" + info + '}';
		}

	}

}
