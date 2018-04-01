package appeng.core.me.parts.part.device;

import appeng.core.me.AppEngME;
import appeng.core.me.api.network.DeviceUUID;
import appeng.core.me.api.network.NetBlock;
import appeng.core.me.api.network.device.DeviceRegistryEntry;
import appeng.core.me.api.network.storage.caps.ItemNetworkStorage;
import appeng.core.me.network.GlobalNBDManagerImpl;
import appeng.core.me.network.storage.caps.NetworkStorageCaps;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ITickable;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Queue;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface ImportBus {

	class Network extends PartDeviceD2N.Network<Part, Physical, Network> implements ITickable {

		public Network(@Nonnull DeviceRegistryEntry<Network, Physical> registryEntry, @Nonnull DeviceUUID uuid, @Nullable NetBlock netBlock){
			super(registryEntry, uuid, netBlock);
		}

		protected Queue<ItemStack> importBuffer = new LinkedList<>();

		protected int bufferLimit = 1;
		protected double updateInterval = 10;

		protected long lastUpdate; //FIXME transient or serialized?

		@Override
		public void update(){
			long current = GlobalNBDManagerImpl.getInstance().currentServerTick();
			if(current - lastUpdate > updateInterval){
				lastUpdate = current;
				for(double i = 0; i < 1/updateInterval; i++){
					ItemStack next = importBuffer.peek();
					if(next != null) getNetBlock().flatMap(NetBlock::getNetwork).ifPresent(network -> {
						if(network.getCapability(NetworkStorageCaps.item, null).store(new ItemNetworkStorage.Entry(next), next.getCount(), next.getCount()) > 0) importBuffer.poll();
					});
				}
			}
		}

		@Override
		public NBTTagCompound serializeNBT(){
			NBTTagCompound nbt = super.serializeNBT();
			NBTTagList queue = new NBTTagList();
			importBuffer.stream().map(ItemStack::serializeNBT).forEach(queue::appendTag);
			nbt.setTag("buffer", queue);
			return nbt;
		}

		@Override
		protected void deserializeNBT(NBTTagCompound nbt){
			super.deserializeNBT(nbt);
			importBuffer.clear();
			((Iterable<NBTTagCompound>) nbt.getTag("buffer")).forEach(tag -> importBuffer.add(new ItemStack(tag)));
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
			importStuff(AppEngME.INSTANCE.getDevicesHelper().getAllWITargetCPs(this, world).map(cp -> cp.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)).filter(Objects::nonNull));
		}

		protected void importStuff(Stream<IItemHandler> itemHandlers){
			if(networkCounterpart.hasNetwork() && networkCounterpart.importBuffer.size() < networkCounterpart.bufferLimit) oih: for(IItemHandler itemHandler : itemHandlers.collect(Collectors.toSet())) for(int i = 0; i < itemHandler.getSlots(); i++) if(!itemHandler.getStackInSlot(i).isEmpty()){
				networkCounterpart.importBuffer.add(itemHandler.extractItem(i, 1, false));
				break oih;
			}
		}

	}

}
