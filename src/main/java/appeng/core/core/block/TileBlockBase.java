package appeng.core.core.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public class TileBlockBase<T extends TileEntity> extends Block {

	protected BiFunction<World, IBlockState, T> tile;

	public TileBlockBase(Material blockMaterialIn, MapColor blockMapColorIn, BiFunction<World, IBlockState, T> tile){
		super(blockMaterialIn, blockMapColorIn);
		this.tile = tile;
	}

	public TileBlockBase(Material blockMaterialIn, MapColor blockMapColorIn, Supplier<T> tile){
		this(blockMaterialIn, blockMapColorIn, (world, integer) -> tile.get());
	}

	public TileBlockBase(Material materialIn, BiFunction<World, IBlockState, T> tile){
		super(materialIn);
		this.tile = tile;
	}

	public TileBlockBase(Material materialIn, Supplier<T> tile){
		this(materialIn, (world, integer) -> tile.get());
	}

	@Override
	public boolean hasTileEntity(IBlockState state){
		return true;
	}

	@Nullable
	@Override
	public TileEntity createTileEntity(World worldIn, IBlockState state){
		return tile.apply(worldIn, state);
	}

	public T getTileEntity(IBlockAccess world, BlockPos pos){
		return (T) world.getTileEntity(pos);
	}

}
