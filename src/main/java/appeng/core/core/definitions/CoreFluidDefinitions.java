package appeng.core.core.definitions;

import appeng.api.bootstrap.DefinitionFactory;
import appeng.api.bootstrap.IDefinitionBuilder;
import appeng.core.AppEng;
import appeng.core.core.AppEngCore;
import appeng.core.core.api.bootstrap.FluidBlockCustomizer;
import appeng.core.core.api.bootstrap.IBlockBuilder;
import appeng.core.core.api.bootstrap.IFluidBuilder;
import appeng.core.core.api.definition.IFluidDefinition;
import appeng.core.core.api.definitions.ICoreFluidDefinitions;
import appeng.core.core.block.IonEnvironmentFluidBlock;
import appeng.core.core.client.bootstrap.BlockColorComponent;
import appeng.core.core.client.render.color.IonEnvironmentFluidBlockColor;
import appeng.core.core.fluid.IonEnvironmentFluid;
import appeng.core.lib.definitions.Definitions;
import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.relauncher.Side;

import javax.annotation.Nonnull;
import java.util.Optional;

public class CoreFluidDefinitions extends Definitions<Fluid, IFluidDefinition<Fluid>> implements ICoreFluidDefinitions {

	public CoreFluidDefinitions(DefinitionFactory factory){
		AppEngCore.INSTANCE.getCraftingIonRegistry().normal2ionized.keySet().forEach(fluid -> factory.addDefault(factory.<Fluid, IFluidDefinition<Fluid>, IFluidBuilder<Fluid, ?>, Fluid>definitionBuilder(new ResourceLocation(AppEng.MODID, "ae3_ion_" + fluid.getName()), ih(new IonEnvironmentFluid(fluid))).createBlock(new FluidBlockCustomizer<Fluid, BlockFluidBase>() {

			@Nonnull
			@Override
			public BlockFluidBase createBlock(IFluidDefinition<Fluid> nfluid){
				return new IonEnvironmentFluidBlock(nfluid.maybe().get(), fluid.getBlock());
			}

			@Nonnull
			@Override
			public IBlockBuilder<BlockFluidBase, ?> customize(@Nonnull IBlockBuilder<BlockFluidBase, ?> builder){
				return builder.doNotRemapSubmodule().initializationComponent(Side.CLIENT, new BlockColorComponent<>(() -> Optional.of(new IonEnvironmentFluidBlockColor(fluid))));
			}

		}).<IDefinitionBuilder.DefinitionInitializationComponent.Init<Fluid, IFluidDefinition<Fluid>>>initializationComponent(null, def -> AppEngCore.INSTANCE.getCraftingIonRegistry().registerIonVariant(fluid, def.maybe().get())).build()));
	}

	private DefinitionFactory.InputHandler<Fluid, Fluid> ih(Fluid entity){
		return new DefinitionFactory.InputHandler<Fluid, Fluid>(entity) {};
	}

}
