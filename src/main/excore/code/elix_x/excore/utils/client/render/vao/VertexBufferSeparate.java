package code.elix_x.excore.utils.client.render.vao;

import java.nio.ByteBuffer;

import net.minecraft.client.renderer.BufferBuilder;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;

import code.elix_x.excore.utils.client.render.vbo.VBO;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.client.renderer.vertex.VertexFormatElement.EnumUsage;

public class VertexBufferSeparate extends VertexBuffer {

	private static VertexFormat removePaddings(VertexFormat format){
		VertexFormat nf = new VertexFormat();
		for(VertexFormatElement element : format.getElements()){
			if(element.getUsage() != VertexFormatElement.EnumUsage.PADDING) nf.addElement(element);
		}
		return nf;
	}

	private final VBO[] vbos;
	private final int[] indexMap;
	
	private VertexBufferSeparate(VertexFormat format, int drawMode, int vertexCount){
		super(removePaddings(format), drawMode, vertexCount);
		this.vbos = new VBO[this.format.getElementCount()];
		for(int i = 0; i < this.vbos.length; i++){
			this.vbos[i] = new VBO();
		}
		this.indexMap = new int[this.vbos.length];
		for(int i = 0; i < this.indexMap.length; i++){
			this.indexMap[i] = i;
		}
	}

	public VertexBufferSeparate(BufferBuilder vertexBuffer){
		this(vertexBuffer.getVertexFormat(), vertexBuffer.getDrawMode(), vertexBuffer.getVertexCount());
		ByteBuffer bytebuf = vertexBuffer.getByteBuffer();
		ByteBuffer[] byteBuffers = new ByteBuffer[vbos.length];
		for(int e = 0; e < byteBuffers.length; e++){
			byteBuffers[e] = createByteBuffer(vertexCount, this.format.getElement(e));
		}
		for(int v = 0; v < vertexCount; v++){
			int ee = 0;
			for(int e = 0; e < vertexBuffer.getVertexFormat().getElementCount(); e++){
				if(vertexBuffer.getVertexFormat().getElement(e).getUsage() == EnumUsage.PADDING){
					bytebuf.get(new byte[vertexBuffer.getVertexFormat().getElement(e).getSize()]);
					continue;
				} else{
					ByteBuffer buffer = byteBuffers[ee];
					VertexFormatElement element = this.format.getElement(ee);
					byte[] bytes = new byte[element.getSize()];
					bytebuf.get(bytes);
					buffer.put(bytes);
					ee++;
				}
			}
		}
		vao.bind();
		for(int b = 0; b < vbos.length; b++){
			VBO vbo = vbos[b];
			ByteBuffer buffer = byteBuffers[b];
			VertexFormatElement formatElement = format.getElement(b);
			buffer.flip();
			vbo.bind();
			vbo.data(buffer, GL15.GL_STATIC_DRAW);
			vao.vboSeparate(vbo, indexMap[b], formatElement);
			vbo.unbind();
		}
		vao.unbind();
	}

	@Override
	protected void renderPre(){
		super.renderPre();
		for(int i : indexMap){
			GL20.glEnableVertexAttribArray(i);
		}
	}

	@Override
	protected void renderPost(){
		for(int i : indexMap){
			GL20.glDisableVertexAttribArray(i);
		}
		super.renderPost();
	}
	
	@Override
	public void cleanUp(){
		for(VBO vbo : vbos){
			vbo.cleanUp();
		}
		super.cleanUp();
	}

}
