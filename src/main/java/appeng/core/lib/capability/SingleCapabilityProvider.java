package appeng.core.lib.capability;

import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SingleCapabilityProvider<T> implements ICapabilityProvider {

	protected final Capability<T> capability;
	protected final T value;

	public SingleCapabilityProvider(Capability<T> capability, T value){
		this.capability = capability;
		this.value = value;
	}

	@Override
	public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing){
		return capability == this.capability;
	}

	@Nullable
	@Override
	public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing){
		return capability == this.capability ? (T) value : null;
	}


	public static class Serializeable<T> extends SingleCapabilityProvider<T> implements INBTSerializable<NBTBase> {

		public Serializeable(Capability<T> capability, T value){
			super(capability, value);
		}

		@Override
		public NBTBase serializeNBT(){
			return capability.writeNBT(value, null);
		}

		@Override
		public void deserializeNBT(NBTBase nbt){
			capability.readNBT(value, null, nbt);
		}

	}

}
