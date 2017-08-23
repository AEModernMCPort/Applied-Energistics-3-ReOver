package appeng.core.skyfall.skyobject.objects;

import net.minecraft.util.math.Vec3d;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

public class Meteorite extends SkyobjectFalling<Meteorite, MeteoriteProvider> {

	public Meteorite(MeteoriteProvider provider){
		super(provider);
	}

	@Override
	protected Pair<Vec3d, Vec3d> calcSpawnPosForce(){
		Vec3d pos = triangulateStartPos(new Vec3d(5000, 64, 5000), 0, 35, 7500);
		Vec3d force = triangulateStartingForce(new Vec3d(5000, 64, 5000), 0, 35, 7500, physics.getMass() * 250);
		return new ImmutablePair<>(pos, force);
	}
}
