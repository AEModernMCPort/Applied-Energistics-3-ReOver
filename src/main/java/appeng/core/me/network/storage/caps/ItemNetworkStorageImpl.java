package appeng.core.me.network.storage.caps;

import appeng.core.me.api.network.Network;
import appeng.core.me.api.network.storage.caps.ItemNetworkStorage;
import appeng.core.me.network.storage.atomic.SubtypedAtomicNetworkStorageImpl;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

public class ItemNetworkStorageImpl extends SubtypedAtomicNetworkStorageImpl<ItemNetworkStorage.Entry, Item> implements ItemNetworkStorage {

	public ItemNetworkStorageImpl(Network network){
		super(network, Entry::getItem, item -> {
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setString("id", item.getRegistryName().toString());
			return nbt;
		}, nbt -> Item.REGISTRY.getObject(new ResourceLocation(nbt.getString("id"))), Entry::serializeNBT, Entry::deserializeNBT);
	}

	public ItemNetworkStorageImpl(){
		this(null);
	}

}
