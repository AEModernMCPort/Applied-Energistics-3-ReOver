package appeng.core.skyfall.skyobject.objects;

import appeng.core.lib.world.ExpandleMutableBlockAccess;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class Meteorite extends SkyobjectFalling<Meteorite, MeteoriteProvider> {

	public Meteorite(MeteoriteProvider provider){
		super(provider);
	}

	@Override
	public void onSpawn(World world){
		EntityPlayer player = world.playerEntities.get(0);
		physics.setPos(new Vec3d(player.posX, player.posY + 25, player.posZ));
	}
}
