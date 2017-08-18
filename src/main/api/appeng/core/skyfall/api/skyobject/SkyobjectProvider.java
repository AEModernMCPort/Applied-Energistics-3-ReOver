package appeng.core.skyfall.api.skyobject;

import code.elix_x.excore.utils.world.MutableBlockAccess;
import net.minecraftforge.registries.IForgeRegistryEntry;

import java.util.Random;

public interface SkyobjectProvider<S extends Skyobject<S, P>, P extends SkyobjectProvider<S, P>> extends IForgeRegistryEntry<P> {

	float getDefaultWeight();



	S newSkyobjectInstance();

	void generate(MutableBlockAccess world, Random random);

}
