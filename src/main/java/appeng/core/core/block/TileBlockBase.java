package appeng.core.core.block;

import appeng.api.entry.TileRegistryEntry;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class TileBlockBase<T extends TileEntity> extends Block implements ITileEntityProvider {

	private final TileRegistryEntry<T> tile;

	public TileBlockBase(Material blockMaterialIn, MapColor blockMapColorIn, TileRegistryEntry<T> tile){
		super(blockMaterialIn, blockMapColorIn);
		this.tile = tile;
	}

	public TileBlockBase(Material materialIn, TileRegistryEntry<T> tile){
		super(materialIn);
		this.tile = tile;
	}

	@Nullable
	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta){
		return null;
	}
}
