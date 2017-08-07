package appeng.core.core.definitions;

import appeng.api.bootstrap.DefinitionFactory;
import appeng.core.AppEng;
import appeng.core.core.AppEngCore;
import appeng.core.core.api.bootstrap.IFluidBuilder;
import appeng.core.core.api.definition.IEntityDefinition;
import appeng.core.core.api.definition.IFluidDefinition;
import appeng.core.core.api.definitions.ICoreEntityDefinitions;
import appeng.core.core.api.definitions.ICoreFluidDefinitions;
import appeng.core.core.block.IonEnvironmentFluidBlock;
import appeng.core.core.fluid.IonEnvironmentFluid;
import appeng.core.lib.definitions.Definitions;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.common.registry.EntityEntry;

public class CoreFluidDefinitions extends Definitions<Fluid, IFluidDefinition<Fluid>> implements ICoreFluidDefinitions {

	public CoreFluidDefinitions(DefinitionFactory factory){
		AppEngCore.INSTANCE.getCraftingIonRegistry().ionEnvironmentFluids.forEach(fluid -> factory.addDefault(factory.<Fluid, IFluidDefinition<Fluid>, IFluidBuilder<Fluid, ?>, Fluid>definitionBuilder(new ResourceLocation(AppEng.MODID, "ae3_ion_" + fluid.getName()), ih(new IonEnvironmentFluid(fluid))).createBlock(nfluid -> new IonEnvironmentFluidBlock(nfluid.maybe().get(), fluid.getBlock())).build()));
	}

	private DefinitionFactory.InputHandler<Fluid, Fluid> ih(Fluid entity){
		return new DefinitionFactory.InputHandler<Fluid, Fluid>(entity) {};
	}

}
