package appeng.core.me.parts.container;

import appeng.core.AppEng;
import appeng.core.me.AppEngME;
import appeng.core.me.api.definitions.IMEBlockDefinitions;
import appeng.core.me.api.parts.PartEvent;
import appeng.core.me.api.parts.container.IPartsContainer;
import appeng.core.me.api.parts.container.PartInfo;
import appeng.core.me.api.parts.container.PartUUID;
import appeng.core.me.api.parts.container.PartsAccess;
import appeng.core.me.api.parts.part.Part;
import appeng.core.me.parts.part.PartsHelper;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

public class WorldPartsAccess extends ContainerBasedPartAccess {

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

	@Override
	protected void markBlockRangeForUpdate(BlockPos pos1, BlockPos pos2){
		world.markBlockRangeForRenderUpdate(pos1, pos2);
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
		public void worldTick(TickEvent.WorldTickEvent event){
			if(event.phase == TickEvent.Phase.END){
				PartsAccess.Mutable worldPartsAccess = event.world.getCapability(PartsHelper.worldPartsAccessCapability, null);
				if(worldPartsAccess instanceof WorldPartsAccess) ((WorldPartsAccess) worldPartsAccess).tickableParts.forEach(ITickable::update);
			}
		}

		@SubscribeEvent(priority = EventPriority.LOWEST)
		public void onPartSet(PartEvent.Set event){
			if(event.getPartsAccess() instanceof WorldPartsAccess && event.getPart() instanceof ITickable && event.getCreated().isPresent()) ((WorldPartsAccess) event.getPartsAccess()).tickableParts.add((ITickable) event.getPart());
		}

		@SubscribeEvent(priority = EventPriority.LOWEST)
		public void onPartRemove(PartEvent.Remove event){
			if(event.getPartsAccess() instanceof WorldPartsAccess && event.getRemoved().isPresent()){
				Part part = ((Pair<PartUUID, PartInfo>) event.getRemoved().get()).getRight().getPart();
				if(part instanceof ITickable) ((WorldPartsAccess) event.getPartsAccess()).tickableParts.remove(part);
			}
		}

	}

}
