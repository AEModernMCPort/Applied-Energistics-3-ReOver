package appeng.core.skyfall.skyobject.objects;

import appeng.core.lib.math.OrientedBox;
import appeng.core.lib.util.NbtUtils;
import appeng.core.lib.world.ExpandleMutableBlockAccess;
import appeng.core.skyfall.AppEngSkyfall;
import appeng.core.skyfall.api.skyobject.Skyobject;
import appeng.core.skyfall.api.skyobject.SkyobjectPhysics;
import code.elix_x.excore.utils.world.MutableBlockAccess;
import com.google.common.base.Predicates;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import org.apache.commons.lang3.mutable.MutableDouble;
import org.apache.commons.lang3.mutable.MutableObject;
import org.joml.Quaterniond;
import org.joml.Vector3d;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class SkyobjectFallingPhysics implements SkyobjectPhysics {

	protected final Skyobject.PhysicsDriven skyobject;

	public SkyobjectFallingPhysics(Skyobject.PhysicsDriven skyobject){
		this.skyobject = skyobject;
	}

	/*
	 * Dirty
	 */

	protected boolean dirty = false;

	public boolean isDirty(){
		return dirty;
	}

	public void setDirty(boolean dirty){
		this.dirty = dirty;
	}

	/*
	 * Tick
	 */

	public void onWorldChanged(){

	}

	@Override
	public boolean tick(World world){
		prevTickPos = pos;
		prevTickRot = rot;

		if(!world.isRemote){
			int steps = 5;
			double t = 1 / (20d * steps);
			for(int i = 0; i < steps; i++){
				List<Vec3d> forces = new ArrayList<>();
				List<Vec3d> torques = new ArrayList<>();
				MinecraftForge.EVENT_BUS.post(new GatherForcesEvent(skyobject, forces, torques));
				Vec3d force = sumVecs(forces);
				momentum = momentum.add(sumVecs(forces).scale(t));
				amomentum = amomentum.add(sumVecs(torques).scale(t));
				handleCollision(world, t);
				addPos(momentum.scale(1 / getMass()).scale(t));
				addRot(amomentum.scale(1 / getMass()).scale(t));
				if(stop(world)) return true;
			}

			preLoadChunks(world);
		}
		return false;
	}

	/*
	 * Movement
	 */

	protected Vec3d pos = Vec3d.ZERO;
	protected Vec3d rot = Vec3d.ZERO;

	protected Vec3d prevTickPos = Vec3d.ZERO;
	protected Vec3d prevTickRot = Vec3d.ZERO;

	protected Vec3d momentum = Vec3d.ZERO;
	protected Vec3d amomentum = Vec3d.ZERO;

	protected double mass;

	@Override
	public Vec3d getPos(){
		return pos;
	}

	public void setPos(Vec3d pos){
		this.pos = pos;
		setDirty(true);
	}

	public void addPos(Vec3d pos){
		setPos(getPos().add(pos));
	}

	@Override
	public Vec3d getRotation(){
		return rot;
	}

	public void setRot(Vec3d rot){
		this.rot = rot;
		setDirty(true);
	}

	public void addRot(Vec3d rot){
		setRot(getRotation().add(rot));
	}

	public Vec3d getPrevTickPos(){
		return prevTickPos;
	}

	public Vec3d getPrevTickRot(){
		return prevTickRot;
	}

	@Override
	public double getMass(){
		return mass;
	}

	public void setMass(double mass){
		this.mass = mass;
	}

	public Vec3d getMomentum(){
		return momentum;
	}

	public void setMomentum(Vec3d momentum){
		this.momentum = momentum;
	}

	public Vec3d getAngularMomentum(){
		return amomentum;
	}

	public void setAngularMomentum(Vec3d amomentum){
		this.amomentum = amomentum;
	}


	protected Vec3d sumVecs(Collection<Vec3d> vecs){
		if(vecs.isEmpty()) return Vec3d.ZERO;
		MutableObject<Vec3d> res = new MutableObject<>(Vec3d.ZERO);
		vecs.forEach(vec -> res.setValue(res.getValue().add(vec)));
		return res.getValue();
	}

	/*
	 * Chunk management
	 */

//	protected ForgeChunkManager.Ticket ticket;

	protected void preLoadChunks(World world){
		/*if(ticket == null) ticket = ForgeChunkManager.requestTicket(AppEng.instance(), world, ForgeChunkManager.Type.NORMAL);
		if(ticket == null); // :(
		else {
			Set<ChunkPos> currentChunks = ticket.getChunkList();
			Set<ChunkPos> newChunks = calcChunksToLoad();
			newChunks.stream().filter(Predicates.not(currentChunks::contains)).forEach(chunk -> ForgeChunkManager.forceChunk(ticket, chunk));
			currentChunks.stream().filter(Predicates.not(newChunks::contains)).forEach(chunk -> ForgeChunkManager.unforceChunk(ticket, chunk));
		}*/
	}

	protected Set<ChunkPos> calcChunksToLoad(){
		Set<ChunkPos> chunks = new HashSet<>();
		return chunks;
	}

	/*
	 * Collision
	 */

	protected Quaterniond rot(){
		return new Quaterniond().rotate(new Vector3d(rot.x, rot.y, rot.z));
	}

	protected OrientedBox localToGlobal(OrientedBox ob){
		return ob.rotateAroundOrigin(rot()).translate(new Vector3d(pos.x, pos.y, pos.z));
	}

	protected AxisAlignedBB getLocalAABB(){
		return new AxisAlignedBB(-1, -1, -1, 1, 1, 1);
	}

	protected OrientedBox getLocalBoundingBox(){
		return new OrientedBox(getLocalAABB());
	}

	protected Stream<AxisAlignedBB> getLocalSurfaceBoundingBoxes(){
		return Stream.of(getLocalAABB());
	}

	protected Vector3d handleCollision(World world, double t){
		AxisAlignedBB gaabb = localToGlobal(getLocalBoundingBox()).getBoundingBoxMC();
		if(gaabb.minY < 255){ //FIXME Cubic chunks is a thing
			//FIXME A cheap, but precise enough approximation to avoid calculation of collision, when there is nothing to collide
			long timeStart = System.currentTimeMillis();

			Map<BlockPos, IBlockState> affectedBlocks = getCollidingBlocks(world);
			Vector3d totalReaction = new Vector3d(0, 0, 0);
			affectedBlocks.forEach((pos, state) -> totalReaction.add(dirPosToCenter(pos).mul(blockMaxReactionForce(world, pos, state))));
			if(totalReaction.lengthSquared() > 0){
				Vector3d maxForce = new Vector3d(momentum.x, momentum.y, momentum.z).div(t);
				double mFPL = Math.abs(maxForce.length() * totalReaction.angleCos(maxForce));
				totalReaction.mul(Math.min(mFPL, totalReaction.length()) / totalReaction.length());
				double prevM = momentum.length();
				double prevMY = momentum.y;
				if(prevMY > 0) prevMY = -prevMY;
				momentum = momentum.add(new Vec3d(totalReaction.x, totalReaction.y, totalReaction.z).scale(t));
				affectBlocks(world, affectedBlocks, totalReaction);

				long dT = System.currentTimeMillis() - timeStart;
				//AppEngSkyfall.logger.info("Collision took " + dT + "ms");
				return totalReaction;
			}
		}
		return new Vector3d(0, 0, 0);
	}

	protected Map<BlockPos, IBlockState> getCollidingBlocks(World world){
		Map<BlockPos, IBlockState> affectedBlocks = new HashMap<>();

		AxisAlignedBB gaabb = localToGlobal(getLocalBoundingBox()).getBoundingBoxMC();
		Set<OrientedBox> skyobjectBoxes = getLocalSurfaceBoundingBoxes().map(OrientedBox::new).map(this::localToGlobal).collect(Collectors.toSet());
		StreamSupport.stream(BlockPos.getAllInBox(new BlockPos(gaabb.minX, gaabb.minY, gaabb.minZ), new BlockPos(gaabb.maxX, gaabb.maxY, gaabb.maxZ)).spliterator(), false).filter(canBlockBeAffected(world)).forEach(pos -> {
			OrientedBox posBox = new OrientedBox(new AxisAlignedBB(pos));
			for(OrientedBox sBox : skyobjectBoxes) if(posBox.intersects(sBox)){
				affectedBlocks.put(pos, world.getBlockState(pos));
				break;
			}
		});

		return affectedBlocks;
	}

	protected Predicate<BlockPos> canBlockBeAffected(World world){
		return pos -> !(world.isAirBlock(pos) || world.getBlockState(pos).getMaterial().isLiquid());
	}

	protected void affectBlocks(World world, Map<BlockPos, IBlockState> affectedBlocks, Vector3d totalReaction){
		double rPb = totalReaction.length() / affectedBlocks.size();
		affectedBlocks.forEach((pos, state) -> {
			double reactionFactor = rPb / (blockMaxReactionForce(world, pos, state) * (0.5 + world.rand.nextDouble()));
			if(reactionFactor >= 1/5d) world.setBlockToAir(pos);
			else if(reactionFactor >= 1/10d){
				EntityFallingBlock entity = new EntityFallingBlock(world, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, state);
				entity.fallTime = 5;
				TileEntity tile = world.getTileEntity(pos);
				if(tile != null) entity.tileEntityData = tile.serializeNBT();
				Vector3d sDir = dirPosToCenter(pos).cross(new Vector3d(momentum.x, momentum.y, momentum.z)).normalize();
				if(sDir.y < 0) sDir.mul(-1);
				Vector3d speed = sDir.mul(5d);
				entity.motionX = speed.x;
				entity.motionY = speed.y;
				entity.motionZ = speed.z;
				world.setBlockToAir(pos);
				world.spawnEntity(entity);
			}
		});
	}

	protected Vector3d dirPosToCenter(BlockPos pos){
		return new Vector3d(this.pos.x, this.pos.y, this.pos.z).sub(new Vector3d(pos.getX(), pos.getY(), pos.getZ())).normalize();
	}

	/**
	 * Maximum reaction force that the block can apply.<br>
	 * Also used for determining what happens to it based on the force applied (and RNG):
	 * <br><tt>F < R/16</tt> 		--> Nothing
	 * <br><tt>R/16 <= F < R/2</tt> --> Shot away
	 * <br><tt>R/2 <= F</tt>		--> Destroyed
	 *
	 * @param world world
	 * @param pos   position
	 * @param state the block
	 * @return maximum reaction force
	 */
	protected double blockMaxReactionForce(World world, BlockPos pos, IBlockState state){
		return state.getBlockHardness(world, pos) < 0 ? 7.5E5 : Math.pow(state.getBlockHardness(world, pos), 0.75) * Math.pow(state.getBlock().getExplosionResistance(null), 2) * 1.5E4;
	}

	protected double blockMass(IBlockState block){
		return 1;
	}

	/*
	 * Stop
	 */

	protected boolean stop(World world){
		if(getPos().y == Double.NaN || getPos().y < 0) return true;
		if(momentum.scale(1/getMass()).lengthSquared() <= 100){
			AppEngSkyfall.logger.info("Meteorite landed at " + pos);
			transpose(world);
			return true;
		}
		return false;
	}


	protected void transpose(World world){

	}

	/*
	 * IO
	 */

	@Override
	public NBTTagCompound serializeNBT(){
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setTag("pos", NbtUtils.serializeVec3d(pos));
		nbt.setTag("rot", NbtUtils.serializeVec3d(rot));
		nbt.setTag("prevPos", NbtUtils.serializeVec3d(prevTickPos));
		nbt.setTag("prevRot", NbtUtils.serializeVec3d(prevTickRot));
		nbt.setTag("momentum", NbtUtils.serializeVec3d(momentum));
		nbt.setTag("amomentum", NbtUtils.serializeVec3d(amomentum));
		return nbt;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt){
		pos = NbtUtils.deserializeVec3d(nbt.getCompoundTag("pos"));
		rot = NbtUtils.deserializeVec3d(nbt.getCompoundTag("rot"));
		prevTickPos = NbtUtils.deserializeVec3d(nbt.getCompoundTag("prevPos"));
		prevTickRot = NbtUtils.deserializeVec3d(nbt.getCompoundTag("prevRot"));
		momentum = NbtUtils.deserializeVec3d(nbt.getCompoundTag("momentum"));
		amomentum = NbtUtils.deserializeVec3d(nbt.getCompoundTag("amomentum"));
	}

	public static class WorldDriven extends SkyobjectFallingPhysics implements LocalBlockAccessDriven {

		protected final MutableBlockAccess localBlockAccess;
		protected AxisAlignedBB blockAccessBox;

		public WorldDriven(Skyobject.PhysicsDriven skyobject, MutableBlockAccess localBlockAccess, AxisAlignedBB blockAccessBox){
			super(skyobject);
			this.localBlockAccess = localBlockAccess;
			this.blockAccessBox = blockAccessBox;
			recalcMass();
		}

		@Override
		public MutableBlockAccess getLocalBlockAccess(){
			return localBlockAccess;
		}

		@Override
		protected AxisAlignedBB getLocalAABB(){
			return blockAccessBox;
		}

		@Override
		protected Stream<AxisAlignedBB> getLocalSurfaceBoundingBoxes(){
			return StreamSupport.stream(BlockPos.getAllInBox(new BlockPos(blockAccessBox.minX, blockAccessBox.minY, blockAccessBox.minZ), new BlockPos(blockAccessBox.maxX, blockAccessBox.maxY, blockAccessBox.maxZ)).spliterator(), false).filter(Predicates.not(localBlockAccess::isAirBlock)).map(AxisAlignedBB::new);
		}

		@Override
		protected void transpose(World world){
			BlockPos.getAllInBox(new BlockPos(blockAccessBox.minX, blockAccessBox.minY, blockAccessBox.minZ), new BlockPos(blockAccessBox.maxX, blockAccessBox.maxY, blockAccessBox.maxZ)).forEach(pos -> {
				if(!localBlockAccess.isAirBlock(pos)){
					IBlockState block = localBlockAccess.getBlockState(pos);
					Vector3d wv = new Vector3d(pos.getX() + 0.5d, pos.getY() + 0.5d, pos.getZ() + 0.5d).rotate(rot()).add(this.pos.x, this.pos.y, this.pos.z);
					world.setBlockState(new BlockPos(wv.x, wv.y, wv.z), block, /*1*/3);
				}
			});
			AxisAlignedBB modified = localToGlobal(getLocalBoundingBox()).getBoundingBoxMC();
			world.markBlockRangeForRenderUpdate(new BlockPos(modified.minX, modified.minY, modified.minZ), new BlockPos(modified.maxX, modified.maxY, modified.maxZ));
		}

		public void recalcMass(){
			MutableDouble mass = new MutableDouble();
			BlockPos.getAllInBox(new BlockPos(blockAccessBox.minX, blockAccessBox.minY, blockAccessBox.minZ), new BlockPos(blockAccessBox.maxX, blockAccessBox.maxY, blockAccessBox.maxZ)).forEach(pos -> mass.add(blockMass(localBlockAccess.getBlockState(pos))));
			setMass(mass.getValue());
		}

		@Override
		public void onWorldChanged(){
			if(localBlockAccess instanceof ExpandleMutableBlockAccess) blockAccessBox = ((ExpandleMutableBlockAccess) localBlockAccess).getBlockAccessBoundingBox();
			recalcMass();
		}
	}
}
