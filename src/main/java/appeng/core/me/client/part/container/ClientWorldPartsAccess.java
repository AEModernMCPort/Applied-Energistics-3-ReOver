package appeng.core.me.client.part.container;

import appeng.core.AppEng;
import appeng.core.me.AppEngME;
import appeng.core.me.api.client.part.PartRenderingHandler;
import appeng.core.me.api.parts.PartPositionRotation;
import appeng.core.me.api.parts.container.PartUUID;
import appeng.core.me.api.parts.part.Part;
import appeng.core.me.client.part.ClientPartHelper;
import appeng.core.me.parts.container.WorldPartsAccess;
import appeng.core.me.parts.part.PartsHelperImpl;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.joml.Vector3d;
import org.lwjgl.BufferUtils;

import javax.annotation.Nonnull;
import java.util.*;

public class ClientWorldPartsAccess extends WorldPartsAccess {

	protected Map<Part.State, PartRenderingHandler.Dynamic> dynamicRHs = new HashMap<>();
	protected List<Part.State> created = new ArrayList<>();
	protected List<Part.State> removed = new ArrayList<>();

	public ClientWorldPartsAccess(World world){
		super(world);
	}

	@Override
	protected <P extends Part<P, S>, S extends Part.State<P, S>> void onPartAppeared(@Nonnull S part){
		super.onPartAppeared(part);
		if(world.isRemote) created.add(part);
	}

	@Override
	protected <P extends Part<P, S>, S extends Part.State<P, S>> void onPartDisappeared(@Nonnull S part){
		super.onPartDisappeared(part);
		if(world.isRemote) removed.add(part);
	}

	@Mod.EventBusSubscriber(modid = AppEng.MODID)
	public static class EventHandler {

		@SubscribeEvent
		public static <P extends Part<P, S>, S extends Part.State<P, S>> void postWorldRender(RenderWorldLastEvent event){
			ClientPartHelper cph = AppEngME.proxy.clientPartHelper().get();
			ClientWorldPartsAccess partsAccess = (ClientWorldPartsAccess) Minecraft.getMinecraft().world.getCapability(PartsHelperImpl.worldPartsAccessCapability, null);
			partsAccess.created.stream().filter(p -> !partsAccess.removed.contains(p)).forEach(p -> cph.<P, S>getRenderingHandler(p.getPart()).createDynamicRH((S) p).ifPresent(drh -> {
				drh.init();
				partsAccess.dynamicRHs.put(p, drh);
			}));
			partsAccess.removed.stream().map(partsAccess.dynamicRHs::remove).filter(Objects::nonNull).forEach(PartRenderingHandler.Dynamic::cleanup);
			partsAccess.created.clear();
			partsAccess.removed.clear();

			if(!partsAccess.dynamicRHs.isEmpty()){
				GlStateManager.pushMatrix();
				EntityPlayer player = Minecraft.getMinecraft().player;
				GlStateManager.translate(-(player.prevPosX + (player.posX - player.prevPosX) * event.getPartialTicks()), -(player.prevPosY + (player.posY - player.prevPosY) * event.getPartialTicks()), -(player.prevPosZ + (player.posZ - player.prevPosZ) * event.getPartialTicks()));
				partsAccess.dynamicRHs.forEach((part, drh) -> {
					GlStateManager.pushMatrix();
					PartPositionRotation positionRotation = part.getAssignedPosRot();
					Vector3d trans = positionRotation.getPosition().asVector3d();
					GlStateManager.translate(trans.x, trans.y, trans.z);
					GlStateManager.pushMatrix();
					GlStateManager.multMatrix(positionRotation.getRotation().getRotationF().get(BufferUtils.createFloatBuffer(16)));
					drh.render(event.getPartialTicks());
					GlStateManager.popMatrix();
					GlStateManager.popMatrix();
				});
				GlStateManager.popMatrix();
			}
		}

	}

}
