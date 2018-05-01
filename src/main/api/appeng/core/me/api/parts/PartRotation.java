package appeng.core.me.api.parts;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.common.util.INBTSerializable;
import org.apache.commons.lang3.tuple.Pair;
import org.joml.*;

import java.lang.Math;
import java.util.Arrays;
import java.util.stream.Stream;

public final class PartRotation implements INBTSerializable<NBTTagCompound> {

	private EnumFacing forward;
	private EnumFacing up;

	public PartRotation(EnumFacing forward, EnumFacing up){
		this.forward = forward;
		this.up = up;
	}

	public PartRotation(Pair<EnumFacing, EnumFacing> facings){
		this(facings.getLeft(), facings.getRight());
	}

	public PartRotation(){
		this(EnumFacing.SOUTH, EnumFacing.UP);
	}

	//Transform

	public PartRotation inverse(){
		//TODO I am pretty sure there is a much better way...
		Matrix4f nm = getRotationF().invertAffine();
		Vector4f fwd = nm.transform(new Vector4f(0, 0, 1, 0));
		Vector4f u = nm.transform(new Vector4f(0, 1, 0, 0));
		return new PartRotation(EnumFacing.getFacingFromVector(fwd.x, fwd.y, fwd.z), EnumFacing.getFacingFromVector(u.x, u.y, u.z));
	}

	/**
	 * Like matrix multiplication - first applies <tt>rot</tt>, then <tt>this</tt>.
	 *
	 * @param rot rotation to combine with
	 * @return combined rotation
	 */
	public PartRotation mul(PartRotation rot){
		return new PartRotation(rot.rotate(forward), rot.rotate(up));
	}

	public PartRotation andThen(PartRotation rot){
		return rot.mul(this);
	}

	//Export as matrix

	public Matrix4f getRotationF(){
		return new Matrix4f().rotateTowards(new Vector3f(forward.getDirectionVec().getX(), forward.getDirectionVec().getY(), forward.getDirectionVec().getZ()), new Vector3f(up.getDirectionVec().getX(), up.getDirectionVec().getY(), up.getDirectionVec().getZ()));
	}

	public Matrix4d getRotationD(){
		return new Matrix4d().rotateTowards(new Vector3d(forward.getDirectionVec().getX(), forward.getDirectionVec().getY(), forward.getDirectionVec().getZ()), new Vector3d(up.getDirectionVec().getX(), up.getDirectionVec().getY(), up.getDirectionVec().getZ()));
	}

	//Rotate

	private Vector3f toVecf(Vec3i vec){
		return new Vector3f(vec.getX(), vec.getY(), vec.getZ());
	}

	public Vector4f rotate(Vector4f vec){
		return getRotationF().transform(vec);
	}

	public Vector3f rotate(Vector3f vec){
		Vector4f result = rotate(new Vector4f(vec, 1));
		return new Vector3f(result.x, result.y, result.z);
	}

	public Vector4d rotate(Vector4d vec){
		return getRotationD().transform(vec);
	}

	public Vector3d rotate(Vector3d vec){
		Vector4d result = rotate(new Vector4d(vec, 1));
		return new Vector3d(result.x, result.y, result.z);
	}

	public EnumFacing rotate(EnumFacing dir){
		Vector3f rot = rotate(toVecf(dir.getDirectionVec()));
		return EnumFacing.getFacingFromVector(rot.x, rot.y, rot.z);
	}

	/**
	 * Rotates given argument around origin
	 *
	 * @param pos a position to rotate
	 * @return rotated position
	 */
	public BlockPos rotate(BlockPos pos){
		Vector4f res = rotate(new Vector4f(pos.getX(), pos.getY(), pos.getZ(), 1));
		return new BlockPos(Math.round(res.x * 100) / 100f, Math.round(res.y * 100) / 100f, Math.round(res.z * 100) / 100f);
	}

	public AxisAlignedBB rotate(AxisAlignedBB box){
		Vector3d first = rotate(new Vector3d(box.minX, box.minY, box.minZ));
		Vector3d second = rotate(new Vector3d(box.maxX, box.maxY, box.maxZ));
		return new AxisAlignedBB(first.x, first.y, first.z, second.x, second.y, second.z);
	}

	/**
	 * Rotates given <i>voxel</i> by this rotation (around origin).<br>
	 * Note: Voxel is a volumetric object, an entire box. As such, rotation of Voxel(0,0,0) is possible and outputs different result from (0,0,0)
	 *
	 * @param voxel voxel to rotate
	 * @return rotated <i>voxel</i>
	 */
	public VoxelPosition rotate(VoxelPosition voxel){
		return new VoxelPosition(rotate(voxel.getBB()).getCenter());
	}

	public VoxelPositionSide rotate(VoxelPositionSide voxelPositionSide){
		return new VoxelPositionSide(rotate(voxelPositionSide.getVoxel()), rotate(voxelPositionSide.getSide()));
	}

	//IO

	@Override
	public NBTTagCompound serializeNBT(){
		NBTTagCompound nbt = new NBTTagCompound();
		/*nbt.setFloat("x", rotation.x());
		nbt.setFloat("y", rotation.y());
		nbt.setFloat("z", rotation.z());
		nbt.setFloat("w", rotation.w());*/
		nbt.setInteger("forward", forward.ordinal());
		nbt.setInteger("up", up.ordinal());
		return nbt;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt){
		//rotation = new Quaternionf(nbt.getFloat("x"), nbt.getFloat("y"), nbt.getFloat("z"), nbt.getFloat("w"));
		forward = EnumFacing.values()[nbt.getInteger("forward")];
		up = EnumFacing.values()[nbt.getInteger("up")];
	}

	public static PartRotation fromNBT(NBTTagCompound nbt){
		PartRotation rotation = new PartRotation();
		rotation.deserializeNBT(nbt);
		return rotation;
	}

	//Util

	public static Stream<PartRotation> allPossibleRotations(){
		return Arrays.stream(EnumFacing.values()).flatMap(front -> Arrays.stream(EnumFacing.values()).filter(up -> front.getAxis() != up.getAxis()).map(up -> new PartRotation(front, up)));
	}

	//...

	@Override
	public boolean equals(Object o){
		if(this == o) return true;
		if(!(o instanceof PartRotation)) return false;

		PartRotation rotation = (PartRotation) o;

		if(forward != rotation.forward) return false;
		return up == rotation.up;
	}

	@Override
	public int hashCode(){
		int result = forward.hashCode();
		result = 31 * result + up.hashCode();
		return result;
	}

	@Override
	public String toString(){
		return "PartRotation{" + "forward=" + forward + ", up=" + up + '}';
	}
}
