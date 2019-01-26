package code.elix_x.excore.utils.client.render.perspective;

import code.elix_x.excomms.reflection.ReflectionHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.util.math.MathHelper;

public class PerspectiveHelper {

	private static final ReflectionHelper.AMethod<EntityRenderer, Float> getFOVModifier = new ReflectionHelper.AClass<>(EntityRenderer.class).<Float>getDeclaredMethod(new String[]{"getFOVModifier", "func_78481_a"}, float.class, boolean.class).orElseThrow(() -> new IllegalArgumentException("Failed to reflect fields necessary for shaders debug")).setAccessible(true);

	public static float getFOVModifier(EntityRenderer entityRenderer, float partialTicks, boolean useFOVSetting){
		return getFOVModifier.invoke(entityRenderer, partialTicks, useFOVSetting).get();
	}

	public static float getFOVModifier(float partialTicks){
		return getFOVModifier(Minecraft.getMinecraft().entityRenderer, partialTicks, true);
	}

	public static float getAspectRatio(){
		return Minecraft.getMinecraft().displayWidth / (float) Minecraft.getMinecraft().displayHeight;
	}

	public static final float zNear = 0.05f;

	public static float getZFar(){
		return Minecraft.getMinecraft().gameSettings.renderDistanceChunks * 16 * MathHelper.SQRT_2;
	}

}
