package appeng.core.core.bootstrap;

import appeng.api.bootstrap.DefinitionFactory;
import appeng.core.core.api.definition.IBiomeDefinition;
import appeng.core.core.api.bootstrap.IBiomeBuilder;
import appeng.core.lib.bootstrap.DefinitionBuilder;
import appeng.core.core.definition.BiomeDefinition;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;

public class BiomeDefinitionBuilder<B extends Biome> extends DefinitionBuilder<B, B, IBiomeDefinition<B>, BiomeDefinitionBuilder<B>> implements IBiomeBuilder<B, BiomeDefinitionBuilder<B>> {

	public BiomeDefinitionBuilder(DefinitionFactory factory, ResourceLocation registryName, B biome){
		super(factory, registryName, biome, "biome");
	}

	@Override
	public IBiomeDefinition<B> def(B biome){
		if(biome == null) return new BiomeDefinition<>(registryName, null);

		return new BiomeDefinition(registryName, biome);
	}
}
