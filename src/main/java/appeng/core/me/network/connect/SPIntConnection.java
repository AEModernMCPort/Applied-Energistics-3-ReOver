package appeng.core.me.network.connect;

import appeng.core.me.api.network.block.Connection;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

public class SPIntConnection extends AConnection<Integer, NBTTagInt> implements Connection<Integer, NBTTagInt> {

	public SPIntConnection(ResourceLocation id, double maxDistance){
		super(id, maxDistance);
	}

	@Nonnull
	@Override
	public Integer join(@Nonnull Integer param1, @Nonnull Integer param2){
		return Math.min(param1, param2);
	}

	@Nonnull
	@Override
	public Integer add(@Nonnull Integer param1, @Nonnull Integer param2){
		return param1 + param2;
	}

	@Nonnull
	@Override
	public Integer subtract(@Nonnull Integer param, @Nonnull Integer sub){
		return param - sub;
	}

	@Nonnull
	@Override
	public Integer divide(@Nonnull Integer param, int parts){
		return Math.floorDiv(param, parts) + 1;
	}

	@Nonnull
	@Override
	public Integer mul(@Nonnull Integer param, double d){
		return (int) (param * d);
	}

	@Override
	public NBTTagInt serializeParam(Integer param){
		return new NBTTagInt(param);
	}

	@Override
	public Integer deserializeParam(NBTTagInt nbt){
		return nbt.getInt();
	}

}
