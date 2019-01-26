package code.elix_x.excore.utils.client.render;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import org.lwjgl.opengl.GL20;
import org.lwjgl.util.glu.Project;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;

public class OpenGLHelper {

	public static void enableClientState(VertexFormat format){
		for(int i = 0; i < format.getElementCount(); i++){
			VertexFormatElement element = format.getElement(i);
			switch(element.getUsage()){
				case POSITION:
					GlStateManager.glEnableClientState(GL_VERTEX_ARRAY);
					break;
				case NORMAL:
					GlStateManager.glEnableClientState(GL_NORMAL_ARRAY);
					break;
				case COLOR:
					GlStateManager.glEnableClientState(GL_COLOR_ARRAY);
					break;
				case UV:
					OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit + element.getIndex());
					GlStateManager.glEnableClientState(GL_TEXTURE_COORD_ARRAY);
					break;
				case PADDING:
					break;
				case GENERIC:
					GL20.glEnableVertexAttribArray(element.getIndex());
					break;
				default:
					break;
			}
		}
	}

	public static void disableClientState(VertexFormat format){
		for(VertexFormatElement element : format.getElements()){
			switch(element.getUsage()){
				case POSITION:
					GlStateManager.glDisableClientState(GL_VERTEX_ARRAY);
					break;
				case NORMAL:
					GlStateManager.glDisableClientState(GL_NORMAL_ARRAY);
					break;
				case COLOR:
					GlStateManager.glDisableClientState(GL_COLOR_ARRAY);
					// is this really needed?
					GlStateManager.resetColor();
					break;
				case UV:
					OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit + element.getIndex());
					GlStateManager.glDisableClientState(GL_TEXTURE_COORD_ARRAY);
					OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit);
					break;
				case PADDING:
					break;
				case GENERIC:
					glDisableVertexAttribArray(element.getIndex());
				default:
					break;
			}
		}
	}

	public static void projectionMatrix(float fov, float aspectRatio, float zNear, float zFar){
		GlStateManager.loadIdentity();
		Project.gluPerspective(fov, aspectRatio, zNear, zFar);
	}

}
