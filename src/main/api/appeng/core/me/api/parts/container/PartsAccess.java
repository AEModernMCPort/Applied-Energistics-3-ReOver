package appeng.core.me.api.parts.container;

import appeng.core.me.api.parts.PartPositionRotation;
import appeng.core.me.api.parts.VoxelPosition;
import appeng.core.me.api.parts.part.Part;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Optional;

public interface PartsAccess {

	<P extends Part<P, S>, S extends Part.State<P, S>> Optional<Pair<S, PartPositionRotation>> getPart(VoxelPosition position);

	//Voxel info

	Optional<VoxelPosition> getPartAtVoxel(VoxelPosition position);

	//Direct access

	default <P extends Part<P, S>, S extends Part.State<P, S>> Optional<Pair<S, PartPositionRotation>> getAPartAtVoxel(VoxelPosition position){
		return getPartAtVoxel(position).flatMap(this::getPart);
	}

	interface Mutable extends PartsAccess {

		boolean canPlace(PartPositionRotation positionRotation, Part part);

		void setPart(PartPositionRotation positionRotation, Part.State part);

		<P extends Part<P, S>, S extends Part.State<P, S>> Optional<Pair<S, PartPositionRotation>> removePart(VoxelPosition position);

	}
}
