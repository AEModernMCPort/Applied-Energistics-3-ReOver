package appeng.core.skyfall.skyobject.objects;

import appeng.core.skyfall.AppEngSkyfall;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.joml.Vector2d;

public class Meteorite extends SkyobjectFalling<Meteorite, MeteoriteProvider> {

	public Meteorite(MeteoriteProvider provider){
		super(provider);
	}

	@Override
	protected Pair<Vec3d, Vec3d> calcSpawnPosMomentum(World world){
		Vec3d lading = landingPos(world);
		double startY = startY(world);
		Vector2d piTheta = piTheta(world);

		Vec3d pos = triangulateStartPos(lading, piTheta.x, piTheta.y, startY);
		Vec3d force = triangulateStartingForce(lading, piTheta.x, piTheta.y, startY, physics.getMass() * entrySpeed(world));

		return new ImmutablePair<>(pos, force);
	}

	protected Vec3d landingPos(World world){
		return new Vec3d(1000, 64, 1000);
	}

	protected double startY(World world){
		return 1000;
	}

	protected Vector2d piTheta(World world){
		return new Vector2d(-Math.PI + world.rand.nextDouble()*2*Math.PI, Math.toRadians(AppEngSkyfall.INSTANCE.config.meteorite.nextCreaseAngle(world.rand)));
	}

	protected double entrySpeed(World world){
		return 25;
	}

}
