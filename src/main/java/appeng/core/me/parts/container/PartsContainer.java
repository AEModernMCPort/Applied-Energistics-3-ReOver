package appeng.core.me.parts.container;

import appeng.core.me.api.parts.PartPositionRotation;
import appeng.core.me.api.parts.VoxelPosition;
import appeng.core.me.api.parts.container.*;
import appeng.core.me.api.parts.part.Part;
import com.google.common.base.Predicates;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTUtil;
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

	//Link to outside world

	protected PartsAccess.Mutable globalAccess;
	protected World world;
	protected BlockPos globalPosition;

	@Override
	public PartsAccess.Mutable getGlobalAccess(){
		return globalAccess;
	}

	@Override
	public Optional<World> getWorld(){
		return Optional.ofNullable(world);
	}

	@Override
	public void setGlobalAccess(@Nonnull Mutable globalAccess, @Nullable World world){
		this.globalAccess = globalAccess;
		this.world = world;
	}

	@Override
	public BlockPos getGlobalPosition(){
		return globalPosition;
	}

	@Override
	public void setGlobalPosition(BlockPos globalPosition){
		this.globalPosition = globalPosition;
	}

	/*
	 * Load-unload (server only)
	 */

	@Override
	public void onLoad(){
		ownedParts.values().forEach(info -> info.getPart().onLoad((Part.State) info.getState().orElse(null), globalAccess, world, info.getPositionRotation()));
	}

	//FIXME Not yet called when the world unloads
	@Override
	public void onUnload(){
		ownedParts.values().forEach(info -> info.getPart().onUnload((Part.State) info.getState().orElse(null), globalAccess, world, info.getPositionRotation()));
	}

	/*
	 * Voxel access view
	 */

	//Internal

	protected final int[][][] voxels = new int[VOXELSPERBLOCKAXISI][VOXELSPERBLOCKAXISI][VOXELSPERBLOCKAXISI];
	protected final List<LocalPartInfo> parts = new ArrayList<>();

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

	protected int getUid(LocalPartInfo part){
		return parts.indexOf(part);
	}

	protected LocalPartInfo getPart(int uid){
		return hasPart(uid) ? parts.get(uid) : null;
	}

	protected void setPart(int uid, LocalPartInfo part){
		if(part == null && uid == parts.size() - 1) parts.remove(uid);
		else if(uid == parts.size()) parts.add(part);
		else parts.set(uid, part);
	}

	protected class LocalPartInfo {

		BlockPos relOwningContainerPos;
		PartUUID partUUID;

		protected LocalPartInfo(BlockPos relOwningContainerPos, PartUUID partUUID){
			this.relOwningContainerPos = relOwningContainerPos;
			this.partUUID = partUUID;
		}

		protected Pair<BlockPos, PartUUID> toGlobal(){
			return new ImmutablePair<>(relOwningContainerPos.add(globalPosition), partUUID);
		}

		protected NBTTagCompound serializeNBT(){
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setTag("pos", NBTUtil.createPosTag(relOwningContainerPos));
			nbt.setTag("uuid", partUUID.serializeNBT());
			return nbt;
		}

		@Override
		public boolean equals(Object o){
			if(this == o) return true;
			if(!(o instanceof LocalPartInfo)) return false;
			LocalPartInfo that = (LocalPartInfo) o;
			return Objects.equals(relOwningContainerPos, that.relOwningContainerPos) && Objects.equals(partUUID, that.partUUID);
		}

		@Override
		public int hashCode(){
			return Objects.hash(relOwningContainerPos, partUUID);
		}

		@Override
		public String toString(){
			return "LocalPartInfo{" + "relOwningContainerPos=" + relOwningContainerPos + ", partUUID=" + partUUID + '}';
		}

	}

	protected LocalPartInfo fromGlobal(BlockPos globalOwningContainerPos, PartUUID partUUID){
		return new LocalPartInfo(globalOwningContainerPos.subtract(globalPosition), partUUID);
	}

	protected LocalPartInfo fromGlobal(Pair<BlockPos, PartUUID> globalInfo){
		return fromGlobal(globalInfo.getLeft(), globalInfo.getRight());
	}

	protected LocalPartInfo fromNBT(NBTTagCompound nbt){
		return new LocalPartInfo(NBTUtil.getPosFromTag(nbt.getCompoundTag("pos")), PartUUID.createPartUUID(nbt.getCompoundTag("uuid")));
	}

	//Impl

	@Override
	public boolean hasPart(@Nonnull BlockPos voxel){
		return hasPart(getUid(voxel));
	}

	@Override
	@Nullable
	public Pair<BlockPos, PartUUID> get(@Nonnull BlockPos voxel){
		return Optional.ofNullable(getPart(getUid(voxel))).map(LocalPartInfo::toGlobal).orElse(null);
	}

	@Override
	public void set(@Nonnull BlockPos owningCPos, @Nonnull PartUUID part, @Nonnull Stream<BlockPos> voxels){
		final int uid = nextUid();
		setPart(uid, fromGlobal(owningCPos, part));
		voxels.forEach(voxel -> setUid(voxel, uid));
	}

	@Override
	public void remove(@Nonnull BlockPos owningCPos, @Nonnull PartUUID part, @Nonnull Stream<BlockPos> voxels){
		final int uid = getUid(fromGlobal(owningCPos, part));
		setPart(uid, null);
		voxels.forEach(this::removeUid);
	}

	@Override
	public boolean isEmpty(){
		return parts.isEmpty() || parts.stream().allMatch(Predicates.isNull());
	}

	//Owned parts

	protected Map<PartUUID, PartInfo> ownedParts = new HashMap<>();

	@Nonnull
	@Override
	public Map<PartUUID, PartInfo> getOwnedParts(){
		return Collections.unmodifiableMap(ownedParts);
	}

	@Override
	public <P extends Part<P, S>, S extends Part.State<P, S>> Optional<PartInfo<P, S>> getOwnedPart(@Nonnull PartUUID partUUID){
		return Optional.ofNullable(ownedParts.get(partUUID));
	}

	@Override
	public <P extends Part<P, S>, S extends Part.State<P, S>> void setOwnedPart(@Nonnull PartUUID partUUID, @Nonnull PartInfo<P, S> partInfo){
		ownedParts.put(partUUID, partInfo);
	}

	@Override
	public <P extends Part<P, S>, S extends Part.State<P, S>> Optional<PartInfo<P, S>> removeOwnedPart(@Nonnull PartUUID partUUID){
		return Optional.ofNullable(ownedParts.remove(partUUID));
	}

	//Delegate

	@Override
	public boolean canPlace(@Nonnull PartPositionRotation positionRotation, @Nonnull Part part){
		return getGlobalAccess().canPlace(positionRotation, part);
	}

	@Nonnull
	@Override
	public <P extends Part<P, S>, S extends Part.State<P, S>> Optional<PartInfo<P, S>> getPart(@Nonnull VoxelPosition position){
		return getGlobalAccess().getPart(position);
	}

	@Override
	public <P extends Part<P, S>, S extends Part.State<P, S>> Optional<PartUUID> setPart(@Nonnull PartPositionRotation positionRotation, @Nonnull S part){
		return getGlobalAccess().setPart(positionRotation, part);
	}

	@Nonnull
	@Override
	public <P extends Part<P, S>, S extends Part.State<P, S>> Optional<Pair<PartUUID, PartInfo<P, S>>> removePart(@Nonnull VoxelPosition position){
		return getGlobalAccess().removePart(position);
	}

	@Override
	public <P extends Part<P, S>, S extends Part.State<P, S>> void markDirty(@Nonnull S part){
		getGlobalAccess().markDirty(part);
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

	//IO

	@Override
	public NBTTagCompound serializeNBT(){
		NBTTagCompound nbt = new NBTTagCompound();
		NBTTagList parts = new NBTTagList();
		this.parts.forEach(part -> parts.appendTag(part == null ? new NBTTagCompound() : part.serializeNBT()));
		nbt.setTag("parts", parts);
		nbt.setIntArray("voxels", flatten());
		NBTTagList owned = new NBTTagList();
		this.ownedParts.forEach((uuid, info) -> {
			NBTTagCompound next = new NBTTagCompound();
			next.setTag("uuid", uuid.serializeNBT());
			next.setTag("info", info.serializeNBT());
			owned.appendTag(next);
		});
		nbt.setTag("owned", owned);
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
		nbt.getTagList("parts", 10).forEach(tag -> parts.add(tag.hasNoTags() ? null : fromNBT((NBTTagCompound) tag)));
		inflate(nbt.getIntArray("voxels"));
		ownedParts.clear();
		nbt.getTagList("owned", 10).forEach(bnext -> {
			NBTTagCompound next = (NBTTagCompound) bnext;
			ownedParts.put(PartUUID.createPartUUID(next.getCompoundTag("uuid")), PartInfoImpl.deserializeNBT(next.getCompoundTag("info")));
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

