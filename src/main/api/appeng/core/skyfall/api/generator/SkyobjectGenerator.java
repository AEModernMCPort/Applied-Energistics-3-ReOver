package appeng.core.skyfall.api.generator;

import net.minecraft.world.IBlockAccess;
import net.minecraftforge.registries.IForgeRegistryEntry;

import java.util.Random;

public interface SkyobjectGenerator extends IForgeRegistryEntry<SkyobjectGenerator> {

	float getDefaultWeight();

	void generate(MutableBlockAccess world, Random random);

}
