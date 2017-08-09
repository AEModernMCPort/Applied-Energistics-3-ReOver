package appeng.core.lib.capability;

import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nullable;

public class DelegateCapabilityStorage<T extends INBTSerializable> implements Capability.IStorage<T> {

	@Nullable
	@Override
	public NBTBase writeNBT(Capability<T> capability, T instance, EnumFacing side){
		return instance.serializeNBT();
	}

	@Override
	public void readNBT(Capability<T> capability, T instance, EnumFacing side, NBTBase nbt){
		instance.deserializeNBT(nbt);
	}
}
