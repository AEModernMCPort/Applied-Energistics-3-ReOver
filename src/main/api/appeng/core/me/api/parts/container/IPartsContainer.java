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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.stream.Stream;

import static appeng.core.me.api.parts.container.GlobalVoxelsInfo.VOXELSPERBLOCKAXISI;

public interface IPartsContainer extends PartsAccess.Mutable, INBTSerializable<NBTTagCompound> {

	//Link with outside world

	World getWorld();
	BlockPos getGlobalPosition();

	void setWorld(World world);
	void setGlobalPosition(BlockPos pos);

	@Nonnull
	default VoxelPosition getGlobalOriginVoxelPosition(){
		return new VoxelPosition(getGlobalPosition(), BlockPos.ORIGIN);
	}

	@Nonnull
	default VoxelPosition getGlobalCenterVoxelPosition(){
		return new VoxelPosition(getGlobalPosition(), new BlockPos(VOXELSPERBLOCKAXISI / 2, VOXELSPERBLOCKAXISI / 2, VOXELSPERBLOCKAXISI / 2));
	}

	//Owned parts

	@Nonnull
	Map<Part.State, PartPositionRotation> getOwnedParts();

	//Voxel information

	boolean hasPart(@Nonnull BlockPos voxel);
	default boolean isEmpty(@Nonnull BlockPos voxel){
		return !hasPart(voxel);
	}
	@Nullable VoxelPosition get(@Nonnull BlockPos voxel);
	boolean canPlace(@Nonnull VoxelPosition part, @Nonnull Stream<BlockPos> voxels);
	void set(@Nonnull VoxelPosition part, @Nonnull Stream<BlockPos> voxels);
	void remove(@Nonnull VoxelPosition part, @Nonnull Stream<BlockPos> voxels);

	boolean isEmpty();

	//Ray tracing

	@Nullable
	RayTraceResult rayTrace(@Nonnull Vec3d start, @Nonnull Vec3d end);

}
