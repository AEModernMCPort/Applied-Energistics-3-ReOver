package appeng.core.me.api;

import appeng.api.definitions.IDefinitionsProvider;
import appeng.core.me.api.network.GlobalNBDManager;
import appeng.core.me.api.network.NBDIO;
import appeng.core.me.api.parts.part.Part;
import appeng.core.me.api.parts.placement.PartPlacementLogic;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface IME extends IDefinitionsProvider {

	String NAME = "me";

	PartPlacementLogic createDefaultPlacementLogic(Part part);

	void registerVoxelConnectivity(ResourceLocation connect);

	@Nonnull
	NBDIO getNBDIO();

	/**
	 * Retrieves world to networks interface for the current server. Null only if there is no server running.
	 *
	 * @return current world to networks interface
	 */
	@Nullable
	GlobalNBDManager getGlobalNBDManager();

}
