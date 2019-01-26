package code.elix_x.excore.utils.client.render.world;

import code.elix_x.excore.utils.client.render.OpenGLHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.IBlockAccess;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MultiChunkBlockAccessRenderer {

	public static int findOptimalBlockDimension(int size, int maxBlock, int minBlock){
		for(int next = maxBlock; next >= minBlock; next--) if(size % next == 0) return next;
		return maxBlock;
	}

	protected final IBlockAccess world;
	// Shapes API has to be rewritten to support IBlockAccesses
	// private final Shape3D shape;
	protected final AxisAlignedBB shape;
	protected final AxisAlignedBB shapeResult;
	protected final Vec3i renderBlock;

	protected int maxUpdatesPerFrame = -1;

	protected boolean[] layers = new boolean[BlockRenderLayer.values().length];

	protected final List<SingleChunkBlockAccessRenderer> renderers = new ArrayList<>();

	public MultiChunkBlockAccessRenderer(IBlockAccess world, AxisAlignedBB shape, AxisAlignedBB shapeResult, Vec3i renderBlock){
		this.world = world;
		this.shape = shape;
		this.shapeResult = shapeResult;
		this.renderBlock = renderBlock;

		initRenderers();
	}

	public MultiChunkBlockAccessRenderer(IBlockAccess world, AxisAlignedBB shape, Vec3d posResult, Vec3i renderBlock){
		this(world, shape, shape.offset(posResult.subtract(shape.getCenter())), renderBlock);
	}

	public MultiChunkBlockAccessRenderer(IBlockAccess world, AxisAlignedBB shape, Vec3i renderBlock){
		this(world, shape, shape, renderBlock);
	}

	public MultiChunkBlockAccessRenderer(IBlockAccess world, AxisAlignedBB shape, AxisAlignedBB shapeResult){
		this.world = world;
		this.shape = shape;
		this.shapeResult = shapeResult;
		this.renderBlock = new Vec3i(findOptimalBlockDimension((int) (this.shape.maxX - this.shape.minX), 16, 4), findOptimalBlockDimension((int) (this.shape.maxY - this.shape.minY), 16, 4), findOptimalBlockDimension((int) (this.shape.maxZ - this.shape.minZ), 16, 4));

		initRenderers();
	}

	public MultiChunkBlockAccessRenderer(IBlockAccess world, AxisAlignedBB shape, Vec3d posResult){
		this(world, shape, shape.offset(posResult.subtract(shape.getCenter())));
	}

	public MultiChunkBlockAccessRenderer(IBlockAccess world, AxisAlignedBB shape){
		this(world, shape, shape);
	}

	/**
	 * Maximum number of single chunk (re)uploads to perform each frame.<br>
	 * Special values:
	 * <ul>
	 *     <li><tt>-1</tt> for unlimited updates</li>
	 *     <li><tt>0</tt> to disable updates</li>
	 * </ul>
	 * @param maxUpdatesPerFrame maximum number of single chunk (re)uploads to perform each frame
	 */
	public void setMaxUpdatesPerFrame(int maxUpdatesPerFrame){
		this.maxUpdatesPerFrame = maxUpdatesPerFrame;
	}

	protected void initRenderers(){
		renderers.clear();
		for(int x = 0; x < Math.ceil((shape.maxX - shape.minX) / renderBlock.getX()); x++){
			for(int y = 0; y < Math.ceil((shape.maxY - shape.minY) / renderBlock.getY()); y++){
				for(int z = 0; z < Math.ceil((shape.maxZ - shape.minZ) / renderBlock.getZ()); z++){
					renderers.add(new SingleChunkBlockAccessRenderer(world, new AxisAlignedBB(shape.minX + x * renderBlock.getX(), shape.minY + y * renderBlock.getY(), shape.minZ + z * renderBlock.getZ(), Math.min(shape.minX + (x + 1) * renderBlock.getX(), shape.maxX), Math.min(shape.minY + (y + 1) * renderBlock.getY(), shape.maxY), Math.min(shape.minZ + (z + 1) * renderBlock.getZ(), shape.maxZ))));
				}
			}
		}
	}

	public boolean doRenderLayer(BlockRenderLayer layer){
		return layers[layer.ordinal()];
	}

	public boolean isEmpty(){
		boolean notEmpty = false;
		for(BlockRenderLayer layer : BlockRenderLayer.values()) notEmpty |= doRenderLayer(layer);
		return !notEmpty;
	}

	public void markDirty(AxisAlignedBB region){
		for(SingleChunkBlockAccessRenderer renderer : renderers) if(renderer.shape.intersects(region)) renderer.markDirty();
	}

	public void markDirty(BlockPos region){
		markDirty(new AxisAlignedBB(region.add(-1, -1, -1), region.add(1, 1, 1)));
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

	protected void updateCheck(){
		if(maxUpdatesPerFrame != 0) updateCheck(maxUpdatesPerFrame);
	}

	/**
	 * Performs update checks and reuploads chunks (during the call!)
	 * @param maxUpdates maximum number of updates to perform
	 * @return number of updates performed
	 */
	public int updateCheck(int maxUpdates){
		int updatesLeft = maxUpdates;
		Iterator<SingleChunkBlockAccessRenderer> iterator = renderers.iterator();
		while(updatesLeft != 0 && iterator.hasNext()) if(iterator.next().updateCheck()) updatesLeft--;
		if(updatesLeft != maxUpdates){
			outer: for(BlockRenderLayer layer : BlockRenderLayer.values()){
				for(SingleChunkBlockAccessRenderer renderer : renderers){
					if(renderer.doRenderLayer(layer)){
						layers[layer.ordinal()] = true;
						continue outer;
					}
				}
			}
		}
		return maxUpdates - updatesLeft;
	}

	protected void renderSetup(){
		OpenGLHelper.enableClientState(DefaultVertexFormats.BLOCK);
		Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		RenderHelper.disableStandardItemLighting();
		Minecraft.getMinecraft().entityRenderer.enableLightmap();
	}

	protected void renderPre(){
		GlStateManager.pushMatrix();
		double scaleX = (shapeResult.maxX - shapeResult.minX) / (shape.maxX - shape.minX);
		double scaleY = (shapeResult.maxY - shapeResult.minY) / (shape.maxY - shape.minY);
		double scaleZ = (shapeResult.maxZ - shapeResult.minZ) / (shape.maxZ - shape.minZ);
		GlStateManager.scale(scaleX, scaleY, scaleZ);
		GlStateManager.translate(renderBlock.getX() / 2d, renderBlock.getY() / 2d, renderBlock.getZ() / 2d);
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
		for(SingleChunkBlockAccessRenderer renderer : renderers) if(renderer.doRenderLayer(layer)){
			GlStateManager.pushMatrix();
			GlStateManager.translate(renderer.shape.minX - shape.minX, renderer.shape.minY - shape.minY, renderer.shape.minZ - shape.minZ);
			renderer.renderLayer(layer);
			GlStateManager.popMatrix();
		}
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

	public void cleanUp(){
		renderers.forEach(SingleChunkBlockAccessRenderer::cleanUp);
	}

}