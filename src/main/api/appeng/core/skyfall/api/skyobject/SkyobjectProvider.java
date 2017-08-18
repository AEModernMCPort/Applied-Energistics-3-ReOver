package appeng.core.skyfall.api.skyobject;

import code.elix_x.excore.utils.world.MutableBlockAccess;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.registries.IForgeRegistryEntry;

import java.util.function.Supplier;

public interface SkyobjectProvider<S extends Skyobject<S, P>, P extends SkyobjectProvider<S, P>> extends IForgeRegistryEntry<P>, Supplier<S> {

	int getDefaultWeight();


	void generate(MutableBlockAccess world, long seed);


	NBTTagCompound serializeNBT(S skyobject);

	S deserializeNBT(NBTTagCompound nbt);

}
