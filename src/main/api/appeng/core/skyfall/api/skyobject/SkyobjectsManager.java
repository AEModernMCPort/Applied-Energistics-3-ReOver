package appeng.core.skyfall.api.skyobject;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.stream.Stream;

public interface SkyobjectsManager extends INBTSerializable<NBTTagCompound> {

	void tick(World world);

	Stream<Skyobject> getAllSkyobjects();

	//Commands

	void killall();

	void spawn();

}
