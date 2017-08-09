package appeng.core.core.api.crafting.ion;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import java.util.Optional;

public interface IonEnvironmentContext {

	/**
	 * Maybe the world of the environment
	 * @return the world of the environment
	 */
	Optional<World> world();

	/**
	 * Maybe the position of the environment
	 * @return the position of the environment
	 */
	Optional<BlockPos> pos();

	/**
	 * Maybe capabilities of the environment
	 * @return capabilities of the environment
	 */
	Optional<ICapabilityProvider> capabilities();

	/**
	 * Singleton
	 */
	interface Change {}

}
