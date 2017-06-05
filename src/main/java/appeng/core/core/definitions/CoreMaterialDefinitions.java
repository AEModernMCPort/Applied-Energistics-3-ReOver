package appeng.core.core.definitions;

import appeng.api.bootstrap.DefinitionFactory;
import appeng.api.definitions.IMaterialDefinition;
import appeng.core.AppEng;
import appeng.core.api.definitions.ICoreMaterialDefinitions;
import appeng.core.api.material.Material;
import appeng.core.lib.definitions.Definitions;
import net.minecraft.util.ResourceLocation;

public class CoreMaterialDefinitions extends Definitions<Material, IMaterialDefinition<Material>> implements ICoreMaterialDefinitions {

	private static final String MATERIALSMODELSLOCATION = "material/";
	private static final String MATERIALSMODELSVARIANT = "inventory";

	public CoreMaterialDefinitions(DefinitionFactory registry){
		registry.definitionBuilder(new ResourceLocation(AppEng.MODID, "invalid"), ih(new Material())).setFeature(null).build();
	}

	private DefinitionFactory.InputHandler<Material, Material> ih(Material material){
		return new DefinitionFactory.InputHandler<Material, Material>(material) {};
	}

}
