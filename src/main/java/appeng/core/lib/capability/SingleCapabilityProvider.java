package appeng.core.lib.capability;

import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SingleCapabilityProvider<T> implements ICapabilityProvider {

	private final Capability<T> capability;
	private final T value;

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

}
