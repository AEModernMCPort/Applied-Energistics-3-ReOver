package appeng.core.me.parts.part.device;

import appeng.core.me.AppEngME;
import appeng.core.me.api.network.DeviceUUID;
import appeng.core.me.api.network.NetBlock;
import appeng.core.me.api.network.NetworkUUID;
import appeng.core.me.api.network.block.Connection;
import appeng.core.me.api.network.device.DeviceRegistryEntry;
import appeng.core.me.api.parts.PartColor;
import appeng.core.me.api.parts.container.PartsAccess;
import appeng.core.me.network.NetworkImpl;
import appeng.core.me.network.device.NetDeviceBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface Controller {

	class Network extends NetDeviceBase<Network, Physical> {

		public Network(@Nonnull DeviceRegistryEntry<Network, Physical> registryEntry, @Nonnull DeviceUUID uuid, @Nullable NetBlock netBlock){
			super(registryEntry, uuid, netBlock);
		}

	}

	class Part extends PartDevice<Part, Physical, Network> {

		public Part(){
			super(false);
		}

		@Override
		public Physical createNewState(){
			return new Physical(this);
		}

		@Override
		public void onPlaced(@Nullable Physical part, @Nonnull PartsAccess.Mutable world, @Nullable World theWorld, @Nullable EntityPlayer placer, @Nullable EnumHand hand){
			super.onPlaced(part, world, theWorld, placer, hand);
			if(theWorld != null && placer != null){
				NetworkImpl network = new NetworkImpl(new NetworkUUID());
				network.initialize(part.networkCounterpart, theWorld, part);
			}
		}
	}

	class Physical extends PartDevice.PartDeviceState<Part, Physical, Network> {

		public Physical(Part part){
			super(part);
		}

		protected final DeviceRegistryEntry<Network, Physical> reg = AppEngME.INSTANCE.<Network, Physical>getDeviceRegistry().getValue(getPart().getRegistryName());

		@Override
		protected Network createNewNetworkCounterpart(){
			return new Network(reg, new DeviceUUID(), null);
		}

		@Override
		public PartColor getColor(){
			return PartColor.values()[0];
		}

		@Override
		public <Param extends Comparable<Param>> Param getConnectionRequirement(Connection<Param, ?> connection){
			return null;
		}

	}

}
