package appeng.core.staticfire.gui;

import appeng.core.AppEng;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;

public class TestGui extends GuiScreen{

    ResourceLocation testGuiTexture = new ResourceLocation(AppEng.MODID,"textures/gui/test.png");


    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        Minecraft.getMinecraft().renderEngine.bindTexture(testGuiTexture);
        int centerX = (width/2)-176/2;
        int centerY = (height/2)-166/2;
        super.drawTexturedModalRect(centerX,centerY,0,0,176,166);

    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    public void initGui() {
    }
}
