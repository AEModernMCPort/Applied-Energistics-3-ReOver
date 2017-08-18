package appeng.core.skyfall.skyobject;

import appeng.core.skyfall.api.skyobject.SkyobjectProvider;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.registries.IForgeRegistryEntry;

import java.util.function.Function;

public abstract class SkyobjectProviderImpl<S extends SkyobjectImpl<S, P>, P extends SkyobjectProviderImpl<S, P>> extends IForgeRegistryEntry.Impl<P> implements SkyobjectProvider<S, P> {

	protected Function<P, S> skyobjectSupplier;
	protected int defaultWeight;

	public SkyobjectProviderImpl(Function<P, S> skyobjectSupplier, int defaultWeight){
		this.skyobjectSupplier = skyobjectSupplier;
		this.defaultWeight = defaultWeight;
	}

	@Override
	public S get(){
		return skyobjectSupplier.apply((P) this);
	}

	@Override
	public int getDefaultWeight(){
		return defaultWeight;
	}

	@Override
	public NBTTagCompound serializeNBT(S skyobject){
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setLong("seed", skyobject.seed);
		return nbt;
	}

	@Override
	public S deserializeNBT(NBTTagCompound nbt){
		S skyobject = get();
		skyobject.seed = nbt.getLong("seed");
		return skyobject;
	}
}
