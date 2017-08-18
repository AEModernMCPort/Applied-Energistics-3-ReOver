package appeng.core.skyfall.skyobject;

import appeng.core.skyfall.api.skyobject.Skyobject;

public abstract class SkyobjectImpl<S extends SkyobjectImpl<S, P>, P extends SkyobjectProviderImpl<S, P>> implements Skyobject<S, P> {

	protected final P provider;

	protected boolean dead;

	public SkyobjectImpl(P provider){
		this.provider = provider;
	}

	@Override
	public P getProvider(){
		return provider;
	}

	@Override
	public boolean isDead(){
		return dead;
	}

}
