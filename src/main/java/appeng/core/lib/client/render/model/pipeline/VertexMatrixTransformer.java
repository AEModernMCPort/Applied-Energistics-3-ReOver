package appeng.core.lib.client.render.model.pipeline;

import code.elix_x.excomms.pipeline.PipelineElement;
import code.elix_x.excore.utils.client.render.model.vertex.DefaultUnpackedVertex;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.lwjgl.util.vector.Vector3f;

public class VertexMatrixTransformer implements PipelineElement<DefaultUnpackedVertex, DefaultUnpackedVertex> {

	private Matrix4f matrix;

	public VertexMatrixTransformer(Matrix4f matrix){
		setMatrix(matrix);
	}

	public Matrix4f getMatrix(){
		return matrix;
	}

	public void setMatrix(Matrix4f matrix){
		this.matrix = matrix;
	}

	@Override
	public DefaultUnpackedVertex pipe(DefaultUnpackedVertex in){
		Vector4f trans = matrix.transform(new Vector4f(in.getPos().x, in.getPos().y, in.getPos().z, 1));
		//TODO Apply transform with w=0 to normals
		return in.setPos(new Vector3f(trans.x, trans.y, trans.z));
	}

}
