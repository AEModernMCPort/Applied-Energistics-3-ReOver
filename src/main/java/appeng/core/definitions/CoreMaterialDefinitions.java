
package appeng.core.definitions;


import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.util.ResourceLocation;

import appeng.api.definitions.IMaterialDefinition;
import appeng.core.AppEng;
import appeng.core.api.definitions.ICoreMaterialDefinitions;
import appeng.core.api.material.Material;
import appeng.core.lib.bootstrap.FeatureFactory;
import appeng.core.lib.definitions.Definitions;


public class CoreMaterialDefinitions extends Definitions<Material, IMaterialDefinition<Material>> implements ICoreMaterialDefinitions
{

	private static final String MATERIALSMODELSLOCATION = "material/";
	private static final String MATERIALSMODELSVARIANT = "inventory";

	public CoreMaterialDefinitions( FeatureFactory registry )
	{
		init();
	}

}
