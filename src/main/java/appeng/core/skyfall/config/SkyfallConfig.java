package appeng.core.skyfall.config;

import appeng.core.api.definitions.ICoreBlockDefinitions;
import appeng.core.core.AppEngCore;
import appeng.core.core.block.SkystoneBlock;
import appeng.core.lib.util.BlockState2String;
import appeng.core.skyfall.api.generator.SkyobjectGenerator;
import appeng.core.skyfall.block.CertusInfusedBlock;
import com.google.common.base.Predicates;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.*;
import java.util.stream.Collectors;

public class SkyfallConfig {

	private Map<ResourceLocation, Float> weights = new HashMap<>();

	public Meteorite meteorite = new Meteorite();

	public SkyfallConfig(){

	}

	public float getWeight(ResourceLocation gen){
		return weights.get(gen);
	}

	public void initPostLoad(IForgeRegistry<SkyobjectGenerator> registry){
		for(SkyobjectGenerator generator : registry) if(!weights.containsKey(generator.getRegistryName())) weights.put(generator.getRegistryName(), generator.getDefaultWeight());
		meteorite.initPostLoad();
	}

	public static class Meteorite {

		public float minRadius = 5;
		public float maxRadius = 110;
		public Set<String> allowedBlocks = new HashSet<>();

		public Meteorite(){

		}

		public void populateFromStates(IBlockState... defaultStates){
			allowedBlocks = Arrays.stream(defaultStates).filter(Predicates.notNull()).map(state -> BlockState2String.toString(state)).collect(Collectors.toSet());
		}

		public void initPostLoad(){
			if(allowedBlocks.isEmpty()) populateFromStates(AppEngCore.INSTANCE.<Block, ICoreBlockDefinitions>definitions(Block.class).skystone().maybe().map(block -> block.getDefaultState().withProperty(SkystoneBlock.VARIANT, SkystoneBlock.Variant.STONE)).orElse(null), AppEngCore.INSTANCE.<Block, ICoreBlockDefinitions>definitions(Block.class).skystone().maybe().map(block -> block.getDefaultState().withProperty(SkystoneBlock.VARIANT, SkystoneBlock.Variant.BLOCK)).orElse(null), Blocks.STONE.getDefaultState(), Blocks.OBSIDIAN.getDefaultState(), Blocks.ICE.getDefaultState());

			minRadius = Math.max(minRadius, 1);
			maxRadius = Math.min(maxRadius, 110);
//			CertusInfusedBlock.recompile(this);
		}

	}

}
