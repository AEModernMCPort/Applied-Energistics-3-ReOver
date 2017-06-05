package appeng.core.api.net.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public interface GuiHandler {

	<T> void registerGuiElement(ResourceLocation gui, GuiHandlerElement<T> element);

	<T> void registerGuiClientElement(ResourceLocation gui, GuiHandlerElement<T> element);

	void display(ResourceLocation gui, EntityPlayer player, World world, int x, int y, int z);

	@FunctionalInterface
	interface GuiHandlerElement<T> {

		T getGuiElement(EntityPlayer player, World world, int x, int y, int z);

	}

}
