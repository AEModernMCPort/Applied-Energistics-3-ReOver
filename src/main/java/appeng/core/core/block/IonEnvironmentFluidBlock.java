package appeng.core.core.block;

import appeng.core.core.tile.IonEnvironmentTile;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidFinite;
import net.minecraftforge.fluids.Fluid;

import javax.annotation.Nullable;

public class IonEnvironmentFluidBlock extends BlockFluidFinite {

	protected Block original;

	public IonEnvironmentFluidBlock(Fluid fluid, Block original){
		super(fluid, original.getMaterial(original.getDefaultState()));
		this.original = original;
	}

	@Override
	public boolean hasTileEntity(IBlockState state){
		return true;
	}

	@Nullable
	@Override
	public TileEntity createTileEntity(World world, IBlockState state){
		return new IonEnvironmentTile(definedFluid);
	}

}
