package appeng.core.skyfall.api.skyobject;

import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public interface SkyobjectPhysics {

	Vec3d getPos();

	Vec3d getRotation();

	void tick(World world);

}
