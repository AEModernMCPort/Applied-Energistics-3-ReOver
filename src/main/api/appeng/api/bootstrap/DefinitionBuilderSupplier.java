package appeng.api.bootstrap;

import appeng.api.definition.IDefinition;
import net.minecraft.util.ResourceLocation;

@FunctionalInterface
public interface DefinitionBuilderSupplier<T, D extends IDefinition<T>, B extends IDefinitionBuilder, I> {

	B apply(DefinitionFactory factory, ResourceLocation registryName, I input);

}
