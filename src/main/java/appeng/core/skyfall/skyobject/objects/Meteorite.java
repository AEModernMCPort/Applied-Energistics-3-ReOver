package appeng.core.skyfall.skyobject.objects;

import appeng.core.skyfall.skyobject.SkyobjectImpl;
import net.minecraft.world.World;

public class Meteorite extends SkyobjectImpl<Meteorite, MeteoriteProvider> {

	public Meteorite(MeteoriteProvider provider){
		super(provider);
	}

	@Override
	public void tick(World world){

	}

}
