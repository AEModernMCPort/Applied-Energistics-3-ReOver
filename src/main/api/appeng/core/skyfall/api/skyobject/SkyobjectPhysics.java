package appeng.core.skyfall.api.skyobject;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.util.INBTSerializable;

public interface SkyobjectPhysics extends INBTSerializable<NBTTagCompound> {

	Vec3d getPos();

	Vec3d getRotation();

	boolean tick(World world);

}
