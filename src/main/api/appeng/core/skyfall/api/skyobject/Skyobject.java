package appeng.core.skyfall.api.skyobject;

public interface Skyobject<S extends Skyobject<S, P>, P extends SkyobjectProvider<S, P>> {

	P getProvider();

}
