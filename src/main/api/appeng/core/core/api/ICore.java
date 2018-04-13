package appeng.core.core.api;

import appeng.api.definitions.IDefinitionsProvider;
import appeng.core.core.api.know.EternalWiki;
import appeng.core.core.api.net.gui.GuiHandler;

public interface ICore extends IDefinitionsProvider {

	String NAME = "core";

	GuiHandler guiHandler();

	EternalWiki getEternalWiki();

}
