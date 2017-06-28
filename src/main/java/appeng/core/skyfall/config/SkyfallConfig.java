package appeng.core.skyfall.config;

import appeng.core.skyfall.api.generator.SkyobjectGenerator;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.HashMap;
import java.util.Map;

public class SkyfallConfig {

	public SkyfallConfig(){

	}

	private Map<ResourceLocation, Float> weights = new HashMap<>();

	public float getWeight(ResourceLocation gen){
		return weights.get(gen);
	}

	public void populateMissingWeights(IForgeRegistry<SkyobjectGenerator> registry){
		for(SkyobjectGenerator generator : registry) if(!weights.containsKey(generator.getRegistryName())) weights.put(generator.getRegistryName(), generator.getDefaultWeight());
	}

}
