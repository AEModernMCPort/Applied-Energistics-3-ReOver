package appeng.core.spatial.definitions;

import appeng.api.definitions.IDimensionTypeDefinition;
import appeng.core.lib.bootstrap_olde.FeatureFactory;
import appeng.core.lib.definitions.Definitions;
import appeng.core.spatial.api.definitions.ISpatialDimensionTypeDefinitions;
import net.minecraft.world.DimensionType;

public class SpatialDimensionTypeDefinitions extends Definitions<DimensionType, IDimensionTypeDefinition<DimensionType>>
		implements ISpatialDimensionTypeDefinitions {

	public SpatialDimensionTypeDefinitions(FeatureFactory registry){
		init();
	}

}
