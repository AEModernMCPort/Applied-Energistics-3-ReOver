package appeng.core.me.parts.part.device;

import appeng.core.me.AppEngME;
import appeng.core.me.api.network.DeviceUUID;
import appeng.core.me.api.network.NetBlock;
import appeng.core.me.api.network.device.DeviceRegistryEntry;
import appeng.core.me.api.network.storage.caps.ItemNetworkStorage;
import appeng.core.me.api.parts.container.PartsAccess;
import appeng.core.me.network.GlobalNBDManagerImpl;
import appeng.core.me.network.storage.caps.NetworkStorageCaps;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ITickable;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import org.apache.commons.lang3.mutable.MutableObject;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface ExportBus {

	class Network extends PartDeviceD2N.Network<Part, Physical, Network> implements ITickable {

		public Network(@Nonnull DeviceRegistryEntry<Network, Physical> registryEntry, @Nonnull DeviceUUID uuid, @Nullable NetBlock netBlock){
			super(registryEntry, uuid, netBlock);
		}

		ItemNetworkStorage.Entry selected;
		protected Queue<ItemStack> exportBuffer = new LinkedList<>();

		protected int bufferLimit = 1;
		protected double oneItemPerTicks = 10;

		protected long lastUpdate; //FIXME transient or serialized?

		@Override
		public void update(){
			long current = GlobalNBDManagerImpl.getInstance().currentServerTick();
			if(current - lastUpdate > oneItemPerTicks){
				if(selected != null && exportBuffer.size() < bufferLimit){
					lastUpdate = current;
					getNetwork().ifPresent(network -> {
						int ext = network.getCapability(NetworkStorageCaps.item, null).store(selected, -(int) Math.ceil(1 / oneItemPerTicks), -1);
						if(ext < 0) exportBuffer.add(selected.asStack(-ext));
					});
				}
			}
		}

		@Override
		public NBTTagCompound serializeNBT(){
			NBTTagCompound nbt = super.serializeNBT();
			if(selected != null) nbt.setTag("selected", selected.serializeNBT());
			NBTTagList queue = new NBTTagList();
			exportBuffer.stream().map(ItemStack::serializeNBT).forEach(queue::appendTag);
			nbt.setTag("buffer", queue);
			return nbt;
		}

		@Override
		protected void deserializeNBT(NBTTagCompound nbt){
			super.deserializeNBT(nbt);
			if(nbt.hasKey("selected")) selected = ItemNetworkStorage.Entry.deserializeNBT(nbt.getCompoundTag("selected"));
			exportBuffer.clear();
			((Iterable<NBTTagCompound>) nbt.getTag("buffer")).forEach(tag -> exportBuffer.add(new ItemStack(tag)));
		}

	}

	class Part extends PartDevice<Part, Physical, Network> {

		public Part(){
			super(true);
		}

		@Override
		public Physical createNewState(){
			return new Physical(this);
		}

		@Override
		public EnumActionResult onRightClick(@Nullable Physical part, @Nonnull PartsAccess.Mutable world, @Nonnull World theWorld, @Nonnull EntityPlayer player, @Nonnull EnumHand hand){
			if(!theWorld.isRemote) part.networkCounterpart.selected = ItemNetworkStorage.Entry.ofItemStack(player.getHeldItem(hand));
			return EnumActionResult.SUCCESS;
		}

	}

	class Physical extends PartDeviceD2N.Physical<Part, Physical, Network> implements ITickable {

		public Physical(Part part){
			super(part);
		}

		@Override
		protected Network createNewNetworkCounterpart(){
			return new Network(getReg(), new DeviceUUID(), null);
		}

		@Override
		public void update(){
			exportStuff(AppEngME.INSTANCE.getDevicesHelper().getAllWITargetCPs(this, world).map(cp -> cp.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)).filter(Objects::nonNull));
		}

		protected void exportStuff(Stream<IItemHandler> itemHandlers){
			if(networkCounterpart.hasNetwork()){
				ItemStack next = networkCounterpart.exportBuffer.peek();
				if(next != null){
					List<Pair<IItemHandler, Integer>> dests = new ArrayList<>();
					oeh: for(IItemHandler itemHandler : itemHandlers.collect(Collectors.toSet())) for(int i = 0; i < itemHandler.getSlots(); i++){
						ItemStack rem = itemHandler.insertItem(i, next, true);
						if(rem.getCount() < next.getCount()){
							next = rem;
							dests.add(new ImmutablePair<>(itemHandler, i));
							if(next.isEmpty()) break oeh;
						}
					}
					if(next.isEmpty()){
						MutableObject<ItemStack> rem = new MutableObject<>(networkCounterpart.exportBuffer.poll());
						dests.forEach(ihSlot -> rem.setValue(ihSlot.getLeft().insertItem(ihSlot.getRight(), rem.getValue(), false)));
					}
				}
			}
		}

	}

}
