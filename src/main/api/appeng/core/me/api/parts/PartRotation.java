package appeng.core.me.api.parts;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.common.util.INBTSerializable;
import org.apache.commons.lang3.tuple.Pair;
import org.joml.*;

import java.lang.Math;

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

	public Matrix4f getRotationF(){
		return new Matrix4f().rotateTowards(new Vector3f(forward.getDirectionVec().getX(), forward.getDirectionVec().getY(), forward.getDirectionVec().getZ()), new Vector3f(up.getDirectionVec().getX(), up.getDirectionVec().getY(), up.getDirectionVec().getZ()));
	}

	public Matrix4d getRotationD(){
		return new Matrix4d().rotateTowards(new Vector3d(forward.getDirectionVec().getX(), forward.getDirectionVec().getY(), forward.getDirectionVec().getZ()), new Vector3d(up.getDirectionVec().getX(), up.getDirectionVec().getY(), up.getDirectionVec().getZ()));
	}

	private Vector3f toVecf(Vec3i vec){
		return new Vector3f(vec.getX(), vec.getY(), vec.getZ());
	}

	//TODO Mipa
	/*public PartRotation rotate(EnumFacing.Axis axis, EnumFacing.AxisDirection direction, int mulOfQuart){
		switch(axis){
			case X:
				return new PartRotation(new Quaternionf().rotateX((float) Math.toRadians(mulOfQuart * 90 * direction.getOffset())).mul(rotation));
			case Y:
				return new PartRotation(new Quaternionf().rotateY((float) Math.toRadians(mulOfQuart * 90 * direction.getOffset())).mul(rotation));
			case Z:
				return new PartRotation(new Quaternionf().rotateZ((float) Math.toRadians(mulOfQuart * 90 * direction.getOffset())).mul(rotation));
			default:
				return this;
		}
	}*/

	public Vector4f applyRotation(Vector4f vec){
		return getRotationF().transform(vec);
	}

	public Vector3f applyRotation(Vector3f vec){
		Vector4f result = applyRotation(new Vector4f(vec, 1));
		return new Vector3f(result.x, result.y, result.z);
	}

	public Vector4d applyRotation(Vector4d vec){
		return getRotationD().transform(vec);
	}

	public Vector3d applyRotation(Vector3d vec){
		Vector4d result = applyRotation(new Vector4d(vec, 1));
		return new Vector3d(result.x, result.y, result.z);
	}

	/**
	 * Rotates given argument around origin
	 * @param pos a position to rotate
	 * @return rotated position
	 */
	public BlockPos applyRotation(BlockPos pos){
		Vector4f res = applyRotation(new Vector4f(pos.getX(), pos.getY(), pos.getZ(), 1));
		return new BlockPos(Math.round(res.x * 100) / 100f, Math.round(res.y * 100) / 100f, Math.round(res.z * 100) / 100f);
	}

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
