package code.elix_x.excore.utils.client.render.model.vertex;

import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;

public class PackedVertex {

	private final VertexFormat format;
	private final float[][] data;

	public PackedVertex(VertexFormat format, float[][] data){
		this.format = format;
		this.data = data;
	}

	public VertexFormat getFormat(){
		return format;
	}

	public float[][] getData(){
		return data;
	}

	public float[] getElementData(VertexFormatElement element){
		assert format.getElements().contains(element) : "Invalid vertex format element - it was not in the format of the vertex";
		return data[format.getElements().indexOf(element)];
	}

	public DefaultUnpackedVertex unpack(){
		return new DefaultUnpackedVertex(format, data);
	}

}
