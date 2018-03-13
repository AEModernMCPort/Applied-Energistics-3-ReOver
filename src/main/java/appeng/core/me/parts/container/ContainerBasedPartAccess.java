package appeng.core.me.parts.container;

import appeng.core.AppEng;
import appeng.core.me.AppEngME;
import appeng.core.me.api.parts.PartEvent;
import appeng.core.me.api.parts.PartPositionRotation;
import appeng.core.me.api.parts.VoxelPosition;
import appeng.core.me.api.parts.container.IPartsContainer;
import appeng.core.me.api.parts.container.PartInfo;
import appeng.core.me.api.parts.container.PartUUID;
import appeng.core.me.api.parts.container.PartsAccess;
import appeng.core.me.api.parts.part.Part;
import appeng.core.me.parts.part.PartsHelperImpl;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import java.util.Optional;
import java.util.stream.Stream;

public abstract class ContainerBasedPartAccess implements PartsAccess.Mutable {

	// Util

	protected PartsHelperImpl partsHelper(){
		return AppEngME.INSTANCE.getPartsHelper();
	}

	// Needed interfaces

	protected abstract Optional<IPartsContainer> getContainer(BlockPos pos);

	protected abstract boolean canCreateContainer(BlockPos pos);

	protected abstract Optional<IPartsContainer> createContainer(BlockPos pos);

	protected abstract void removeContainer(BlockPos pos);

	@Override
	public <P extends Part<P, S>, S extends Part.State<P, S>> void markDirty(@Nonnull S part){
		getContainer(part.getAssignedPosRot().getPosition().getGlobalPosition()).ifPresent(IPartsContainer::markDirty);
	}

	// Impl

	@Override
	@Nonnull
	public <P extends Part<P, S>, S extends Part.State<P, S>> Optional<PartInfo<P, S>> getPart(@Nonnull VoxelPosition position){
		return getContainer(position.getGlobalPosition()).map(container -> container.get(position.getLocalPosition())).flatMap(relUUID -> getContainer(relUUID.getLeft()).flatMap(container -> container.getOwnedPart(relUUID.getRight())));
	}

	protected Stream<BlockPos> getAffectedContainers(Part part, PartPositionRotation positionRotation){
		return partsHelper().getVoxels(part, positionRotation).map(VoxelPosition::getGlobalPosition).distinct();
	}

	@Override
	public boolean canPlace(@Nonnull PartPositionRotation positionRotation, @Nonnull Part part){
		return getAffectedContainers(part, positionRotation).allMatch(pos -> canPlace(pos, part, positionRotation));
	}

	protected boolean canPlace(BlockPos pos, Part part, PartPositionRotation positionRotation){
		return getContainer(pos).map(container -> container.canPlace(partsHelper().getVoxels(pos, part, positionRotation))).orElse(canCreateContainer(pos));
	}

	@Override
	public <P extends Part<P, S>, S extends Part.State<P, S>> Optional<PartUUID> setPart(@Nonnull PartPositionRotation positionRotation, @Nonnull S part){
		PartEvent.Set<P, S> event = new PartEvent.Set(this, part.getPart(), positionRotation, part);
		MinecraftForge.EVENT_BUS.post(event);
		return event.getCreated();
	}

	@Override
	@Nonnull
	public <P extends Part<P, S>, S extends Part.State<P, S>> Optional<Pair<PartUUID, PartInfo<P, S>>> removePart(@Nonnull VoxelPosition position){
		PartEvent.Remove event = new PartEvent.Remove(this, position);
		MinecraftForge.EVENT_BUS.post(event);
		return event.getRemoved();
	}

	@Mod.EventBusSubscriber(modid = AppEng.MODID)
	public static class EventHandler {

		@SubscribeEvent
		public static void onPartSet(PartEvent.Set event){
			if(event.getPartsAccess() instanceof ContainerBasedPartAccess){
				ContainerBasedPartAccess partsAccess = (ContainerBasedPartAccess) event.getPartsAccess();
				PartPositionRotation positionRotation = event.getPositionRotation();
				Part.State part = event.getState();
				PartUUID partUUID = new PartUUID();

				partsAccess.getAffectedContainers(part.getPart(), positionRotation).forEach(pos -> {
					Optional<IPartsContainer> container = partsAccess.getContainer(pos);
					if(!container.isPresent()) container = partsAccess.createContainer(pos);
					container.get().set(positionRotation.getPosition().getGlobalPosition(), partUUID, partsAccess.partsHelper().getVoxels(pos, part.getPart(), positionRotation));
				});
				partsAccess.getContainer(positionRotation.getPosition().getGlobalPosition()).get().setOwnedPart(partUUID, new PartInfoImpl(part.getPart(), positionRotation, part));
				part.assignPosRot(positionRotation);
				event.setCreated(partUUID);
				partsAccess.<Part, Part.State>markDirty(part);
			}
		}

		@SubscribeEvent
		public static <P extends Part<P, S>, S extends Part.State<P, S>> void onPartRemove(PartEvent.Remove event){
			if(event.getPartsAccess() instanceof ContainerBasedPartAccess){
				ContainerBasedPartAccess partsAccess = (ContainerBasedPartAccess) event.getPartsAccess();
				VoxelPosition position = event.getPosition();

				Optional<Pair<BlockPos, PartUUID>> removedUUID = partsAccess.getContainer(position.getGlobalPosition()).map(container -> container.get(position.getLocalPosition()));
				Optional<PartInfo<P, S>> removedPart = removedUUID.flatMap(relUUID -> partsAccess.getContainer(relUUID.getLeft()).flatMap(container -> container.removeOwnedPart(relUUID.getRight())));
				removedPart.ifPresent(part -> partsAccess.getAffectedContainers(part.getPart(), part.getPositionRotation()).forEach(pos -> partsAccess.getContainer(pos).ifPresent(container -> {
					container.remove(removedUUID.get().getLeft(), removedUUID.get().getRight(), partsAccess.partsHelper().getVoxels(pos, part.getPart(), part.getPositionRotation()));
					if(container.isEmpty()) partsAccess.removeContainer(pos);
				})));
				removedUUID.ifPresent(uuid -> event.setRemoved(new ImmutablePair<>(uuid.getRight(), removedPart.get())));
				removedPart.flatMap(PartInfo::getState).ifPresent(partsAccess::markDirty);
			}
		}

	}

}
