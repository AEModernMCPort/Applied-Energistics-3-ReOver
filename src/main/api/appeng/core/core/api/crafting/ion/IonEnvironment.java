package appeng.core.core.api.crafting.ion;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fluids.Fluid;

import javax.annotation.Nonnull;

public interface IonEnvironment extends INBTSerializable<NBTTagCompound> {

	@Nonnull
	Fluid getEnvironment();



	Iterable<Ion> getIons();

	int getAmount(Ion ion);

	void addIons(Ion ion, int amount);

}
