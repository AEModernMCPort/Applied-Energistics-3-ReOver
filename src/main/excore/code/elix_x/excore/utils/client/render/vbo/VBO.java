package code.elix_x.excore.utils.client.render.vbo;

import java.nio.ByteBuffer;

import org.lwjgl.opengl.GL15;

public class VBO {

	private final int vboId;

	public VBO(int vboId){
		this.vboId = vboId;
	}

	public VBO(){
		this(GL15.glGenBuffers());
	}

	public int getVboId(){
		return vboId;
	}

	public void bind(){
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId);
	}

	public void data(ByteBuffer buffer, int usage){
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, usage);
	}

	public void unbind(){
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
	}

	public void cleanUp(){
		unbind();
		GL15.glDeleteBuffers(vboId);
	}

}
