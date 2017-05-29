package appeng.api.bootstrap;

import appeng.api.definitions.IDefinition;
import net.minecraft.util.ResourceLocation;

public interface DefinitionFactory {

	<T, D extends IDefinition<T>, B extends DefinitionBuilder<T, D, B>, I> B definitionBuilder(ResourceLocation registryName, I input);

}
