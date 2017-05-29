package appeng.core.lib.bootstrap_olde;

import appeng.api.bootstrap.DefinitionBuilder;
import appeng.api.definitions.IBiomeDefinition;
import net.minecraft.world.biome.Biome;

public interface IBiomeBuilder<B extends Biome, BB extends IBiomeBuilder<B, BB>>
		extends DefinitionBuilder<B, IBiomeDefinition<B>, BB> {

}
