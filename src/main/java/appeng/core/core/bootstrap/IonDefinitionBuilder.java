package appeng.core.core.bootstrap;

import appeng.api.bootstrap.DefinitionFactory;
import appeng.core.core.api.bootstrap.IIonBuilder;
import appeng.core.core.api.crafting.ion.Ion;
import appeng.core.core.api.definition.IIonDefinition;
import appeng.core.core.definition.IonDefinition;
import appeng.core.lib.bootstrap.DefinitionBuilder;
import net.minecraft.util.ResourceLocation;

public class IonDefinitionBuilder<I extends Ion> extends DefinitionBuilder<I, I, IIonDefinition<I>, IonDefinitionBuilder<I>> implements IIonBuilder<I, IonDefinitionBuilder<I>> {

	public IonDefinitionBuilder(DefinitionFactory factory, ResourceLocation registryName, I ion){
		super(factory, registryName, ion, "ion");
	}

	@Override
	protected IIonDefinition<I> def(I ion){
		if(ion == null) return new IonDefinition<>(registryName, null);

		return new IonDefinition<>(registryName, ion);
	}
}
