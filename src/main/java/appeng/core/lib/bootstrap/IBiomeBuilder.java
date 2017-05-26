
package appeng.core.lib.bootstrap;


import net.minecraft.world.biome.Biome;

import appeng.api.definitions.IBiomeDefinition;


public interface IBiomeBuilder<B extends Biome, BB extends IBiomeBuilder<B, BB>> extends IDefinitionBuilder<B, IBiomeDefinition<B>, BB>
{

}
