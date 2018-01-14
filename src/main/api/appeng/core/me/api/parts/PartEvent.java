package appeng.core.me.api.parts;

import appeng.api.event.Multiphased;
import appeng.core.me.api.parts.container.PartsAccess;
import appeng.core.me.api.parts.part.Part;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Optional;

public class PartEvent<P extends Part<P, S>, S extends Part.State<P, S>> extends Event {

	private final PartsAccess.Mutable partsAccess;

	public PartEvent(PartsAccess.Mutable partsAccess){
		this.partsAccess = partsAccess;
	}

	public PartsAccess.Mutable getPartsAccess(){
		return partsAccess;
	}

	@Cancelable
	@Multiphased
	public static class Set<P extends Part<P, S>, S extends Part.State<P, S>> extends PartEvent<P, S> {

		private final P part;
		private final PartPositionRotation positionRotation;
		private final S state;

		public Set(PartsAccess.Mutable partsAccess, P part, PartPositionRotation positionRotation, S state){
			super(partsAccess);
			this.part = part;
			this.positionRotation = positionRotation;
			this.state = state;
		}

		public P getPart(){
			return part;
		}

		public PartPositionRotation getPositionRotation(){
			return positionRotation;
		}

		public S getState(){
			return state;
		}

	}

	@Cancelable
	@Multiphased
	public static class Remove<P extends Part<P, S>, S extends Part.State<P, S>> extends PartEvent<P, S> {

		private final VoxelPosition position;

		private Optional<Pair<S, PartPositionRotation>> removed = Optional.empty();

		public Remove(PartsAccess.Mutable partsAccess, VoxelPosition position){
			super(partsAccess);
			this.position = position;
		}

		public VoxelPosition getPosition(){
			return position;
		}

		public Optional<Pair<S, PartPositionRotation>> getRemoved(){
			return removed;
		}

		public void setRemoved(Optional<Pair<S, PartPositionRotation>> removed){
			this.removed = removed;
		}

	}

}
