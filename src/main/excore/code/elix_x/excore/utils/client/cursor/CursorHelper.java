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
package code.elix_x.excore.utils.client.cursor;

import java.awt.image.BufferedImage;
import java.nio.IntBuffer;

import javax.imageio.ImageIO;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Cursor;
import org.lwjgl.input.Mouse;

import com.google.common.base.Throwables;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

public class CursorHelper {

	public static final Cursor defaultCursor = Mouse.getNativeCursor();

	public static Cursor createCursor(ResourceLocation texture){
		try{
			BufferedImage image = ImageIO.read(Minecraft.getMinecraft().getResourceManager().getResource(texture).getInputStream());
			return new Cursor(image.getWidth(), image.getHeight(), image.getWidth() / 2, image.getHeight() / 2, 1, IntBuffer.wrap(image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth())), null);
		} catch(Exception e){
			throw Throwables.propagate(e);
		}
	}

	public static Cursor createCursor(ResourceLocation texture, int hotSpotX, int hotSpotY){
		try{
			BufferedImage image = ImageIO.read(Minecraft.getMinecraft().getResourceManager().getResource(texture).getInputStream());
			return new Cursor(image.getWidth(), image.getHeight(), hotSpotX, hotSpotY, 1, IntBuffer.wrap(image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth())), null);
		} catch(Exception e){
			throw Throwables.propagate(e);
		}
	}

	public static void setCursor(Cursor cursor){
		try{
			Mouse.setNativeCursor(cursor);
		} catch(LWJGLException e){
			Throwables.propagate(e);
		}
	}

	public static void resetCursor(){
		setCursor(defaultCursor);
	}

}
