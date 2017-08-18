package appeng.core.skyfall.api.skyobject;

import net.minecraft.util.ITickable;

public interface Skyobject<S extends Skyobject<S, P>, P extends SkyobjectProvider<S, P>> extends ITickable {

	P getProvider();

	boolean isDead();

}
