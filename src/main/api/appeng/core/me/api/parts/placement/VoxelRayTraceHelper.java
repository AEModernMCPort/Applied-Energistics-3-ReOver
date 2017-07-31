package appeng.core.me.api.parts.placement;

import appeng.core.me.api.parts.VoxelPosition;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

import static appeng.core.me.api.parts.container.GlobalVoxelsInfo.*;

public class VoxelRayTraceHelper {

	public static boolean hitsVoxel(RayTraceResult rayTraceResult){
		return rayTraceResult.hitInfo instanceof VoxelPosition;
	}

	public static VoxelPosition getTargetVoxel(RayTraceResult rayTraceResult){
		return (VoxelPosition) rayTraceResult.hitInfo;
	}

	public static RayTraceResult setTargetVoxel(RayTraceResult rayTraceResult, VoxelPosition voxelPosition){
		rayTraceResult.hitInfo = voxelPosition;
		return rayTraceResult;
	}

	public static VoxelPosition getOrApproximateHitVoxel(RayTraceResult rayTraceResult){
		return hitsVoxel(rayTraceResult) ? getTargetVoxel(rayTraceResult) : new VoxelPosition(rayTraceResult.hitVec.add(new Vec3d(rayTraceResult.sideHit.getDirectionVec()).scale(-VOXELSIZED2/2)));
	}

}
