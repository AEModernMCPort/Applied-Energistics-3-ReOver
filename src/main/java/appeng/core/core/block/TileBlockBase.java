package appeng.core.core.block;

import appeng.api.entry.TileRegistryEntry;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public class TileBlockBase<T extends TileEntity> extends Block implements ITileEntityProvider {

	protected BiFunction<World, Integer, T> tile;

	public TileBlockBase(Material blockMaterialIn, MapColor blockMapColorIn, BiFunction<World, Integer, T> tile){
		super(blockMaterialIn, blockMapColorIn);
		this.tile = tile;
	}

	public TileBlockBase(Material blockMaterialIn, MapColor blockMapColorIn, Supplier<T> tile){
		this(blockMaterialIn, blockMapColorIn, (world, integer) -> tile.get());
	}

	public TileBlockBase(Material materialIn, BiFunction<World, Integer, T> tile){
		super(materialIn);
		this.tile = tile;
	}

	public TileBlockBase(Material materialIn, Supplier<T> tile){
		this(materialIn, (world, integer) -> tile.get());
	}

	@Nullable
	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta){
		return tile.apply(worldIn, meta);
	}
}
