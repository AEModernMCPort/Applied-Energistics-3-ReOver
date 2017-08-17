package appeng.core.skyfall.config;

import appeng.api.bootstrap.InitializationComponent;
import appeng.api.config.ConfigCompilable;
import appeng.core.AppEng;
import appeng.core.lib.util.BlockState2String;
import appeng.core.skyfall.AppEngSkyfall;
import appeng.core.skyfall.api.generator.SkyobjectGenerator;
import com.google.common.collect.Lists;
import net.minecraft.util.ResourceLocation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SkyfallConfig implements ConfigCompilable, InitializationComponent.Init {

	private Map<ResourceLocation, Float> weights = new HashMap<>();

	public Meteorite meteorite = new Meteorite();

	public SkyfallConfig(){

	}

	@Override
	public void compile(){

	}

	@Override
	public void init(){
		for(SkyobjectGenerator generator : AppEngSkyfall.INSTANCE.getSkyobjectGeneratorsRegistry()) if(!weights.containsKey(generator.getRegistryName())) weights.put(generator.getRegistryName(), generator.getDefaultWeight());
		meteorite.init();
	}

	@Override
	public void decompile(){
		meteorite.decompile();
	}

	public float getWeight(ResourceLocation gen){
		return weights.get(gen);
	}

	public static class Meteorite {

		public float minRadius = 5;
		public float maxRadius = 110;
		public List<ResourceLocation> allowedBlocks = Lists.newArrayList(new ResourceLocation(AppEng.MODID,"skystone"), new ResourceLocation("minecraft:stone"), new ResourceLocation("minecraft:cobblestone"), new ResourceLocation("minecraft:ice"), new ResourceLocation("minecraft:obsidian"));

		public Meteorite(){

		}

		public void init(){
			minRadius = Math.min(minRadius, maxRadius);
			maxRadius = Math.max(minRadius, maxRadius);
			minRadius = Math.max(minRadius, 1);
			maxRadius = Math.min(maxRadius, 110);
			allowedBlocks = allowedBlocks.stream().filter(BlockState2String::isValidBlock).collect(Collectors.toList());
		}

		private void decompile(){

		}

	}

}
