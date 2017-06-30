package appeng.core.skyfall.config;

import appeng.core.api.definitions.ICoreBlockDefinitions;
import appeng.core.core.AppEngCore;
import appeng.core.core.block.SkystoneBlock;
import appeng.core.lib.util.BlockState2String;
import appeng.core.skyfall.api.generator.SkyobjectGenerator;
import appeng.core.skyfall.block.CertusInfusedBlock;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
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
		private Set<String> allowedBlocks;
		private transient ImmutableSet<IBlockState> allowedBlockStatesSet;
		private transient ImmutableList<IBlockState> allowedBlockStatesList;

		public Meteorite(IBlockState... defaultStates){
			allowedBlockStatesSet = ImmutableSet.copyOf(Arrays.stream(defaultStates).filter(Predicates.notNull()).collect(Collectors.toSet()));
			allowedBlocks = allowedBlockStatesSet.stream().map(state -> BlockState2String.toString(state)).collect(Collectors.toSet());
		}

		public Meteorite(){
			this(AppEngCore.INSTANCE.<Block, ICoreBlockDefinitions>definitions(Block.class).skystone().maybe().map(block -> block.getDefaultState().withProperty(SkystoneBlock.VARIANT, SkystoneBlock.Variant.STONE)).orElse(null), AppEngCore.INSTANCE.<Block, ICoreBlockDefinitions>definitions(Block.class).skystone().maybe().map(block -> block.getDefaultState().withProperty(SkystoneBlock.VARIANT, SkystoneBlock.Variant.BLOCK)).orElse(null), Blocks.STONE.getDefaultState(), Blocks.OBSIDIAN.getDefaultState(), Blocks.ICE.getDefaultState());
		}

		public void initPostLoad(){
			allowedBlockStatesSet = ImmutableSet.copyOf(allowedBlocks.stream().limit(16).map(s -> BlockState2String.fromString(s)).collect(Collectors.toSet()));
			allowedBlockStatesList = ImmutableList.copyOf(allowedBlockStatesSet);
			minRadius = Math.max(minRadius, 1);
			maxRadius = Math.min(maxRadius, 110);
			CertusInfusedBlock.recompile(this);
		}

		public ImmutableSet<IBlockState> allowedBlockStatesSet(){
			return allowedBlockStatesSet;
		}

		public ImmutableList<IBlockState> allowedBlockStatesList(){
			return allowedBlockStatesList;
		}

	}

}
