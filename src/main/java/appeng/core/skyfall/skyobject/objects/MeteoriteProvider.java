package appeng.core.skyfall.skyobject.objects;

import appeng.core.skyfall.skyobject.SkyobjectProviderImpl;

public class MeteoriteProvider extends SkyobjectProviderImpl<Meteorite, MeteoriteProvider> {

	public MeteoriteProvider(int defaultWeight){
		super(Meteorite::new, defaultWeight);
	}

	@Override
	public Meteorite generate(long seed){
		return get();
	}
}
