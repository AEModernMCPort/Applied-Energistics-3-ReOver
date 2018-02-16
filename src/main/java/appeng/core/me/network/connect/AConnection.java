package appeng.core.me.network.connect;

import appeng.core.me.api.network.block.Connection;
import net.minecraft.nbt.NBTBase;
import net.minecraft.util.ResourceLocation;

public abstract class AConnection<P extends Comparable<P>, N extends NBTBase> implements Connection<P, N> {

	private final ResourceLocation id;

	public AConnection(ResourceLocation id){
		this.id = id;
	}

	@Override
	public ResourceLocation getId(){
		return id;
	}

}
