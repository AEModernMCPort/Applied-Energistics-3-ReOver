package appeng.core.skyfall.skyobject;

import appeng.core.AppEng;
import appeng.core.lib.capability.SingleCapabilityProvider;
import appeng.core.skyfall.AppEngSkyfall;
import appeng.core.skyfall.api.skyobject.Skyobject;
import appeng.core.skyfall.api.skyobject.SkyobjectsManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = AppEng.MODID)
public class SkyobjectsManagerImpl implements SkyobjectsManager {

	@SubscribeEvent
	public static void attachToWorld(AttachCapabilitiesEvent<World> event){
		event.addCapability(new ResourceLocation(AppEng.MODID, "skyobjects_manager"), new SingleCapabilityProvider<>(AppEngSkyfall.skyobjectsManagerCapability, new SkyobjectsManagerImpl()));
	}

	@SubscribeEvent
	public static void tickSkyobjects(TickEvent.WorldTickEvent event){
		if(event.phase == TickEvent.Phase.END) event.world.getCapability(AppEngSkyfall.skyobjectsManagerCapability, null).tick(event.world);
	}

	protected List<Skyobject> skyobjects = new ArrayList<>();

	@Override
	public void tick(World world){
		skyobjects.forEach(skyobject -> skyobject.tick(world));
	}

	@Override
	public NBTTagCompound serializeNBT(){
		NBTTagCompound nbt = new NBTTagCompound();
		NBTTagList skyobjects = new NBTTagList();
		this.skyobjects.forEach(skyobject -> {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setString("id", skyobject.getProvider().getRegistryName().toString());
			tag.setTag("data", skyobject.getProvider().serializeNBT(skyobject));
			skyobjects.appendTag(tag);
		});
		nbt.setTag("skyobjects", skyobjects);
		return nbt;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt){
		NBTTagList skyobjects = nbt.getTagList("skyobjects", 10);
		skyobjects.forEach(tag -> this.skyobjects.add(AppEngSkyfall.INSTANCE.getSkyobjectProvidersRegistry().getValue(new ResourceLocation(((NBTTagCompound) tag).getString("id"))).deserializeNBT(((NBTTagCompound) tag).getCompoundTag("data"))));
	}

}
