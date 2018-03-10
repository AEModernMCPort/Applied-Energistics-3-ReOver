package appeng.core.me.parts.part.device;

import appeng.core.me.AppEngME;
import appeng.core.me.api.network.DeviceUUID;
import appeng.core.me.api.network.NetBlock;
import appeng.core.me.api.network.device.DeviceRegistryEntry;
import net.minecraft.util.ITickable;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface ImportBus {

	class Network extends PartDeviceD2N.Network<Part, Physical, Network> {

		public Network(@Nonnull DeviceRegistryEntry<Network, Physical> registryEntry, @Nonnull DeviceUUID uuid, @Nullable NetBlock netBlock){
			super(registryEntry, uuid, netBlock);
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
			oih: for(IItemHandler itemHandler : itemHandlers.collect(Collectors.toSet())) for(int i = 0; i < itemHandler.getSlots(); i++) if(!itemHandler.getStackInSlot(i).isEmpty()){
				itemHandler.extractItem(i, 1, false);
				break oih;
			}
		}

	}

}
