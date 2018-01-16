package appeng.core.me.api.parts.part;

import appeng.core.me.api.parts.PartRotation;
import appeng.core.me.api.parts.container.PartsAccess;

import java.util.stream.Stream;

public interface PartGroup<P extends PartGroup<P, S>, S extends Part.State<P, S>> extends Part<P, S> {

	default Stream<PartRotation> possibleRotations(){
		return supportsRotation() ? PartRotation.allPossibleRotations() : Stream.of(new PartRotation());
	}

	/**
	 * Compiles a sample access of required parts to form this group.<br>
	 * Note: states are ignored.
	 *
	 * @param targetRotation rotation to compile parts for
	 * @return access that should match to compile this group
	 */
	PartsAccess compileRequiredParts(PartRotation targetRotation);

}
