package appeng.core.lib.entry;

import appeng.api.entry.TileRegistryEntry;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

public class TileRegistryEntryImpl<T extends TileEntity> implements TileRegistryEntry<T> {

	private final ResourceLocation registryName;
	private final Class<T> tileClass;

	public TileRegistryEntryImpl(ResourceLocation registryName, Class<T> tileClass){
		this.registryName = registryName;
		this.tileClass = tileClass;
	}

	@Override
	public ResourceLocation getRegistryName(){
		return registryName;
	}

	@Override
	public Class<T> getTileClass(){
		return tileClass;
	}
	
}
