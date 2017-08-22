package appeng.core.skyfall.skyobject.objects;

import appeng.core.lib.world.ExpandleMutableBlockAccess;
import appeng.core.skyfall.skyobject.SkyobjectImpl;
import code.elix_x.excore.utils.client.render.world.MultiChunkBlockAccessRenderer;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.apache.commons.lang3.mutable.MutableObject;

import java.util.Optional;

public abstract class SkyobjectFalling<S extends SkyobjectFalling<S, P>, P extends SkyobjectFallingProvider<S, P>> extends SkyobjectImpl<S, P> {

	protected ExpandleMutableBlockAccess world = new ExpandleMutableBlockAccess(){

		@Override
		protected Chunk createNewChunk(BlockPos chunkPos){
			return new Chunk(chunkPos, new ChunkStorage<IBlockState, NBTTagInt>(chunkSize, new ChunkStorage.ChunkStorageSerializer.PaletteChunkStorageSerializer(), state -> new NBTTagInt(state == null ? -1 : Block.getStateId(state)), id -> id.getInt() == -1 ? null : Block.getStateById(id.getInt())));
		}

	};
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
		return Optional.ofNullable(world.getBlockAccessBoundingBox()).map(box -> box.offset(physics.getPos()).offset(-box.getCenter().x, -box.getCenter().y, -box.getCenter().z)).orElse(null);
	}

	protected MutableObject<MultiChunkBlockAccessRenderer> renderer;

	@Override
	public void render(float partialTicks){
		if(renderer == null) renderer = new MutableObject<>(new MultiChunkBlockAccessRenderer(world, world.getBlockAccessBoundingBox(), new Vec3d(0, 0, 0)));
		GlStateManager.pushMatrix();
		GlStateManager.translate(physics.getPos().x, physics.getPos().y, physics.getPos().z);
		AxisAlignedBB box = world.getBlockAccessBoundingBox();
		GlStateManager.translate((box.minX - box.maxX)/2, (box.minY - box.maxY)/2, (box.minZ - box.maxZ)/2);
		renderer.getValue().render();
		GlStateManager.popMatrix();
	}
}
