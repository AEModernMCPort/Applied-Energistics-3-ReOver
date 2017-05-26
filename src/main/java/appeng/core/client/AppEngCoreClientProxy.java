package appeng.core.client;

import appeng.core.AppEng;
import appeng.core.api.util.AECableType;
import appeng.core.api.util.AEColor;
import appeng.core.entity.EntityTinyTNTPrimed;
import appeng.core.hooks.TickHandler;
import appeng.core.hooks.TickHandler.PlayerColor;
import appeng.core.lib.client.render.RenderFloatingItem;
import appeng.core.lib.client.render.model.ModelsCache;
import appeng.core.lib.client.render.model.UVLModelLoader;
import appeng.core.lib.entity.EntityFloatingItem;
import appeng.core.lib.helpers.IMouseWheelItem;
import appeng.core.lib.sync.network.NetworkHandler;
import appeng.core.lib.sync.packets.PacketValueConfig;
import appeng.core.me.AppEngME;
import appeng.core.me.api.part.PartRegistryEntry;
import appeng.core.me.part.AEBasePart;
import appeng.core.server.AppEngCoreServerProxy;
import appeng.miscellaneous.client.render.RenderTinyTNTPrimed;
import appeng.tools.client.render.texture.ParticleTextures;
import com.google.common.collect.ImmutableMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.io.IOException;

public class AppEngCoreClientProxy extends AppEngCoreServerProxy {

	@Override
	public void preInit(FMLPreInitializationEvent event){
		MinecraftForge.EVENT_BUS.register(this);
		ModelLoaderRegistry.registerLoader(UVLModelLoader.INSTANCE);
		((IReloadableResourceManager) Minecraft.getMinecraft().getResourceManager()).registerReloadListener(ModelsCache.INSTANCE);
	}

	@Override
	public void init(FMLInitializationEvent event){

	}

	@Override
	public void postInit(FMLPostInitializationEvent event){
		final RenderManager inst = Minecraft.getMinecraft().getRenderManager();

		inst.entityRenderMap.put(EntityTinyTNTPrimed.class, new RenderTinyTNTPrimed(inst));
		inst.entityRenderMap.put(EntityFloatingItem.class, new RenderFloatingItem(inst));
	}

	@SubscribeEvent
	public void postPlayerRender(final RenderLivingEvent.Pre p){
		final PlayerColor player = TickHandler.INSTANCE.getPlayerColors().get(p.getEntity().getEntityId());
		if(player != null){
			final AEColor col = player.myColor;

			final float r = 0xff & (col.mediumVariant >> 16);
			final float g = 0xff & (col.mediumVariant >> 8);
			final float b = 0xff & (col.mediumVariant);
			GL11.glColor3f(r / 255.0f, g / 255.0f, b / 255.0f);
		}
	}

	@SubscribeEvent
	public void onModelBakeEvent(final ModelBakeEvent event){
		UVLModelLoader.INSTANCE.setLoader(event.getModelLoader());
	}

	@SubscribeEvent
	public void wheelEvent(final MouseEvent me){
		if(me.getDwheel() == 0){
			return;
		}

		final Minecraft mc = Minecraft.getMinecraft();
		final EntityPlayer player = mc.player;
		if(player.isSneaking()){
			final EnumHand hand;
			if(player.getHeldItem(EnumHand.MAIN_HAND) != null && player.getHeldItem(EnumHand.MAIN_HAND).getItem() instanceof IMouseWheelItem){
				hand = EnumHand.MAIN_HAND;
			} else if(player.getHeldItem(EnumHand.OFF_HAND) != null && player.getHeldItem(EnumHand.OFF_HAND).getItem() instanceof IMouseWheelItem){
				hand = EnumHand.OFF_HAND;
			} else {
				return;
			}

			final ItemStack is = player.getHeldItem(hand);
			try{
				NetworkHandler.instance.sendToServer(new PacketValueConfig("Item", me.getDwheel() > 0 ? "WheelUp" : "WheelDown"));
				me.setCanceled(true);
			} catch(final IOException e){
				AELog.debug(e);
			}
		}
	}

	@SubscribeEvent
	public void onTextureStitch(final TextureStitchEvent.Pre event){
		ParticleTextures.registerSprite(event);
		for(AECableType type : AECableType.VALIDCABLES){
			for(IModel model : new IModel[]{ModelsCache.INSTANCE.getOrLoadModel(type.getModel()), ModelsCache.INSTANCE.getOrLoadModel(type.getConnectionModel()), ModelsCache.INSTANCE.getOrLoadModel(type.getStraightModel())}){
				for(ResourceLocation location : model.getTextures()){
					for(AEColor color : AEColor.values()){
						if(type.displayedChannels() > 0){
							for(int i = 0; i <= type.displayedChannels(); i++){
								event.getMap().registerSprite(AEBasePart.replaceProperties(location, ImmutableMap.of("color", color.name(), "channels", String.valueOf(i))));
							}
						} else {
							event.getMap().registerSprite(AEBasePart.replaceProperties(location, ImmutableMap.of("color", color.name())));
						}
					}
				}
			}
		}
		for(PartRegistryEntry part : AppEngME.INSTANCE.getPartRegistry()){
			//			if( !part.isCable() )
			//			{
			//TODO 1.11.2-CD:A - First, this does belong to core module. Second, move to format-based models (EXCore).
			IModel model = ModelsCache.INSTANCE.getOrLoadModel(new ResourceLocation(part.getRegistryName().getResourceDomain(), "part/" + part.getRegistryName().getResourcePath()));
			for(ResourceLocation location : model.getTextures()){
				for(AEColor color : AEColor.values()){
					event.getMap().registerSprite(AEBasePart.replaceProperties(location, ImmutableMap.of("color", color.name())));
				}
			}
			//			}
		}

		for(ResourceLocation location : ModelsCache.INSTANCE.getOrLoadModel(new ResourceLocation(AppEng.MODID, "part/cable_facade")).getTextures()){
			event.getMap().registerSprite(location);
		}
	}
}