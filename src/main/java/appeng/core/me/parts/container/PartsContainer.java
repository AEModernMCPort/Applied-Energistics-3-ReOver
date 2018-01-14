package appeng.core.me.parts.container;

import appeng.core.me.AppEngME;
import appeng.core.me.api.parts.PartPositionRotation;
import appeng.core.me.api.parts.VoxelPosition;
import appeng.core.me.api.parts.container.*;
import appeng.core.me.api.parts.part.Part;
import appeng.core.me.parts.part.PartsHelper;
import com.google.common.base.Predicates;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Stream;

import static appeng.core.me.api.parts.container.GlobalVoxelsInfo.*;

public class PartsContainer implements IPartsContainer {

	public static PartsHelper partsHelper(){
		return AppEngME.INSTANCE.getPartsHelper();
	}

	//Link to outside world

	protected World world;
	protected BlockPos globalPosition;

	@Override
	public World getWorld(){
		return world;
	}
	@Override
	public BlockPos getGlobalPosition(){
		return globalPosition;
	}
	@Override
	public void setWorld(World world){
		this.world = world;
	}
	@Override
	public void setGlobalPosition(BlockPos globalPosition){
		this.globalPosition = globalPosition;
	}

	public PartsAccess.Mutable getWorldPartsAccess(){
		return world.getCapability(PartsHelper.worldPartsAccessCapability, null);
	}

	//Voxel access view

	protected final int[][][] voxels = new int[VOXELSPERBLOCKAXISI][VOXELSPERBLOCKAXISI][VOXELSPERBLOCKAXISI];
	protected final List<VoxelPosition> parts = new ArrayList<>();

	protected int nextUid(){
		int next = parts.indexOf(null);
		return next != -1 ? next : parts.size();
	}

	protected boolean hasPart(int uid){
		return uid >= 0;
	}

	protected int getUid(BlockPos voxel){
		return voxels[voxel.getX()][voxel.getY()][voxel.getZ()] - 1;
	}

	protected void setUid(BlockPos voxel, int uid){
		voxels[voxel.getX()][voxel.getY()][voxel.getZ()] = uid + 1;
	}

	protected void removeUid(BlockPos voxel){
		setUid(voxel, -1);
	}


	protected int getUid(VoxelPosition part){
		return parts.indexOf(part);
	}

	protected VoxelPosition getPart(int uid){
		return parts.get(uid);
	}

	protected void setPart(int uid, VoxelPosition part){
		if(part == null && uid == parts.size() - 1) parts.remove(uid);
		else if(uid == parts.size()) parts.add(part);
		else parts.set(uid, part);
	}

	@Override
	public boolean hasPart(@Nonnull BlockPos voxel){
		return hasPart(getUid(voxel));
	}

	@Override
	@Nullable
	public VoxelPosition get(@Nonnull BlockPos voxel){
		int uid = getUid(voxel);
		return hasPart(uid) ? getPart(uid).add(getGlobalOriginVoxelPosition()) : null;
	}

	@Override
	public boolean canPlace(@Nonnull VoxelPosition part, @Nonnull Stream<BlockPos> voxels){
		return voxels.allMatch(this::isEmpty);
	}

	@Override
	public void set(@Nonnull VoxelPosition part, @Nonnull Stream<BlockPos> voxels){
		final int uid = nextUid();
		setPart(uid, part.asRelativeTo(getGlobalOriginVoxelPosition()));
		voxels.forEach(voxel -> setUid(voxel, uid));
	}

	@Override
	public void remove(@Nonnull VoxelPosition part, @Nonnull Stream<BlockPos> voxels){
		final int uid = getUid(part.asRelativeTo(getGlobalOriginVoxelPosition()));
		setPart(uid, null);
		voxels.forEach(this::removeUid);
	}

	@Override
	public boolean isEmpty(){
		return parts.isEmpty() || parts.stream().allMatch(Predicates.isNull());
	}

	//Delegate to world

	@Override
	public boolean canPlace(@Nonnull PartPositionRotation positionRotation, @Nonnull Part part){
		return getWorldPartsAccess().canPlace(positionRotation, part);
	}

	@Override
	@Nonnull
	public Optional<VoxelPosition> getPartAtVoxel(@Nonnull VoxelPosition position){
		return getWorldPartsAccess().getPartAtVoxel(position);
	}

	//Ray trace

	@Override
	@Nullable
	public RayTraceResult rayTrace(@Nonnull Vec3d start, @Nonnull Vec3d end){
		Vec3d globalPosition = new Vec3d(this.globalPosition);
		Vec3d lstart = start.subtract(globalPosition);
		Vec3d lend = end.subtract(globalPosition);
		double d2 = lstart.squareDistanceTo(lend);
		AxisAlignedBB bbox = new AxisAlignedBB(BlockPos.ORIGIN);
		Vec3d addon = lend.subtract(lstart).normalize().scale(VOXELSIZED2);
		Vec3d current = lstart;
		while(lstart.squareDistanceTo(current) <= d2){
			if(bbox.contains(current)){
				BlockPos voxel = new VoxelPosition(BlockPos.ORIGIN, new BlockPos(Math.floor(current.x * VOXELSPERBLOCKAXISD), Math.floor(current.y * VOXELSPERBLOCKAXISD), Math.floor(current.z * VOXELSPERBLOCKAXISD))).getLocalPosition();
				if(hasPart(voxel)){
					RayTraceResult intercept = getVoxelBB(voxel).calculateIntercept(lstart, lend);
					if(intercept == null) return null;
					RayTraceResult result = new RayTraceResult(intercept.hitVec.add(globalPosition), intercept.sideHit, this.globalPosition);
					result.hitInfo = new VoxelPosition(this.globalPosition, voxel);
					return result;
				}
			}
			current = current.add(addon);
		}
		return null;
	}

	//Owned parts

	protected Map<BlockPos, Part.State> ownedPartsPositions = new HashMap<>();
	protected Map<Part.State, PartPositionRotation> ownedPartsInfos = new HashMap<>();

	@Override
	@Nonnull
	public <P extends Part<P, S>, S extends Part.State<P, S>> Optional<Pair<S, PartPositionRotation>> getPart(@Nonnull VoxelPosition position){
		S state = (S) ownedPartsPositions.get(position.getLocalPosition());
		return Optional.of(new ImmutablePair<>(state, getPartInfo(state)));
	}

	public PartPositionRotation getPartInfo(Part.State part){
		return ownedPartsInfos.get(part).addPos(getGlobalOriginVoxelPosition());
	}

	@Override
	public void setPart(@Nonnull PartPositionRotation positionRotation, @Nonnull Part.State part){
		ownedPartsPositions.put(positionRotation.getPosition().getLocalPosition(), part);
		ownedPartsInfos.put(part, positionRotation.posAsRelativeTo(getGlobalOriginVoxelPosition()));
	}

	@Override
	@Nonnull
	public <P extends Part<P, S>, S extends Part.State<P, S>> Optional<Pair<S, PartPositionRotation>> removePart(@Nonnull VoxelPosition position){
		S state = (S) ownedPartsPositions.remove(position.getLocalPosition());
		if(state == null) return Optional.empty();
		else return Optional.of(new ImmutablePair<>(state, ownedPartsInfos.remove(state).addPos(getGlobalOriginVoxelPosition())));
	}

	@Override
	@Nonnull
	public Map<Part.State, PartPositionRotation> getOwnedParts(){
		return Collections.unmodifiableMap(ownedPartsInfos);
	}

	//IO

	@Override
	public NBTTagCompound serializeNBT(){
		NBTTagCompound nbt = new NBTTagCompound();
		NBTTagList parts = new NBTTagList();
		this.parts.forEach(part -> parts.appendTag(part == null ? new NBTTagCompound() : part.serializeNBT()));
		nbt.setTag("parts", parts);
		nbt.setIntArray("voxels", flatten());
		NBTTagList states = new NBTTagList();
		this.ownedPartsInfos.forEach((state, info) -> {
			NBTTagCompound next = new NBTTagCompound();
			next.setTag("state", partsHelper().serialize(state));
			next.setTag("info", info.serializeNBT());
			states.appendTag(next);
		});
		nbt.setTag("states", states);
		return nbt;
	}

	public int[] flatten(){
		int[] voxels = new int[VOXELSPERBLOCKAXISI * VOXELSPERBLOCKAXISI * VOXELSPERBLOCKAXISI];
		GlobalVoxelsInfo.allVoxelsInABlock().forEach(voxel -> voxels[voxel.getX() << (VOXELSPERBLOCKAXISIBITCOUNT * 2) | voxel.getY() << VOXELSPERBLOCKAXISIBITCOUNT | voxel.getZ()] = getUid(voxel));
		return voxels;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt){
		parts.clear();
		nbt.getTagList("parts", 10).forEach(tag -> parts.add(tag.hasNoTags() ? null : VoxelPosition.fromNBT((NBTTagCompound) tag)));
		inflate(nbt.getIntArray("voxels"));
		ownedPartsPositions.clear();
		ownedPartsInfos.clear();
		nbt.getTagList("states", 10).forEach(bnext -> {
			NBTTagCompound next = (NBTTagCompound) bnext;
			Part.State state = partsHelper().deserialize(next.getCompoundTag("state"));
			PartPositionRotation info = PartPositionRotation.fromNBT(next.getCompoundTag("info"));
			ownedPartsPositions.put(info.getPosition().getLocalPosition(), state);
			ownedPartsInfos.put(state, info);
		});
	}

	public void inflate(int[] voxels){
		GlobalVoxelsInfo.allVoxelsInABlock().forEach(voxel -> setUid(voxel, voxels[voxel.getX() << (VOXELSPERBLOCKAXISIBITCOUNT * 2) | voxel.getY() << VOXELSPERBLOCKAXISIBITCOUNT | voxel.getZ()]));
	}

	public enum Storage implements Capability.IStorage<IPartsContainer> {

		INSTANCE;

		@Nullable
		@Override
		public NBTBase writeNBT(Capability<IPartsContainer> capability, IPartsContainer instance, EnumFacing side){
			return instance.serializeNBT();
		}

		@Override
		public void readNBT(Capability<IPartsContainer> capability, IPartsContainer instance, EnumFacing side, NBTBase nbt){
			instance.deserializeNBT((NBTTagCompound) nbt);
		}

	}

}

