package appeng.core.core.api.know;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.Optional;

public interface IKnow extends INBTSerializable<NBTTagCompound> {

	boolean isAware(String know);

	boolean doesKnow(String know);

	void learn(String know);

	void forget(String know);

	<K extends INBTSerializable<NBTTagCompound>> Optional<K> getSpecialKnow(String specialKnow);

}
