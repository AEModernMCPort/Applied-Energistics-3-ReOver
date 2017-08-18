package appeng.core.skyfall.skyobject;

import appeng.core.skyfall.api.skyobject.Skyobject;

public class SkyobjectImpl<S extends SkyobjectImpl<S, P>, P extends SkyobjectProviderImpl<S, P>> implements Skyobject<S, P> {

	protected final P provider;

	public SkyobjectImpl(P provider){
		this.provider = provider;
	}

	@Override
	public P getProvider(){
		return provider;
	}

}
