package appeng.core.core.block;

import appeng.core.core.AppEngCore;
import appeng.core.core.crafting.ion.CraftingIonRegistry;
import appeng.core.core.tile.IonEnvironmentTile;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidFinite;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;
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

	@Override
	public void onEntityCollidedWithBlock(World world, BlockPos pos, IBlockState state, Entity entity){
		if(entity instanceof EntityItem && ((EntityItem) entity).getItem().hasCapability(CraftingIonRegistry.ionProviderCapability, null)) AppEngCore.INSTANCE.getCraftingIonRegistry().onIonEntityItemEnterEnvironment(world, pos, (EntityItem) entity, ((EntityItem) entity).getItem().getCapability(CraftingIonRegistry.ionProviderCapability, null));
	}

	@Override
	public int place(World world, BlockPos pos, @Nonnull FluidStack fluidStack, boolean doPlace){
		int placed = super.place(world, pos, fluidStack, doPlace);
		if(placed > 0 && doPlace) if(fluidStack.tag != null && fluidStack.tag.hasKey("environment")) world.getTileEntity(pos).getCapability(CraftingIonRegistry.ionEnvironmentCapability, null).deserializeNBT(fluidStack.tag.getCompoundTag("environment"));
		return placed;
	}

	@Override
	public FluidStack drain(World world, BlockPos pos, boolean doDrain){
		FluidStack drained = super.drain(world, pos, doDrain);
		if(drained.tag == null) drained.tag = new NBTTagCompound();
		drained.tag.setTag("environment", world.getTileEntity(pos).getCapability(CraftingIonRegistry.ionEnvironmentCapability, null).serializeNBT());
		return drained;
	}
}
