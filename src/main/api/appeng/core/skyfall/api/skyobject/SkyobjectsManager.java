package appeng.core.skyfall.api.skyobject;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.INBTSerializable;

public interface SkyobjectsManager extends INBTSerializable<NBTTagCompound> {

	void tick(World world);

}
