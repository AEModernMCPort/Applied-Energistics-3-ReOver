package appeng.core.me.parts.placement;

import appeng.core.me.AppEngME;
import appeng.core.me.api.parts.PartPositionRotation;
import appeng.core.me.api.parts.PartRotation;
import appeng.core.me.api.parts.VoxelPosition;
import appeng.core.me.api.parts.part.Part;
import appeng.core.me.api.parts.placement.PartPlacementLogic;
import appeng.core.me.api.parts.placement.VoxelRayTraceHelper;
import appeng.core.me.parts.part.PartsHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

public class DefaultPartPlacementLogic<P extends Part<P, S>, S extends Part.State<P, S>> implements PartPlacementLogic {

	protected final P part;

	public DefaultPartPlacementLogic(P part){
		this.part = part;
	}

	protected PartsHelper partsHelper(){
		return AppEngME.INSTANCE.getPartsHelper();
	}

	protected boolean supportsRotation(){
		return partsHelper().supportsRotation(part);
	}

	@Override
	public PartPositionRotation getPlacementPosition(EntityPlayer player, RayTraceResult rayTrace){
		AxisAlignedBB bbox = partsHelper().getPartVoxelBB(part);
		VoxelPosition hit = VoxelRayTraceHelper.getOrApproximateHitVoxel(rayTrace);
		if(supportsRotation()){
			PartRotation rotation = new PartRotation(getForwardUp(player, rayTrace.sideHit));
			AxisAlignedBB rbbox = partsHelper().applyTransforms(bbox, new PartPositionRotation(new VoxelPosition(), rotation));
			return new PartPositionRotation(offsetByBBox(hit, rayTrace.sideHit, rbbox), rotation);
		} else {
			return new PartPositionRotation(offsetByBBox(hit, rayTrace.sideHit, bbox), PartsHelper.noRotation);
		}
	}

	protected VoxelPosition offsetByBBox(VoxelPosition voxel, EnumFacing sideHit, AxisAlignedBB voxelBBox){
		AxisAlignedBB original = new AxisAlignedBB(voxel.getLocalPosition());
		voxelBBox = voxelBBox.offset(voxel.getLocalPosition());
		while(voxelBBox.intersects(original)){
			voxel = voxel.add(new VoxelPosition(new BlockPos(sideHit.getDirectionVec())));
			voxelBBox = voxelBBox.offset(new Vec3d(sideHit.getDirectionVec()));
		}
		return voxel;
	}

	protected Pair<EnumFacing, EnumFacing> getForwardUp(EntityPlayer player, EnumFacing sideHit){
		EnumFacing forward = sideHit;
		EnumFacing up = forward == EnumFacing.UP ? player.getHorizontalFacing() : forward == EnumFacing.DOWN ? player.getHorizontalFacing().getOpposite() : EnumFacing.UP;
		return new ImmutablePair<>(forward, up);
	}

}