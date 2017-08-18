package appeng.core.skyfall.skyobject;

public class Skyobject<S extends Skyobject<S, P>, P extends SkyobjectProvider<S, P>> implements appeng.core.skyfall.api.skyobject.Skyobject<S, P> {

	protected final P provider;

	public Skyobject(P provider){
		this.provider = provider;
	}

	@Override
	public P getProvider(){
		return provider;
	}

}
