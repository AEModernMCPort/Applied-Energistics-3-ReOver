package appeng.core.skyfall.skyobject.objects;

import appeng.core.skyfall.skyobject.SkyobjectImpl;
import code.elix_x.excore.utils.world.MutableBlockAccess;
import net.minecraft.world.World;

public class Meteorite extends SkyobjectImpl<Meteorite, MeteoriteProvider> {

	MutableBlockAccess world;

	public Meteorite(MeteoriteProvider provider){
		super(provider);
	}

	@Override
	public void tick(World world){

	}

}
