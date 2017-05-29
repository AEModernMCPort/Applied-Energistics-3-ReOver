package appeng.core.definitions;

import appeng.api.definitions.IMaterialDefinition;
import appeng.core.AppEng;
import appeng.core.api.definitions.ICoreMaterialDefinitions;
import appeng.core.api.material.Material;
import appeng.core.lib.bootstrap_olde.FeatureFactory;
import appeng.core.lib.definitions.Definitions;
import net.minecraft.util.ResourceLocation;

public class CoreMaterialDefinitions extends Definitions<Material, IMaterialDefinition<Material>>
		implements ICoreMaterialDefinitions {

	private static final String MATERIALSMODELSLOCATION = "material/";
	private static final String MATERIALSMODELSVARIANT = "inventory";

	public CoreMaterialDefinitions(FeatureFactory registry){
		registry.material(new ResourceLocation(AppEng.MODID, "invalid"), new Material()).build();

		init();
	}

}
