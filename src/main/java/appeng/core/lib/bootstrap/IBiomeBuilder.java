package appeng.core.lib.bootstrap;

import appeng.api.definitions.IBiomeDefinition;
import net.minecraft.world.biome.Biome;

public interface IBiomeBuilder<B extends Biome, BB extends IBiomeBuilder<B, BB>>
		extends IDefinitionBuilder<B, IBiomeDefinition<B>, BB> {

}
