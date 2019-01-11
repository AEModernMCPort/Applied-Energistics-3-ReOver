package appeng.core.lib.recipe;

import appeng.api.recipe.IGRecipe;
import appeng.api.recipe.IGRecipeRegistry;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.registries.RegistryBuilder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GRecipeRegistry<R extends IGRecipe<R>> extends IForgeRegistryEntry.Impl<IGRecipeRegistry<R>> implements IGRecipeRegistry<R> {

	private final IForgeRegistry<R> registryDelegate;

	public GRecipeRegistry(ResourceLocation id, Class<R> type){
		registryDelegate = new RegistryBuilder<R>().setName(new ResourceLocation(id.getNamespace(), "recipe/" + id.getPath())).setType(type).disableSaving().create();
		setRegistryName(id);
	}

	@Override
	public Class<R> getRegistrySuperType(){
		return registryDelegate.getRegistrySuperType();
	}

	@Override
	public void register(R value){
		registryDelegate.register(value);
	}

	@Override
	public void registerAll(R... values){
		registryDelegate.registerAll(values);
	}

	@Override
	public boolean containsKey(ResourceLocation key){
		return registryDelegate.containsKey(key);
	}

	@Override
	public boolean containsValue(R value){
		return registryDelegate.containsValue(value);
	}

	@Nullable
	@Override
	public R getValue(ResourceLocation key){
		return registryDelegate.getValue(key);
	}

	@Nullable
	@Override
	public ResourceLocation getKey(R value){
		return registryDelegate.getKey(value);
	}

	@Nonnull
	@Override
	public Set<ResourceLocation> getKeys(){
		return registryDelegate.getKeys();
	}

	@Nonnull
	@Override
	public List<R> getValues(){
		return registryDelegate.getValues();
	}

	@Nonnull
	@Override
	public Set<Map.Entry<ResourceLocation, R>> getEntries(){
		return registryDelegate.getEntries();
	}

	@Override
	public <T> T getSlaveMap(ResourceLocation slaveMapName, Class<T> type){
		return registryDelegate.getSlaveMap(slaveMapName, type);
	}

	@Override
	public Iterator<R> iterator(){
		return registryDelegate.iterator();
	}
}
