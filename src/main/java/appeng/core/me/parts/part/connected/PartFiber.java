package appeng.core.me.parts.part.connected;

import appeng.core.AppEng;
import appeng.core.me.AppEngME;
import appeng.core.me.api.definitions.IMEPartDefinitions;
import appeng.core.me.api.parts.PartColor;
import appeng.core.me.api.parts.PartPositionRotation;
import appeng.core.me.api.parts.PartRotation;
import appeng.core.me.api.parts.VoxelPosition;
import appeng.core.me.api.parts.container.PartInfo;
import appeng.core.me.api.parts.container.PartsAccess;
import appeng.core.me.api.parts.part.Part;
import appeng.core.me.api.parts.part.PartGroup;
import appeng.core.me.parts.container.SmallDetachedPartAccess;
import com.google.common.collect.Lists;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

public abstract class PartFiber<P extends PartFiber<P, S>, S extends PartFiber.FiberState<P, S>> extends PartConnected<P, S> {

	public PartFiber(boolean supportsRotation, PartColor color){
		super(supportsRotation, color);
	}

	public static class FiberState<P extends PartFiber<P, S>, S extends FiberState<P, S>> extends PartConnected.ConnectedState<P, S> {

		public FiberState(P part){
			super(part);
		}

		@Override
		public NBTTagCompound serializeNBT(){
			return new NBTTagCompound();
		}

		@Override
		public void deserializeNBT(NBTTagCompound nbt){

		}

	}

	public static class Micro extends PartFiber<PartFiber.Micro, MicroState> {

		protected ResourceLocation[] meshes = {new ResourceLocation(AppEng.MODID, "me/fiber/siocertic/micro/" + color.name().toLowerCase() + "_node.obj"), new ResourceLocation(AppEng.MODID, "me/fiber/siocertic/micro/" + color.name().toLowerCase() + "_line.obj")};

		public Micro(PartColor color){
			super(false, color);
		}

		@Override
		public MicroState createNewState(){
			return new MicroState(this);
		}

		@Nonnull
		@Override
		public List<ResourceLocation> getMeshes(){
			return Lists.newArrayList(meshes);
		}

		@Override
		public void onPlaced(@Nullable MicroState part, @Nonnull PartsAccess.Mutable world, @Nullable World theWorld, @Nullable EntityPlayer placer, @Nullable EnumHand hand){
			for(EnumFacing dir : EnumFacing.values()){
				world.getPart(part.getAssignedPosRot().getPosition().offsetLocal(dir)).flatMap(PartInfo::getState).ifPresent(s -> {
					if(s instanceof MicroState){
						MicroState adjMC = (MicroState) s;
						if(part.canConnect(adjMC)){
							part.setConnection(dir, true);
							adjMC.setConnection(dir.getOpposite(), true);
							world.markDirty(adjMC);
						}
					}
				});
			}
			world.markDirty(part);
		}

		@Override
		public void onBroken(@Nullable MicroState part, @Nonnull PartsAccess.Mutable world, @Nullable World theWorld, @Nullable EntityPlayer breaker){
			for(EnumFacing dir : part.connections){
				world.getPart(part.getAssignedPosRot().getPosition().offsetLocal(dir)).flatMap(PartInfo::getState).ifPresent(s -> {
					if(s instanceof MicroState){
						((MicroState) s).setConnection(dir.getOpposite(), false);
						world.markDirty(s);
					}
				});
			}
		}
	}

	public static class MicroState extends FiberState<Micro, MicroState> {

		public MicroState(PartFiber.Micro part){
			super(part);
		}

		protected Set<EnumFacing> connections = new HashSet<>();

		protected void setConnection(EnumFacing sideFrom, boolean present){
			if(present) connections.add(sideFrom);
			else connections.remove(sideFrom);
		}

		protected boolean canConnect(MicroState other){
			return getPart().color.canConnect(other.getPart().color);
		}

		@Nullable
		public EnumFacing getLine(){
			return connections.size() == 2 ? (connections.contains(EnumFacing.EAST) && connections.contains(EnumFacing.WEST) ? EnumFacing.EAST : connections.contains(EnumFacing.UP) && connections.contains(EnumFacing.DOWN) ? EnumFacing.UP : connections.contains(EnumFacing.SOUTH) && connections.contains(EnumFacing.NORTH) ? EnumFacing.SOUTH : null) : null;
		}

		@Override
		public ResourceLocation getMesh(){
			return getPart().meshes[getLine() != null ? 1 : 0];
		}

		@Override
		public NBTTagCompound serializeNBT(){
			NBTTagCompound nbt = super.serializeNBT();
			nbt.setIntArray("connections", connections.stream().mapToInt(EnumFacing::ordinal).toArray());
			return nbt;
		}

		@Override
		public void deserializeNBT(NBTTagCompound nbt){
			super.deserializeNBT(nbt);
			connections.clear();
			for(int i : nbt.getIntArray("connections")) connections.add(EnumFacing.values()[i]);
		}
	}

	public static class Normal extends PartFiber<Normal, NormalState> implements PartGroup<PartFiber.Normal, NormalState> {

		public Normal(PartColor color){
			super(true, color);
		}

		@Override
		public NormalState createNewState(){
			return new NormalState(this);
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

			@Override
			public NormalState.JointState createNewState(){
				return new NormalState.JointState(this);
			}
		}

	}

	public static class NormalState extends FiberState<Normal, NormalState> {

		public NormalState(PartFiber.Normal part){
			super(part);
		}

		public static class JointState extends FiberState<Normal.Joint, JointState> {

			public JointState(PartFiber.Normal.Joint part){
				super(part);
			}
		}

	}

}
