package code.elix_x.excore.utils.client.render.pipeline.model;

import code.elix_x.excomms.pipeline.PipelineElement;
import code.elix_x.excomms.pipeline.list.ListPipelineElement;
import code.elix_x.excore.utils.client.render.model.UnpackedBakedQuad;
import code.elix_x.excore.utils.client.render.model.UnpackedSimpleBakedModel;
import net.minecraft.util.EnumFacing;

public class ModelQuadsPipeline implements PipelineElement<UnpackedSimpleBakedModel, UnpackedSimpleBakedModel> {

	private final ListPipelineElement<UnpackedBakedQuad, UnpackedBakedQuad> pipeline;

	public ModelQuadsPipeline(ListPipelineElement<UnpackedBakedQuad, UnpackedBakedQuad> pipeline){
		this.pipeline = pipeline;
	}

	@Override
	public UnpackedSimpleBakedModel pipe(UnpackedSimpleBakedModel in){
		in.setGeneralQuads(pipeline.pipe(in.getGeneralQuads()));
		for(EnumFacing facing : EnumFacing.values()) in.setFaceQuads(facing, pipeline.pipe(in.getFaceQuads(facing)));
		return in;
	}

}
