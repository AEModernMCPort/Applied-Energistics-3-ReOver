package appeng.core.skyfall.skyobject;

import appeng.core.AppEng;
import appeng.core.lib.capability.SingleCapabilityProvider;
import appeng.core.skyfall.AppEngSkyfall;
import appeng.core.skyfall.api.skyobject.SkyobjectsManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = AppEng.MODID)
public class SkyobjectsManagerImpl implements SkyobjectsManager {

	@SubscribeEvent
	public static void attachToWorld(AttachCapabilitiesEvent<World> event){
		event.addCapability(new ResourceLocation(AppEng.MODID, "skyobjects_manager"), new SingleCapabilityProvider<>(AppEngSkyfall.skyobjectsManagerCapability, new SkyobjectsManagerImpl()));
	}

	@Override
	public NBTTagCompound serializeNBT(){
		return null;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt){

	}

}
