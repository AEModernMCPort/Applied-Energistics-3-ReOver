package appeng.core.core.crafting.ion;

import appeng.core.core.AppEngCore;
import appeng.core.core.api.crafting.ion.IonEnvironment;
import appeng.core.core.api.crafting.ion.IonProvider;
import code.elix_x.excomms.color.RGBA;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Sets;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import org.apache.commons.lang3.mutable.MutableObject;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

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
			world.setBlockState(pos, ionized.getBlock().getDefaultState().withProperty(BlockFluidBase.LEVEL, 15));
			world.getTileEntity(pos).getCapability(AppEngCore.ionEnvironmentCapability, null).addIons(ionProvider);
			world.markBlockRangeForRenderUpdate(pos, pos);
			item.setDead();
		} else if(ionized2normal.containsKey(fluid)){
			world.getTileEntity(pos).getCapability(AppEngCore.ionEnvironmentCapability, null).addIons(ionProvider);
			world.markBlockRangeForRenderUpdate(pos, pos);
			item.setDead();
		}
	}

	public RGBA getColor(IonEnvironment environment, RGBA original){
		/*MutableObject<RGBA> color = new MutableObject<>(original);
		environment.getIons().forEach(ion -> color.setValue(blend(color.getValue(), ion.getColorModifier(), amount2mul(environment.getAmount(ion)))));
		return color.getValue();*/
		Set<RGBA> colors = new HashSet<>();
		colors.add(original);
		environment.getIons().forEach(ion -> colors.add(new RGBA(ion.getColorModifier().getRF(), ion.getColorModifier().getGF(), ion.getColorModifier().getBF(), amount2mul(environment.getAmount(ion)))));
		return blend(colors);
	}

	public RGBA blend(Set<RGBA> colors){
		double aSum = colors.stream().mapToDouble(RGBA::getAF).sum();
		double r = colors.stream().mapToDouble(color -> color.getRF() * (color.getAF() / aSum)).sum();
		double g = colors.stream().mapToDouble(color -> color.getGF() * (color.getAF() / aSum)).sum();
		double b = colors.stream().mapToDouble(color -> color.getBF() * (color.getAF() / aSum)).sum();
		return new RGBA((float) r, (float) g, (float) b, 1f);
	}

	public float amount2mul(int amount){
		return -1f/(2*amount) + 1f;
	}

}
