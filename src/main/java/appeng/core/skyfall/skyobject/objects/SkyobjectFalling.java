package appeng.core.skyfall.skyobject.objects;

import appeng.core.lib.world.ExpandleMutableBlockAccess;
import appeng.core.skyfall.api.skyobject.Skyobject;
import appeng.core.skyfall.api.skyobject.SkyobjectPhysics;
import appeng.core.skyfall.skyobject.SkyobjectImpl;
import code.elix_x.excore.utils.client.render.world.MultiChunkBlockAccessRenderer;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.apache.commons.lang3.mutable.MutableObject;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.joml.Matrix4f;
import org.joml.Vector4f;

import java.util.Optional;
import java.util.stream.Stream;

public abstract class SkyobjectFalling<S extends SkyobjectFalling<S, P>, P extends SkyobjectFallingProvider<S, P>> extends SkyobjectImpl<S, P> implements Skyobject.PhysicsDriven<S, P>, Skyobject.Syncable<S, P> {

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
	public SkyobjectPhysics getPhysics(){
		return physics;
	}

	//Spawn

	@Override
	public void onSpawn(World world){
		Pair<Vec3d, Vec3d> posForce = calcSpawnPosForce();
		physics.setPos(posForce.getLeft());
		physics.setForce(posForce.getRight());
		Pair<Vec3d, Vec3d> rotTorque = calcSpawnRotTorque();
		physics.setRot(rotTorque.getLeft());
		physics.setTorque(rotTorque.getRight());
	}

	protected Pair<Vec3d, Vec3d> calcSpawnPosForce(){
		return new ImmutablePair<>(Vec3d.ZERO, Vec3d.ZERO);
	}

	protected Pair<Vec3d, Vec3d> calcSpawnRotTorque(){
		return new ImmutablePair<>(Vec3d.ZERO, Vec3d.ZERO);
	}

	protected Vec3d triangulateTargetToStart(Vec3d landingTarget, double phi, double theta, double startY){
		Vector4f res = new Vector4f((float)((startY - landingTarget.y) / Math.sin(theta)), 0, 0, 0).mul(new Matrix4f().rotateZ((float) theta).rotateY((float) phi + (float) Math.PI));
		return new Vec3d(res.x, res.y, res.z);
	}

	protected Vec3d triangulateStartPos(Vec3d landingTarget, double phi, double theta, double startY){
		return landingTarget.add(triangulateTargetToStart(landingTarget, phi, theta, startY));
	}

	protected Vec3d triangulateStartingForce(Vec3d landingTarget, double phi, double theta, double startY, double force){
		return triangulateTargetToStart(landingTarget, phi, theta, startY).normalize().scale(-force);
	}

	//Rendering

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
		GlStateManager.rotate((float) physics.getRotation().x, 1, 0, 0);
		GlStateManager.rotate((float) physics.getRotation().y, 0, 1, 0);
		GlStateManager.rotate((float) physics.getRotation().z, 0, 0, 1);
		AxisAlignedBB box = world.getBlockAccessBoundingBox();
		GlStateManager.translate((box.minX - box.maxX)/2, (box.minY - box.maxY)/2, (box.minZ - box.maxZ)/2);
		renderer.getValue().render();
		GlStateManager.popMatrix();
	}

	@Override
	public boolean isDirty(){
		return physics.isDirty() || world.isDirty();
	}

	@Override
	public Stream<NBTTagCompound> getSyncCompounds(boolean sendEverything){
		Stream stream = Stream.empty();
		if(sendEverything || physics.isDirty()){
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setString("type", "physics");
			nbt.setTag("value", physics.serializeNBT());
			stream = Stream.concat(stream, Stream.of(nbt));
			physics.setDirty(false);
		}
		if(sendEverything || world.isDirty()) stream = Stream.concat(stream, (sendEverything ? world.getAllChunks() : world.getDirtyChunks()).map(chunk -> {
			chunk.setDirty(false);
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setString("type", "chunk");
			nbt.setTag("pos", NBTUtil.createPosTag(chunk.getChunkPos()));
			nbt.setTag("data", chunk.serializeNBT());
			return nbt;
		}));
		return stream;
	}

	@Override
	public void readNextSyncCompound(NBTTagCompound nbt){
		switch(nbt.getString("type")){
			case "physics":
				physics.deserializeNBT(nbt.getCompoundTag("value"));
				break;
			case "chunk":
				world.getOrCreateChunk(NBTUtil.getPosFromTag(nbt.getCompoundTag("pos"))).deserializeNBT(nbt.getCompoundTag("data"));
				break;
		}
	}
}
