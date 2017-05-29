package appeng.api.bootstrap;

import appeng.api.definitions.IDefinition;

import java.util.function.Consumer;

public interface DefinitionBuilder<T, D extends IDefinition<T>, B extends DefinitionBuilder<T, D, B>> {

	B build(Consumer<D> callback);

	B preInit(Consumer<D> callback);

	B init(Consumer<D> callback);

	B postInit(Consumer<D> callback);

	D build();

}
