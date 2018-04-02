package appeng.core.me.api.network.storage.caps;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.INBTSerializable;

public interface NetworkStorageSpace extends INBTSerializable<NBTTagCompound> {

	int getAllocated(ResourceLocation storage);
	int getFree(ResourceLocation storage);

	boolean occupy(ResourceLocation storage, int su);
	void free(ResourceLocation storage, int su);

}
