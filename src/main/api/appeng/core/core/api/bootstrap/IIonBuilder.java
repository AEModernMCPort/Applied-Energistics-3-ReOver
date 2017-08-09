package appeng.core.core.api.bootstrap;

import appeng.api.bootstrap.IDefinitionBuilder;
import appeng.core.core.api.crafting.ion.Ion;
import appeng.core.core.api.definition.IIonDefinition;

public interface IIonBuilder<I extends Ion, II extends IIonBuilder<I, II>> extends IDefinitionBuilder<I, IIonDefinition<I>, II> {

}
