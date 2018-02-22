package appeng.core.me.parts.part.device;

import appeng.core.me.AppEngME;
import appeng.core.me.api.network.DeviceUUID;
import appeng.core.me.api.network.NetBlock;
import appeng.core.me.api.network.block.Connection;
import appeng.core.me.api.network.device.DeviceRegistryEntry;
import appeng.core.me.api.parts.PartColor;
import appeng.core.me.network.connect.ConnectionsParams;
import appeng.core.me.network.device.NetDeviceBase;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface PartDeviceD2N {

	class Network<P extends PartDevice<P, S, N>, S extends Physical<P, S, N>, N extends Network<P, S, N>> extends NetDeviceBase<N, S>{

		protected final ConnectionsParams<?> params;

		public Network(@Nonnull DeviceRegistryEntry<N, S> registryEntry, @Nonnull DeviceUUID uuid, @Nullable NetBlock netBlock, ConnectionsParams<?> params){
			super(registryEntry, uuid, netBlock);
			this.params = params;
		}

	}

	abstract class Physical<P extends PartDevice<P, S, N>, S extends Physical<P, S, N>, N extends Network<P, S, N>> extends PartDevice.PartDeviceState<P, S, N> {

		public Physical(P part){
			super(part);
		}

		private DeviceRegistryEntry<N, S> reg;

		protected DeviceRegistryEntry<N, S> getReg(){
			return reg != null ? reg : (reg = AppEngME.INSTANCE.<N, S>getDeviceRegistry().getValue(getPart().getRegistryName()));
		}

		@Override
		public PartColor getColor(){
			return PartColor.values()[0];
		}

		@Override
		public <Param extends Comparable<Param>> Param getConnectionRequirement(Connection<Param, ?> connection){
			return getNetworkCounterpart().params.getParam(connection);
		}

	}

}
