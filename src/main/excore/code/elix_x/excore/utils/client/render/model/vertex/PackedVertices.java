package code.elix_x.excore.utils.client.render.model.vertex;

import java.util.Iterator;
import java.util.List;

import com.google.common.collect.ImmutableList;

import net.minecraft.client.renderer.vertex.VertexFormat;

public class PackedVertices implements Iterable<PackedVertex> {

	private final int mode;
	private final VertexFormat format;
	private final ImmutableList<PackedVertex> vertices;

	public PackedVertices(int mode, VertexFormat format, PackedVertex... vertices){
		this.mode = mode;
		this.format = format;
		this.vertices = ImmutableList.copyOf(vertices);
	}

	public PackedVertices(int mode, VertexFormat format, List<PackedVertex> vertices){
		this.mode = mode;
		this.format = format;
		this.vertices = ImmutableList.copyOf(vertices);
	}

	public PackedVertices(int mode, VertexFormat format, float[][][] vertexData){
		this.mode = mode;
		this.format = format;
		ImmutableList.Builder<PackedVertex> verticesBuilder = ImmutableList.builder();
		for(float[][] vertex : vertexData){
			verticesBuilder.add(new PackedVertex(format, vertex));
		}
		this.vertices = verticesBuilder.build();
	}

	public int getMode(){
		return mode;
	}

	public VertexFormat getFormat(){
		return format;
	}

	public List<PackedVertex> getVertices(){
		return vertices;
	}

	@Override
	public Iterator<PackedVertex> iterator(){
		return vertices.iterator();
	}

	public float[][][] getData(){
		float[][][] data = new float[vertices.size()][][];
		for(int v = 0; v < data.length; v++)
			data[v] = vertices.get(v).getData();
		return data;
	}

	public DefaultUnpackedVertices unpack(){
		return new DefaultUnpackedVertices(format, vertices);
	}

}
