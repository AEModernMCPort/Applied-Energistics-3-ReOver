package appeng.core.skyfall.skyobject.objects;

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
		nbt.setTag("physics", skyobject.physics.serializeNBT());
		return nbt;
	}

	@Override
	public S deserializeNBT(NBTTagCompound nbt){
		S skyobject = super.deserializeNBT(nbt);
		skyobject.world.deserializeNBT(nbt.getCompoundTag("world"));
		skyobject.physics.deserializeNBT(nbt.getCompoundTag("physics"));
		return skyobject;
	}

}
