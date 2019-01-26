package code.elix_x.excore.utils.client.render.vao;

import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import code.elix_x.excore.utils.client.render.vbo.VBO;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.client.renderer.vertex.VertexFormatElement.EnumUsage;

public class VAO {

	private final int vaoId;

	public VAO(){
		this.vaoId = GL30.glGenVertexArrays();
	}

	public void bind(){
		GL30.glBindVertexArray(vaoId);
	}

	public void vboSeparate(VBO vbo, int vboIndex, VertexFormatElement format){
		GL20.glVertexAttribPointer(vboIndex, format.getElementCount(), format.getType().getGlConstant(), false, 0, 0);
	}

	public void vboSingle(VBO vbo, int vboIndex, VertexFormat format, VertexFormatElement elementFormat){
		GL20.glVertexAttribPointer(vboIndex, elementFormat.getElementCount(), elementFormat.getType().getGlConstant(), false, format.getSize(), format.getOffset(format.getElements().indexOf(elementFormat)));
	}

	public void unbind(){
		GL30.glBindVertexArray(0);
	}

	public void cleanUp(){
		unbind();
		GL30.glDeleteVertexArrays(vaoId);
	}

}
