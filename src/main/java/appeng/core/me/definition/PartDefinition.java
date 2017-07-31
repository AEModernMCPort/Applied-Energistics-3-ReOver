package appeng.core.me.definition;

import appeng.core.lib.definition.Definition;
import appeng.core.me.api.definition.IPartDefinition;
import appeng.core.me.api.parts.part.Part;
import net.minecraft.util.ResourceLocation;

public class PartDefinition<P extends Part<P, S>, S extends Part.State<P, S>> extends Definition<P> implements IPartDefinition<P, S> {

	public PartDefinition(ResourceLocation identifier, P p){
		super(identifier, p);
	}
}
