/*******************************************************************************
 * Copyright 2016 Elix_x
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package code.elix_x.excore.utils.client.render.wtw;

import code.elix_x.excore.utils.client.render.IVertexBuffer;
import code.elix_x.excore.utils.client.render.vbo.VertexBufferSingleVBO;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;
import java.util.Stack;
import java.util.function.Consumer;

public class WTWRenderer implements Runnable {

	private static final WTWRenderer INSTANCE = new WTWRenderer();
	private static Stack<WTWRenderer> current = new Stack<>();
	private static int depth = 0;
	private static int stencilDepth = 0;

	private static IVertexBuffer depthOverridePlane;
	private static boolean reuploadDOP = false;

	static {
		MinecraftForge.EVENT_BUS.register(WTWRenderer.class);
		current.push(INSTANCE);
	}

	private static IVertexBuffer getDepthOverridePlane(){
		if(reuploadDOP && depthOverridePlane != null){
			depthOverridePlane.cleanUp();
			depthOverridePlane = null;
			reuploadDOP = false;
		}
		if(depthOverridePlane == null){
			BufferBuilder buff = new BufferBuilder(8);
			buff.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
			buff.pos(-1, -1, 1).color(0f, 1f, 0f, 1f).endVertex();
			buff.pos(1, -1, 1).color(0f, 1f, 0f, 1f).endVertex();
			buff.pos(1, 1, 1).color(0f, 1f, 0f, 1f).endVertex();
			buff.pos(-1, 1, 1).color(0f, 1f, 0f, 1f).endVertex();
			buff.finishDrawing();
			depthOverridePlane = new VertexBufferSingleVBO(buff);
		}
		return depthOverridePlane;
	}

	public static void drawDepthOverridePlane(){
		GlStateManager.depthFunc(GL11.GL_ALWAYS);
		GlStateManager.disableCull();

		GlStateManager.pushMatrix();
		GlStateManager.loadIdentity();

		GlStateManager.matrixMode(GL11.GL_PROJECTION);
		GlStateManager.pushMatrix();
		GlStateManager.loadIdentity();
		GlStateManager.matrixMode(GL11.GL_MODELVIEW);

		//WTH?
		GlStateManager.disableTexture2D();
		getDepthOverridePlane().draw();
		GlStateManager.enableTexture2D();

		GlStateManager.matrixMode(GL11.GL_PROJECTION);
		GlStateManager.popMatrix();
		GlStateManager.matrixMode(GL11.GL_MODELVIEW);

		GlStateManager.popMatrix();

		GlStateManager.enableCull();
		GlStateManager.depthFunc(GL11.GL_LEQUAL);
	}

	@SubscribeEvent(priority = EventPriority.LOW)
	public static void renderWorldLast(RenderWorldLastEvent event){
		GL11.glClearStencil(0);
		GL11.glClear(GL11.GL_STENCIL_BUFFER_BIT);
		INSTANCE.run();
	}

	public static WTWRenderer instance(){
		return current.peek();
	}

	public static void pushInstance(){
		current.push(new WTWRenderer());
	}

	public static WTWRenderer popInstance(){
		return current.pop();
	}

	private Multimap<Phase, Consumer<WTWRenderer>> renderers = ArrayListMultimap.create();

	private void render(@Nonnull Phase phase, Consumer<WTWRenderer> renderer){
		renderers.put(phase, renderer);
	}

	private void render(@Nonnull Phase phase, Runnable renderer){
		render(phase, wtw -> renderer.run());
	}

	public void run(){
		//TODO user config
		if(depth > 16) return;
		depth++;
		for(Phase phase : Phase.values()){
			if(renderers.containsKey(phase)){
				phase.phasePre(this);
				renderers.get(phase).forEach(renderer -> renderer.accept(this));
				phase.phasePost(this);
			}
		}
		renderers.clear();
		depth--;
	}

	public enum Phase {

		NORMAL{

			public void render(Runnable... phaseSpecifics){
				instance().render(this, phaseSpecifics[0]);
			}

		},
		DEPTHREADONLY{
			@Override
			void phasePre(WTWRenderer wtw){
				GlStateManager.depthMask(false);
			}

			public void render(Runnable... phaseSpecifics){
				instance().render(this, phaseSpecifics[0]);
			}

			@Override
			void phasePost(WTWRenderer wtw){
				GlStateManager.depthMask(true);
			}
		},
		STENCIL{
			@Override
			void phasePre(WTWRenderer wtw){
				assert Minecraft.getMinecraft().getFramebuffer().isStencilEnabled() : "WTW Renderer can't work without stencils. Please enable them";

				if(stencilDepth == 0) GL11.glEnable(GL11.GL_STENCIL_TEST);
				stencilDepth++;
			}

			public void render(Runnable... phaseSpecifics){
				instance().render(this, wtw -> {
					//Stencil setup
					GlStateManager.colorMask(false, false, false, false);
					GlStateManager.depthMask(false);
					GL11.glStencilFunc(GL11.GL_EQUAL, stencilDepth - 1, 255);
					GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_INCR);
					GL11.glStencilMask(255);
					phaseSpecifics[0].run();

					GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_KEEP);
					GL11.glStencilFunc(GL11.GL_EQUAL, stencilDepth, 255);

					//Color write
					GlStateManager.depthMask(true);
					GlStateManager.colorMask(true, true, true, true);
					phaseSpecifics[1].run();

					//Stencil cleanup
					GlStateManager.colorMask(false, false, false, false);
					GlStateManager.depthMask(false);

					GL11.glStencilFunc(GL11.GL_EQUAL, stencilDepth, 255);
					GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_DECR);
					GL11.glStencilMask(255);
					phaseSpecifics[0].run();

					GlStateManager.depthMask(true);
					GlStateManager.colorMask(true, true, true, true);
				});
			}

			@Override
			void phasePost(WTWRenderer wtw){
				stencilDepth--;
				if(stencilDepth == 0) GL11.glDisable(GL11.GL_STENCIL_TEST);
			}
		},
		STENCILDEPTHREADWRITE{

			@Override
			void phasePre(WTWRenderer wtw){
				assert Minecraft.getMinecraft().getFramebuffer().isStencilEnabled() : "WTW Renderer can't work without stencils. Please enable them";

				if(stencilDepth == 0) GL11.glEnable(GL11.GL_STENCIL_TEST);
				stencilDepth++;
			}

			public void render(Runnable... phaseSpecifics){
				instance().render(this, wtw -> {
					//Stencil setup
					GlStateManager.colorMask(false, false, false, false);
					GlStateManager.depthMask(false);
					GL11.glStencilFunc(GL11.GL_EQUAL, stencilDepth - 1, 255);
					GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_INCR);
					GL11.glStencilMask(255);
					phaseSpecifics[0].run();

					GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_KEEP);
					GL11.glStencilFunc(GL11.GL_EQUAL, stencilDepth, 255);

					//Depth clean
					GlStateManager.depthMask(true);
					WTWRenderer.drawDepthOverridePlane();

					//Color write
					GlStateManager.colorMask(true, true, true, true);
					phaseSpecifics[1].run();

					//Stencil cleanup
					GlStateManager.colorMask(false, false, false, false);
					GlStateManager.depthMask(false);

					GL11.glStencilFunc(GL11.GL_EQUAL, stencilDepth, 255);
					GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_DECR);
					GL11.glStencilMask(255);
					phaseSpecifics[0].run();

					GlStateManager.depthMask(true);
					GlStateManager.colorMask(true, true, true, true);
				});
			}

			@Override
			void phasePost(WTWRenderer wtw){
				stencilDepth--;
				if(stencilDepth == 0) GL11.glDisable(GL11.GL_STENCIL_TEST);
			}

		};

		public abstract void render(Runnable... phaseSpecifics);

		void phasePre(WTWRenderer wtw){

		}

		void phasePost(WTWRenderer wtw){

		}

	}


}
