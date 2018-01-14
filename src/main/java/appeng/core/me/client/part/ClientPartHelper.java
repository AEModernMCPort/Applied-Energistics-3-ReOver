package appeng.core.me.client.part;

import appeng.core.core.client.render.model.ModelRegManagerHelper;
import appeng.core.lib.client.render.model.pipeline.QuadMatrixTransformer;
import appeng.core.me.AppEngME;
import appeng.core.me.api.parts.PartPositionRotation;
import appeng.core.me.api.parts.VoxelPosition;
import appeng.core.me.api.parts.container.IPartsContainer;
import appeng.core.me.api.parts.container.IWorldPartsAccess;
import appeng.core.me.api.parts.part.Part;
import appeng.core.me.api.parts.placement.VoxelRayTraceHelper;
import appeng.core.me.item.PartPlacerItem;
import appeng.core.me.parts.part.PartsHelper;
import code.elix_x.excomms.color.RGBA;
import code.elix_x.excomms.pipeline.Pipeline;
import code.elix_x.excomms.pipeline.PipelineElement;
import code.elix_x.excomms.pipeline.list.ListPipelineElement;
import code.elix_x.excore.utils.client.render.model.UnpackedBakedQuad;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joml.Matrix4d;
import org.joml.Matrix4f;
import org.joml.Vector4d;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static appeng.core.me.api.parts.container.GlobalVoxelsInfo.*;

public class ClientPartHelper {

	public static final Logger logger = LogManager.getLogger("Client Parts Helper");

	public static org.lwjgl.util.vector.Matrix4f toLWJGL(Matrix4f joml){
		org.lwjgl.util.vector.Matrix4f lwjgl = new org.lwjgl.util.vector.Matrix4f();
		lwjgl.m00 = joml.m00();
		lwjgl.m01 = joml.m01();
		lwjgl.m02 = joml.m02();
		lwjgl.m03 = joml.m03();
		lwjgl.m10 = joml.m10();
		lwjgl.m11 = joml.m11();
		lwjgl.m12 = joml.m12();
		lwjgl.m13 = joml.m13();
		lwjgl.m20 = joml.m20();
		lwjgl.m21 = joml.m21();
		lwjgl.m22 = joml.m22();
		lwjgl.m23 = joml.m23();
		lwjgl.m30 = joml.m30();
		lwjgl.m31 = joml.m31();
		lwjgl.m32 = joml.m32();
		lwjgl.m33 = joml.m33();
		return lwjgl;
	}

	protected PartsHelper partsHelper(){
		return AppEngME.INSTANCE.getPartsHelper();
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public <P extends Part<P, S>, S extends Part.State<P, S>> void allPartsRegistered(RegistryEvent.Register event){
		if(event.getGenericType() == Part.class){
			logger.info("Registering client part models");
			((RegistryEvent.Register<P>) event).getRegistry().forEach(part -> {
				part.getMeshes().forEach(mesh -> ModelRegManagerHelper.loadAndRegisterModel(new ModelResourceLocation(mesh, null), PartsHelper.getFullStateMeshLocation(mesh)));
				//TODO FIXME Make this perspective aware
				ModelRegManagerHelper.loadAndRegisterModel(new ModelResourceLocation(part.getRootMesh(), "inventory"), PartsHelper.getFullStateMeshLocation(part.getRootMesh()), optional -> !optional.isPresent() ? Optional.of(new TRSRTransformation(TRSRTransformation.toVecmath(toLWJGL(computeItemMatrix(part))))) : Optional.empty(), DefaultVertexFormats.ITEM);
			});
		}
	}

	protected Matrix4f computeItemMatrix(Part part){
		//FIXME Why //KILLMEPLZ is not a marker?
		Matrix4d ematrix = new Matrix4d().rotate(Math.PI / 4, 1, 1, 1).scale(VOXELSIZED);
		AxisAlignedBB obbox = partsHelper().getPartVoxelBB(part);
		Vector4d v1 = ematrix.transform(new Vector4d(obbox.minX, obbox.minY, obbox.minZ, 1));
		Vector4d v2 = ematrix.transform(new Vector4d(obbox.maxX, obbox.maxY, obbox.maxZ, 1));
		AxisAlignedBB ebbox = new AxisAlignedBB(v1.x, v1.y, v1.z, v2.x, v2.y, v2.z);
		AxisAlignedBB target = new AxisAlignedBB(0, 0, 0, 1, 1, 1);
		double scaleX = (target.maxX - target.minX) / (ebbox.maxX - ebbox.minX);
		double scaleY = (target.maxY - target.minY) / (ebbox.maxY - ebbox.minY);
		double scaleZ = (target.maxZ - target.minZ) / (ebbox.maxZ - ebbox.minZ);
		float scale = (float) Math.min(scaleX, Math.min(scaleY, scaleZ));
		return new Matrix4f().translate((float) -obbox.getCenter().x * VOXELSIZEF, (float) -obbox.getCenter().y * VOXELSIZEF, 0).translate(0.5f, 0.5f, -10).rotateY((float) Math.toRadians(45)).rotateX((float) Math.toRadians(33.5)).scale(scale);
	}

	public List<BakedQuad> bakeQuads(IPartsContainer container){
		List<BakedQuad> quads = new ArrayList<>();
		container.getOwnedParts().forEach((state, partPositionRotation) -> quads.addAll(new Pipeline<List<BakedQuad>, List<BakedQuad>>(ListPipelineElement.wrapperE(new Pipeline<>((PipelineElement<BakedQuad, UnpackedBakedQuad>) UnpackedBakedQuad::unpack, new QuadMatrixTransformer(new Matrix4f().translate(partPositionRotation.getPosition().getLocalPosition().getX() / VOXELSPERBLOCKAXISF, partPositionRotation.getPosition().getLocalPosition().getY() / VOXELSPERBLOCKAXISF, partPositionRotation.getPosition().getLocalPosition().getZ() / VOXELSPERBLOCKAXISF).mul(partPositionRotation.getRotation().getRotationF())), (PipelineElement<UnpackedBakedQuad, BakedQuad>) quad -> quad.pack(DefaultVertexFormats.BLOCK)))).pipe(ModelRegManagerHelper.getModel(new ModelResourceLocation(state.getMesh(), null)).getQuads(null, null, 0))));
		return quads;
	}

	@SubscribeEvent
	public void drawHighlight(DrawBlockHighlightEvent event){
		if(event.getTarget().typeOfHit == RayTraceResult.Type.BLOCK){
			VoxelPosition targetVoxel = null;
			if(event.getTarget().hitInfo instanceof VoxelPosition){
				targetVoxel = VoxelRayTraceHelper.getOrApproximateHitVoxel(event.getTarget());
				IWorldPartsAccess worldPartsAccess = event.getPlayer().world.getCapability(PartsHelper.worldPartsAccessCapability, null);
				worldPartsAccess.getAPartAtVoxel(targetVoxel).map(stateInfo -> partsHelper().getGlobalBBox(stateInfo.getLeft().getPart(), stateInfo.getRight())).ifPresent(box -> drawSelectionBox(box, event.getPlayer(), event.getPartialTicks()));

				//TODO Remove this debug stuff???
				if(event.getPlayer().isSneaking()){
					IPartsContainer container = event.getPlayer().world.getTileEntity(event.getTarget().getBlockPos()).getCapability(PartsHelper.partsContainerCapability, null);
					allVoxelsInABlockStream().filter(container::hasPart).forEach(voxel -> drawSelectionBox(new VoxelPosition(event.getTarget().getBlockPos(), voxel).getBB(), event.getPlayer(), event.getPartialTicks(), new RGBA(0, 0, 0, 0.3f), Mode.OUTLINE));
				}
				event.setCanceled(true);
			}
			if(event.getPlayer().getHeldItem(EnumHand.MAIN_HAND).getItem() instanceof PartPlacerItem || event.getPlayer().getHeldItem(EnumHand.MAIN_HAND).getItem() instanceof PartPlacerItem){
				PartPlacerItem partPlacerItem = (PartPlacerItem) (event.getPlayer().getHeldItem(EnumHand.MAIN_HAND).getItem() instanceof PartPlacerItem ? event.getPlayer().getHeldItem(EnumHand.MAIN_HAND).getItem() : event.getPlayer().getHeldItem(EnumHand.OFF_HAND).getItem());
				targetVoxel = VoxelRayTraceHelper.getOrApproximateHitVoxel(event.getTarget());

				IWorldPartsAccess worldPartsAccess = event.getPlayer().world.getCapability(PartsHelper.worldPartsAccessCapability, null);
				PartPositionRotation positionRotation = partPlacerItem.getPartPlacementLogic().getPlacementPosition(event.getPlayer(), event.getTarget());
				AxisAlignedBB selectionBBox = partsHelper().getGlobalBBox(partPlacerItem.getPPart(), positionRotation);
				if(worldPartsAccess.canPlace(positionRotation, partPlacerItem.getPPart())) drawSelectionBox(selectionBBox, event.getPlayer(), event.getPartialTicks(), new RGBA(0, 1f, 0, 0.5f), Mode.INLINE);
				else drawSelectionBox(selectionBBox, event.getPlayer(), event.getPartialTicks(), new RGBA(1f, 0, 0, 0.5f), Mode.INLINE);

				partsHelper().getVoxels(partPlacerItem.getPPart(), positionRotation).forEach(voxel -> drawSelectionBox(voxel.getBB(), event.getPlayer(), event.getPartialTicks(), new RGBA(0, 0, 1f, 0.6f), Mode.NONE));
			}
			if(targetVoxel != null){
				drawSelectionBox(targetVoxel.getBB(), event.getPlayer(), event.getPartialTicks());
			}
		}
	}

	public void drawSelectionBox(AxisAlignedBB box, EntityPlayer player, float partialTicks){
		drawSelectionBox(box, player, partialTicks, new RGBA(0, 0, 0, 0.4f), Mode.OUTLINE);
	}

	public void drawSelectionBox(AxisAlignedBB box, EntityPlayer player, float partialTicks, RGBA color, Mode mode){
		GlStateManager.enableBlend();
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		GlStateManager.glLineWidth(2.0F);
		GlStateManager.disableTexture2D();
		GlStateManager.depthMask(false);

		double d0 = player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTicks;
		double d1 = player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTicks;
		double d2 = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTicks;
		RenderGlobal.drawSelectionBoundingBox(box.grow(mode.mul * 0.0020000000949949026D).offset(-d0, -d1, -d2), color.getRF(), color.getGF(), color.getBF(), color.getAF());

		GlStateManager.depthMask(true);
		GlStateManager.enableTexture2D();
		GlStateManager.disableBlend();
	}

	enum Mode {
		OUTLINE(1), NONE(0), INLINE(-1);

		int mul;

		Mode(int mul){
			this.mul = mul;
		}

	}

}
