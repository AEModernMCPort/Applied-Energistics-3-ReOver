package appeng.core.api;

import com.google.common.reflect.TypeToken;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.*;
import net.minecraftforge.fml.common.registry.IForgeRegistryEntry;
import net.minecraftforge.fml.common.registry.PersistentRegistryManager;
import net.minecraftforge.fml.common.registry.RegistryDelegate;

import java.util.HashMap;
import java.util.Map;

/**
 * Interface version of {@linkplain IForgeRegistryEntry.Impl}.<br>
 * Temporarily in here. Will be moved soon.
 *
 * @param <T>
 * @author Elix_x
 */
public interface IForgeRegistryEntryImpl<T extends IForgeRegistryEntryImpl<T>> extends IForgeRegistryEntry<T> {

	@Override
	default T setRegistryName(ResourceLocation name){
		return Delegate.setRegistryName((T) this, name);
	}

	default T setRegistryName(String name){
		return Delegate.setRegistryName((T) this, name);
	}

	default T setRegistryName(String modID, String name){
		return Delegate.setRegistryName((T) this, modID, name);
	}

	@Override
	default ResourceLocation getRegistryName(){
		return Delegate.getRegistryName((T) this);
	}

	@Override
	default Class<T> getRegistryType(){
		return Delegate.getRegistryType((T) this);
	}

	class Delegate<T extends IForgeRegistryEntryImpl<T>> {

		private static Map<IForgeRegistryEntryImpl<?>, Delegate<?>> delegates = new HashMap<>();

		private static <T extends IForgeRegistryEntryImpl<T>> Delegate<T> getDelegate(T entry){
			Delegate delegate = delegates.get(entry);
			if(delegate == null){
				delegates.put(entry, delegate = new Delegate<>());
			}
			return delegate;
		}

		private static <T extends IForgeRegistryEntryImpl<T>> T setRegistryName(T entry, ResourceLocation name){
			return getDelegate(entry).setRegistryName(name);
		}

		private static <T extends IForgeRegistryEntryImpl<T>> T setRegistryName(T entry, String name){
			return getDelegate(entry).setRegistryName(name);
		}

		private static <T extends IForgeRegistryEntryImpl<T>> T setRegistryName(T entry, String modID, String name){
			return getDelegate(entry).setRegistryName(modID, name);
		}

		private static <T extends IForgeRegistryEntryImpl<T>> ResourceLocation getRegistryName(T entry){
			return getDelegate(entry).getRegistryName();
		}

		private static <T extends IForgeRegistryEntryImpl<T>> Class<T> getRegistryType(T entry){
			return getDelegate(entry).getRegistryType();
		}

		;

		/*
		 * Plain {@linkplain IForgeRegistryEntry.Impl} copypasta.
		 */

		private TypeToken<T> token = new TypeToken<T>(getClass()) {

		};
		private final RegistryDelegate<T> delegate = PersistentRegistryManager.makeDelegate((T) this, (Class<T>) token.getRawType());
		private ResourceLocation registryName = null;

		private Delegate(){

		}

		public final T setRegistryName(String name){
			if(getRegistryName() != null){
				throw new IllegalStateException("Attempted to set registry name with existing registry name! New: " + name + " Old: " + getRegistryName());
			}

			int index = name.lastIndexOf(':');
			String oldPrefix = index == -1 ? "" : name.substring(0, index);
			name = index == -1 ? name : name.substring(index + 1);
			ModContainer mc = Loader.instance().activeModContainer();
			String prefix = mc == null || (mc instanceof InjectedModContainer && ((InjectedModContainer) mc).wrappedContainer instanceof FMLContainer) ? "minecraft" : mc.getModId().toLowerCase();
			if(!oldPrefix.equals(prefix) && oldPrefix.length() > 0){
				FMLLog.bigWarning("Dangerous alternative prefix `%s` for name `%s`, expected `%s` invalid registry invocation/invalid name?", oldPrefix, name, prefix);
				prefix = oldPrefix;
			}
			this.registryName = new ResourceLocation(prefix, name);
			return (T) this;
		}

		// Helper functions
		public final T setRegistryName(ResourceLocation name){
			return setRegistryName(name.toString());
		}

		public final T setRegistryName(String modID, String name){
			return setRegistryName(modID + ":" + name);
		}

		public final ResourceLocation getRegistryName(){
			if(delegate.name() != null){
				return delegate.name();
			}
			return registryName != null ? registryName : null;
		}

		public final Class<T> getRegistryType(){
			return (Class<T>) token.getRawType();
		}

	}

}
