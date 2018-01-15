package appeng.core.me.api.parts.container;

import appeng.core.me.api.parts.PartPositionRotation;
import appeng.core.me.api.parts.VoxelPosition;
import appeng.core.me.api.parts.part.Part;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
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

	}
}
