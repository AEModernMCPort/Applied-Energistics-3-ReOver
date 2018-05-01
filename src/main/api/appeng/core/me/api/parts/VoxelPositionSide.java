package appeng.core.me.api.parts;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.function.Function;

public final class VoxelPositionSide implements INBTSerializable<NBTTagCompound> {

	private VoxelPosition voxel;
	private EnumFacing side;

	public VoxelPositionSide(@Nonnull VoxelPosition voxel, @Nonnull EnumFacing side){
		this.voxel = voxel;
		this.side = side;
	}

	public VoxelPositionSide(){
		this(new VoxelPosition(), EnumFacing.DOWN);
	}

	public VoxelPosition getVoxel(){
		return voxel;
	}

	public EnumFacing getSide(){
		return side;
	}

	public VoxelPositionSide flipFromTo(){
		return new VoxelPositionSide(voxel.offsetLocal(side), side.getOpposite());
	}

	public VoxelPositionSide transformVoxel(Function<VoxelPosition, VoxelPosition> transform){
		return new VoxelPositionSide(transform.apply(voxel), side);
	}

	public VoxelPositionSide transformSide(Function<EnumFacing, EnumFacing> transform){
		return new VoxelPositionSide(voxel, transform.apply(side));
	}

	//IO & EH2S

	@Override
	public NBTTagCompound serializeNBT(){
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setTag("voxel", voxel.serializeNBT());
		nbt.setInteger("side", side.ordinal());
		return nbt;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt){
		voxel.deserializeNBT(nbt.getCompoundTag("voxel"));
		side = EnumFacing.values()[nbt.getInteger("side")];
	}

	public static VoxelPositionSide fromNBT(NBTTagCompound nbt){
		VoxelPositionSide vs = new VoxelPositionSide();
		vs.deserializeNBT(nbt);
		return vs;
	}

	@Override
	public boolean equals(Object o){
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;
		VoxelPositionSide that = (VoxelPositionSide) o;
		return Objects.equals(voxel, that.voxel) && side == that.side;
	}

	@Override
	public int hashCode(){
		return Objects.hash(voxel, side);
	}

	@Override
	public String toString(){
		return "VoxelPositionSide{" + "voxel=" + voxel + ", side=" + side + '}';
	}

}
