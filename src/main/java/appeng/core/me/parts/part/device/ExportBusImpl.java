package appeng.core.me.parts.part.device;

import appeng.core.AppEng;
import appeng.core.lib.capability.DelegateCapabilityStorage;
import appeng.core.lib.capability.SingleCapabilityProvider;
import appeng.core.me.AppEngME;
import appeng.core.me.api.network.DeviceUUID;
import appeng.core.me.api.network.NetBlock;
import appeng.core.me.api.network.NetDevice;
import appeng.core.me.api.network.device.DeviceRegistryEntry;
import appeng.core.me.api.network.device.ExportBus;
import appeng.core.me.api.network.device.behavior.BehaviorDriven;
import appeng.core.me.api.network.storage.caps.ItemNetworkStorage;
import appeng.core.me.api.parts.container.PartsAccess;
import appeng.core.me.network.GlobalNBDManagerImpl;
import appeng.core.me.network.storage.caps.NetworkStorageCaps;
import code.elix_x.excomms.optional.NullableOptional;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ITickable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import org.apache.commons.lang3.mutable.MutableInt;
import org.apache.commons.lang3.mutable.MutableObject;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface ExportBusImpl extends ExportBus {

	class Network extends PartDeviceD2N.BehaviorDriven.Network<Part, Physical, Network, Behavior> implements ExportBus.Network<Part, Physical, Network>, ITickable {

		public Network(@Nonnull DeviceRegistryEntry<Network, Physical> registryEntry, @Nonnull DeviceUUID uuid, @Nullable NetBlock netBlock){
			super(registryEntry, uuid, netBlock);
		}

		protected long lastUpdate; //FIXME transient or serialized?
		protected long sinceLastUpdate;

		@Override
		public long ticksSinceLastUpdate(){
			return sinceLastUpdate;
		}

		@Override
		public void update(){
			long current = GlobalNBDManagerImpl.getInstance().currentServerTick();
			sinceLastUpdate = current - lastUpdate;
			if(behaviorDrivenAction(Behavior::update).filter(res -> res == BehaviorOperationResult.SUCCESS).isPresent()) lastUpdate = current;
		}

		@Override
		public boolean canAcceptForExport(){
			return behaviorDrivenAction(Behavior::canAcceptForExport).orElse(BehaviorOperationResult.FAIL) == BehaviorOperationResult.SUCCESS;
		}

		@Override
		public boolean addForExport(ItemStack stack){
			return behaviorDrivenAction(behavior -> behavior.addForExport(stack)).orElse(BehaviorOperationResult.FAIL) == BehaviorOperationResult.SUCCESS;
		}

		@Override
		public Optional<ItemStack> nextItemForExport(){
			return behaviorDrivenGetAction(Behavior::nextItemForExport).orElseOpt(Optional.empty());
		}

		@Override
		public boolean confirmExport(ItemStack stack){
			return behaviorDrivenAction(behavior -> behavior.confirmExport(stack)).orElse(BehaviorOperationResult.FAIL) == BehaviorOperationResult.SUCCESS;
		}

		@Override
		public boolean export(ItemStack stack){
			return behaviorDrivenAction(behavior -> behavior.exportItem(stack)).orElse(BehaviorOperationResult.FAIL) == BehaviorOperationResult.SUCCESS;
		}

	}

	class Part extends PartDevice<Part, Physical, Network> implements ExportBus.Part<Part, Physical, Network> {

		public Part(){
			super(true);
		}

		@Override
		public Physical createNewState(){
			return new Physical(this);
		}

		@Override
		public EnumActionResult onRightClick(@Nullable Physical part, @Nonnull PartsAccess.Mutable world, @Nonnull World theWorld, @Nonnull EntityPlayer player, @Nonnull EnumHand hand){
			if(!theWorld.isRemote) part.networkCounterpart.behaviorDrivenAction(behavior -> behavior.onRightClick(world, theWorld, player, hand));
			return EnumActionResult.SUCCESS;
		}

	}

	class Physical extends PartDeviceD2N.BehaviorDriven.Physical<Part, Physical, Network, Behavior> implements ExportBus.Physical<Part, Physical, Network>, ITickable {

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
			if(event.getObject() instanceof ExportBus.Network) event.addCapability(new ResourceLocation(AppEng.MODID, "export_bus_default_data"), new SingleCapabilityProvider.Serializeable<>(dataCap, new Data()));
		}

		@SubscribeEvent
		public static <N extends ExportBus.Network<P, S, N>, S extends ExportBus.Physical<P, S, N>, P extends ExportBus.Part<P, S, N>> void addDefaultBehavior(BehaviorDriven.AttachDeviceBehaviorEvent<ExportBus.Network> eve){
			eve.<N, S, ExportBus.Behavior>event(ExportBus.Network.class).ifPresent(event -> event.addBehavior(new DefaultBehavior((Network) event.getDevice()), 0));
		}

		public static class Data implements INBTSerializable<NBTTagCompound> {

			ItemNetworkStorage.Entry selected;
			int bufferLimit = 1;
			double oneItemPerTicks = 10;

			Queue<ItemStack> buffer = new LinkedList<>();

			@Override
			public NBTTagCompound serializeNBT(){
				NBTTagCompound nbt = new NBTTagCompound();
				if(selected != null) nbt.setTag("selected", selected.serializeNBT());
				NBTTagList queue = new NBTTagList();
				buffer.stream().map(ItemStack::serializeNBT).forEach(queue::appendTag);
				nbt.setTag("buffer", queue);
				return nbt;
			}

			@Override
			public void deserializeNBT(NBTTagCompound nbt){
				if(nbt.hasKey("selected")) selected = ItemNetworkStorage.Entry.deserializeNBT(nbt.getCompoundTag("selected"));
				buffer.clear();
				((Iterable<NBTTagCompound>) nbt.getTag("buffer")).forEach(tag -> buffer.add(new ItemStack(tag)));
			}

		}

		protected final Network exportBus;
		protected final Data data;

		public DefaultBehavior(Network exportBus){
			this.exportBus = exportBus;
			data = this.exportBus.getCapability(dataCap, null);
		}

		@Nonnull
		@Override
		public <P extends ExportBus.Part<P, S, N>, S extends ExportBus.Physical<P, S, N>, N extends ExportBus.Network<P, S, N>> BehaviorDriven.BehaviorOperationResult update(){
			if(exportBus.ticksSinceLastUpdate() > data.oneItemPerTicks){
				if(data.selected != null && exportBus.canAcceptForExport()){
					MutableInt res = new MutableInt();
					exportBus.getNetwork().ifPresent(network -> {
						int ext = network.getCapability(NetworkStorageCaps.item, null).store(data.selected, -(int) Math.ceil(1 / data.oneItemPerTicks), -1);
						if(ext < 0) if(!exportBus.addForExport(data.selected.asStack(-ext))) throw new IllegalArgumentException("How? Seriously. I asked whether you could accept, and you said \"YES!\", but when i did it, you failed me... WHYYYYYY D; ???");
						res.setValue(ext);
					});
					return res.intValue() < 0 ? BehaviorDriven.BehaviorOperationResult.SUCCESS : BehaviorDriven.BehaviorOperationResult.FAIL;
				}
			}
			return BehaviorDriven.BehaviorOperationResult.PASS;
		}

		@Nonnull
		@Override
		public <P extends ExportBus.Part<P, S, N>, S extends ExportBus.Physical<P, S, N>, N extends ExportBus.Network<P, S, N>> BehaviorDriven.BehaviorOperationResult updatePhysical(){
			return exportBus.nextItemForExport().map(exportBus::export).map(b -> b ? BehaviorDriven.BehaviorOperationResult.SUCCESS : BehaviorDriven.BehaviorOperationResult.FAIL).orElse(BehaviorDriven.BehaviorOperationResult.PASS);
		}

		@Nonnull
		@Override
		public <P extends ExportBus.Part<P, S, N>, S extends ExportBus.Physical<P, S, N>, N extends ExportBus.Network<P, S, N>> BehaviorDriven.BehaviorOperationResult onRightClick(@Nonnull PartsAccess.Mutable world, @Nonnull World theWorld, @Nonnull EntityPlayer player, @Nonnull EnumHand hand){
			data.selected = ItemNetworkStorage.Entry.ofItemStack(player.getHeldItem(hand));
			return BehaviorDriven.BehaviorOperationResult.SUCCESS;
		}

		@Nonnull
		@Override
		public <P extends ExportBus.Part<P, S, N>, S extends ExportBus.Physical<P, S, N>, N extends ExportBus.Network<P, S, N>> BehaviorDriven.BehaviorOperationResult canAcceptForExport(){
			return data.buffer.size() < data.bufferLimit ? BehaviorDriven.BehaviorOperationResult.SUCCESS : BehaviorDriven.BehaviorOperationResult.FAIL;
		}

		@Nonnull
		@Override
		public <P extends ExportBus.Part<P, S, N>, S extends ExportBus.Physical<P, S, N>, N extends ExportBus.Network<P, S, N>> BehaviorDriven.BehaviorOperationResult addForExport(ItemStack stack){
			if(data.buffer.size() < data.bufferLimit){
				data.buffer.add(stack);
				return BehaviorDriven.BehaviorOperationResult.SUCCESS;
			}
			return BehaviorDriven.BehaviorOperationResult.FAIL;
		}

		@Nonnull
		@Override
		public <P extends ExportBus.Part<P, S, N>, S extends ExportBus.Physical<P, S, N>, N extends ExportBus.Network<P, S, N>> NullableOptional<ItemStack> nextItemForExport(){
			return NullableOptional.of(data.buffer.peek());
		}

		@Nonnull
		@Override
		public <P extends ExportBus.Part<P, S, N>, S extends ExportBus.Physical<P, S, N>, N extends ExportBus.Network<P, S, N>> BehaviorDriven.BehaviorOperationResult confirmExport(ItemStack stack){
			if(data.buffer.peek() == stack){
				data.buffer.poll();
				return BehaviorDriven.BehaviorOperationResult.SUCCESS;
			}
			return BehaviorDriven.BehaviorOperationResult.PASS;
		}

		@Nonnull
		@Override
		public <P extends ExportBus.Part<P, S, N>, S extends ExportBus.Physical<P, S, N>, N extends ExportBus.Network<P, S, N>> BehaviorDriven.BehaviorOperationResult exportItem(@Nonnull ItemStack stack){
			if(!exportBus.getPhysicalCounterpart().isPresent()) return BehaviorDriven.BehaviorOperationResult.PASS;
			Physical exportBusPhysical = exportBus.getPhysicalCounterpart().get();
			return exportStuff(stack, AppEngME.INSTANCE.getDevicesHelper().getAllWITargetCPs(exportBusPhysical, exportBusPhysical.world).map(cp -> cp.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)).filter(Objects::nonNull));
		}

		protected BehaviorDriven.BehaviorOperationResult exportStuff(ItemStack exp, Stream<IItemHandler> itemHandlers){
			ItemStack next = exp.copy();
			List<Pair<IItemHandler, Integer>> dests = new ArrayList<>();
			oeh: for(IItemHandler itemHandler : itemHandlers.collect(Collectors.toSet())) for(int i = 0; i < itemHandler.getSlots(); i++){
				ItemStack rem = itemHandler.insertItem(i, next, true);
				if(rem.getCount() < next.getCount()){
					next = rem;
					dests.add(new ImmutablePair<>(itemHandler, i));
					if(next.isEmpty()) break oeh;
				}
			}
			if(next.isEmpty() && exportBus.confirmExport(exp)){
				MutableObject<ItemStack> rem = new MutableObject<>(exp);
				dests.forEach(ihSlot -> rem.setValue(ihSlot.getLeft().insertItem(ihSlot.getRight(), rem.getValue(), false)));
				return BehaviorDriven.BehaviorOperationResult.SUCCESS;
			}
			return BehaviorDriven.BehaviorOperationResult.FAIL;
		}

	}

}
