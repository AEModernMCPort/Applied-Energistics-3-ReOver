package appeng.api.recipe;

import net.minecraftforge.registries.IForgeRegistryEntry;

public interface IGRecipe<R extends IGRecipe<R>> extends IForgeRegistryEntry<R> {

	default String getGroup(){
		return "";
	}

}
