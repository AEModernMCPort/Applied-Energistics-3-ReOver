package appeng.core.skyfall.skyobject;

import net.minecraftforge.registries.IForgeRegistryEntry;

public abstract class SkyobjectProvider<S extends Skyobject<S, P>, P extends SkyobjectProvider<S, P>> extends IForgeRegistryEntry.Impl<P> implements appeng.core.skyfall.api.skyobject.SkyobjectProvider<S, P> {

	private float defaultWeight;

	public SkyobjectProvider(float defaultWeight){
		this.defaultWeight = defaultWeight;
	}

	@Override
	public float getDefaultWeight(){
		return defaultWeight;
	}
}
