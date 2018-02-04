package appeng.core.me.api;

import appeng.api.definitions.IDefinitionsProvider;
import appeng.core.me.api.network.Network;
import appeng.core.me.api.network.NetworkUUID;
import appeng.core.me.api.network.GlobalNBDManager;
import appeng.core.me.api.parts.part.Part;
import appeng.core.me.api.parts.placement.PartPlacementLogic;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

import java.util.function.BiFunction;

public interface IME extends IDefinitionsProvider {

	String NAME = "me";

	PartPlacementLogic createDefaultPlacementLogic(Part part);

	<N extends Network> void registerNetworkLoader(ResourceLocation id, BiFunction<NetworkUUID, NBTTagCompound, N> loader);

	/**
	 * Retrieves world to networks interface for the current server. Null only if there is no server running.
	 *
	 * @return current world to networks interface
	 */
	GlobalNBDManager getGlobalNBDManager();

}
