package appeng.core.me.parts.part.connected;

import appeng.core.AppEng;
import appeng.core.me.AppEngME;
import appeng.core.me.api.definitions.IMEPartDefinitions;
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

import java.util.function.Predicate;

public abstract class PartFiber<P extends PartFiber<P, S>, S extends PartFiber.CableState<P, S>> extends PartConnected<P, S> {

	public PartFiber(boolean supportsRotation, PartColor color){
		super(supportsRotation, color);
	}

	@Override
	public S createNewState(){
		return (S) new CableState(this);
	}

	public static class CableState<P extends PartFiber<P, S>, S extends CableState<P, S>> extends PartConnected.ConnectedState<P, S> {

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

	public static class Micro extends PartFiber<PartFiber.Micro, MicroState> {

		public Micro(PartColor color){
			super(false, color);
		}

	}

	public static class MicroState extends CableState<PartFiber.Micro, MicroState> {

		public MicroState(PartFiber.Micro part){
			super(part);
		}
	}

	public static class Normal extends PartFiber<Normal, NormalState> implements PartGroup<PartFiber.Normal, NormalState> {

		public Normal(PartColor color){
			super(true, color);
		}

		protected Micro getMicroVariant(){
			//FIXME Dirty.
			return AppEngME.INSTANCE.<Micro, MicroState>getPartRegistry().getValue(new ResourceLocation(AppEng.MODID, "siocertic_fiber_micro_" + this.color.name().toLowerCase()));
		}

		@Override
		public PartsAccess compileRequiredParts(PartRotation targetRotation){
			PartsAccess.Mutable access = new SmallDetachedPartAccess();
			VoxelPosition recertic = new PartPositionRotation(new VoxelPosition(), targetRotation).getRotationCenterPosition();
			AppEngME.INSTANCE.<Micro, IMEPartDefinitions<Micro, MicroState>>definitions(Part.class).get(new ResourceLocation(AppEng.MODID, "recertic_fiber")).maybe().ifPresent(part -> access.setPart(new PartPositionRotation(recertic, new PartRotation()), part.createNewState()));
			AppEngME.INSTANCE.getPartsHelper().getVoxels(this, new PartPositionRotation(new VoxelPosition(), targetRotation)).filter(Predicate.isEqual(recertic).negate()).forEach(voxel -> access.setPart(new PartPositionRotation(voxel, new PartRotation()), getMicroVariant().createNewState()));
			return access;
		}

		public static class Joint extends PartFiber<PartFiber.Normal.Joint, NormalState.JointState> {

			public Joint(PartColor color){
				super(false, color);
			}

		}

	}

	public static class NormalState extends CableState<PartFiber.Normal, NormalState> {

		public NormalState(PartFiber.Normal part){
			super(part);
		}

		public static class JointState extends CableState<PartFiber.Normal.Joint, PartFiber.NormalState.JointState> {

			public JointState(PartFiber.Normal.Joint part){
				super(part);
			}
		}

	}

}
