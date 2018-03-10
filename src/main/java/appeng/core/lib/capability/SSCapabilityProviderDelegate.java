package appeng.core.lib.capability;

import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SSCapabilityProviderDelegate implements ICapabilityProvider {

	protected final ICapabilityProvider delegate;
	protected final EnumFacing sideDelegateTo;

	public SSCapabilityProviderDelegate(@Nonnull ICapabilityProvider delegate, @Nullable EnumFacing sideDelegateTo){
		this.delegate = delegate;
		this.sideDelegateTo = sideDelegateTo;
	}

	@Override
	public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing){
		return delegate.hasCapability(capability, sideDelegateTo);
	}

	@Nullable
	@Override
	public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing){
		return delegate.getCapability(capability, sideDelegateTo);
	}

}
