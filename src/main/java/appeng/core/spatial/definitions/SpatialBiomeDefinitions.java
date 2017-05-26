
package appeng.core.spatial.definitions;


import net.minecraft.world.biome.Biome;

import appeng.api.definitions.IBiomeDefinition;
import appeng.core.lib.bootstrap.FeatureFactory;
import appeng.core.lib.definitions.Definitions;
import appeng.core.spatial.api.definitions.ISpatialBiomeDefinitions;
import appeng.core.spatial.world.BiomeGenStorage;


public class SpatialBiomeDefinitions extends Definitions<Biome, IBiomeDefinition<Biome>> implements ISpatialBiomeDefinitions
{

	public SpatialBiomeDefinitions( FeatureFactory registry )
	{
		init();
	}

}
