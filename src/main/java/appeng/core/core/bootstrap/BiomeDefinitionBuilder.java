package appeng.core.core.bootstrap;

import appeng.api.definitions.IBiomeDefinition;
import appeng.core.api.bootstrap.IBiomeBuilder;
import appeng.core.lib.bootstrap.DefinitionBuilder;
import appeng.core.lib.bootstrap.DefinitionFactory;
import appeng.core.lib.bootstrap_olde.FeatureFactory;
import appeng.core.lib.definitions.BiomeDefinition;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;

public class BiomeDefinitionBuilder<B extends Biome>
		extends DefinitionBuilder<B, B, IBiomeDefinition<B>, BiomeDefinitionBuilder<B>>
		implements IBiomeBuilder<B, BiomeDefinitionBuilder<B>> {

	BiomeDefinitionBuilder(DefinitionFactory factory, ResourceLocation registryName, B biome){
		super(factory, registryName, biome);
	}

	@Override
	public IBiomeDefinition<B> def(B biome){
		return new BiomeDefinition(registryName, biome);
	}
}
