package appeng.core.core.crafting.ion;

import net.minecraft.nbt.NBTTagInt;
import net.minecraftforge.common.util.INBTSerializable;

public class InWorldIonEnvTemperatureListener implements INBTSerializable<NBTTagInt> {

	public int temp;

	@Override
	public NBTTagInt serializeNBT(){
		return new NBTTagInt(temp);
	}

	@Override
	public void deserializeNBT(NBTTagInt nbt){
		temp = nbt.getInt();
	}
}
