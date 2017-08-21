package appeng.core.skyfall.api.skyobject;

import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;

public interface Skyobject<S extends Skyobject<S, P>, P extends SkyobjectProvider<S, P>> {

	P getProvider();

	default void onSpawn(World world){}

	void tick(World world);

	boolean isDead();


	//Client only

	AxisAlignedBB getRendererBoundingBox();

	void render(float partialTicks);

}
