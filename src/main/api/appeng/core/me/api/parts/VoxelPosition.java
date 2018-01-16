package appeng.core.me.api.parts;

import appeng.core.me.api.parts.container.GlobalVoxelsInfo;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.util.INBTSerializable;
import org.joml.Vector3d;

import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static appeng.core.me.api.parts.container.GlobalVoxelsInfo.*;

public final class VoxelPosition implements INBTSerializable<NBTTagCompound> {

	public static int floorModPos(int a, int b){
		int mod = Math.floorMod(a, b);
		return mod >= 0 ? mod : mod + b;
	}

	public static Stream<VoxelPosition> allVoxelsIn(AxisAlignedBB gbbox){
		return StreamSupport.stream(BlockPos.getAllInBox(new VoxelPosition(new Vec3d(gbbox.minX, gbbox.minY, gbbox.minZ)).getGlobalVoxelLevelPosition(), new VoxelPosition(new Vec3d(gbbox.maxX, gbbox.maxY, gbbox.maxZ)).getGlobalVoxelLevelPosition()).spliterator(), false).map(VoxelPosition::new);
	}

	private BlockPos globalPosition;
	private BlockPos localPosition;

	public VoxelPosition(BlockPos globalPosition, BlockPos localPosition){
		this.globalPosition = globalPosition.add(Math.floor(localPosition.getX() / VOXELSPERBLOCKAXISD), Math.floor(localPosition.getY() / VOXELSPERBLOCKAXISD), Math.floor(localPosition.getZ() / VOXELSPERBLOCKAXISD));
		this.localPosition = new BlockPos(floorModPos(localPosition.getX(), VOXELSPERBLOCKAXISI), floorModPos(localPosition.getY(), VOXELSPERBLOCKAXISI), floorModPos(localPosition.getZ(), VOXELSPERBLOCKAXISI));
	}

	public VoxelPosition(BlockPos localPosition){
		this(BlockPos.ORIGIN, localPosition);
	}

	public VoxelPosition(){
		this(BlockPos.ORIGIN);
	}

	public VoxelPosition(Vec3d position){
		this(new BlockPos(position), new BlockPos(position.subtract(new Vec3d(new BlockPos(position))).scale(VOXELSPERBLOCKAXISD)));
	}

	public BlockPos getGlobalPosition(){
		return globalPosition;
	}

	public BlockPos getLocalPosition(){
		return localPosition;
	}

	public BlockPos getGlobalVoxelLevelPosition(){
		return new BlockPos(globalPosition.getX() * VOXELSPERBLOCKAXISI, globalPosition.getY() * VOXELSPERBLOCKAXISI, globalPosition.getZ() * VOXELSPERBLOCKAXISI).add(localPosition);
	}

	public VoxelPosition add(VoxelPosition position){
		return new VoxelPosition(globalPosition.add(position.globalPosition), localPosition.add(position.localPosition));
	}

	public VoxelPosition asRelativeTo(VoxelPosition position){
		return new VoxelPosition(globalPosition.subtract(position.globalPosition), localPosition.subtract(position.localPosition));
	}

	public Vec3d asVec3d(){
		return new Vec3d(globalPosition).add(new Vec3d(localPosition).scale(VOXELSIZED));
	}

	public Vec3d globalCenterVec(){
		return asVec3d().addVector(VOXELSIZED2, VOXELSIZED2, VOXELSIZED2);
	}

	public Vector3d asVector3d(){
		return new Vector3d(globalPosition.getX(), globalPosition.getY(), globalPosition.getZ()).add(new Vector3d(localPosition.getX(), localPosition.getY(), localPosition.getZ()).mul(VOXELSIZED));
	}

	public Vector3d globalCenterVector(){
		return asVector3d().add(VOXELSIZED2, VOXELSIZED2, VOXELSIZED2);
	}

	public AxisAlignedBB getBB(){
		return GlobalVoxelsInfo.getVoxelBB(localPosition).offset(globalPosition);
	}

	public AxisAlignedBB translate(AxisAlignedBB box){
		return box.offset(asVec3d());
	}

	@Override
	public NBTTagCompound serializeNBT(){
		NBTTagCompound nbtTag = new NBTTagCompound();
		nbtTag.setTag("gpos", NBTUtil.createPosTag(globalPosition));
		nbtTag.setTag("lpos", NBTUtil.createPosTag(localPosition));
		return nbtTag;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt){
		globalPosition = NBTUtil.getPosFromTag(nbt.getCompoundTag("gpos"));
		localPosition = NBTUtil.getPosFromTag(nbt.getCompoundTag("lpos"));
	}

	public static VoxelPosition fromNBT(NBTTagCompound nbt){
		VoxelPosition pos = new VoxelPosition();
		pos.deserializeNBT(nbt);
		return pos;
	}

	@Override
	public boolean equals(Object o){
		if(this == o) return true;
		if(!(o instanceof VoxelPosition)) return false;

		VoxelPosition that = (VoxelPosition) o;

		if(!globalPosition.equals(that.globalPosition)) return false;
		return localPosition.equals(that.localPosition);
	}

	@Override
	public int hashCode(){
		int result = globalPosition.hashCode();
		result = 31 * result + localPosition.hashCode();
		return result;
	}

	@Override
	public String toString(){
		return "VoxelPosition{" + "globalPosition=" + globalPosition + ", localPosition=" + localPosition + '}';
	}
}
