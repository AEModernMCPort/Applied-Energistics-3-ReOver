package appeng.core.skyfall.skyobject;

public class SkyobjectImpl<S extends SkyobjectImpl<S, P>, P extends SkyobjectProvider<S, P>> implements appeng.core.skyfall.api.skyobject.Skyobject<S, P> {

	protected final P provider;

	public SkyobjectImpl(P provider){
		this.provider = provider;
	}

	@Override
	public P getProvider(){
		return provider;
	}

}
