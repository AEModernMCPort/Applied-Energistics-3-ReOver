package appeng.api.recipe;

import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

public interface IGRecipeRegistry<R extends IGRecipe<R>> extends IForgeRegistryEntry<IGRecipeRegistry<R>>, IForgeRegistry<R> {

	@Override
	default Class<IGRecipeRegistry<R>> getRegistryType(){
		return (Class) IGRecipeRegistry.class;
	}

}
