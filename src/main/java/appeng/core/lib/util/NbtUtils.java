package appeng.core.lib.util;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.Vec3d;

public class NbtUtils {

	public static NBTTagCompound serializeVec3d(Vec3d vec){
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setDouble("x", vec.x);
		nbt.setDouble("y", vec.y);
		nbt.setDouble("z", vec.z);
		return nbt;
	}

	public static Vec3d deserializeVec3d(NBTTagCompound nbt){
		return new Vec3d(nbt.getDouble("x"), nbt.getDouble("y"), nbt.getDouble("z"));
	}

}
