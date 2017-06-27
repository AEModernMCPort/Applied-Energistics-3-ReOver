package appeng.api.recipe;

import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

public interface IGRecipeRegistry<R extends IGRecipe<R>> extends IForgeRegistryEntry<IGRecipeRegistry>, IForgeRegistry<R> {

	@Override
	default Class<IGRecipeRegistry> getRegistryType(){
		return IGRecipeRegistry.class;
	}
	
}
