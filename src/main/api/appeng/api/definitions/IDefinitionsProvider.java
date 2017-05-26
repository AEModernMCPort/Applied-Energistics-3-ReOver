package appeng.api.definitions;

public interface IDefinitionsProvider {

	<T, D extends IDefinitions<T, ? extends IDefinition<T>>> D definitions(Class<T> clas);

}
