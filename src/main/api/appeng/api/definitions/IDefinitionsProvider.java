package appeng.api.definitions;

import appeng.api.definition.IDefinition;
import appeng.api.definitions.IDefinitions;

public interface IDefinitionsProvider {

	<T, D extends IDefinitions<T, ? extends IDefinition<T>>> D definitions(Class<T> clas);

}
