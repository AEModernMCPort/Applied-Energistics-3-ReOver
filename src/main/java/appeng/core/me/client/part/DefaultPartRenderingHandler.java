package appeng.core.me.client.part;

import appeng.core.core.client.render.model.ModelRegManagerHelper;
import appeng.core.lib.client.render.model.pipeline.QuadMatrixTransformer;
import appeng.core.me.api.client.part.PartRenderingHandler;
import appeng.core.me.api.parts.PartPositionRotation;
import appeng.core.me.api.parts.PartRotation;
import appeng.core.me.api.parts.VoxelPosition;
import appeng.core.me.api.parts.part.Part;
import code.elix_x.excomms.pipeline.Pipeline;
import code.elix_x.excomms.pipeline.PipelineElement;
import code.elix_x.excomms.pipeline.list.ListPipelineElement;
import code.elix_x.excore.utils.client.render.model.UnpackedBakedQuad;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import org.joml.Matrix4f;

import java.util.Collection;
import java.util.List;

import static appeng.core.me.api.parts.container.GlobalVoxelsInfo.VOXELSPERBLOCKAXISF;
import static appeng.core.me.api.parts.container.GlobalVoxelsInfo.VOXELSPERBLOCKAXISI;

public class DefaultPartRenderingHandler<P extends Part<P, S>, S extends Part.State<P, S>> implements PartRenderingHandler<P, S> {

	@Override
	public Collection<BakedQuad> getQuads(S state, PartPositionRotation partPositionRotation){
		PartRotation rotation = partPositionRotation.getRotation();
		VoxelPosition rp = partPositionRotation.getRotationCenterPosition();
		Vec3i add = rp.getGlobalPosition().subtract(partPositionRotation.getPosition().getGlobalPosition());
		BlockPos renderingPosition = rp.getLocalPosition().add(add.getX() * VOXELSPERBLOCKAXISI, add.getY() * VOXELSPERBLOCKAXISI, add.getZ() * VOXELSPERBLOCKAXISI);
		return new Pipeline<List<BakedQuad>, List<BakedQuad>>(ListPipelineElement.wrapperE(new Pipeline<>((PipelineElement<BakedQuad, UnpackedBakedQuad>) UnpackedBakedQuad::unpack, new QuadMatrixTransformer(new Matrix4f().translate(renderingPosition.getX() / VOXELSPERBLOCKAXISF, renderingPosition.getY() / VOXELSPERBLOCKAXISF, renderingPosition.getZ() / VOXELSPERBLOCKAXISF).mul(rotation.getRotationF())), (PipelineElement<UnpackedBakedQuad, BakedQuad>) quad -> quad.pack(DefaultVertexFormats.BLOCK)))).pipe(ModelRegManagerHelper.getModel(new ModelResourceLocation(state.getMesh(), null)).getQuads(null, null, 0));
	}

}
