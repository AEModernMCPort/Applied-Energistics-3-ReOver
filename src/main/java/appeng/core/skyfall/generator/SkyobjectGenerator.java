package appeng.core.skyfall.generator;

import net.minecraftforge.registries.IForgeRegistryEntry;

public abstract class SkyobjectGenerator extends IForgeRegistryEntry.Impl<appeng.core.skyfall.api.generator.SkyobjectGenerator> implements appeng.core.skyfall.api.generator.SkyobjectGenerator {

	private float defaultWeight;

	public SkyobjectGenerator(float defaultWeight){
		this.defaultWeight = defaultWeight;
	}

	@Override
	public float getDefaultWeight(){
		return defaultWeight;
	}
}
