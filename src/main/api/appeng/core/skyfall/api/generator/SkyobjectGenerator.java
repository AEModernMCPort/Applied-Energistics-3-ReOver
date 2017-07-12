package appeng.core.skyfall.api.generator;

import code.elix_x.excore.utils.world.MutableBlockAccess;
import net.minecraftforge.registries.IForgeRegistryEntry;

import java.util.Random;

public interface SkyobjectGenerator extends IForgeRegistryEntry<SkyobjectGenerator> {

	float getDefaultWeight();

	void generate(MutableBlockAccess world, Random random);

}
