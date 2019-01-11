package appeng.core.me.block;

import appeng.core.core.block.TileBlockBase;
import appeng.core.lib.block.property.UnlistedPropertyGeneric;
import appeng.core.lib.raytrace.RayTraceHelper;
import appeng.core.me.AppEngME;
import appeng.core.me.api.parts.VoxelPosition;
import appeng.core.me.api.parts.VoxelPositionSide;
import appeng.core.me.api.parts.container.GlobalVoxelsInfo;
import appeng.core.me.api.parts.container.IPartsContainer;
import appeng.core.me.api.parts.container.PartsAccess;
import appeng.core.me.parts.part.PartsHelperImpl;
import appeng.core.me.tile.PartsContainerTile;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import org.apache.commons.lang3.mutable.MutableObject;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

public class PartsContainerBlock extends TileBlockBase<PartsContainerTile> {

	public PartsContainerBlock(){
		super(Material.IRON, PartsContainerTile::new);
	}

	public Optional<IPartsContainer> getContainer(IBlockAccess world, BlockPos pos){
		return Optional.ofNullable(world).map(w -> this.getTileEntity(w, pos)).map(tile -> tile.getCapability(PartsHelperImpl.partsContainerCapability, null));
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
	public BlockRenderLayer getRenderLayer(){
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

	/*
	 * Interaction
	 */

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ){
		RayTraceResult trace = RayTraceHelper.rayTrace(player);
		if(trace.hitInfo instanceof VoxelPosition){
			PartsAccess.Mutable partsAccess = world.getCapability(PartsHelperImpl.worldPartsAccessCapability, null);
			MutableObject<EnumActionResult> result = new MutableObject<>(EnumActionResult.PASS);
			partsAccess.getPart((VoxelPosition) trace.hitInfo).ifPresent(part -> result.setValue(part.getPart().onRightClick(part.getState().orElse(null), partsAccess, world, player, hand, part.getState().flatMap(s -> AppEngME.INSTANCE.getPartsHelper().getPlayerInterface(s, new VoxelPositionSide((VoxelPosition) trace.hitInfo, trace.sideHit))).orElse(null))));
			return result.getValue() == EnumActionResult.SUCCESS;
		}
		return false;
	}

	/*
	 * NO!
	 * NEVER!
	 */

	@Override
	public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest){
		return false;
	}

}
