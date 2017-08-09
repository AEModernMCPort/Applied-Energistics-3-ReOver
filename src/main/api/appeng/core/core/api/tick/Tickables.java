package appeng.core.core.api.tick;

import appeng.api.AEModInfo;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.AttachCapabilitiesEvent;

import java.util.function.Consumer;

public interface Tickables<T> extends Consumer<ChildrenTickable<T>> {

	void tick(T parent);

	ResourceLocation KEY = new ResourceLocation(AEModInfo.MODID, "tickables");

	/**
	 * Retrieves cap provider providing pending tickables cap
	 * @param event attach caps event
	 * @param <T> type thing having tickables
	 * @return cap provider providing pending tickables cap
	 */
	static <T> ICapabilityProvider getPendingTickablesProvider(AttachCapabilitiesEvent<T> event){
		return event.getCapabilities().get(KEY);
	}

}
