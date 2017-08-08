package appeng.core.core.api.definitions;

import appeng.api.AEModInfo;
import appeng.api.definitions.IDefinitions;
import appeng.core.core.api.crafting.ion.Ion;
import appeng.core.core.api.definition.IIonDefinition;
import net.minecraft.util.ResourceLocation;

public interface ICoreIonDefinitions extends IDefinitions<Ion, IIonDefinition<Ion>> {
	
	default IIonDefinition<Ion> certus(){
		return get(new ResourceLocation(AEModInfo.MODID, "certus"));
	}

	default IIonDefinition<Ion> quartz(){
		return get(new ResourceLocation(AEModInfo.MODID, "quartz"));
	}

	default IIonDefinition<Ion> redstone(){
		return get(new ResourceLocation(AEModInfo.MODID, "redstone"));
	}

	default IIonDefinition<Ion> sulfur(){
		return get(new ResourceLocation(AEModInfo.MODID, "sulfur"));
	}

	default IIonDefinition<Ion> ender(){
		return get(new ResourceLocation(AEModInfo.MODID, "ender"));
	}
	
}
