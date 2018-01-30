package appeng.core.me.api.network;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

/**
 * Device entity existing on the network thread.<br>
 * Interacts <b>only</b> on the network thread with the netwok.<br>
 * Manages <b>all</b> scheduled tasks, as network counterpart is the one managing all scheduled tasks for both counterparts.<br><br>
 *
 * If implements {@link ITickable}, {@linkplain ITickable#update() update()} will be called from (one of) network thread(s) on each update cycle (<u>on inconsistent time intervals</u>). A device must schedule tasks for other devices instead of direct interaction (possible concurrency).
 *
 * @param <N> Network counterpart type
 * @param <P> In-world counterpart type
 *
 * @author Elix_x
 */
public interface NetDevice<N extends NetDevice<N, P>, P extends PhysicalDevice<N, P>> extends ICapabilityProvider, INBTSerializable<NBTTagCompound> {

	@Nonnull DeviceUUID getUUID();

	Optional<P> getPhysicalCounterpart();

	@Nonnull Optional<NetBlock> getNetBlock();
	void switchNetBlock(@Nullable NetBlock block);
	default boolean hasNetwork(){
		return getNetBlock().map(NetBlock::hasNetwork).orElse(false);
	}

}
