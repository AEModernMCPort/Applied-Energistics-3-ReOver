package appeng.core.skyfall.skyobject.objects;

import appeng.core.skyfall.skyobject.SkyobjectProviderImpl;
import code.elix_x.excore.utils.world.MutableBlockAccess;

public class MeteoriteProvider extends SkyobjectProviderImpl<Meteorite, MeteoriteProvider> {

	public MeteoriteProvider(int defaultWeight){
		super(Meteorite::new, defaultWeight);
	}

	@Override
	public void generate(MutableBlockAccess world, long seed){

	}
}
