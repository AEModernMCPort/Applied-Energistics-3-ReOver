package appeng.core.skyfall.skyobject;

import net.minecraftforge.registries.IForgeRegistryEntry;

import java.util.function.Function;
import java.util.function.Supplier;

public abstract class SkyobjectProvider<S extends Skyobject<S, P>, P extends SkyobjectProvider<S, P>> extends IForgeRegistryEntry.Impl<P> implements appeng.core.skyfall.api.skyobject.SkyobjectProvider<S, P> {

	protected Function<P, S> skyobjectSupplier;
	protected float defaultWeight;

	public SkyobjectProvider(Function<P, S> skyobjectSupplier, float defaultWeight){
		this.skyobjectSupplier = skyobjectSupplier;
		this.defaultWeight = defaultWeight;
	}

	@Override
	public S get(){
		return skyobjectSupplier.apply((P) this);
	}

	@Override
	public float getDefaultWeight(){
		return defaultWeight;
	}
}
