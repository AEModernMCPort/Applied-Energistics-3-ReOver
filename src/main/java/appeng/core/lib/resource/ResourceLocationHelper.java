package appeng.core.lib.resource;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

public class ResourceLocationHelper {

	public static ResourceLocation parent(ResourceLocation resourceLocation){
		return new ResourceLocation(resourceLocation.getNamespace(), resourceLocation.getPath().substring(0, resourceLocation.getPath().lastIndexOf('/')));
	}

	public static ResourceLocation relative(ResourceLocation origin, String location){
		if(location.contains("/")){
			return new ResourceLocation(origin.getNamespace(), origin.getPath() + (origin.getPath().endsWith("/") || location.startsWith("/") ? "" : "/") + location);
		} else if(origin.getPath().contains(".")){
			return relative(parent(origin), location);
		} else {
			return relative(origin, "/" + location);
		}
	}

	public static NBTTagCompound serialize(ResourceLocation resourceLocation){
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setString("domain", resourceLocation.getNamespace());
		nbt.setString("path", resourceLocation.getPath());
		return nbt;
	}

	public static ResourceLocation deserialize(NBTTagCompound nbt){
		return new ResourceLocation(nbt.getString("domain"), nbt.getString("path"));
	}

}