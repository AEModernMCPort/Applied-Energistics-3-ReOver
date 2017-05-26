package appeng.core.lib.bootstrap;

import appeng.api.definitions.IDefinition;
import appeng.core.lib.features.AEFeature;

import java.util.function.Consumer;

public interface IDefinitionBuilder<T, D extends IDefinition<T>, B extends IDefinitionBuilder<T, D, B>> {

	B features(AEFeature... features);

	B addFeatures(AEFeature... features);

	B build(Consumer<D> callback);

	B preInit(Consumer<D> callback);

	B init(Consumer<D> callback);

	B postInit(Consumer<D> callback);

	D build();

}
