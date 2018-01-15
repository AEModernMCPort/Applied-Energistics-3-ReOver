package appeng.core.me.api.parts;

import appeng.api.event.Multiphased;
import appeng.core.me.api.parts.container.PartInfo;
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

		private PartUUID created;

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

		public Optional<PartUUID> getCreated(){
			return Optional.ofNullable(created);
		}

		public void setCreated(PartUUID created){
			this.created = created;
		}

	}

	@Cancelable
	@Multiphased
	public static class Remove<P extends Part<P, S>, S extends Part.State<P, S>> extends PartEvent<P, S> {

		private final VoxelPosition position;

		private Pair<PartUUID, PartInfo<P, S>> removed;

		public Remove(PartsAccess.Mutable partsAccess, VoxelPosition position){
			super(partsAccess);
			this.position = position;
		}

		public VoxelPosition getPosition(){
			return position;
		}

		public Optional<Pair<PartUUID, PartInfo<P, S>>> getRemoved(){
			return Optional.ofNullable(removed);
		}

		public void setRemoved(Pair<PartUUID, PartInfo<P, S>> removed){
			this.removed = removed;
		}

	}

}
