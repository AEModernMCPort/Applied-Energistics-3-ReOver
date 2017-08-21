package appeng.core.skyfall.skyobject.objects;

import appeng.core.lib.world.ExpandleMutableBlockAccess;
import appeng.core.skyfall.skyobject.SkyobjectProviderImpl;
import net.minecraft.nbt.NBTTagCompound;

import java.util.function.Function;

public abstract class SkyobjectFallingProvider<S extends SkyobjectFalling<S, P>, P extends SkyobjectFallingProvider<S, P>> extends SkyobjectProviderImpl<S, P> {

	public SkyobjectFallingProvider(Function<P, S> skyobjectSupplier, int defaultWeight){
		super(skyobjectSupplier, defaultWeight);
	}

	@Override
	public NBTTagCompound serializeNBT(S skyobject){
		NBTTagCompound nbt = super.serializeNBT(skyobject);
		nbt.setTag("world", skyobject.world.serializeNBT());
		return nbt;
	}

	@Override
	public S deserializeNBT(NBTTagCompound nbt){
		S skyobject = super.deserializeNBT(nbt);
		(skyobject.world = new ExpandleMutableBlockAccess()).deserializeNBT(nbt.getCompoundTag("world"));
		return skyobject;
	}

}
