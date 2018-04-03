package appeng.core.me.network.connect;

import appeng.core.me.api.network.block.Connection;
import net.minecraft.nbt.NBTBase;
import net.minecraft.util.ResourceLocation;

public abstract class AConnection<P extends Comparable<P>, N extends NBTBase> implements Connection<P, N> {

	private final ResourceLocation id;
	private final double maxDistance;

	public AConnection(ResourceLocation id, double maxDistance){
		this.id = id;
		this.maxDistance = maxDistance;
	}

	@Override
	public ResourceLocation getId(){
		return id;
	}

	@Override
	public double maxDistance(){
		return maxDistance;
	}

	@Override
	public String toString(){
		return "connect[" + id + "]";
	}

}
