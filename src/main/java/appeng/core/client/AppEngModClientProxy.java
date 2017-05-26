package appeng.core.client;

import appeng.core.AppEngModProxy;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiErrorScreen;
import net.minecraftforge.fml.client.CustomModLoadingErrorDisplayException;

public class AppEngModClientProxy implements AppEngModProxy {

	@Override
	public void moduleLoadingException(String exceptionText, String guiText){
		throw new CustomModLoadingErrorDisplayException(exceptionText, null) {

			@Override
			public void initGui(GuiErrorScreen errorScreen, FontRenderer fontRenderer){

			}

			@Override
			public void drawScreen(GuiErrorScreen errorScreen, FontRenderer fontRenderer, int mouseRelX, int mouseRelY, float tickTime){
				errorScreen.drawDefaultBackground();
				errorScreen.drawCenteredString(fontRenderer, guiText, errorScreen.width / 2, 75, 0xFFFFFF);
			}
		};
	}

}