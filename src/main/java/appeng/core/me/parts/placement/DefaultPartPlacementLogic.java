package appeng.core.me.parts.placement;

import appeng.core.me.AppEngME;
import appeng.core.me.api.parts.PartPositionRotation;
import appeng.core.me.api.parts.PartRotation;
import appeng.core.me.api.parts.VoxelPosition;
import appeng.core.me.api.parts.part.Part;
import appeng.core.me.api.parts.placement.PartPlacementLogic;
import appeng.core.me.api.parts.placement.VoxelRayTraceHelper;
import appeng.core.me.parts.part.PartsHelperImpl;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.*;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

public class DefaultPartPlacementLogic<P extends Part<P, S>, S extends Part.State<P, S>> implements PartPlacementLogic {

	protected final P part;

	public DefaultPartPlacementLogic(P part){
		this.part = part;
	}

	protected PartsHelperImpl partsHelper(){
		return AppEngME.INSTANCE.getPartsHelper();
	}

	protected boolean supportsRotation(){
		return partsHelper().supportsRotation(part);
	}

	@Override
	public PartPositionRotation getPlacementPosition(EntityPlayer player, RayTraceResult rayTrace){
		AxisAlignedBB bbox = partsHelper().getPartVoxelBB(part);
		VoxelPosition hit = VoxelRayTraceHelper.getOrApproximateHitVoxel(rayTrace);
		PartRotation rotation;
		VoxelPosition position = hit;
		AxisAlignedBB offsetBB;
		if(supportsRotation()){
			rotation = new PartRotation(getForwardUp(player, rayTrace.sideHit));
			offsetBB = partsHelper().applyTransforms(bbox, new PartPositionRotation(new VoxelPosition(), rotation));
		} else {
			rotation = new PartRotation();
			offsetBB = bbox;
		}

		Vec3i offsetByBox;
		switch(rayTrace.sideHit){
			case DOWN:
				offsetByBox = new Vec3i(0, 0 - offsetBB.maxY, 0);
				break;
			case UP:
				offsetByBox = new Vec3i(0, 0 - offsetBB.minY + 1, 0);
				break;
			case NORTH:
				offsetByBox = new Vec3i(0, 0, 0 - offsetBB.maxZ);
				break;
			case SOUTH:
				offsetByBox = new Vec3i(0, 0, 0 - offsetBB.minZ + 1);
				break;
			case WEST:
				offsetByBox = new Vec3i(0 - offsetBB.maxX, 0, 0);
				break;
			case EAST:
				offsetByBox = new Vec3i(0 - offsetBB.minX + 1, 0, 0);
				break;
			default:
				offsetByBox = new Vec3i(0, 0, 0);
		}

		position = position.add(new VoxelPosition(new BlockPos(offsetByBox)));
		return new PartPositionRotation(position, rotation);
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
		EnumFacing forward = sideHit.getOpposite();
		EnumFacing up = forward == EnumFacing.UP ? player.getHorizontalFacing() : forward == EnumFacing.DOWN ? player.getHorizontalFacing().getOpposite() : EnumFacing.UP;
		return new ImmutablePair<>(forward, up);
	}

}