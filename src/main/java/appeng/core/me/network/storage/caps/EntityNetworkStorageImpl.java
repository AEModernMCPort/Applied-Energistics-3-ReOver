package appeng.core.me.network.storage.caps;

import appeng.core.me.api.network.Network;
import appeng.core.me.api.network.storage.caps.EntityNetworkStorage;
import appeng.core.me.network.storage.atomic.SubtypedAtomicNetworkStorageImpl;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class EntityNetworkStorageImpl extends SubtypedAtomicNetworkStorageImpl<EntityNetworkStorage.Entry, EntityEntry> implements EntityNetworkStorage {

	public EntityNetworkStorageImpl(Network network){
		super(network, Entry::getEntity, entry -> {
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setString("id", entry.getRegistryName().toString());
			return nbt;
		}, nbt -> GameRegistry.findRegistry(EntityEntry.class).getValue(new ResourceLocation(nbt.getString("id"))), Entry::serializeNBT, Entry::deserializeNBT);
	}

	public EntityNetworkStorageImpl(){
		this(null);
	}

}
