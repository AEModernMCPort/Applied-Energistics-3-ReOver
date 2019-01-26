package code.elix_x.excore.utils.client.resource;

import code.elix_x.excomms.reflection.ReflectionHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.resources.SimpleReloadableResourceManager;

import javax.annotation.Nonnull;
import java.util.List;

public class ResourcePacksHelper {

	private static final List<IResourcePack> defaultResourcePacks = new ReflectionHelper.AClass<>(Minecraft.class).<List<IResourcePack>>getDeclaredField("defaultResourcePacks", "field_110449_ao").orElseThrow(() -> new IllegalArgumentException("Failed to reflect fields necessary for resource packs helper")).setAccessible(true).get(Minecraft.getMinecraft()).get();

	public static void addDefaultResourcePack(@Nonnull IResourcePack resourcePack){
		defaultResourcePacks.add(resourcePack);
		addReloadResourcePack(resourcePack);
	}

	public static void addReloadResourcePack(@Nonnull IResourcePack resourcePack){
		((SimpleReloadableResourceManager) Minecraft.getMinecraft().getResourceManager()).reloadResourcePack(resourcePack);
	}

}
