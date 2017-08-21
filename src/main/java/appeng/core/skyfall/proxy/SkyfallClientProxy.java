package appeng.core.skyfall.proxy;

import appeng.core.skyfall.AppEngSkyfall;
import code.elix_x.excore.utils.client.render.wtw.WTWRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import org.lwjgl.opengl.GL11;

public class SkyfallClientProxy extends SkyfallProxy {

	public SkyfallClientProxy(){
		super(Side.CLIENT);
	}

	@SubscribeEvent
	public void renderSkyobjects(RenderWorldLastEvent event){
		//FIXME Frustum culling
		WTWRenderer.Phase.STENCILDEPTHREADWRITE.render(() -> {
			GlStateManager.pushMatrix();
			GlStateManager.disableTexture2D();
			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder buffer = tessellator.getBuffer();
			buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
			Minecraft.getMinecraft().world.getCapability(AppEngSkyfall.skyobjectsManagerCapability, null).getAllSkyobjects().forEach(skyobject -> drawBox(buffer, skyobject.getRendererBoundingBox()));
			tessellator.draw();
			GlStateManager.enableTexture2D();
			GlStateManager.popMatrix();
		}, () -> Minecraft.getMinecraft().world.getCapability(AppEngSkyfall.skyobjectsManagerCapability, null).getAllSkyobjects().forEach(skyobject -> skyobject.render(event.getPartialTicks())));
	}

	protected void drawBox(BufferBuilder buffer, AxisAlignedBB box){
		if(box != null) RenderGlobal.addChainedFilledBoxVertices(buffer, box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ, 1, 1, 1, 1);
	}

}
