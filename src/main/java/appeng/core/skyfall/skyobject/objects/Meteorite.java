package appeng.core.skyfall.skyobject.objects;

import appeng.core.lib.world.ExpandleMutableBlockAccess;

public class Meteorite extends SkyobjectFalling<Meteorite, MeteoriteProvider> {

	ExpandleMutableBlockAccess world;

	public Meteorite(MeteoriteProvider provider){
		super(provider);
	}

}
