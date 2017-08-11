package appeng.core.core.api.crafting.ion;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fluids.Fluid;

import javax.annotation.Nonnull;
import java.util.Map;

public interface IonEnvironment extends INBTSerializable<NBTTagCompound> {

	@Nonnull
	Fluid getEnvironment();



	Map<Ion, Integer> getIons();

	int getAmount(Ion ion);

	void addIons(Ion ion, int amount);

}
