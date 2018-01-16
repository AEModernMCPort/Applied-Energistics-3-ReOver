package appeng.core.me.parts.part.connected;

import appeng.core.AppEng;
import appeng.core.me.AppEngME;
import appeng.core.me.api.definition.IPartDefinition;
import appeng.core.me.api.parts.PartColor;
import appeng.core.me.api.parts.PartPositionRotation;
import appeng.core.me.api.parts.PartRotation;
import appeng.core.me.api.parts.VoxelPosition;
import appeng.core.me.api.parts.container.PartsAccess;
import appeng.core.me.api.parts.part.Part;
import appeng.core.me.api.parts.part.PartGroup;
import appeng.core.me.parts.container.SmallDetachedPartAccess;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

import java.util.stream.Stream;

public abstract class PartCable<P extends PartCable<P, S>, S extends PartCable.CableState<P, S>> extends PartConnected<P, S> {

	public PartCable(boolean supportsRotation, PartColor color){
		super(supportsRotation, color);
	}

	@Override
	public S createNewState(){
		return (S) new CableState(this);
	}

	public static class Micro extends PartCable<PartCable.Micro, CableState.Micro> {

		public Micro(PartColor color){
			super(false, color);
		}

	}

	public static class Normal extends PartCable<PartCable.Normal, PartCable.CableState.Normal> implements PartGroup<PartCable.Normal, PartCable.CableState.Normal> {

		public Normal(PartColor color){
			super(true, color);
		}

		protected Micro getMicroVariant(){
			//FIXME Dirty.
			return AppEngME.INSTANCE.<Micro, CableState.Micro>getPartRegistry().getValue(new ResourceLocation(AppEng.MODID, "glass_cable_micro_" + this.color.name().toLowerCase()));
		}

		@Override
		public Stream<PartRotation> possibleRotations(){
			return Stream.of(new PartRotation());
		}

		@Override
		public PartsAccess compileRequiredParts(PartRotation targetRotation){
			PartsAccess.Mutable access = new SmallDetachedPartAccess();
//			access.setPart(new PartPositionRotation(new VoxelPosition(), new PartRotation()), getMicroVariant().createNewState());
			AppEngME.INSTANCE.getPartsHelper().getVoxels(this, new PartPositionRotation(new VoxelPosition(), new PartRotation())).forEach(voxel -> access.setPart(new PartPositionRotation(voxel, new PartRotation()), getMicroVariant().createNewState()));
			return access;
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

		public static class Micro extends CableState<PartCable.Micro, PartCable.CableState.Micro> {

			public Micro(PartCable.Micro part){
				super(part);
			}
		}

		public static class Normal extends CableState<PartCable.Normal, PartCable.CableState.Normal> {

			public Normal(PartCable.Normal part){
				super(part);
			}
		}

	}

}
