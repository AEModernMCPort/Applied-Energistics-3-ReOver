package appeng.core.skyfall.proxy;

import appeng.api.module.AEStateEvent;
import appeng.core.skyfall.AppEngSkyfall;
import code.elix_x.excore.utils.client.render.OpenGLHelper;
import code.elix_x.excore.utils.client.render.perspective.PerspectiveHelper;
import code.elix_x.excore.utils.client.render.vbo.VertexBufferSingleVBO;
import code.elix_x.excore.utils.client.render.wtw.WTWRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import org.lwjgl.opengl.GL11;

public class SkyfallClientProxy extends SkyfallProxy {

	public SkyfallClientProxy(){
		super(Side.CLIENT);
	}

	@Override
	public void preInit(AEStateEvent.AEPreInitializationEvent event){
		MinecraftForge.EVENT_BUS.register(this);

		super.preInit(event);
	}

	@Override
	public void postInit(AEStateEvent.AEPostInitializationEvent event){
		Minecraft.getMinecraft().getFramebuffer().enableStencil();

		super.postInit(event);
	}

	private VertexBufferSingleVBO skySphere;

	@SubscribeEvent
	public void renderSkyobjects(RenderWorldLastEvent event){
		//FIXME Frustum culling???
		WTWRenderer.Phase.STENCILDEPTHREADWRITE.render(() -> {
			GlStateManager.pushMatrix();
			GlStateManager.disableTexture2D();
			GlStateManager.disableCull();

			GlStateManager.matrixMode(GL11.GL_PROJECTION);
			GlStateManager.pushMatrix();
			OpenGLHelper.projectionMatrix(PerspectiveHelper.getFOVModifier(event.getPartialTicks()), PerspectiveHelper.getAspectRatio(), PerspectiveHelper.zNear, 1000000);
			GlStateManager.matrixMode(GL11.GL_MODELVIEW);

			Entity entity = Minecraft.getMinecraft().getRenderViewEntity();
			double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * event.getPartialTicks();
			double y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * event.getPartialTicks();
			double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * event.getPartialTicks();
			GlStateManager.translate(-x, -y, -z);

			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder buffer = tessellator.getBuffer();
			buffer.begin(GL11.GL_TRIANGLE_STRIP, DefaultVertexFormats.POSITION_COLOR);
			Minecraft.getMinecraft().world.getCapability(AppEngSkyfall.skyobjectsManagerCapability, null).getAllSkyobjects().forEach(skyobject -> drawBox(buffer, skyobject.getRendererBoundingBox(event.getPartialTicks())));
			tessellator.draw();

			GlStateManager.matrixMode(GL11.GL_PROJECTION);
			GlStateManager.popMatrix();
			GlStateManager.matrixMode(GL11.GL_MODELVIEW);

			GlStateManager.enableCull();
			GlStateManager.enableTexture2D();
			GlStateManager.popMatrix();
		}, () -> {
			GlStateManager.pushMatrix();

			GlStateManager.matrixMode(GL11.GL_PROJECTION);
			GlStateManager.pushMatrix();
			OpenGLHelper.projectionMatrix(PerspectiveHelper.getFOVModifier(event.getPartialTicks()), PerspectiveHelper.getAspectRatio(), PerspectiveHelper.zNear, 1000000);
			GlStateManager.matrixMode(GL11.GL_MODELVIEW);

			Entity entity = Minecraft.getMinecraft().getRenderViewEntity();
			double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * event.getPartialTicks();
			double y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * event.getPartialTicks();
			double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * event.getPartialTicks();
			GlStateManager.translate(-x, -y, -z);

			Minecraft.getMinecraft().world.getCapability(AppEngSkyfall.skyobjectsManagerCapability, null).getAllSkyobjects().forEach(skyobject -> skyobject.render(event.getPartialTicks()));

			GlStateManager.matrixMode(GL11.GL_PROJECTION);
			GlStateManager.popMatrix();
			GlStateManager.matrixMode(GL11.GL_MODELVIEW);

			GlStateManager.popMatrix();
		});
	}

	protected void drawBox(BufferBuilder buffer, AxisAlignedBB box){
		if(box != null) RenderGlobal.addChainedFilledBoxVertices(buffer, box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ, 1, 1, 1, 1);
	}

}
