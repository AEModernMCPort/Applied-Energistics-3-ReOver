package appeng.core.me.api.parts;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;

public final class PartPositionRotation implements INBTSerializable<NBTTagCompound> {

	private VoxelPosition position;
	private PartRotation rotation;

	public PartPositionRotation(VoxelPosition position, PartRotation rotation){
		this.position = position;
		this.rotation = rotation;
	}

	public PartPositionRotation(PartRotation rotation, VoxelPosition rotationCenterPosition){
		this(rotationCenterPosition.add(rotation.rotate(new VoxelPosition())), rotation);
	}

	PartPositionRotation(){
		this(new VoxelPosition(), new PartRotation());
	}

	public VoxelPosition getPosition(){
		return position;
	}

	public PartRotation getRotation(){
		return rotation;
	}

	public VoxelPosition getRotationCenterPosition(){
		return position.substract(rotation.rotate(new VoxelPosition()));
	}

	public PartPositionRotation addPos(VoxelPosition position){
		return new PartPositionRotation(this.position.add(position), rotation);
	}

	public PartPositionRotation addPos(PartPositionRotation positionRotation){
		return addPos(positionRotation.position);
	}

	public PartPositionRotation posAsRelativeTo(VoxelPosition position){
		return new PartPositionRotation(this.position.substract(position), rotation);
	}

	public PartPositionRotation posAsRelativeTo(PartPositionRotation positionRotation){
		return posAsRelativeTo(positionRotation.position);
	}

	//Transform

	public VoxelPosition transform(VoxelPosition voxel){
		return rotation.rotate(voxel).add(getRotationCenterPosition());
	}

	public VoxelPositionSide transform(VoxelPositionSide voxelPositionSide){
		return rotation.rotate(voxelPositionSide).transformVoxel(v -> v.add(getRotationCenterPosition()));
	}

	public VoxelPosition untransform(VoxelPosition rvoxel){
		return rotation.inverse().rotate(rvoxel.substract(getRotationCenterPosition()));
	}

	public VoxelPositionSide untransform(VoxelPositionSide voxelPositionSide){
		return rotation.inverse().rotate(voxelPositionSide.transformVoxel(v -> v.substract(getRotationCenterPosition())));
	}

	@Override
	public NBTTagCompound serializeNBT(){
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setTag("pos", position.serializeNBT());
		nbt.setTag("rot", rotation.serializeNBT());
		return nbt;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt){
		position.deserializeNBT(nbt.getCompoundTag("pos"));
		rotation.deserializeNBT(nbt.getCompoundTag("rot"));
	}

	public static PartPositionRotation fromNBT(NBTTagCompound nbt){
		PartPositionRotation positionRotation = new PartPositionRotation();
		positionRotation.deserializeNBT(nbt);
		return positionRotation;
	}

	@Override
	public boolean equals(Object o){
		if(this == o) return true;
		if(!(o instanceof PartPositionRotation)) return false;

		PartPositionRotation that = (PartPositionRotation) o;

		if(!position.equals(that.position)) return false;
		return rotation.equals(that.rotation);
	}

	@Override
	public int hashCode(){
		int result = position.hashCode();
		result = 31 * result + rotation.hashCode();
		return result;
	}

	@Override
	public String toString(){
		return "PartPositionRotation{" + "position=" + position + ", rotation=" + rotation + '}';
	}
}
