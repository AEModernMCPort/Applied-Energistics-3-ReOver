package code.elix_x.excore.utils.client.render.vao;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import net.minecraft.client.renderer.BufferBuilder;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;

import code.elix_x.excore.utils.client.render.vbo.VBO;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.client.renderer.vertex.VertexFormatElement.EnumUsage;

public class VertexBufferSingle extends VertexBuffer {

	private final VBO[] vbos;

	private VertexBufferSingle(VertexFormat format, int drawMode, int vertexCount){
		super(format, drawMode, vertexCount);
		Set<Integer> vboIndexes = new HashSet<>();
		for(VertexFormatElement element : format.getElements()){
			vboIndexes.add(element.getIndex());
		}
		this.vbos = new VBO[vboIndexes.size()];
		for(int i = 0; i < this.vbos.length; i++){
			this.vbos[i] = new VBO();
		}
	}

	public VertexBufferSingle(BufferBuilder vertexBuffer){
		this(vertexBuffer.getVertexFormat(), vertexBuffer.getDrawMode(), vertexBuffer.getVertexCount());
		int[] sizes = new int[vbos.length];
		Arrays.fill(sizes, 0);
		for(VertexFormatElement element : format.getElements()){
			sizes[element.getIndex()] += element.getSize();
		}
		ByteBuffer bytebuf = vertexBuffer.getByteBuffer();
		ByteBuffer[] byteBuffers = new ByteBuffer[vbos.length];
		for(int e = 0; e < byteBuffers.length; e++){
			byteBuffers[e] = BufferUtils.createByteBuffer(sizes[e] * vertexCount);
		}
		for(int v = 0; v < vertexCount; v++){
			for(VertexFormatElement element : format.getElements()){
				ByteBuffer buffer = byteBuffers[element.getIndex()];
				byte[] bytes = new byte[element.getSize()];
				bytebuf.get(bytes);
				buffer.put(bytes);
			}
		}
		System.out.println(Arrays.toString(byteBuffers));
		vao.bind();
		for(int b = 0; b < vbos.length; b++){
			VBO vbo = vbos[b];
			ByteBuffer buffer = byteBuffers[b];
			VertexFormatElement formatElement = format.getElement(b);
			buffer.flip();
			vbo.bind();
			vbo.data(buffer, GL15.GL_STATIC_DRAW);
			for(VertexFormatElement element : format.getElements()){
				if(element.getIndex() == b){
					vao.vboSingle(vbo, b, format, formatElement);
				}
			}
			vbo.unbind();
		}
		vao.unbind();
	}

	@Override
	protected void renderPre(){
		super.renderPre();
		for(int i = 0; i < vbos.length; i++){
			GL20.glEnableVertexAttribArray(i);
		}
	}

	@Override
	protected void renderPost(){
		for(int i = 0; i < vbos.length; i++){
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
