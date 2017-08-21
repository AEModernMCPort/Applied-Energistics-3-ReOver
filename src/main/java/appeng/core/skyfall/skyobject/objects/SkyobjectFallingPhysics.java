package appeng.core.skyfall.skyobject.objects;

import appeng.core.skyfall.api.skyobject.Skyobject;
import appeng.core.skyfall.api.skyobject.SkyobjectPhysics;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class SkyobjectFallingPhysics implements SkyobjectPhysics {

	protected final Skyobject skyobject;

	protected Vec3d pos;
	protected Vec3d rot;

	public SkyobjectFallingPhysics(Skyobject skyobject){
		this.skyobject = skyobject;
	}

	@Override
	public Vec3d getPos(){
		return pos;
	}

	@Override
	public Vec3d getRotation(){
		return rot;
	}

	@Override
	public void tick(World world){

	}

}
