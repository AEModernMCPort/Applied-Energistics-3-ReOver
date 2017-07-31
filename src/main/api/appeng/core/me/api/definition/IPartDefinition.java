package appeng.core.me.api.definition;

import appeng.api.definition.IDefinition;
import appeng.core.me.api.parts.part.Part;

public interface IPartDefinition<P extends Part<P, S>, S extends Part.State<P, S>> extends IDefinition<P> {}
