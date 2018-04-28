package appeng.core.me.parts.part.device;

import appeng.core.AppEng;
import appeng.core.lib.capability.DelegateCapabilityStorage;
import appeng.core.lib.capability.SingleCapabilityProvider;
import appeng.core.me.AppEngME;
import appeng.core.me.api.network.DeviceUUID;
import appeng.core.me.api.network.NetBlock;
import appeng.core.me.api.network.NetDevice;
import appeng.core.me.api.network.device.DeviceRegistryEntry;
import appeng.core.me.api.network.device.ImportBus;
import appeng.core.me.api.network.device.behavior.BehaviorDriven;
import appeng.core.me.api.network.storage.caps.ItemNetworkStorage;
import appeng.core.me.network.GlobalNBDManagerImpl;
import appeng.core.me.network.storage.caps.NetworkStorageCaps;
import code.elix_x.excomms.optional.NullableOptional;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ITickable;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface ImportBusImpl extends ImportBus {

	class Network extends PartDeviceD2N.BehaviorDriven.Network<Part, Physical, Network, Behavior> implements ImportBus.Network<Part, Physical, Network>, ITickable {

		public Network(@Nonnull DeviceRegistryEntry<Network, Physical> registryEntry, @Nonnull DeviceUUID uuid, @Nullable NetBlock netBlock){
			super(registryEntry, uuid, netBlock);
		}

		protected long lastUpdate; //FIXME transient or serialized?
		protected long sinceLastUpdate;

		@Override
		public void update(){
			long current = GlobalNBDManagerImpl.getInstance().currentServerTick();
			sinceLastUpdate = current - lastUpdate;
			if(behaviorDrivenAction(ImportBus.Behavior::update).filter(res -> res == BehaviorOperationResult.SUCCESS).isPresent()) lastUpdate = current;
		}

		@Override
		public long ticksSinceLastUpdate(){
			return sinceLastUpdate;
		}

		@Override
		public boolean canAcceptForImport(){
			return behaviorDrivenAction(Behavior::canAcceptForImport).orElse(BehaviorOperationResult.FAIL) == BehaviorOperationResult.SUCCESS;
		}

		@Override
		public boolean addForImport(ItemStack stack){
			return behaviorDrivenAction(behavior -> behavior.addForImport(stack)).orElse(BehaviorOperationResult.FAIL) == BehaviorOperationResult.SUCCESS;
		}

		@Override
		public Optional<ItemStack> nextItemForImport(){
			return behaviorDrivenGetAction(Behavior::nextItemForImport).orElseOpt(Optional.empty());
		}

		@Override
		public boolean confirmImport(ItemStack stack){
			return behaviorDrivenAction(behavior -> behavior.confirmImport(stack)).orElse(BehaviorOperationResult.FAIL) == BehaviorOperationResult.SUCCESS;
		}

		@Override
		public boolean importItem(ItemStack stack){
			return behaviorDrivenAction(behavior -> behavior.importItem(stack)).orElse(BehaviorOperationResult.FAIL) == BehaviorOperationResult.SUCCESS;
		}

	}

	class Part extends PartDevice<Part, Physical, Network> implements ImportBus.Part<Part, Physical, Network> {

		public Part(){
			super(true);
		}

		@Override
		public Physical createNewState(){
			return new Physical(this);
		}

	}

	class Physical extends PartDeviceD2N.BehaviorDriven.Physical<Part, Physical, Network, Behavior> implements ImportBus.Physical<Part, Physical, Network>, ITickable {

		public Physical(Part part){
			super(part);
		}

		@Override
		protected Network createNewNetworkCounterpart(){
			return new Network(getReg(), new DeviceUUID(), null);
		}

		@Override
		public void update(){
			networkCounterpart.behaviorDrivenAction(Behavior::updatePhysical);
		}

	}

	@Mod.EventBusSubscriber(modid = AppEng.MODID)
	class DefaultBehavior implements Behavior {

		@CapabilityInject(Data.class)
		public static Capability<Data> dataCap;

		public static void registerCaps(){
			CapabilityManager.INSTANCE.register(Data.class, new DelegateCapabilityStorage<>(), Data::new);
		}

		@SubscribeEvent
		public static void addDefaultData(AttachCapabilitiesEvent<NetDevice> event){
			if(event.getObject() instanceof ImportBus.Network) event.addCapability(new ResourceLocation(AppEng.MODID, "import_bus_default_data"), new SingleCapabilityProvider.Serializeable<>(dataCap, new Data()));
		}

		@SubscribeEvent
		public static <N extends ImportBus.Network<P, S, N>, S extends ImportBus.Physical<P, S, N>, P extends ImportBus.Part<P, S, N>> void addDefaultBehavior(BehaviorDriven.AttachDeviceBehaviorEvent<ImportBus.Network> eve){
			eve.<N, S, ImportBus.Behavior>event(ImportBus.Network.class).ifPresent(event -> event.addBehavior(new DefaultBehavior((Network) event.getDevice()), 0));
		}

		public static class Data implements INBTSerializable<NBTTagCompound> {

			int bufferLimit = 1;
			double oneItemPerTicks = 10;

			Queue<ItemStack> buffer = new LinkedList<>();

			@Override
			public NBTTagCompound serializeNBT(){
				NBTTagCompound nbt = new NBTTagCompound();
				NBTTagList queue = new NBTTagList();
				buffer.stream().map(ItemStack::serializeNBT).forEach(queue::appendTag);
				nbt.setTag("buffer", queue);
				return nbt;
			}

			@Override
			public void deserializeNBT(NBTTagCompound nbt){
				buffer.clear();
				((Iterable<NBTTagCompound>) nbt.getTag("buffer")).forEach(tag -> buffer.add(new ItemStack(tag)));
			}

		}

		protected final Network importBus;
		protected final Data data;

		public DefaultBehavior(Network importBus){
			this.importBus = importBus;
			data = this.importBus.getCapability(dataCap, null);
		}

		@Nonnull
		@Override
		public <P extends ImportBus.Part<P, S, N>, S extends ImportBus.Physical<P, S, N>, N extends ImportBus.Network<P, S, N>> BehaviorDriven.BehaviorOperationResult update(){
			if(importBus.ticksSinceLastUpdate() > data.oneItemPerTicks)	return importBus.nextItemForImport().map(importBus::importItem).map(b -> b ? BehaviorDriven.BehaviorOperationResult.SUCCESS : BehaviorDriven.BehaviorOperationResult.FAIL).orElse(BehaviorDriven.BehaviorOperationResult.PASS);
			return BehaviorDriven.BehaviorOperationResult.PASS;
		}

		@Nonnull
		@Override
		public <P extends ImportBus.Part<P, S, N>, S extends ImportBus.Physical<P, S, N>, N extends ImportBus.Network<P, S, N>> BehaviorDriven.BehaviorOperationResult updatePhysical(){
			if(!importBus.getPhysicalCounterpart().isPresent()) return BehaviorDriven.BehaviorOperationResult.PASS;
			Physical importBusPhysical = importBus.getPhysicalCounterpart().get();
			return importStuff(AppEngME.INSTANCE.getDevicesHelper().getAllWITargetCPs(importBusPhysical, importBusPhysical.world).map(cp -> cp.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)).filter(Objects::nonNull));
		}

		protected BehaviorDriven.BehaviorOperationResult importStuff(Stream<IItemHandler> itemHandlers){
			int timp = (int) Math.ceil(1/data.oneItemPerTicks);
			int remaining = timp;
			if(importBus.canAcceptForImport()) oih: for(IItemHandler itemHandler : randomize(itemHandlers)) for(int i = 0; i < itemHandler.getSlots(); i++){
				ItemStack stack = itemHandler.getStackInSlot(i);
				if(!stack.isEmpty()){
					int imp = Math.min(stack.getCount(), remaining);
					ItemStack extr = itemHandler.extractItem(i, imp, false);
					if(!extr.isEmpty()){
						importBus.addForImport(extr);
						remaining -= imp;
						if(remaining == 0 || !importBus.canAcceptForImport()) break oih;
					}
				}
			}
			return remaining < timp ? BehaviorDriven.BehaviorOperationResult.SUCCESS : BehaviorDriven.BehaviorOperationResult.FAIL;
		}

		protected List<IItemHandler> randomize(Stream<IItemHandler> itemHandlers){
			ArrayList<IItemHandler> res = itemHandlers.distinct().collect(Collectors.toCollection(ArrayList::new));
			Collections.shuffle(res);
			return res;
		}

		@Nonnull
		@Override
		public <P extends ImportBus.Part<P, S, N>, S extends ImportBus.Physical<P, S, N>, N extends ImportBus.Network<P, S, N>> BehaviorDriven.BehaviorOperationResult canAcceptForImport(){
			return data.buffer.size() < data.bufferLimit ? BehaviorDriven.BehaviorOperationResult.SUCCESS : BehaviorDriven.BehaviorOperationResult.FAIL;
		}

		@Nonnull
		@Override
		public <P extends ImportBus.Part<P, S, N>, S extends ImportBus.Physical<P, S, N>, N extends ImportBus.Network<P, S, N>> BehaviorDriven.BehaviorOperationResult addForImport(ItemStack stack){
			if(data.buffer.size() < data.bufferLimit){
				data.buffer.add(stack);
				return BehaviorDriven.BehaviorOperationResult.SUCCESS;
			}
			return BehaviorDriven.BehaviorOperationResult.FAIL;
		}

		@Nonnull
		@Override
		public <P extends ImportBus.Part<P, S, N>, S extends ImportBus.Physical<P, S, N>, N extends ImportBus.Network<P, S, N>> NullableOptional<ItemStack> nextItemForImport(){
			return NullableOptional.of(data.buffer.peek());
		}

		@Nonnull
		@Override
		public <P extends ImportBus.Part<P, S, N>, S extends ImportBus.Physical<P, S, N>, N extends ImportBus.Network<P, S, N>> BehaviorDriven.BehaviorOperationResult confirmImport(ItemStack stack){
			if(data.buffer.peek() == stack){
				data.buffer.poll();
				return BehaviorDriven.BehaviorOperationResult.SUCCESS;
			}
			return BehaviorDriven.BehaviorOperationResult.PASS;
		}

		@Nonnull
		@Override
		public <P extends ImportBus.Part<P, S, N>, S extends ImportBus.Physical<P, S, N>, N extends ImportBus.Network<P, S, N>> BehaviorDriven.BehaviorOperationResult importItem(@Nonnull ItemStack stack){
			Optional<appeng.core.me.api.network.Network> network = importBus.getNetwork();
			if(!network.isPresent()) return BehaviorDriven.BehaviorOperationResult.PASS;
			if(network.get().getCapability(NetworkStorageCaps.item, null).store(ItemNetworkStorage.Entry.ofItemStack(stack), stack.getCount(), stack.getCount()) > 0){
				if(!importBus.confirmImport(stack)) throw new IllegalArgumentException("How? Seriously. I asked whether you could accept, and you said \"YES!\", but when i did it, you failed me... WHYYYYYY D; ???");
				return BehaviorDriven.BehaviorOperationResult.SUCCESS;
			}
			return BehaviorDriven.BehaviorOperationResult.FAIL;
		}

	}

}
