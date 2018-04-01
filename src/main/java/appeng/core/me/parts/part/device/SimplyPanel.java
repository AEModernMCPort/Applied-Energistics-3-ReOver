package appeng.core.me.parts.part.device;

import appeng.core.me.AppEngME;
import appeng.core.me.api.network.DeviceUUID;
import appeng.core.me.api.network.NetBlock;
import appeng.core.me.api.network.device.DeviceRegistryEntry;
import net.minecraft.util.ITickable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface SimplyPanel {

	class Network extends PartDeviceD2N.Network<Part, Physical, Network> implements ITickable {

		static int nextId = 0;
		int id;

		public Network(@Nonnull DeviceRegistryEntry<Network, Physical> registryEntry, @Nonnull DeviceUUID uuid, @Nullable NetBlock netBlock){
			super(registryEntry, uuid, netBlock);
			id = nextId++;
		}

		long prevTick = -1;
		long ticks = 0;

		@Override
		public void update(){
			ticks++;
			if(prevTick == -1) prevTick = System.currentTimeMillis();
			else if(System.currentTimeMillis() - prevTick > 2500){
				AppEngME.logger.info(String.format("Panel #%s Tick - %s ticks in 2.5secs on %s", id, ticks, Thread.currentThread().getName()));
				prevTick = System.currentTimeMillis();
				ticks = 0;
			}
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

	class Physical extends PartDeviceD2N.Physical<Part, Physical, Network> {

		public Physical(Part part){
			super(part);
		}

		@Override
		protected Network createNewNetworkCounterpart(){
			return new Network(getReg(), new DeviceUUID(), null);
		}

	}

}
