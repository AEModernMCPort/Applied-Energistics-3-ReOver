package appeng.core.me.parts.part.connected;

import appeng.core.me.api.network.block.DeviceColor;
import appeng.core.me.parts.part.PartBase;

public abstract class PartConnected<P extends PartConnected<P, S>, S extends PartConnected.ConnectedState<P, S>> extends PartBase<P, S> {

	protected final DeviceColor color;

	public PartConnected(DeviceColor color){
		this.color = color;
	}

	public PartConnected(boolean supportsRotation, DeviceColor color){
		super(supportsRotation);
		this.color = color;
	}

	public static abstract class ConnectedState<P extends PartConnected<P, S>, S extends PartConnected.ConnectedState<P, S>> extends PartBase.StateBase<P, S> {

		public ConnectedState(P part){
			super(part);
		}
	}

}
