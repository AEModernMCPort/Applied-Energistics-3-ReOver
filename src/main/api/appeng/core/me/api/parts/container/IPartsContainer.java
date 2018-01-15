package appeng.core.me.api.parts.container;

import appeng.core.me.api.parts.PartPositionRotation;
import appeng.core.me.api.parts.VoxelPosition;
import appeng.core.me.api.parts.part.Part;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.util.INBTSerializable;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static appeng.core.me.api.parts.container.GlobalVoxelsInfo.VOXELSPERBLOCKAXISI;

public interface IPartsContainer extends PartsAccess.Mutable, INBTSerializable<NBTTagCompound> {

	//Link with outside world

	World getWorld();

	BlockPos getGlobalPosition();

	void setWorld(World world);

	void setGlobalPosition(BlockPos pos);

	@Nonnull
	default VoxelPosition getGlobalOriginVoxelPosition(){
		return new VoxelPosition(getGlobalPosition(), BlockPos.ORIGIN);
	}

	@Nonnull
	default VoxelPosition getGlobalCenterVoxelPosition(){
		return new VoxelPosition(getGlobalPosition(), new BlockPos(VOXELSPERBLOCKAXISI / 2, VOXELSPERBLOCKAXISI / 2, VOXELSPERBLOCKAXISI / 2));
	}

	//Owned parts

	@Nonnull
	Map<PartUUID, PartInfo> getOwnedParts();

	/**
	 * Retrieves the owned part by UUID.<br><br>
	 * <b><u>TO BE USED BY {@link PartsAccess}es ONLY!</u></b>
	 *
	 * @param partUUID uuid of the part
	 * @param <P>
	 * @param <S>
	 * @return part info
	 */
	<P extends Part<P, S>, S extends Part.State<P, S>> Optional<PartInfo<P, S>> getOwnedPart(@Nonnull PartUUID partUUID);

	/**
	 * Sets part as owned by this container.<br><br>
	 * <b><u>TO BE USED BY {@link PartsAccess}es ONLY!</u></b>
	 *
	 * @param partUUID uuid of the part
	 * @param partInfo part
	 * @param <P>
	 * @param <S>
	 */
	<P extends Part<P, S>, S extends Part.State<P, S>> void setOwnedPart(@Nonnull PartUUID partUUID, @Nonnull PartInfo<P, S> partInfo);

	/**
	 * Removes the part by UUID.<br><br>
	 * <b><u>TO BE USED BY {@link PartsAccess}es ONLY!</u></b>
	 *
	 * @param partUUID uuid of the part
	 * @param <P>
	 * @param <S>
	 * @return removed part info
	 */
	<P extends Part<P, S>, S extends Part.State<P, S>> Optional<PartInfo<P, S>> removeOwnedPart(@Nonnull PartUUID partUUID);

	//Voxel information

	/**
	 * Checks whether there's a part at given voxel
	 *
	 * @param voxel voxel
	 * @return whether there's a part at the voxel
	 */
	boolean hasPart(@Nonnull BlockPos voxel);

	/**
	 * Chechks whether the voxel is empty
	 *
	 * @param voxel voxel
	 * @return whether the voxel is empty
	 */
	default boolean isEmpty(@Nonnull BlockPos voxel){
		return !hasPart(voxel);
	}

	/**
	 * Gets part reference at voxel<br><br>
	 * <b><u>TO BE USED BY {@link PartsAccess}es ONLY!</u></b>
	 *
	 * @param voxel voxel
	 * @return reference of the part at voxel
	 */
	@Nullable
	Pair<BlockPos, PartUUID> get(@Nonnull BlockPos voxel);

	/**
	 * Checks whether given voxels can be occupied in this container.<br><br>
	 * <b><u>TO BE USED BY {@link PartsAccess}es ONLY!</u></b>
	 *
	 * @param voxels voxels to check
	 * @return whether all of the voxels can be occupied
	 */
	default boolean canPlace(@Nonnull Stream<BlockPos> voxels){
		return voxels.allMatch(this::isEmpty);
	}

	/**
	 * Places the part. No {@linkplain #canPlace(Stream)} checks are performed.<br><br>
	 * <b><u>TO BE USED BY {@link PartsAccess}es ONLY!</u></b>
	 *
	 * @param owningCPos owning container's position
	 * @param part       part UUID
	 * @param voxels     all voxels that the part should occupy in this container
	 */
	void set(@Nonnull BlockPos owningCPos, @Nonnull PartUUID part, @Nonnull Stream<BlockPos> voxels);

	/**
	 * Removes the part.<br><br>
	 * <b><u>TO BE USED BY {@link PartsAccess}es ONLY!</u></b>
	 *
	 * @param owningCPos owning container's position
	 * @param part       part UUID
	 * @param voxels     all voxels occupied by the part in this container
	 */
	void remove(@Nonnull BlockPos owningCPos, @Nonnull PartUUID part, @Nonnull Stream<BlockPos> voxels);

	/**
	 * Checks whether all voxels of this container are empty
	 *
	 * @return whether this container is empty
	 */
	boolean isEmpty();

	//Ray tracing

	@Nullable
	RayTraceResult rayTrace(@Nonnull Vec3d start, @Nonnull Vec3d end);

	//WARNING: DELEGATION

	/**
	 * Retrieves part at given voxel<br><br>
	 * <b>DO NOT USE FROM OWNING {@linkplain PartsAccess}! Possible delegation (back) to the owning access</b>
	 *
	 * @param position voxel position
	 * @param <P>
	 * @param <S>
	 * @return part at voxel
	 */
	@Nonnull
	@Override
	<P extends Part<P, S>, S extends Part.State<P, S>> Optional<PartInfo<P, S>> getPart(@Nonnull VoxelPosition position);

	/**
	 * Checks whether given part can be placed according to passed parameters<br><br>
	 * <b>DO NOT USE FROM OWNING {@linkplain PartsAccess}! Possible delegation (back) to the owning access</b>
	 *
	 * @param positionRotation position and rotation to check
	 * @param part             part to check
	 * @return whether the part can be placed with given position & rotation
	 */
	boolean canPlace(@Nonnull PartPositionRotation positionRotation, @Nonnull Part part);

	/**
	 * Places the part at given position & rotation<br><br>
	 * <b>DO NOT USE FROM OWNING {@linkplain PartsAccess}! Possible delegation (back) to the owning access</b>
	 *
	 * @param positionRotation position and rotation to place at
	 * @param part             part to place
	 * @param <P>
	 * @param <S>
	 * @return UUID of placed part, if successfully placed, or <tt>empty</tt>
	 */
	@Nonnull
	<P extends Part<P, S>, S extends Part.State<P, S>> Optional<PartUUID> setPart(@Nonnull PartPositionRotation positionRotation, @Nonnull S part);

	/**
	 * Removes the part at given voxel<br><br>
	 * <b>DO NOT USE FROM OWNING {@linkplain PartsAccess}! Possible delegation (back) to the owning access</b>
	 *
	 * @param position voxel position to remove the part at
	 * @param <P>
	 * @param <S>
	 * @return UUID and info of removed part, if successfully removed, or <tt>empty</tt>
	 */
	@Nonnull
	<P extends Part<P, S>, S extends Part.State<P, S>> Optional<Pair<PartUUID, PartInfo<P, S>>> removePart(@Nonnull VoxelPosition position);

}
