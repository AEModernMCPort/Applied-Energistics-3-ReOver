package appeng.core.core.api.crafting.ion;

import code.elix_x.excomms.color.RGBA;
import net.minecraftforge.registries.IForgeRegistryEntry;

public interface Ion extends IForgeRegistryEntry<Ion> {

	default RGBA getColorModifier(){
		return new RGBA(1f, 1f, 1f, 1f);
	}

}
