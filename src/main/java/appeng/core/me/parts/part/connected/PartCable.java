package appeng.core.me.parts.part.connected;

import appeng.core.me.api.parts.PartColor;
import net.minecraft.nbt.NBTTagCompound;

public abstract class PartCable<P extends PartCable<P, S>, S extends PartCable.CableState<P, S>> extends PartConnected<P, S> {

	public PartCable(boolean supportsRotation, PartColor color){
		super(supportsRotation, color);
	}

	@Override
	public S createNewState(){
		return (S) new CableState(this);
	}

	public static class Micro extends PartCable {

		public Micro(PartColor color){
			super(false, color);
		}
	}

	public static class Normal extends PartCable {

		public Normal(PartColor color){
			super(true, color);
		}
	}

	public static class CableState<P extends PartCable<P, S>, S extends CableState<P, S>> extends PartConnected.ConnectedState<P, S>{

		public CableState(P part){
			super(part);
		}

		@Override
		public NBTTagCompound serializeNBT(){
			return null;
		}

		@Override
		public void deserializeNBT(NBTTagCompound nbt){

		}

	}

}
