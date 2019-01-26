package code.elix_x.excore.utils.client.render.pipeline.quad;

import code.elix_x.excomms.pipeline.PipelineElement;
import code.elix_x.excore.utils.client.render.model.UnpackedBakedQuad;
import code.elix_x.excore.utils.client.render.model.vertex.DefaultUnpackedVertices;

public class QuadVerticesPipeline implements PipelineElement<UnpackedBakedQuad, UnpackedBakedQuad> {

	private final PipelineElement<DefaultUnpackedVertices, DefaultUnpackedVertices> pipeline;

	public QuadVerticesPipeline(PipelineElement<DefaultUnpackedVertices, DefaultUnpackedVertices> pipeline){
		this.pipeline = pipeline;
	}

	@Override
	public UnpackedBakedQuad pipe(UnpackedBakedQuad in){
		in.setVertices(pipeline.pipe(in.getVertices()));
		return in;
	}

}
