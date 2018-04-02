package appeng.core.me.network.storage.caps;

import appeng.core.AppEng;
import appeng.core.lib.capability.SingleCapabilityProvider;
import appeng.core.me.api.network.Network;
import appeng.core.me.api.network.storage.caps.EntityNetworkStorage;
import appeng.core.me.api.network.storage.caps.FluidNetworkStorage;
import appeng.core.me.api.network.storage.caps.ItemNetworkStorage;
import appeng.core.me.api.network.storage.caps.NetworkStorageSpace;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = AppEng.MODID)
public class NetworkStorageCaps {

	public static final ResourceLocation NSSRL = new ResourceLocation(AppEng.MODID, "nss");
	public static final ResourceLocation ITEMRL = new ResourceLocation(AppEng.MODID, "ns_item");
	public static final ResourceLocation BLOCKRL = new ResourceLocation(AppEng.MODID, "ns_block");
	public static final ResourceLocation FLUIDRL = new ResourceLocation(AppEng.MODID, "ns_fluid");
	public static final ResourceLocation ENTITYRL = new ResourceLocation(AppEng.MODID, "ns_entity");

	@CapabilityInject(NetworkStorageSpace.class)
	public static Capability<NetworkStorageSpace> nss;

	@CapabilityInject(ItemNetworkStorage.class)
	public static Capability<ItemNetworkStorage> item;

	/*
	 * TODO 1.13 Implement
	@CapabilityInject(BlockNetworkStorage.class)
	public static Capability<BlockNetworkStorage> block;
	*/

	@CapabilityInject(FluidNetworkStorage.class)
	public static Capability<FluidNetworkStorage> fluid;

	@CapabilityInject(EntityNetworkStorage.class)
	public static Capability<EntityNetworkStorage> entity;

	@SubscribeEvent
	public static void attachStorageCaps(AttachCapabilitiesEvent<Network> event){
		event.addCapability(NSSRL, new SingleCapabilityProvider.Serializeable<>(nss, new NetworkStorageSpaceImpl()));

		event.addCapability(ITEMRL, new SingleCapabilityProvider.Serializeable<>(item, new ItemNetworkStorageImpl()));
//		event.addCapability(BLOCKRL, new SingleCapabilityProvider.Serializeable<>(block, new BlockNetworkStorageImpl())); TODO 1.13 Implement
		event.addCapability(FLUIDRL, new SingleCapabilityProvider.Serializeable<>(fluid, new FluidNetworkStorageImpl()));
		event.addCapability(ENTITYRL, new SingleCapabilityProvider.Serializeable<>(entity, new EntityNetworkStorageImpl()));
	}

}
