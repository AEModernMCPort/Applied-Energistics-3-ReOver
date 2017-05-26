
package appeng.core.spatial.definitions;


import net.minecraft.world.DimensionType;

import appeng.api.definitions.IDimensionTypeDefinition;
import appeng.core.lib.bootstrap.FeatureFactory;
import appeng.core.lib.definitions.Definitions;
import appeng.core.spatial.api.definitions.ISpatialDimensionTypeDefinitions;
import appeng.core.spatial.world.StorageWorldProvider;


public class SpatialDimensionTypeDefinitions extends Definitions<DimensionType, IDimensionTypeDefinition<DimensionType>> implements ISpatialDimensionTypeDefinitions
{

	public SpatialDimensionTypeDefinitions( FeatureFactory registry )
	{
		init();
	}

}
