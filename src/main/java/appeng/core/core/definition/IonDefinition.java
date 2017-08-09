package appeng.core.core.definition;

import appeng.core.core.api.crafting.ion.Ion;
import appeng.core.core.api.definition.IIonDefinition;
import appeng.core.lib.definition.Definition;
import net.minecraft.util.ResourceLocation;

public class IonDefinition<I extends Ion> extends Definition<I> implements IIonDefinition<I> {

	public IonDefinition(ResourceLocation identifier, I ion){
		super(identifier, ion);
	}

}
