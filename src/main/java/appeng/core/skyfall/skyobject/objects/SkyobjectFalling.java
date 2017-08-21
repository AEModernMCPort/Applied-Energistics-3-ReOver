package appeng.core.skyfall.skyobject.objects;

import appeng.core.lib.world.ExpandleMutableBlockAccess;
import appeng.core.skyfall.skyobject.SkyobjectImpl;
import code.elix_x.excore.utils.client.render.world.MultiChunkBlockAccessRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.apache.commons.lang3.mutable.MutableObject;

public abstract class SkyobjectFalling<S extends SkyobjectFalling<S, P>, P extends SkyobjectFallingProvider<S, P>> extends SkyobjectImpl<S, P> {

	protected ExpandleMutableBlockAccess world = new ExpandleMutableBlockAccess();
	protected SkyobjectFallingPhysics physics = new SkyobjectFallingPhysics(this);

	public SkyobjectFalling(P provider){
		super(provider);
	}

	@Override
	public void tick(World world){
		dead = physics.tick(world);
	}

	@Override
	public AxisAlignedBB getRendererBoundingBox(){
		return world.getBlockAccessBoundingBox().offset(physics.getPos());
	}

	protected MutableObject<MultiChunkBlockAccessRenderer> renderer;

	@Override
	public void render(float partialTicks){
		if(renderer == null) renderer = new MutableObject<>(new MultiChunkBlockAccessRenderer(world, world.getBlockAccessBoundingBox(), new Vec3d(0, 0, 0)));
		GlStateManager.pushMatrix();
		GlStateManager.translate(physics.getPos().x, physics.getPos().y, physics.getPos().z);
		renderer.getValue().render();
		GlStateManager.popMatrix();
	}
}
