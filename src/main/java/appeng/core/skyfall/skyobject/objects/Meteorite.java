package appeng.core.skyfall.skyobject.objects;

import appeng.core.skyfall.AppEngSkyfall;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

public class Meteorite extends SkyobjectFalling<Meteorite, MeteoriteProvider> {

	public Meteorite(MeteoriteProvider provider){
		super(provider);
	}

	@Override
	protected Pair<Vec3d, Vec3d> calcSpawnPosForce(World world){
		Vec3d pos = triangulateStartPos(new Vec3d(1000, 64, 1000), Math.PI/2, Math.toRadians(20), 750);
		Vec3d force = triangulateStartingForce(new Vec3d(1000, 64, 1000), Math.PI/2, Math.toRadians(20), 750, physics.getMass() * 50);
		AppEngSkyfall.logger.info("Spawned meteorite, starting pos: " + pos);
		return new ImmutablePair<>(pos, force);
	}

}
