package appeng.core.lib.client.resource;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;

public class ClientResourceHelper {

	public static void registerReloadListener(IResourceManagerReloadListener listener){
		((IReloadableResourceManager) Minecraft.getMinecraft().getResourceManager()).registerReloadListener(listener);
	}

}
