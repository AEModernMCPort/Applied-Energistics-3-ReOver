package appeng.core.lib.client.render.model.pipeline;

import code.elix_x.excomms.pipeline.Pipeline;
import code.elix_x.excomms.pipeline.PipelineElement;
import code.elix_x.excomms.pipeline.list.ListPipelineElement;
import code.elix_x.excomms.pipeline.list.ToListTransformersPipelineElement;
import code.elix_x.excore.utils.client.render.model.UnpackedBakedQuad;
import code.elix_x.excore.utils.client.render.model.vertex.DefaultUnpackedVertices;
import org.joml.Matrix4f;

public class QuadMatrixTransformer implements PipelineElement<UnpackedBakedQuad, UnpackedBakedQuad> {

	private VertexMatrixTransformer vertexTransformer = new VertexMatrixTransformer(new Matrix4f());
	private final PipelineElement<DefaultUnpackedVertices, Void> vertexTransformerPipeline = new Pipeline<>(new ToListTransformersPipelineElement.Iterable(), ListPipelineElement.wrapperE(vertexTransformer));

	public QuadMatrixTransformer(Matrix4f matrix){
		setMatrix(matrix);
	}

	public Matrix4f getMatrix(){
		return vertexTransformer.getMatrix();
	}

	public void setMatrix(Matrix4f matrix){
		this.vertexTransformer.setMatrix(matrix);
	}

	@Override
	public UnpackedBakedQuad pipe(UnpackedBakedQuad in){
//		in.setFace(TRSRTransformation.rotate(matrix, in.getFace()));
		vertexTransformerPipeline.pipe(in.getVertices());
		return in;
	}
}
