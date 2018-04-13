package appeng.core.core.api.know;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;

public interface IKnow extends INBTSerializable<NBTTagCompound> {

	boolean isAware(String know);

	boolean doesKnow(String know);

	void learn(String know);

	void forget(String know);

}
