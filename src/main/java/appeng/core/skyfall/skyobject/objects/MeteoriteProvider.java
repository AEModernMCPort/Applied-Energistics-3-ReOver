package appeng.core.skyfall.skyobject.objects;

import appeng.core.skyfall.skyobject.SkyobjectProvider;
import code.elix_x.excore.utils.world.MutableBlockAccess;

import java.util.Random;

public class MeteoriteProvider extends SkyobjectProvider<Meteorite, MeteoriteProvider> {

	public MeteoriteProvider(float defaultWeight){
		super(Meteorite::new, defaultWeight);
	}

	@Override
	public void generate(MutableBlockAccess world, Random random){

	}
}
