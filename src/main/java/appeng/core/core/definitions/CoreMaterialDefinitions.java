package appeng.core.core.definitions;

import appeng.api.bootstrap.DefinitionFactory;
import appeng.core.core.api.definition.IMaterialDefinition;
import appeng.core.AppEng;
import appeng.core.core.api.bootstrap.IMaterialBuilder;
import appeng.core.core.api.definitions.ICoreMaterialDefinitions;
import appeng.core.core.api.material.Material;
import appeng.core.core.bootstrap.component.RegisterToOredictComponent;
import appeng.core.core.material.IonMaterial;
import appeng.core.lib.definitions.Definitions;
import net.minecraft.util.ResourceLocation;

public class CoreMaterialDefinitions extends Definitions<Material, IMaterialDefinition<Material>> implements ICoreMaterialDefinitions {

	private final IMaterialDefinition certusQuartz;
	private final IMaterialDefinition certusRedstone;
	private final IMaterialDefinition certusSulfur;
	private final IMaterialDefinition incertus;

	public CoreMaterialDefinitions(DefinitionFactory registry){
		IMaterialDefinition<Material> m = registry.<Material, IMaterialDefinition<Material>, IMaterialBuilder<Material, ?>, Material>definitionBuilder(new ResourceLocation(AppEng.MODID, "invalid"), ih(new Material())).setFeature(null).build();
		certusQuartz = registry.<IonMaterial, IMaterialDefinition<IonMaterial>, IMaterialBuilder<IonMaterial, ?>, Material>definitionBuilder(new ResourceLocation(AppEng.MODID, "certus_quartz"), ih(new IonMaterial())).initializationComponent(null, new RegisterToOredictComponent.Material<>("certusQuartz")).build();
		certusRedstone = registry.<IonMaterial, IMaterialDefinition<IonMaterial>, IMaterialBuilder<IonMaterial, ?>, Material>definitionBuilder(new ResourceLocation(AppEng.MODID, "certus_redstone"), ih(new IonMaterial())).initializationComponent(null, new RegisterToOredictComponent.Material<>("certusRedstone")).build();
		certusSulfur = registry.<IonMaterial, IMaterialDefinition<IonMaterial>, IMaterialBuilder<IonMaterial, ?>, Material>definitionBuilder(new ResourceLocation(AppEng.MODID, "certus_sulfur"), ih(new IonMaterial())).initializationComponent(null, new RegisterToOredictComponent.Material<>("certusSulfur")).build();
		incertus = registry.<IonMaterial, IMaterialDefinition<IonMaterial>, IMaterialBuilder<IonMaterial, ?>, Material>definitionBuilder(new ResourceLocation(AppEng.MODID, "incertus"), ih(new IonMaterial())).initializationComponent(null, new RegisterToOredictComponent.Material<>("incertus")).build();
	}

	private DefinitionFactory.InputHandler<Material, Material> ih(Material material){
		return new DefinitionFactory.InputHandler<Material, Material>(material) {};
	}

}
