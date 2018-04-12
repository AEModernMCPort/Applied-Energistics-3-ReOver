package appeng.core.me.api.network.device;

import appeng.core.me.api.network.NetDevice;
import appeng.core.me.api.network.PhysicalDevice;

public interface ExportBus {

	interface Network<P extends Part<P, S, N>, S extends Physical<P, S, N>, N extends Network<P, S, N>> extends NetDevice<N, S> {

	}

	interface Part<P extends Part<P, S, N>, S extends Physical<P, S, N>, N extends Network<P, S, N>> extends appeng.core.me.api.parts.part.Part<P, S> {

	}

	interface Physical<P extends Part<P, S, N>, S extends Physical<P, S, N>, N extends Network<P, S, N>> extends PhysicalDevice<N, S>, Part.State<P, S> {

	}

}
