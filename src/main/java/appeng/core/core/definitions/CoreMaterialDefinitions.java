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

	private final IMaterialDefinition certusQuartz;
	private final IMaterialDefinition certusRedstone;
	private final IMaterialDefinition certusSulfur;
	private final IMaterialDefinition incertus;

	public CoreMaterialDefinitions(DefinitionFactory registry){
		IMaterialDefinition<Material> m = registry.<Material, IMaterialDefinition<Material>, IMaterialBuilder<Material, ?>, Material>definitionBuilder(new ResourceLocation(AppEng.MODID, "invalid"), ih(new Material())).setFeature(null).build();
		certusQuartz = registry.<Material, IMaterialDefinition<Material>, IMaterialBuilder<Material, ?>, Material>definitionBuilder(new ResourceLocation(AppEng.MODID, "certus_quartz"), ih(new Material())).build();
		certusRedstone = registry.<Material, IMaterialDefinition<Material>, IMaterialBuilder<Material, ?>, Material>definitionBuilder(new ResourceLocation(AppEng.MODID, "certus_redstone"), ih(new Material())).build();
		certusSulfur = registry.<Material, IMaterialDefinition<Material>, IMaterialBuilder<Material, ?>, Material>definitionBuilder(new ResourceLocation(AppEng.MODID, "certus_sulfur"), ih(new Material())).build();
		incertus = registry.<Material, IMaterialDefinition<Material>, IMaterialBuilder<Material, ?>, Material>definitionBuilder(new ResourceLocation(AppEng.MODID, "incertus"), ih(new Material())).build();
	}

	private DefinitionFactory.InputHandler<Material, Material> ih(Material material){
		return new DefinitionFactory.InputHandler<Material, Material>(material) {};
	}

}
