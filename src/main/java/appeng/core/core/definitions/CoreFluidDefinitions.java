package appeng.core.core.definitions;

import appeng.api.bootstrap.DefinitionFactory;
import appeng.core.core.api.definition.IEntityDefinition;
import appeng.core.core.api.definition.IFluidDefinition;
import appeng.core.core.api.definitions.ICoreEntityDefinitions;
import appeng.core.core.api.definitions.ICoreFluidDefinitions;
import appeng.core.lib.definitions.Definitions;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.common.registry.EntityEntry;

public class CoreFluidDefinitions extends Definitions<Fluid, IFluidDefinition<Fluid>> implements ICoreFluidDefinitions {

	public CoreFluidDefinitions(DefinitionFactory factory){

	}

	private DefinitionFactory.InputHandler<Fluid, Fluid> ih(Fluid entity){
		return new DefinitionFactory.InputHandler<Fluid, Fluid>(entity) {};
	}

}
