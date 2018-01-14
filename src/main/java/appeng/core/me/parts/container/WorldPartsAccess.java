package appeng.core.me.parts.container;

import appeng.core.AppEng;
import appeng.core.me.AppEngME;
import appeng.core.me.api.definitions.IMEBlockDefinitions;
import appeng.core.me.api.parts.PartEvent;
import appeng.core.me.api.parts.PartPositionRotation;
import appeng.core.me.api.parts.VoxelPosition;
import appeng.core.me.api.parts.container.IPartsContainer;
import appeng.core.me.api.parts.container.PartsAccess;
import appeng.core.me.api.parts.part.Part;
import appeng.core.me.parts.part.PartsHelper;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;
import java.util.stream.Stream;

public class WorldPartsAccess implements PartsAccess.Mutable {

	private static Block defContainerBlock;

	public static Block getDefContainerBlock(){
		return defContainerBlock != null ? defContainerBlock : (defContainerBlock = AppEngME.INSTANCE.<Block, IMEBlockDefinitions>definitions(Block.class).partsContainer().maybe().get());
	}

	protected World world;

	public WorldPartsAccess(){
	}

	public WorldPartsAccess(World world){
		this.world = world;
	}

	protected PartsHelper partsHelper(){
		return AppEngME.INSTANCE.getPartsHelper();
	}

	protected Optional<IPartsContainer> getContainer(BlockPos pos){
		return Optional.ofNullable(world.getTileEntity(pos)).map(tileEntity -> tileEntity.getCapability(PartsHelper.partsContainerCapability, null));
	}

	@Override
	@Nonnull
	public <P extends Part<P, S>, S extends Part.State<P, S>> Optional<Pair<S, PartPositionRotation>> getPart(@Nonnull VoxelPosition position){
		return getContainer(position.getGlobalPosition()).flatMap(container -> container.<P, S>getPart(position));
	}

	protected Stream<BlockPos> getAffectedContainers(Part part, PartPositionRotation positionRotation){
		return partsHelper().getVoxels(part, positionRotation).map(VoxelPosition::getGlobalPosition).distinct();
	}

	@Override
	public boolean canPlace(@Nonnull PartPositionRotation positionRotation, @Nonnull Part part){
		return getAffectedContainers(part, positionRotation).allMatch(pos -> canPlace(pos, part, positionRotation));
	}

	protected boolean canPlace(BlockPos pos, Part part, PartPositionRotation positionRotation){
		return getContainer(pos).map(container -> container.canPlace(positionRotation.getPosition(), partsHelper().getVoxels(pos, part, positionRotation))).orElse(world.getBlockState(pos).getBlock().isReplaceable(world, pos));
	}

	@Override
	public void setPart(@Nonnull PartPositionRotation positionRotation, @Nonnull Part.State part){
		MinecraftForge.EVENT_BUS.post(new PartEvent.Set(this, part.getPart(), positionRotation, part));
	}

	protected Optional<IPartsContainer> createContainer(BlockPos pos){
		world.setBlockState(pos, getDefContainerBlock().getDefaultState());
		return getContainer(pos);
	}

	@Override
	@Nonnull
	public <P extends Part<P, S>, S extends Part.State<P, S>> Optional<Pair<S, PartPositionRotation>> removePart(@Nonnull VoxelPosition position){
		PartEvent.Remove event = new PartEvent.Remove(this, position);
		MinecraftForge.EVENT_BUS.post(event);
		return event.getRemoved();
	}

	protected void destroyContainer(BlockPos pos){
		world.setBlockState(pos, Blocks.AIR.getDefaultState());
	}

	//Voxel info

	@Override
	@Nonnull
	public Optional<VoxelPosition> getPartAtVoxel(@Nonnull VoxelPosition position){
		return getContainer(position.getGlobalPosition()).map(partsContainerCapability -> partsContainerCapability.get(position.getLocalPosition()));
	}

	public enum Storage implements Capability.IStorage {

		INSTANCE;

		@Nullable
		@Override
		public NBTBase writeNBT(Capability capability, Object instance, EnumFacing side){
			return null;
		}

		@Override
		public void readNBT(Capability capability, Object instance, EnumFacing side, NBTBase nbt){

		}
	}

	@Mod.EventBusSubscriber(modid = AppEng.MODID)
	public static class EventHandler {

		@SubscribeEvent
		public static void onPartSet(PartEvent.Set event){
			if(event.getPartsAccess() instanceof WorldPartsAccess){
				WorldPartsAccess partsAccess = (WorldPartsAccess) event.getPartsAccess();
				PartPositionRotation positionRotation = event.getPositionRotation();
				Part.State part = event.getState();

				partsAccess.getAffectedContainers(part.getPart(), positionRotation).forEach(pos -> {
					Optional<IPartsContainer> container = partsAccess.getContainer(pos);
					if(!container.isPresent()) container = partsAccess.createContainer(pos);
					partsAccess.world.markBlockRangeForRenderUpdate(pos, pos);
					container.get().set(positionRotation.getPosition(), partsAccess.partsHelper().getVoxels(pos, part.getPart(), positionRotation));
				});
				partsAccess.getContainer(positionRotation.getPosition().getGlobalPosition()).get().setPart(positionRotation, part);
			}
		}

		@SubscribeEvent
		public static <P extends Part<P, S>, S extends Part.State<P, S>> void onPartRemove(PartEvent.Remove event){
			if(event.getPartsAccess() instanceof WorldPartsAccess){
				WorldPartsAccess partsAccess = (WorldPartsAccess) event.getPartsAccess();
				VoxelPosition position = event.getPosition();

				Optional<Pair<S, PartPositionRotation>> removedPart = partsAccess.getContainer(position.getGlobalPosition()).get().removePart(position);
				removedPart.ifPresent(part -> partsAccess.getAffectedContainers(part.getLeft().getPart(), part.getRight()).forEach(pos -> {
					IPartsContainer container = partsAccess.getContainer(pos).get();
					container.remove(position, partsAccess.partsHelper().getVoxels(pos, part.getLeft().getPart(), part.getRight()));
					if(container.isEmpty()) partsAccess.destroyContainer(pos);
					partsAccess.world.markBlockRangeForRenderUpdate(pos, pos);
				}));
				event.setRemoved(removedPart);
			}
		}

	}

}
