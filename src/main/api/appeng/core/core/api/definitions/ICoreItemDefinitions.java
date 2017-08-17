package appeng.core.core.api.definitions;

import appeng.api.AEModInfo;
import appeng.api.definitions.IDefinitions;
import appeng.core.core.api.definition.IItemDefinition;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

public interface ICoreItemDefinitions extends IDefinitions<Item, IItemDefinition<Item>> {

	default IItemDefinition<Item> material(){
		return get("material");
	}

	default IItemDefinition<Item> certusQuartz(){
		return get(new ResourceLocation(AEModInfo.MODID, "certus_quartz"));
	}

	default IItemDefinition<Item> certusRedstone(){
		return get(new ResourceLocation(AEModInfo.MODID, "certus_redstone"));
	}

	default IItemDefinition<Item> certusSulfur(){
		return get(new ResourceLocation(AEModInfo.MODID, "certus_sulfur"));
	}

	default IItemDefinition<Item> certusEnderium(){
		return get(new ResourceLocation(AEModInfo.MODID, "certus_enderium"));
	}



	default IItemDefinition<Item> incertus(){
		return get(new ResourceLocation(AEModInfo.MODID, "incertus"));
	}

	default IItemDefinition<Item> supersolidCertus(){
		return get(new ResourceLocation(AEModInfo.MODID, "supersolid_certus"));
	}

}
