package appeng.core.me.definitions;

import appeng.api.bootstrap.DefinitionFactory;
import appeng.core.AppEng;
import appeng.core.lib.definitions.Definitions;
import appeng.core.me.api.bootstrap.IPartBuilder;
import appeng.core.me.api.definition.IPartDefinition;
import appeng.core.me.api.definitions.IMEPartDefinitions;
import appeng.core.me.api.parts.PartColor;
import appeng.core.me.api.parts.part.Part;
import appeng.core.me.client.part.render.MicroFiberRenderingHandler;
import appeng.core.me.parts.part.PartDummy;
import appeng.core.me.parts.part.connected.PartFiber;
import appeng.core.me.parts.part.connected.PartRecerticFiber;
import appeng.core.me.parts.placement.SideIsBottomPlacementLogic;
import net.minecraft.util.ResourceLocation;

import java.util.Optional;

public class MEPartDefinitions<P extends Part<P, S>, S extends Part.State<P, S>> extends Definitions<P, IPartDefinition<P, S>> implements IMEPartDefinitions<P, S> {

	private final IPartDefinition importBus;

	private final IPartDefinition recerticFiber;

	public MEPartDefinitions(DefinitionFactory registry){
		recerticFiber = registry.<PartFiber.Micro, IPartDefinition<PartFiber.Micro, PartFiber.MicroState>, IPartBuilder<PartFiber.Micro, PartFiber.MicroState, ?>, PartFiber.Micro>definitionBuilder(new ResourceLocation(AppEng.MODID, "recertic_fiber"), ih(new PartRecerticFiber())).rootMesh(new ResourceLocation(AppEng.MODID, "fiber/recertic/recertic_node.obj")).overrideRenderingHandler(() -> Optional.of(new MicroFiberRenderingHandler())).createDefaultPlacerItem().build();
		for(PartColor color : PartColor.values()){
			String colorName = color.name().toLowerCase();
			dynamicallyCompiled((IPartDefinition) registry.<PartFiber.Micro, IPartDefinition<PartFiber.Micro, PartFiber.MicroState>, IPartBuilder<PartFiber.Micro, PartFiber.MicroState, ?>, PartFiber.Micro>definitionBuilder(new ResourceLocation(AppEng.MODID, "siocertic_fiber_micro_" + colorName), ih(new PartFiber.Micro(color))).rootMesh(new ResourceLocation(AppEng.MODID, "fiber/siocertic/micro/" + colorName + "_node.obj")).overrideRenderingHandler(() -> Optional.of(new MicroFiberRenderingHandler())).createDefaultPlacerItem().build());
			dynamicallyCompiled(registry.<P, IPartDefinition<P, S>, IPartBuilder<P, S, ?>, P>definitionBuilder(new ResourceLocation(AppEng.MODID, "siocertic_fiber_normal_" + colorName), ih(new PartFiber.Normal(color))).rootMesh(new ResourceLocation(AppEng.MODID, "fiber/siocertic/normal/" + colorName + ".obj")).createDefaultPlacerItem().build());
			dynamicallyCompiled(registry.<P, IPartDefinition<P, S>, IPartBuilder<P, S, ?>, P>definitionBuilder(new ResourceLocation(AppEng.MODID, "siocertic_fiber_normal_joint_" + colorName), ih(new PartFiber.Normal.Joint(color))).rootMesh(new ResourceLocation(AppEng.MODID, "fiber/siocertic/normal/" + colorName + "_joint.obj")).createDefaultPlacerItem().build());
		}
		registry.<P, IPartDefinition<P, S>, IPartBuilder<P, S, ?>, P>definitionBuilder(new ResourceLocation(AppEng.MODID, "unlocked_block"), ih(new PartDummy(false))).createDefaultPlacerItem().build();
		registry.<P, IPartDefinition<P, S>, IPartBuilder<P, S, ?>, P>definitionBuilder(new ResourceLocation(AppEng.MODID, "tower"), ih(new PartDummy(true))).setEnabledByDefault(false).createDefaultPlacerItem(part -> new SideIsBottomPlacementLogic<>(part.maybe().get())).build();
		registry.<P, IPartDefinition<P, S>, IPartBuilder<P, S, ?>, P>definitionBuilder(new ResourceLocation(AppEng.MODID, "dish"), ih(new PartDummy(true))).setEnabledByDefault(false).createDefaultPlacerItem(part -> new SideIsBottomPlacementLogic<>(part.maybe().get())).build();

		importBus = registry.<P, IPartDefinition<P, S>, IPartBuilder<P, S, ?>, P>definitionBuilder(new ResourceLocation(AppEng.MODID, "import_bus"), ih(new PartDummy(true))).rootMesh(new ResourceLocation(AppEng.MODID, "device/import_bus/import_bus.obj")).createDefaultPlacerItem().build();
	}

	private DefinitionFactory.InputHandler<Part, Part> ih(Part part){
		return new DefinitionFactory.InputHandler<Part, Part>(part) {};
	}

}
