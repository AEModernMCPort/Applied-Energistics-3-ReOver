package appeng.core.core.definitions;

import appeng.api.bootstrap.DefinitionFactory;
import appeng.core.core.api.definition.IMaterialDefinition;
import appeng.core.AppEng;
import appeng.core.core.api.bootstrap.IMaterialBuilder;
import appeng.core.core.api.definitions.ICoreMaterialDefinitions;
import appeng.core.core.api.material.Material;
import appeng.core.lib.definitions.Definitions;
import net.minecraft.util.ResourceLocation;

public class CoreMaterialDefinitions extends Definitions<Material, IMaterialDefinition<Material>> implements ICoreMaterialDefinitions {

	public CoreMaterialDefinitions(DefinitionFactory registry){
		IMaterialDefinition<Material> m = registry.<Material, IMaterialDefinition<Material>, IMaterialBuilder<Material, ?>, Material>definitionBuilder(new ResourceLocation(AppEng.MODID, "invalid"), ih(new Material())).setFeature(null).build();
	}

	private DefinitionFactory.InputHandler<Material, Material> ih(Material material){
		return new DefinitionFactory.InputHandler<Material, Material>(material) {};
	}

}
