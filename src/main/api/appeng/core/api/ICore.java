package appeng.core.api;

import appeng.api.definitions.IDefinitionsProvider;
import appeng.core.api.net.gui.GuiHandler;

public interface ICore extends IDefinitionsProvider {

	String NAME = "core";

	GuiHandler guiHandler();

}
