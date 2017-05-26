
package appeng.core.lib.bootstrap;


import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;

import appeng.api.definitions.IBiomeDefinition;
import appeng.core.lib.definitions.BiomeDefinition;


public class BiomeDefinitionBuilder<B extends Biome> extends DefinitionBuilder<B, B, IBiomeDefinition<B>, BiomeDefinitionBuilder<B>> implements IBiomeBuilder<B, BiomeDefinitionBuilder<B>>
{

	BiomeDefinitionBuilder( FeatureFactory factory, ResourceLocation registryName, B biome )
	{
		super( factory, registryName, biome );
	}

	@Override
	public IBiomeDefinition<B> def( B biome )
	{
		return new BiomeDefinition( registryName, biome );
	}
}
