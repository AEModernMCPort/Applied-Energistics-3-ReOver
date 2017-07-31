package appeng.core.me.block;

import appeng.core.core.block.TileBlockBase;
import appeng.core.lib.block.property.UnlistedPropertyGeneric;
import appeng.core.me.api.parts.container.GlobalVoxelsInfo;
import appeng.core.me.api.parts.container.IPartsContainer;
import appeng.core.me.api.parts.VoxelPosition;
import appeng.core.me.parts.part.PartsHelper;
import appeng.core.me.tile.PartsContainerTile;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

public class PartsContainerBlock extends TileBlockBase<PartsContainerTile> {

	public PartsContainerBlock(){
		super(Material.IRON, PartsContainerTile::new);
	}

	public Optional<IPartsContainer> getContainer(IBlockAccess world, BlockPos pos){
		return Optional.ofNullable(world).map(w -> this.getTileEntity(w, pos)).map(tile -> tile.getCapability(PartsHelper.partsContainerCapability, null));
	}

	@Override
	protected BlockStateContainer createBlockState(){
		return new ExtendedBlockState(this, new IProperty[0], new IUnlistedProperty[]{UnlistedPropertyGeneric.BLOCKACESS, UnlistedPropertyGeneric.POS});
	}

	@Override
	public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos){
		return ((IExtendedBlockState) state).withProperty(UnlistedPropertyGeneric.BLOCKACESS, world).withProperty(UnlistedPropertyGeneric.POS, pos);
	}

	@Override
	public boolean isFullBlock(IBlockState state){
		return false;
	}

	@Override
	public boolean isFullCube(IBlockState state){
		return false;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state){
		return false;
	}

	@Override
	public BlockRenderLayer getBlockLayer(){
		return BlockRenderLayer.CUTOUT_MIPPED;
	}

	@Override
	public void addCollisionBoxToList(IBlockState state, World world, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collisionBBs, @Nullable Entity entityIn, boolean p_185477_7_){
		getContainer(world, pos).ifPresent(container -> StreamSupport.stream(GlobalVoxelsInfo.allVoxelsInABlock().spliterator(), false).filter(container::hasPart).map(voxel -> new VoxelPosition(pos, voxel).getBB()).filter(entityBox::intersects).forEach(collisionBBs::add));
	}

	@Nullable
	@Override
	public RayTraceResult collisionRayTrace(IBlockState blockState, World world, BlockPos pos, Vec3d start, Vec3d end){
		return getContainer(world, pos).map(container -> container.rayTrace(start, end)).orElse(null);
	}
}
