package appeng.core.staticfire.tesr;

import appeng.core.AppEng;
import appeng.core.staticfire.tileEntity.FiberCableMakerTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.model.TRSRTransformation;
import org.lwjgl.opengl.GL11;

public class FiberCableMakerTESR extends TileEntitySpecialRenderer<FiberCableMakerTileEntity> {

    private IModel model;
    private IBakedModel bakedModel;

    public static final ResourceLocation TEXTURE_BEACON_BEAM = new ResourceLocation("textures/entity/beacon_beam.png");

    private IBakedModel getBakedModel() {
        // Since we cannot bake in preInit() we do lazy baking of the model as soon as we need it
        // for rendering
        if (bakedModel == null) {
            try {
                //model = ModelLoaderRegistry.getModel(new ResourceLocation(AppEng.MODID, "blockstates/fiber_cable_maker.json"));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            bakedModel = model.bake(TRSRTransformation.identity(), DefaultVertexFormats.BLOCK,
                    location -> Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(location.toString()));
        }
        return bakedModel;
    }


    @Override
    public void render(FiberCableMakerTileEntity te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        GlStateManager.alphaFunc(516, 0.1F);
        this.bindTexture(TEXTURE_BEACON_BEAM);

        GlStateManager.pushAttrib();
        GlStateManager.pushMatrix();

        // Translate to the location of our tile entity
        GlStateManager.translate(x, y, z);
        GlStateManager.disableRescaleNormal();

        // Render the rotating handles
        //renderHandles(te);

        //renderBeamSegment(x, y, z, partialTicks, 40.F, (double)te.getWorld().getTotalWorldTime(), 1, 10, new float[]{10f,20f,150f},1D,1.1D);

        GlStateManager.popMatrix();
        GlStateManager.popAttrib();
    }

    private void renderHandles(FiberCableMakerTileEntity te) {
        GlStateManager.pushMatrix();

        GlStateManager.translate(.5, 0, .5);
        long angle = (System.currentTimeMillis() / 10) % 360;
        GlStateManager.rotate(angle, 0, 1, 0);

        RenderHelper.disableStandardItemLighting();
        this.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        if (Minecraft.isAmbientOcclusionEnabled()) {
            GlStateManager.shadeModel(GL11.GL_SMOOTH);
        } else {
            GlStateManager.shadeModel(GL11.GL_FLAT);
        }

        World world = te.getWorld();
        // Translate back to local view coordinates so that we can do the acual rendering here
        GlStateManager.translate(-te.getPos().getX(), -te.getPos().getY(), -te.getPos().getZ());

        Tessellator tessellator = Tessellator.getInstance();
        tessellator.getBuffer().begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
        /*
        Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelRenderer().renderModel(
             world,
               getBakedModel(),
              world.getBlockState(te.getPos()),
               te.getPos(),
               Tessellator.getInstance().getBuffer(),

        true);
        */
        tessellator.draw();

        RenderHelper.enableStandardItemLighting();
        GlStateManager.popMatrix();
    }


}
