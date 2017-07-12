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
		private List<String> allowedBlocks = new ArrayList<>();

		public Meteorite(){

		}

		public void initPostLoad(){
			minRadius = Math.max(minRadius, 1);
			maxRadius = Math.min(maxRadius, 110);
		}

	}

}
