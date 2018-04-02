package appeng.core.me.api.network.storage.caps;

import appeng.core.me.api.network.storage.atomic.SubtypedAtomicNetworkStorage;
import code.elix_x.excomms.reflection.ReflectionHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.CapabilityDispatcher;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

public interface ItemNetworkStorage extends SubtypedAtomicNetworkStorage<ItemNetworkStorage.Entry, Item> {

	final class Entry {

		private static final ReflectionHelper.AField<ItemStack, CapabilityDispatcher> capabilities = new ReflectionHelper.AClass<>(ItemStack.class).<CapabilityDispatcher>getDeclaredField("capabilities").orElseThrow(() -> new IllegalArgumentException("Missing fields required for item storage reflection!")).setAccessible(true);

		public final Item item;
		public final NBTTagCompound tag;
		public final CapabilityDispatcher caps;

		public Entry(@Nonnull Item item, @Nullable NBTTagCompound tag, @Nullable CapabilityDispatcher caps){
			this.item = item;
			this.tag = tag;
			this.caps = caps;
		}

		public Item getItem(){
			return item;
		}

		public NBTTagCompound getTag(){
			return tag;
		}

		public CapabilityDispatcher getCaps(){
			return caps;
		}

		@Nullable
		public static Entry ofItemStack(@Nonnull ItemStack stack){
			return stack.isEmpty() ? null : new Entry(stack.getItem(), stack.getTagCompound(), capabilities.get(stack).orElseThrow(() -> new IllegalArgumentException("Something went wrong when reflecting item stack capabilities!")));
		}

		@Nonnull
		public static ItemStack asStack(@Nullable Entry entry, int amount){
			return entry != null ? entry.asStack(amount) : ItemStack.EMPTY;
		}

		@Nonnull
		public ItemStack asStack(int amount){
			ItemStack stack = new ItemStack(item, amount, 0, caps != null ? caps.serializeNBT() : null);
			if(tag != null) stack.setTagCompound(tag.copy());
			return stack;
		}

		public NBTTagCompound serializeNBT(){
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setString("item", item.getRegistryName().toString());
			if(this.tag != null) nbt.setTag("tag", this.tag);
			if(caps != null){
				NBTTagCompound caps = this.caps.serializeNBT();
				if(!caps.hasNoTags()) nbt.setTag("caps", caps);
			}
			return nbt;
		}

		public static Entry deserializeNBT(NBTTagCompound nbt){
			Item item = Item.REGISTRY.getObject(new ResourceLocation(nbt.getString("item")));
			NBTTagCompound tag = nbt.hasKey("tag") ? nbt.getCompoundTag("tag") : null;
			CapabilityDispatcher caps = null;
			if(nbt.hasKey("caps")){
				caps = capabilities.get(new ItemStack(item, 1, 0)).orElseThrow(() -> new IllegalArgumentException("Something went wrong when reflecting item stack capabilities!"));
				caps.deserializeNBT(nbt.getCompoundTag("caps"));
			}
			return new Entry(item, tag, caps);
		}

		@Override
		public boolean equals(Object o){
			if(this == o) return true;
			if(o == null || getClass() != o.getClass()) return false;
			Entry entry = (Entry) o;
			return Objects.equals(item, entry.item) && Objects.equals(tag, entry.tag) && (((caps == null) == (entry.caps == null)) && (caps == null || caps.areCompatible(entry.caps)));
		}

		@Override
		public int hashCode(){
			return Objects.hash(item, tag);
		}

		@Override
		public String toString(){
			return item  + "{tag=" + tag + ", caps=" + caps + '}';
		}
	}

}
