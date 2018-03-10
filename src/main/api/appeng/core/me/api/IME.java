package appeng.core.me.api;

import appeng.api.definitions.IDefinitionsProvider;
import appeng.core.me.api.parts.part.PartsHelper;

public interface IME extends IDefinitionsProvider {

	String NAME = "me";

	PartsHelper getPartsHelper();

}
