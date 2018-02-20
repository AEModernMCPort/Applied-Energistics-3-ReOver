package appeng.core.me.api.parts.container;

import appeng.core.me.api.parts.PartPositionRotation;
import appeng.core.me.api.parts.VoxelPosition;
import appeng.core.me.api.parts.part.Part;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

public interface PartsAccess {

	/**
	 * Retrieves part at given voxel
	 *
	 * @param position voxel position
	 * @param <P>
	 * @param <S>
	 * @return part at voxel
	 */
	@Nonnull
	<P extends Part<P, S>, S extends Part.State<P, S>> Optional<PartInfo<P, S>> getPart(@Nonnull VoxelPosition position);

	interface Mutable extends PartsAccess {

		/**
		 * Checks whether given part can be placed according to passed parameters
		 *
		 * @param positionRotation position and rotation to check
		 * @param part             part to check
		 * @return whether the part can be placed with given position & rotation
		 */
		boolean canPlace(@Nonnull PartPositionRotation positionRotation, @Nonnull Part part);

		/**
		 * Places the part at given position & rotation
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
		 * Removes the part at given voxel
		 *
		 * @param position voxel position to remove the part at
		 * @param <P>
		 * @param <S>
		 * @return UUID and info of removed part, if successfully removed, or <tt>empty</tt>
		 */
		@Nonnull
		<P extends Part<P, S>, S extends Part.State<P, S>> Optional<Pair<PartUUID, PartInfo<P, S>>> removePart(@Nonnull VoxelPosition position);

		/**
		 * Marks the part as dirty
		 * @param part part
		 * @param <P>
		 * @param <S>
		 */
		<P extends Part<P, S>, S extends Part.State<P, S>> void markDirty(@Nonnull S part);

		interface Syncable extends Mutable {

			/**
			 * Receive part (state) update from server (on client) - both add, change and remove:<br>
			 * - If the part does not exist, create from <tt>partId</tt> and {@linkplain Part.State#deserializeNBT(NBTBase) deserialize nbt}<br>
			 * - If the part exists, simply {@linkplain Part.State#deserializeNBT(NBTBase) deserialize nbt}<br>
			 * - If <tt>newData</tt> is null, remove the part
			 * @param positionRotation position & rotation of the part
			 * @param partId id of the part, or <tt>null</tt> if the part should be removed
			 * @param newData new part data, or <tt>null</tt> if the part should be removed
			 * @param <P>
			 * @param <S>
			 */
			<P extends Part<P, S>, S extends Part.State<P, S>> void receiveUpdate(@Nonnull PartPositionRotation positionRotation, @Nullable ResourceLocation partId, @Nullable NBTTagCompound newData);

		}

	}
}
