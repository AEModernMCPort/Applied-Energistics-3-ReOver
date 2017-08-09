package appeng.core.lib.entry;

import appeng.api.entry.TileRegistryEntry;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import java.util.function.BiFunction;

public class TileRegistryEntryImpl<T extends TileEntity> implements TileRegistryEntry<T> {

	public static <T extends TileEntity> BiFunction<World, IBlockState, T> createFromConstructor(Class<T> rClass){
		/*ReflectionHelper.AClass<T> clas = new ReflectionHelper.AClass<>(rClass);
		ReflectionHelper.AConstructor<T> constructor;
		if((constructor = clas.getDeclaredConstructor(World.class, int.class)) != null && constructor.isAccessible()){
			ReflectionHelper.AConstructor<T> aConstructor = constructor;
			return (world, meta) -> aConstructor.newInstance(world, meta);
		} else if((constructor = clas.getDeclaredConstructor(int.class, World.class)) != null && constructor.isAccessible()){
			ReflectionHelper.AConstructor<T> aConstructor = constructor;
			return (world, meta) -> aConstructor.newInstance(meta, world);
		} else if((constructor = clas.getDeclaredConstructor(World.class)) != null && constructor.isAccessible()){
			ReflectionHelper.AConstructor<T> aConstructor = constructor;
			return (world, meta) -> aConstructor.newInstance(world);
		} else if((constructor = clas.getDeclaredConstructor(int.class)) != null && constructor.isAccessible()){
			ReflectionHelper.AConstructor<T> aConstructor = constructor;
			return (world, meta) -> aConstructor.newInstance(meta);
		} else if((constructor = clas.getDeclaredConstructor()) != null && constructor.isAccessible()){
			ReflectionHelper.AConstructor<T> aConstructor = constructor;
			return (world, meta) -> aConstructor.newInstance();
		}
		throw new IllegalArgumentException("Tried to create tile registry entry impl by using class constructor as instantiator, but tile does not have any (accessible) one(s)!");*/
		return null;
	}

	private final ResourceLocation registryName;
	private final Class<T> tileClass;
	private final BiFunction<World, IBlockState, T> instantiator;

	public TileRegistryEntryImpl(ResourceLocation registryName, Class<T> tileClass, BiFunction<World, IBlockState, T> instantiator){
		this.registryName = registryName;
		this.tileClass = tileClass;
		this.instantiator = instantiator;
	}

	public TileRegistryEntryImpl(ResourceLocation registryName, Class<T> tileClass){
		this(registryName, tileClass, createFromConstructor(tileClass));
	}

	@Override
	public ResourceLocation getRegistryName(){
		return registryName;
	}

	@Override
	public Class<T> getTileClass(){
		return tileClass;
	}

	@Override
	public T apply(World world, IBlockState meta){
		//TODO How badly do we need this?
		return instantiator.apply(world, meta);
	}

}
