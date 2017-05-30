package appeng.api.bootstrap;

import appeng.api.definitions.IDefinition;
import net.minecraft.util.ResourceLocation;

@FunctionalInterface
public interface DefinitionBuilderSupplier<T, D extends IDefinition<T>, B extends IDefinitionBuilder<T, D, B>, I> {

	B apply(DefinitionFactory factory, ResourceLocation registryName, I input);

}
