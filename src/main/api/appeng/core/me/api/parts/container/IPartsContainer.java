package appeng.core.me.api.parts.container;

import appeng.core.me.api.parts.PartPositionRotation;
import appeng.core.me.api.parts.VoxelPosition;
import appeng.core.me.api.parts.part.Part;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.Map;
import java.util.stream.Stream;

import static appeng.core.me.api.parts.container.GlobalVoxelsInfo.VOXELSPERBLOCKAXISI;

public interface IPartsContainer extends PartsAccess.Mutable, INBTSerializable<NBTTagCompound> {

	//Link with outside world

	World getWorld();
	BlockPos getGlobalPosition();

	void setWorld(World world);
	void setGlobalPosition(BlockPos pos);

	default VoxelPosition getGlobalOriginVoxelPosition(){
		return new VoxelPosition(getGlobalPosition(), BlockPos.ORIGIN);
	}

	default VoxelPosition getGlobalCenterVoxelPosition(){
		return new VoxelPosition(getGlobalPosition(), new BlockPos(VOXELSPERBLOCKAXISI / 2, VOXELSPERBLOCKAXISI / 2, VOXELSPERBLOCKAXISI / 2));
	}

	//Owned parts

	Map<Part.State, PartPositionRotation> getOwnedParts();

	//Voxel information

	boolean hasPart(BlockPos voxel);
	default boolean isEmpty(BlockPos voxel){
		return !hasPart(voxel);
	}
	VoxelPosition get(BlockPos voxel);
	boolean canPlace(VoxelPosition part, Stream<BlockPos> voxels);
	void set(VoxelPosition part, Stream<BlockPos> voxels);
	void remove(VoxelPosition part, Stream<BlockPos> voxels);

	boolean isEmpty();

	//Ray tracing

	RayTraceResult rayTrace(Vec3d start, Vec3d end);

}
