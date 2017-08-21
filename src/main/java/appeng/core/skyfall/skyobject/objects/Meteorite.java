package appeng.core.skyfall.skyobject.objects;

import appeng.core.lib.world.ExpandleMutableBlockAccess;
import appeng.core.skyfall.skyobject.SkyobjectImpl;
import net.minecraft.world.World;

public class Meteorite extends SkyobjectImpl<Meteorite, MeteoriteProvider> {

	ExpandleMutableBlockAccess world;

	public Meteorite(MeteoriteProvider provider){
		super(provider);
	}

	@Override
	public void tick(World world){

	}

}
