package appeng.core.core.bootstrap;

import appeng.api.bootstrap.DefinitionFactory;
import appeng.core.core.api.bootstrap.FluidBlockCustomizer;
import appeng.core.core.api.bootstrap.IBlockBuilder;
import appeng.core.core.api.bootstrap.IFluidBuilder;
import appeng.core.core.api.definition.IBlockDefinition;
import appeng.core.core.api.definition.IFluidDefinition;
import appeng.core.core.client.bootstrap.StateMapperComponent;
import appeng.core.core.client.statemap.StaticStateMapper;
import appeng.core.core.definition.FluidDefinition;
import appeng.core.lib.bootstrap.DefinitionBuilder;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeVersion;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.relauncher.Side;

import java.util.Optional;
import java.util.function.Function;

public class FluidDefinitionBuilder<F extends Fluid> extends DefinitionBuilder<F, F, IFluidDefinition<F>, FluidDefinitionBuilder<F>> implements IFluidBuilder<F, FluidDefinitionBuilder<F>> {

	private Function<IFluidDefinition<F>, IBlockDefinition<?>> block;

	public FluidDefinitionBuilder(DefinitionFactory factory, ResourceLocation registryName, F fluid){
		super(factory, new ResourceLocation(Loader.instance().activeModContainer().getModId(), fluid.getName()), fluid, "fluid");
	}

	@Override
	public <B extends Block & IFluidBlock> FluidDefinitionBuilder<F> setBlock(Function<IFluidDefinition<F>, IBlockDefinition<B>> block){
		this.block = (Function) block;
		return this;
	}

	@Override
	public <B extends Block & IFluidBlock> FluidDefinitionBuilder<F> createBlock(FluidBlockCustomizer<F, B> customizer){
		return setBlock(def -> customizer.customize(factory.<B, IBlockDefinition<B>, IBlockBuilder<B, ?>, Block>definitionBuilder(registryName, fluidBlockIh(customizer.createBlock(def))).initializationComponent(Side.CLIENT, new StateMapperComponent<>(old -> Optional.of(new StaticStateMapper(new ModelResourceLocation(new ResourceLocation(ForgeVersion.MOD_ID, "fluid"), def.identifier().getResourcePath())))))).setFeature(feature).build());
	}

	@Override
	public IFluidDefinition<F> def(F fluid){
		if(fluid == null) return new FluidDefinition<>(registryName, fluid);

		FluidDefinition<F> definition = new FluidDefinition<>(registryName, fluid);
		if(block != null){
			IBlockDefinition<?> blockDefinition = block.apply(definition);
			blockDefinition.maybe().ifPresent(fluid::setBlock);
			factory.addDefault(blockDefinition);
		}
		return definition;
	}

	@Override
	protected F setRegistryName(F f){
		return f;
	}

	@Override
	protected void register(F f){
		FluidRegistry.registerFluid(f);
	}

	public DefinitionFactory.InputHandler<Block, Block> fluidBlockIh(Block block){
		return new DefinitionFactory.InputHandler<Block, Block>(block){};
	}

}
