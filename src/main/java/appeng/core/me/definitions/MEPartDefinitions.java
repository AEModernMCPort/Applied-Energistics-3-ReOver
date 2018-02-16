package appeng.core.me.definitions;

import appeng.api.bootstrap.DefinitionFactory;
import appeng.core.AppEng;
import appeng.core.lib.definitions.Definitions;
import appeng.core.me.api.bootstrap.IPartBuilder;
import appeng.core.me.api.definition.IPartDefinition;
import appeng.core.me.api.definitions.IMEPartDefinitions;
import appeng.core.me.api.network.block.DeviceColor;
import appeng.core.me.api.parts.part.Part;
import appeng.core.me.parts.part.PartDummy;
import appeng.core.me.parts.part.connected.PartCable;
import appeng.core.me.parts.placement.SideIsBottomPlacementLogic;
import net.minecraft.util.ResourceLocation;

public class MEPartDefinitions<P extends Part<P, S>, S extends Part.State<P, S>> extends Definitions<P, IPartDefinition<P, S>> implements IMEPartDefinitions<P, S> {

	private final IPartDefinition importBus;

	public MEPartDefinitions(DefinitionFactory registry){
		for(DeviceColor color : DeviceColor.values()){
			String colorName = color.name().toLowerCase();
			dynamicallyCompiled(registry.<P, IPartDefinition<P, S>, IPartBuilder<P, S, ?>, P>definitionBuilder(new ResourceLocation(AppEng.MODID, "glass_cable_micro_" + colorName), ih(new PartCable.Micro(color))).rootMesh(new ResourceLocation(AppEng.MODID, "cable/glass/micro/" + colorName + ".obj")).createDefaultPlacerItem().build());
			dynamicallyCompiled(registry.<P, IPartDefinition<P, S>, IPartBuilder<P, S, ?>, P>definitionBuilder(new ResourceLocation(AppEng.MODID, "glass_cable_normal_" + colorName), ih(new PartCable.Normal(color))).rootMesh(new ResourceLocation(AppEng.MODID, "cable/glass/normal/" + colorName + ".obj")).createDefaultPlacerItem().build());
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
