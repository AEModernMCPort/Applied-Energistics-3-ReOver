package appeng.core.me.parts.container;

import appeng.core.AppEng;
import appeng.core.me.AppEngME;
import appeng.core.me.api.definitions.IMEBlockDefinitions;
import appeng.core.me.api.parts.PartEvent;
import appeng.core.me.api.parts.PartPositionRotation;
import appeng.core.me.api.parts.VoxelPosition;
import appeng.core.me.api.parts.container.IPartsContainer;
import appeng.core.me.api.parts.container.PartInfo;
import appeng.core.me.api.parts.container.PartUUID;
import appeng.core.me.api.parts.container.PartsAccess;
import appeng.core.me.api.parts.part.Part;
import appeng.core.me.netio.PartMessage;
import appeng.core.me.parts.part.PartsHelper;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import org.apache.commons.lang3.tuple.Pair;
import org.joml.Vector3d;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

public class WorldPartsAccess extends ContainerBasedPartAccess implements PartsAccess.Mutable.Syncable {

	private static Block defContainerBlock;

	public static Block getDefContainerBlock(){
		return defContainerBlock != null ? defContainerBlock : (defContainerBlock = AppEngME.INSTANCE.<Block, IMEBlockDefinitions>definitions(Block.class).partsContainer().maybe().get());
	}

	protected World world;
	protected Collection<ITickable> tickableParts = new ArrayList<>();

	public WorldPartsAccess(){
	}

	public WorldPartsAccess(World world){
		this.world = world;
	}

	/*
	 * Impl
	 */

	@Override
	protected Optional<IPartsContainer> getContainer(BlockPos pos){
		return Optional.ofNullable(world.getTileEntity(pos)).map(tileEntity -> tileEntity.getCapability(PartsHelper.partsContainerCapability, null));
	}

	@Override
	protected boolean canCreateContainer(BlockPos pos){
		return world.getBlockState(pos).getBlock().isReplaceable(world, pos);
	}

	@Override
	protected Optional<IPartsContainer> createContainer(BlockPos pos){
		world.setBlockState(pos, getDefContainerBlock().getDefaultState());
		return getContainer(pos);
	}

	@Override
	protected void removeContainer(BlockPos pos){
		world.setBlockState(pos, Blocks.AIR.getDefaultState());
	}

	//TODO Remove once BlockBreakEvent is fired on client
	@Override
	protected void markBlockRangeForUpdate(BlockPos pos1, BlockPos pos2){
		world.markBlockRangeForRenderUpdate(pos1, pos2);
	}

	/*
	 * Sync
	 */

	protected <P extends Part<P, S>, S extends Part.State<P, S>> void sendPart(@Nonnull S part, boolean remove){
		Vector3d gp = part.getAssignedPosRot().getPosition().asVector3d();
		AppEngME.INSTANCE.net.sendToAllAround(new PartMessage(part.getAssignedPosRot(), remove ? null : part.getPart().getRegistryName(), remove ? null : part.serializeSyncNBT()), new NetworkRegistry.TargetPoint(world.provider.getDimension(), gp.x, gp.y, gp.z, 128 /*TODO Find correct range*/));
	}

	@Override
	public <P extends Part<P, S>, S extends Part.State<P, S>> void markDirty(@Nonnull S part){
		super.markDirty(part);
		if(!world.isRemote) sendPart(part, false);
	}

	@Override
	public <P extends Part<P, S>, S extends Part.State<P, S>> void receiveUpdate(@Nonnull PartPositionRotation positionRotation, @Nullable ResourceLocation partId, @Nullable NBTTagCompound newData){
		if(partId == null || newData == null) removePart(positionRotation.getRotationCenterPosition());
		else {
			S state = this.<P, S>getPart(positionRotation.getRotationCenterPosition()).flatMap(PartInfo::getState).orElse(null);
			if(state == null) this.setPart(positionRotation, state = AppEngME.INSTANCE.<P, S>getPartRegistry().getValue(partId).createNewState());
			state.deserializeSyncNBT(newData);
			world.markBlockRangeForRenderUpdate(state.getAssignedPosRot().getPosition().getGlobalPosition(), state.getAssignedPosRot().getPosition().getGlobalPosition());
		}
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
		public static void worldTick(TickEvent.WorldTickEvent event){
			if(event.phase == TickEvent.Phase.END){
				PartsAccess.Mutable worldPartsAccess = event.world.getCapability(PartsHelper.worldPartsAccessCapability, null);
				if(worldPartsAccess instanceof WorldPartsAccess) ((WorldPartsAccess) worldPartsAccess).tickableParts.forEach(ITickable::update);
			}
		}

		@SubscribeEvent(priority = EventPriority.LOWEST)
		public static <P extends Part<P, S>, S extends Part.State<P, S>> void onPartSet(PartEvent.Set e){
			PartEvent.Set<P, S> event = e;
			if(event.getPartsAccess() instanceof WorldPartsAccess && event.getCreated().isPresent()){
				WorldPartsAccess partsAccess = (WorldPartsAccess) event.getPartsAccess();
				if(!partsAccess.world.isRemote){
					if(event.getState() != null) partsAccess.markDirty(event.getState());
					if(event.getState() instanceof ITickable) partsAccess.tickableParts.add((ITickable) event.getState());
				} else {
					partsAccess.world.markBlockRangeForRenderUpdate(event.getPositionRotation().getPosition().getGlobalPosition(), event.getPositionRotation().getPosition().getGlobalPosition());
				}
			}
		}

		@SubscribeEvent(priority = EventPriority.LOWEST)
		public static <P extends Part<P, S>, S extends Part.State<P, S>> void onPartRemove(PartEvent.Remove e){
			PartEvent.Remove<P, S> event = e;
			if(event.getPartsAccess() instanceof WorldPartsAccess){
				WorldPartsAccess partsAccess = (WorldPartsAccess) event.getPartsAccess();
				if(!partsAccess.world.isRemote){
					event.getRemoved().flatMap(pair -> pair.getRight().getState()).ifPresent(part -> {
						partsAccess.sendPart(part, true);
						if(part instanceof ITickable) partsAccess.tickableParts.remove(part);
					});
				}
				//TODO Add once BlockBreakEvent is fired on client
				/*else {
					event.getRemoved().map(pair -> pair.getRight().getPositionRotation().getPosition().getGlobalPosition()).ifPresent(pos -> partsAccess.world.markBlockRangeForRenderUpdate(pos, pos));
				}*/
			}
		}

	}

}
