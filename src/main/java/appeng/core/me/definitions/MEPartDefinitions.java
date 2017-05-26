
package appeng.core.me.definitions;


import net.minecraft.util.ResourceLocation;

import appeng.core.AppEng;
import appeng.core.lib.definitions.Definitions;
import appeng.core.lib.features.AEFeature;
import appeng.core.me.api.definitions.IMEPartDefinitions;
import appeng.core.me.api.definitions.IPartDefinition;
import appeng.core.me.api.part.PartRegistryEntry;
import appeng.core.me.bootstrap.MEFeatureFactory;
import appeng.core.me.part.automation.PartAnnihilationPlane;
import appeng.core.me.part.automation.PartExportBus;
import appeng.core.me.part.automation.PartFormationPlane;
import appeng.core.me.part.automation.PartIdentityAnnihilationPlane;
import appeng.core.me.part.automation.PartImportBus;
import appeng.core.me.part.automation.PartLevelEmitter;
import appeng.core.me.part.misc.PartCableAnchor;
import appeng.core.me.part.misc.PartInterface;
import appeng.core.me.part.misc.PartInvertedToggleBus;
import appeng.core.me.part.misc.PartStorageBus;
import appeng.core.me.part.misc.PartToggleBus;
import appeng.core.me.part.networking.PartCableCovered;
import appeng.core.me.part.networking.PartCableGlass;
import appeng.core.me.part.networking.PartCableSmart;
import appeng.core.me.part.networking.PartDenseCable;
import appeng.core.me.part.networking.PartQuartzFiber;
import appeng.core.me.part.reporting.PartConversionMonitor;
import appeng.core.me.part.reporting.PartCraftingTerminal;
import appeng.core.me.part.reporting.PartDarkPanel;
import appeng.core.me.part.reporting.PartInterfaceTerminal;
import appeng.core.me.part.reporting.PartPanel;
import appeng.core.me.part.reporting.PartSemiDarkPanel;
import appeng.core.me.part.reporting.PartStorageMonitor;
import appeng.core.me.part.reporting.PartTerminal;


public class MEPartDefinitions extends Definitions<PartRegistryEntry, IPartDefinition<PartRegistryEntry>> implements IMEPartDefinitions
{

	public MEPartDefinitions( MEFeatureFactory registry )
	{
		init();
	}

}
