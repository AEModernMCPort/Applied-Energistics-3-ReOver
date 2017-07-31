package appeng.core.me.api;

import appeng.api.definitions.IDefinitionsProvider;
import appeng.core.me.api.parts.part.Part;
import appeng.core.me.api.parts.placement.PartPlacementLogic;

public interface IME extends IDefinitionsProvider {

	String NAME = "me";

	PartPlacementLogic createDefaultPlacementLogic(Part part);

}
