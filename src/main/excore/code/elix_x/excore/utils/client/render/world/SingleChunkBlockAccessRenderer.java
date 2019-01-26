package code.elix_x.excore.utils.client.render.world;

import code.elix_x.excore.utils.client.render.IVertexBuffer;
import code.elix_x.excore.utils.client.render.OpenGLHelper;
import code.elix_x.excore.utils.client.render.vbo.VertexBufferSingleVBO;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.MinecraftForgeClient;
import org.lwjgl.opengl.GL11;

public class SingleChunkBlockAccessRenderer {

	protected final IBlockAccess world;
	// Shapes API has to be rewritten to support IBlockAccesses
	// private final Shape3D shape;
	protected final AxisAlignedBB shape;
	protected final AxisAlignedBB shapeResult;

	protected final IVertexBuffer[] vertexBuffers = new IVertexBuffer[BlockRenderLayer.values().length];
	protected boolean needsUpdate = true;

	protected boolean[] layers = new boolean[BlockRenderLayer.values().length];

	public SingleChunkBlockAccessRenderer(IBlockAccess world, AxisAlignedBB shape, AxisAlignedBB shapeResult){
		this.world = world;
		this.shape = shape;
		this.shapeResult = shapeResult;
	}

	public SingleChunkBlockAccessRenderer(IBlockAccess world, AxisAlignedBB shape, Vec3d posResult){
		this(world, shape, shape.offset(posResult.subtract(shape.getCenter())));
	}

	public SingleChunkBlockAccessRenderer(IBlockAccess world, AxisAlignedBB shape){
		this(world, shape, shape);
	}

	public boolean doRenderLayer(BlockRenderLayer layer){
		return layers[layer.ordinal()];
	}

	public boolean isEmpty(){
		boolean notEmpty = false;
		for(BlockRenderLayer layer : BlockRenderLayer.values()) notEmpty |= doRenderLayer(layer);
		return !notEmpty;
	}

	public void markDirty(){
		needsUpdate = true;
	}

	public void render(){
		updateCheck();
		if(!isEmpty()){
			renderSetup();
			renderPre();
			for(BlockRenderLayer layer : BlockRenderLayer.values()){
				if(doRenderLayer(layer)){
					renderLayerSetup(layer);
					renderLayer(layer);
					renderLayerCleanup(layer);
				}
			}
			renderPost();
			renderCleanup();
		}
	}

	protected boolean updateCheck(){
		if(needsUpdate){
			rebuildBuffers();
			needsUpdate = false;
			return true;
		}
		return false;
	}

	protected void renderSetup(){
		OpenGLHelper.enableClientState(DefaultVertexFormats.BLOCK);
		Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		RenderHelper.disableStandardItemLighting();
		Minecraft.getMinecraft().entityRenderer.enableLightmap();
	}

	protected void renderPre(){
		GlStateManager.pushMatrix();
		double scaleX = (shapeResult.maxX - shapeResult.minX) / (shape.maxX + 1 - shape.minX);
		double scaleY = (shapeResult.maxY - shapeResult.minY) / (shape.maxY + 1 - shape.minY);
		double scaleZ = (shapeResult.maxZ - shapeResult.minZ) / (shape.maxZ + 1 - shape.minZ);
		GlStateManager.scale(scaleX, scaleY, scaleZ);
		GlStateManager.translate(-0.5f, -0.5f, -0.5f);
		GlStateManager.translate(shapeResult.getCenter().x / scaleX, shapeResult.getCenter().y / scaleY, shapeResult.getCenter().z / scaleZ);
	}

	protected void renderLayerSetup(BlockRenderLayer layer){
		switch(layer){
			case SOLID:
				GlStateManager.disableAlpha();
				break;
			case CUTOUT_MIPPED:
				GlStateManager.enableAlpha();
				break;
			case CUTOUT:
				GlStateManager.enableAlpha();
				Minecraft.getMinecraft().getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, false);
			case TRANSLUCENT:
				GlStateManager.enableAlpha();
				GlStateManager.enableBlend();
		}
	}

	protected void renderLayer(BlockRenderLayer layer){
		vertexBuffers[layer.ordinal()].draw();
	}

	protected void renderLayerCleanup(BlockRenderLayer layer){
		switch(layer){
			case SOLID:
				break;
			case CUTOUT_MIPPED:
				break;
			case CUTOUT:
				Minecraft.getMinecraft().getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).restoreLastBlurMipmap();
				break;
			case TRANSLUCENT:
				GlStateManager.disableBlend();
				break;
		}
	}

	protected void renderPost(){
		GlStateManager.popMatrix();
	}

	protected void renderCleanup(){
		Minecraft.getMinecraft().entityRenderer.disableLightmap();
		RenderHelper.enableStandardItemLighting();
		OpenGLHelper.disableClientState(DefaultVertexFormats.BLOCK);
	}

	protected void rebuildBuffers(){
		cleanUp();
		layers = new boolean[BlockRenderLayer.values().length];
		BlockRenderLayer prev = MinecraftForgeClient.getRenderLayer();
		for(BlockRenderLayer layer : BlockRenderLayer.values()){
			ForgeHooksClient.setRenderLayer(layer);
			BufferBuilder buffer = Tessellator.getInstance().getBuffer();
			buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
			buffer.setTranslation(-shape.getCenter().x, -shape.getCenter().y, -shape.getCenter().z);
			BlockRendererDispatcher blockRenderer = Minecraft.getMinecraft().getBlockRendererDispatcher();
			for(int x = (int) Math.floor(shape.minX); x <= shape.maxX; x++){
				for(int y = (int) Math.floor(shape.minY); y <= shape.maxY; y++){
 					for(int z = (int) Math.floor(shape.minZ); z <= shape.maxZ; z++){
						BlockPos pos = new BlockPos(x, y, z);
						IBlockState state = world.getBlockState(pos);
						if(state.getMaterial() != Material.AIR && state.getRenderType() != EnumBlockRenderType.INVISIBLE && state.getRenderType() != EnumBlockRenderType.ENTITYBLOCK_ANIMATED && state.getBlock().canRenderInLayer(state, layer)){
							layers[layer.ordinal()] = true;
							blockRenderer.renderBlock(state, pos, world, buffer);
						}
					}
				}
			}
			buffer.finishDrawing();
			buffer.setTranslation(0, 0, 0);
			if(layers[layer.ordinal()]) vertexBuffers[layer.ordinal()] = new VertexBufferSingleVBO(buffer).setModifyClientStates(false);
			else buffer.reset();
		}
		ForgeHooksClient.setRenderLayer(prev);
	}

	public void cleanUp(){
		for(IVertexBuffer buffer : vertexBuffers) if(buffer != null) buffer.cleanUp();
	}

}
