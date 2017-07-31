package appeng.core.me.api.definitions;

import appeng.api.definitions.IDefinitions;
import appeng.core.me.api.definition.IPartDefinition;
import appeng.core.me.api.parts.part.Part;

public interface IMEPartDefinitions<P extends Part<P, S>, S extends Part.State<P, S>> extends IDefinitions<P, IPartDefinition<P, S>> {


}