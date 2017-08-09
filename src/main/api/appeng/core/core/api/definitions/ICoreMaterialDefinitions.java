package appeng.core.core.api.definitions;

import appeng.api.AEModInfo;
import appeng.api.definitions.IDefinitions;
import appeng.core.core.api.definition.IMaterialDefinition;
import appeng.core.core.api.material.Material;
import net.minecraft.util.ResourceLocation;

public interface ICoreMaterialDefinitions extends IDefinitions<Material, IMaterialDefinition<Material>> {

	default IMaterialDefinition<Material> certusQuartz(){
		return get(new ResourceLocation(AEModInfo.MODID, "certus_quartz"));
	}

	default IMaterialDefinition<Material> certusRedstone(){
		return get(new ResourceLocation(AEModInfo.MODID, "certus_redstone"));
	}

	default IMaterialDefinition<Material> certusSulfur(){
		return get(new ResourceLocation(AEModInfo.MODID, "certus_sulfur"));
	}

	default IMaterialDefinition<Material> incertus(){
		return get(new ResourceLocation(AEModInfo.MODID, "incertus"));
	}

}
