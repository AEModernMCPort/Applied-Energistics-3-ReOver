package code.elix_x.excore.utils.client.render.vbo;

import java.nio.ByteBuffer;
import java.util.function.Supplier;

import net.minecraft.client.renderer.BufferBuilder;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;

import code.elix_x.excomms.reflection.ReflectionHelper.AClass;
import code.elix_x.excomms.reflection.ReflectionHelper.AField;
import code.elix_x.excore.utils.client.render.IVertexBuffer;
import code.elix_x.excore.utils.client.render.OpenGLHelper;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;

public class VertexBufferSingleVBO implements IVertexBuffer {

	protected final VertexFormat format;
	protected final int drawMode;
	protected final int vertexCount;
	private final VBO vbo;

	private VertexBufferSingleVBO(VertexFormat format, int drawMode, int vertexCount, VBO vbo){
		this.format = format;
		this.drawMode = drawMode;
		this.vertexCount = vertexCount;
		this.vbo = vbo;
	}

	public VertexBufferSingleVBO(BufferBuilder vertexBuffer){
		this(vertexBuffer.getVertexFormat(), vertexBuffer.getDrawMode(), vertexBuffer.getVertexCount(), new VBO());
		ByteBuffer buffer = vertexBuffer.getByteBuffer();
		vbo.bind();
		vbo.data(buffer, GL15.GL_STATIC_DRAW);
		vbo.unbind();
	}

	private static final Supplier<IllegalArgumentException> EXC = () -> new IllegalArgumentException("Failed ro reflect fields necessary for VertexBufferSingleVBO");
	private static final AClass<net.minecraft.client.renderer.vertex.VertexBuffer> vertexBufferClass = new AClass<>(net.minecraft.client.renderer.vertex.VertexBuffer.class);
	private static final AField<net.minecraft.client.renderer.vertex.VertexBuffer, Integer> glBufferId = vertexBufferClass.<Integer>getDeclaredField("glBufferId", "field_177365_a").orElseThrow(EXC).setAccessible(true);
	private static final AField<net.minecraft.client.renderer.vertex.VertexBuffer, VertexFormat> vertexFormat = vertexBufferClass.<VertexFormat>getDeclaredField("vertexFormat", "field_177363_b").orElseThrow(EXC).setAccessible(true);
	private static final AField<net.minecraft.client.renderer.vertex.VertexBuffer, Integer> count = vertexBufferClass.<Integer>getDeclaredField("count", "field_177363_b").orElseThrow(EXC).setAccessible(true);

	public VertexBufferSingleVBO(net.minecraft.client.renderer.vertex.VertexBuffer vertexBuffer, int drawMode){
		this(vertexFormat.get(vertexBuffer).get(), drawMode, count.get(vertexBuffer).get(), new VBO(glBufferId.get(vertexBuffer).get()));
	}

	private boolean modifyClientStates = true;

	public VertexBufferSingleVBO setModifyClientStates(boolean modifyClientStates){
		this.modifyClientStates = modifyClientStates;
		return this;
	}

	protected void renderPre(){
		vbo.bind();
		if(modifyClientStates) OpenGLHelper.enableClientState(format);
		for(int i = 0; i < format.getElementCount(); i++){
			VertexFormatElement element = format.getElement(i);
			switch(element.getUsage()){
				case POSITION:
					GL11.glVertexPointer(element.getElementCount(), element.getType().getGlConstant(), format.getSize(), format.getOffset(i));
					break;
				case NORMAL:
					GL11.glNormalPointer(element.getType().getGlConstant(), format.getSize(), format.getOffset(i));
					break;
				case COLOR:
					GL11.glColorPointer(element.getElementCount(), element.getType().getGlConstant(), format.getSize(), format.getOffset(i));
					break;
				case UV:
					OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit + element.getIndex());
					GL11.glTexCoordPointer(element.getElementCount(), element.getType().getGlConstant(), format.getSize(), format.getOffset(i));
					break;
				case PADDING:
					break;
				case GENERIC:
					GL20.glEnableVertexAttribArray(element.getIndex());
					GL20.glVertexAttribPointer(element.getIndex(), element.getElementCount(), element.getType().getGlConstant(), false, format.getSize(), format.getOffset(i));
					break;
				default:
					break;
			}
		}
	}

	public final void draw(){
		renderPre();
		GlStateManager.glDrawArrays(drawMode, 0, vertexCount);
		renderPost();
	}

	protected void renderPost(){
		if(modifyClientStates) OpenGLHelper.disableClientState(format);
		vbo.unbind();
	}

	@Override
	public void cleanUp(){
		vbo.cleanUp();
	}

}
