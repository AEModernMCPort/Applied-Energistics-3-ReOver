
package appeng.core.lib.bootstrap;


import java.util.function.Consumer;

import appeng.api.definitions.IDefinition;
import appeng.core.lib.features.AEFeature;


public interface IDefinitionBuilder<T, D extends IDefinition<T>, B extends IDefinitionBuilder<T, D, B>>
{

	B features( AEFeature... features );

	B addFeatures( AEFeature... features );

	B build( Consumer<D> callback );

	B preInit( Consumer<D> callback );

	B init( Consumer<D> callback );

	B postInit( Consumer<D> callback );

	D build();

}
