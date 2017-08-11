package appeng.core.core.material;

import appeng.core.core.AppEngCore;
import appeng.core.core.api.material.Material;
import appeng.core.core.crafting.ion.CraftingIonRegistry;
import net.minecraft.entity.item.EntityItem;

import java.util.Optional;

public class IonMaterial extends Material {

	public IonMaterial(){

	}

	@Override
	public boolean onEntityItemUpdate(EntityItem entityItem){
		Optional.ofNullable(entityItem.getItem().getCapability(CraftingIonRegistry.ionProviderCapability, null)).ifPresent(ionProvider -> AppEngCore.INSTANCE.getCraftingIonRegistry().onIonEntityItemTick(entityItem, ionProvider));
		return false;
	}
}
