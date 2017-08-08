package appeng.core.core.client.render.color;

import appeng.core.core.AppEngCore;
import code.elix_x.excomms.color.RGBA;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fluids.Fluid;

import javax.annotation.Nullable;

public class IonEnvironmentFluidBlockColor implements IBlockColor {

	public Fluid original;

	public IonEnvironmentFluidBlockColor(Fluid original){
		this.original = original;
	}

	@Override
	public int colorMultiplier(IBlockState state, @Nullable IBlockAccess world, @Nullable BlockPos pos, int tintIndex){
		return AppEngCore.INSTANCE.getCraftingIonRegistry().getColor(world.getTileEntity(pos).getCapability(AppEngCore.ionEnvironmentCapability, null), new RGBA(1f, 1f, 1f)).argb();
	}
}
