package appeng.core.me.api;

import appeng.api.definitions.IDefinitionsProvider;
import appeng.core.me.api.parts.part.Part;
import appeng.core.me.api.parts.part.PartsHelper;
import appeng.core.me.api.parts.placement.PartPlacementLogic;

public interface IME extends IDefinitionsProvider {

	String NAME = "me";

	PartsHelper getPartsHelper();

	PartPlacementLogic createDefaultPlacementLogic(Part part);

}
