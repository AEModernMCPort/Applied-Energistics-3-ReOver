package appeng.core.core.definitions;

import appeng.api.bootstrap.DefinitionFactory;
import appeng.core.AppEng;
import appeng.core.core.api.bootstrap.IIonBuilder;
import appeng.core.core.api.crafting.ion.Ion;
import appeng.core.core.api.definition.IIonDefinition;
import appeng.core.core.api.definitions.ICoreIonDefinitions;
import appeng.core.core.crafting.ion.IonImpl;
import appeng.core.lib.definitions.Definitions;
import code.elix_x.excomms.color.RGBA;
import net.minecraft.util.ResourceLocation;

public class CoreIonDefinitions extends Definitions<Ion, IIonDefinition<Ion>> implements ICoreIonDefinitions {

	private final IIonDefinition certus;
	private final IIonDefinition quartz;
	private final IIonDefinition redstone;
	private final IIonDefinition sulfur;
	private final IIonDefinition ender;

	public CoreIonDefinitions(DefinitionFactory factory){
		certus = factory.<Ion, IIonDefinition<Ion>, IIonBuilder<Ion, ?>, Ion>definitionBuilder(new ResourceLocation(AppEng.MODID, "certus"), ih(new IonImpl(new RGBA(174, 215, 255)))).build();
		quartz = factory.<Ion, IIonDefinition<Ion>, IIonBuilder<Ion, ?>, Ion>definitionBuilder(new ResourceLocation(AppEng.MODID, "quartz"), ih(new IonImpl())).build();
		redstone = factory.<Ion, IIonDefinition<Ion>, IIonBuilder<Ion, ?>, Ion>definitionBuilder(new ResourceLocation(AppEng.MODID, "redstone"), ih(new IonImpl())).build();
		sulfur = factory.<Ion, IIonDefinition<Ion>, IIonBuilder<Ion, ?>, Ion>definitionBuilder(new ResourceLocation(AppEng.MODID, "sulfur"), ih(new IonImpl())).build();
		ender = factory.<Ion, IIonDefinition<Ion>, IIonBuilder<Ion, ?>, Ion>definitionBuilder(new ResourceLocation(AppEng.MODID, "ender"), ih(new IonImpl())).build();
	}

	private DefinitionFactory.InputHandler<Ion, Ion> ih(Ion entity){
		return new DefinitionFactory.InputHandler<Ion, Ion>(entity) {};
	}

}
