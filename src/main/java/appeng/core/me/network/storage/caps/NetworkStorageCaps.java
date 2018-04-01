package appeng.core.me.network.storage.caps;

import appeng.core.AppEng;
import appeng.core.lib.capability.SingleCapabilityProvider;
import appeng.core.me.api.network.Network;
import appeng.core.me.api.network.storage.caps.ItemNetworkStorage;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = AppEng.MODID)
public class NetworkStorageCaps {

	public static final ResourceLocation ITEMRL = new ResourceLocation(AppEng.MODID, "item_ns");

	@CapabilityInject(ItemNetworkStorage.class)
	public static Capability<ItemNetworkStorage> item;

	@SubscribeEvent
	public static void attachStorageCaps(AttachCapabilitiesEvent<Network> event){
		event.addCapability(ITEMRL, new SingleCapabilityProvider.Serializeable<>(item, new ItemNetworkStorageImpl()));
	}

}
