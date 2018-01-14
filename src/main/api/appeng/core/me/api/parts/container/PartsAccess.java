package appeng.core.me.api.parts.container;

import appeng.core.me.api.parts.PartPositionRotation;
import appeng.core.me.api.parts.VoxelPosition;
import appeng.core.me.api.parts.part.Part;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import java.util.Optional;

public interface PartsAccess {

	@Nonnull
	<P extends Part<P, S>, S extends Part.State<P, S>> Optional<Pair<S, PartPositionRotation>> getPart(@Nonnull VoxelPosition position);

	//Voxel info

	@Nonnull
	Optional<VoxelPosition> getPartAtVoxel(@Nonnull VoxelPosition position);

	//Direct access

	@Nonnull
	default <P extends Part<P, S>, S extends Part.State<P, S>> Optional<Pair<S, PartPositionRotation>> getAPartAtVoxel(@Nonnull VoxelPosition position){
		return getPartAtVoxel(position).flatMap(this::getPart);
	}

	interface Mutable extends PartsAccess {

		boolean canPlace(@Nonnull PartPositionRotation positionRotation, @Nonnull Part part);

		void setPart(@Nonnull PartPositionRotation positionRotation, @Nonnull Part.State part);

		@Nonnull
		<P extends Part<P, S>, S extends Part.State<P, S>> Optional<Pair<S, PartPositionRotation>> removePart(@Nonnull VoxelPosition position);

	}
}
