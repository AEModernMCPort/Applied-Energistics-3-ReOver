package appeng.core.spatial.definitions;

import appeng.api.bootstrap.DefinitionFactory;
import appeng.api.definitions.IBiomeDefinition;
import appeng.core.lib.definitions.Definitions;
import appeng.core.spatial.api.definitions.ISpatialBiomeDefinitions;
import net.minecraft.world.biome.Biome;

public class SpatialBiomeDefinitions extends Definitions<Biome, IBiomeDefinition<Biome>>
		implements ISpatialBiomeDefinitions {

	public SpatialBiomeDefinitions(DefinitionFactory registry){
		init();
	}

}
