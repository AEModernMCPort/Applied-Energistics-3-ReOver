package appeng.core.me.api.parts;

import appeng.core.me.api.parts.container.GlobalVoxelsInfo;
import com.google.common.collect.AbstractIterator;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.EnumFacing;
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

	public static Iterable<VoxelPosition> allVoxelsInBox(final AxisAlignedBB box){
		final VoxelPosition min = new VoxelPosition(new Vec3d(box.minX, box.minY, box.minZ));
		final VoxelPosition max = new VoxelPosition(new Vec3d(box.maxX, box.maxY, box.maxZ));
		final double dX = box.maxX - box.minX;
		final double dY = box.maxY - box.minY;
		final double dZ = box.maxZ - box.minZ;
		return () -> new AbstractIterator<VoxelPosition>() {

			VoxelPosition previousRel;

			@Override
			protected VoxelPosition computeNext(){
				if(previousRel == null){
					previousRel = new VoxelPosition();
				} else {
					Vec3d prevVec = previousRel.asVec3d();
					if(prevVec.x < dX) previousRel = previousRel.add(new VoxelPosition(new BlockPos(1, 0, 0)));
					else if(prevVec.y < dY)
						previousRel = new VoxelPosition(new Vec3d(0, prevVec.y, prevVec.z)).add(new VoxelPosition(new BlockPos(0, 1, 0)));
					else if(prevVec.z < dZ)
						previousRel = new VoxelPosition(new Vec3d(0, 0, prevVec.z)).add(new VoxelPosition(new BlockPos(0, 0, 1)));
					else return endOfData();
				}
				return previousRel.add(min);
			}

		};
	}

	public static Stream<VoxelPosition> allVoxelsIn(AxisAlignedBB gbbox){
		return StreamSupport.stream(allVoxelsInBox(gbbox).spliterator(), false);
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

	//Transform

	public VoxelPosition add(VoxelPosition position){
		return new VoxelPosition(globalPosition.add(position.globalPosition), localPosition.add(position.localPosition));
	}

	public VoxelPosition substract(VoxelPosition position){
		return new VoxelPosition(globalPosition.subtract(position.globalPosition), localPosition.subtract(position.localPosition));
	}

	public VoxelPosition offsetLocal(EnumFacing direction){
		return new VoxelPosition(globalPosition, localPosition.offset(direction));
	}

	//Local access & convert

	public BlockPos getGlobalPosition(){
		return globalPosition;
	}

	public BlockPos getLocalPosition(){
		return localPosition;
	}

	/**
	 * Global position of origin of this voxel
	 *
	 * @return global position of origin of this voxel
	 */
	public Vec3d asVec3d(){
		return new Vec3d(globalPosition).add(new Vec3d(localPosition).scale(VOXELSIZED));
	}

	/**
	 * Global position of center of this voxel
	 *
	 * @return global position of center of this voxel
	 */
	public Vec3d globalCenterVec(){
		return asVec3d().addVector(VOXELSIZED2, VOXELSIZED2, VOXELSIZED2);
	}

	/**
	 * Global position of origin of this voxel
	 *
	 * @return global position of origin of this voxel
	 */
	public Vector3d asVector3d(){
		return new Vector3d(globalPosition.getX(), globalPosition.getY(), globalPosition.getZ()).add(new Vector3d(localPosition.getX(), localPosition.getY(), localPosition.getZ()).mul(VOXELSIZED));
	}

	/**
	 * Global position of center of this voxel
	 *
	 * @return global position of center of this voxel
	 */
	public Vector3d globalCenterVector(){
		return asVector3d().add(VOXELSIZED2, VOXELSIZED2, VOXELSIZED2);
	}

	/**
	 * Global bounds of this voxel
	 *
	 * @return global bounds of this voxel
	 */
	public AxisAlignedBB getBB(){
		return GlobalVoxelsInfo.getVoxelBB(localPosition).offset(globalPosition);
	}

	//Translate

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
