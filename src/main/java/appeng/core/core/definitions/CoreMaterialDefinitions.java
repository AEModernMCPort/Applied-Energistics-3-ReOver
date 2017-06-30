package appeng.core.core.definitions;

import appeng.api.bootstrap.DefinitionFactory;
import appeng.api.bootstrap.IDefinitionBuilder;
import appeng.api.definitions.IMaterialDefinition;
import appeng.core.AppEng;
import appeng.core.api.bootstrap.IMaterialBuilder;
import appeng.core.api.definitions.ICoreMaterialDefinitions;
import appeng.core.api.material.Material;
import appeng.core.lib.definitions.Definitions;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;

public class CoreMaterialDefinitions extends Definitions<Material, IMaterialDefinition<Material>> implements ICoreMaterialDefinitions {

	public CoreMaterialDefinitions(DefinitionFactory registry){
		IMaterialDefinition<Material> m = registry.<Material, IMaterialDefinition<Material>, IMaterialBuilder<Material, ?>, Material>definitionBuilder(new ResourceLocation(AppEng.MODID, "invalid"), ih(new Material())).setFeature(null).model(new ResourceLocation(AppEng.MODID, "invalid")).build();
	}

	private DefinitionFactory.InputHandler<Material, Material> ih(Material material){
		return new DefinitionFactory.InputHandler<Material, Material>(material) {};
	}

}
