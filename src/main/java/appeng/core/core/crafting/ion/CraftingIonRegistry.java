package appeng.core.core.crafting.ion;

import appeng.core.core.AppEngCore;
import appeng.core.core.api.crafting.ion.IonProvider;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

import javax.annotation.Nonnull;

public class CraftingIonRegistry {

	public BiMap<Fluid, Fluid> normal2ionized = HashBiMap.create();
	public BiMap<Fluid, Fluid> ionized2normal = normal2ionized.inverse();

	public void registerEnvironmentFluid(@Nonnull Fluid fluid){
		normal2ionized.put(fluid, fluid);
	}

	public void registerIonVariant(Fluid original, Fluid ionized){
		normal2ionized.put(original, ionized);
	}

	public void onIonEntityItemTick(EntityItem item, IonProvider ionProvider){
		//TODO Expand to bounding box collision logic
		World world = item.world;
		BlockPos pos = new BlockPos(item.posX, item.posY, item.posZ);
		IBlockState block = world.getBlockState(pos);
		Fluid fluid = FluidRegistry.lookupFluidForBlock(block.getBlock());
		if(normal2ionized.containsKey(fluid) && ionProvider.isReactive(fluid)){
			Fluid ionized = normal2ionized.get(fluid);
			world.setBlockState(pos, ionized.getBlock().getDefaultState());
			world.getTileEntity(pos).getCapability(AppEngCore.ionEnvironmentCapability, null).addIons(ionProvider);
			item.setDead();
		}
	}

}
