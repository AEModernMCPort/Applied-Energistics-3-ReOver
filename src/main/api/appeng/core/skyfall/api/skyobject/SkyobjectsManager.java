package appeng.core.skyfall.api.skyobject;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.UUID;
import java.util.stream.Stream;

public interface SkyobjectsManager extends INBTSerializable<NBTTagCompound> {

	void tick(World world);

	Stream<Skyobject> getAllSkyobjects();

	//Commands

	void killall();

	void spawn();

	interface WithDefaultSyncSupport extends SkyobjectsManager {

		void sendAll(EntityPlayerMP target);

		void receiveAddOrChange(UUID uuid, ResourceLocation id, NBTTagCompound nbt);

		void receiveRemove(UUID uuid);

	}

}
