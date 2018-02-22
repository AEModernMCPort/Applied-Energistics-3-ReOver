package appeng.core.me.definitions;

import appeng.api.bootstrap.DefinitionFactory;
import appeng.core.AppEng;
import appeng.core.lib.definitions.Definitions;
import appeng.core.me.AppEngME;
import appeng.core.me.api.bootstrap.IPartBuilder;
import appeng.core.me.api.definition.IPartDefinition;
import appeng.core.me.api.definitions.IMEPartDefinitions;
import appeng.core.me.api.network.block.Connection;
import appeng.core.me.api.parts.PartColor;
import appeng.core.me.api.parts.part.Part;
import appeng.core.me.client.part.render.MicroFiberRenderingHandler;
import appeng.core.me.network.connect.ConnectionsParams;
import appeng.core.me.network.connect.DataConnection;
import appeng.core.me.parts.part.PartDummy;
import appeng.core.me.parts.part.cpassthrough.PartFiber;
import appeng.core.me.parts.part.cpassthrough.PartRecerticFiber;
import appeng.core.me.parts.part.device.Controller;
import appeng.core.me.parts.part.device.ImportBus;
import appeng.core.me.parts.placement.SideIsBottomPlacementLogic;
import com.google.common.collect.ImmutableMap;
import net.minecraft.util.ResourceLocation;

import java.util.Optional;

public class MEPartDefinitions<P extends Part<P, S>, S extends Part.State<P, S>> extends Definitions<P, IPartDefinition<P, S>> implements IMEPartDefinitions<P, S> {

	private final IPartDefinition recerticFiber;

	private final IPartDefinition controller;

	private final IPartDefinition importBus;

	public MEPartDefinitions(DefinitionFactory registry){
		Connection ENERGY = AppEngME.INSTANCE.getDevicesHelper().ENERGY;
		Connection DATA = AppEngME.INSTANCE.getDevicesHelper().DATA;

		recerticFiber = registry.<PartFiber.Micro, IPartDefinition<PartFiber.Micro, PartFiber.MicroState>, IPartBuilder<PartFiber.Micro, PartFiber.MicroState, ?>, PartFiber.Micro>definitionBuilder(new ResourceLocation(AppEng.MODID, "recertic_fiber"), ih(new PartRecerticFiber(new ConnectionsParams(ImmutableMap.of(ENERGY, 10))))).rootMesh(new ResourceLocation(AppEng.MODID, "fiber/recertic/recertic_node.obj")).overrideRenderingHandler(() -> Optional.of(new MicroFiberRenderingHandler())).createDefaultPlacerItem().build();
		for(PartColor color : PartColor.values()){
			String colorName = color.name().toLowerCase();
			dynamicallyCompiled((IPartDefinition) registry.<PartFiber.Micro, IPartDefinition<PartFiber.Micro, PartFiber.MicroState>, IPartBuilder<PartFiber.Micro, PartFiber.MicroState, ?>, PartFiber.Micro>definitionBuilder(new ResourceLocation(AppEng.MODID, "siocertic_fiber_micro_" + colorName), ih(new PartFiber.Micro(color, new ConnectionsParams(ImmutableMap.of(DATA, new DataConnection.Params(1, 10)))))).rootMesh(new ResourceLocation(AppEng.MODID, "fiber/siocertic/micro/" + colorName + "_node.obj")).overrideRenderingHandler(() -> Optional.of(new MicroFiberRenderingHandler())).createDefaultPlacerItem().build());
			dynamicallyCompiled(registry.<P, IPartDefinition<P, S>, IPartBuilder<P, S, ?>, P>definitionBuilder(new ResourceLocation(AppEng.MODID, "siocertic_fiber_normal_" + colorName), ih(new PartFiber.Normal(color, new ConnectionsParams(ImmutableMap.of(ENERGY, 10, DATA, new DataConnection.Params(8, 10)))))).rootMesh(new ResourceLocation(AppEng.MODID, "fiber/siocertic/normal/" + colorName + ".obj")).createDefaultPlacerItem().build());
			dynamicallyCompiled(registry.<P, IPartDefinition<P, S>, IPartBuilder<P, S, ?>, P>definitionBuilder(new ResourceLocation(AppEng.MODID, "siocertic_fiber_normal_joint_" + colorName), ih(new PartFiber.Normal.Joint(color, new ConnectionsParams(ImmutableMap.of(ENERGY, 10, DATA, new DataConnection.Params(8, 10)))))).rootMesh(new ResourceLocation(AppEng.MODID, "fiber/siocertic/normal/" + colorName + "_joint.obj")).createDefaultPlacerItem().build());
		}
		registry.<P, IPartDefinition<P, S>, IPartBuilder<P, S, ?>, P>definitionBuilder(new ResourceLocation(AppEng.MODID, "unlocked_block"), ih(new PartDummy(false))).createDefaultPlacerItem().build();
		registry.<P, IPartDefinition<P, S>, IPartBuilder<P, S, ?>, P>definitionBuilder(new ResourceLocation(AppEng.MODID, "tower"), ih(new PartDummy(true))).setEnabledByDefault(false).createDefaultPlacerItem(part -> new SideIsBottomPlacementLogic<>(part.maybe().get())).build();
		registry.<P, IPartDefinition<P, S>, IPartBuilder<P, S, ?>, P>definitionBuilder(new ResourceLocation(AppEng.MODID, "dish"), ih(new PartDummy(true))).setEnabledByDefault(false).createDefaultPlacerItem(part -> new SideIsBottomPlacementLogic<>(part.maybe().get())).build();

		controller = registry.<P, IPartDefinition<P, S>, IPartBuilder<P, S, ?>, P>definitionBuilder(new ResourceLocation(AppEng.MODID, "controller"), ih(new Controller.Part())).rootMesh(new ResourceLocation(AppEng.MODID, "controller/controller.obj")).createDefaultPlacerItem().build();

		importBus = registry.<P, IPartDefinition<P, S>, IPartBuilder<P, S, ?>, P>definitionBuilder(new ResourceLocation(AppEng.MODID, "import_bus"), ih(new ImportBus.Part())).rootMesh(new ResourceLocation(AppEng.MODID, "device/import_bus/import_bus.obj")).createDefaultPlacerItem().build();
	}

	private DefinitionFactory.InputHandler<Part, Part> ih(Part part){
		return new DefinitionFactory.InputHandler<Part, Part>(part) {};
	}

}
