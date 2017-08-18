package appeng.core.skyfall.skyobject;

import net.minecraftforge.registries.IForgeRegistryEntry;

import java.util.function.Function;

public abstract class SkyobjectProviderImpl<S extends SkyobjectImpl<S, P>, P extends SkyobjectProviderImpl<S, P>> extends IForgeRegistryEntry.Impl<P> implements appeng.core.skyfall.api.skyobject.SkyobjectProvider<S, P> {

	protected Function<P, S> skyobjectSupplier;
	protected int defaultWeight;

	public SkyobjectProviderImpl(Function<P, S> skyobjectSupplier, int defaultWeight){
		this.skyobjectSupplier = skyobjectSupplier;
		this.defaultWeight = defaultWeight;
	}

	@Override
	public S get(){
		return skyobjectSupplier.apply((P) this);
	}

	@Override
	public int getDefaultWeight(){
		return defaultWeight;
	}
}
