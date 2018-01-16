package appeng.core.me.parts.container;

import appeng.core.me.AppEngME;
import appeng.core.me.api.definitions.IMEBlockDefinitions;
import appeng.core.me.api.parts.container.IPartsContainer;
import appeng.core.me.parts.part.PartsHelper;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;
import java.util.Optional;

public class WorldPartsAccess extends ContainerBasedPartAccess {

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

}
