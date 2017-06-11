package appeng.core.core.net.gui;

import appeng.core.AppEng;
import appeng.core.api.net.gui.GuiHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class CoreGuiHandler implements IGuiHandler, GuiHandler {

	private final Map<ResourceLocation, Integer> guiElements = new HashMap<>();
	private final Map<Integer, GuiHandlerElement> commonElements = new HashMap<>();
	private final Map<Integer, GuiHandlerElement> clientElements = new HashMap<>();

	private int nextId = 0;

	public CoreGuiHandler(){
		NetworkRegistry.INSTANCE.registerGuiHandler(AppEng.instance(), this);
	}

	@Override
	public <T> void registerGuiElement(ResourceLocation gui, GuiHandlerElement<T> element){
		guiElements.put(gui, nextId);
		commonElements.put(nextId, element);
		nextId++;
	}

	@Override
	public <T> void registerGuiClientElement(ResourceLocation gui, GuiHandlerElement<T> element){
		clientElements.put(guiElements.get(gui), element);
	}

	@Override
	public void display(ResourceLocation gui, EntityPlayer player, World world, int x, int y, int z){
		player.openGui(AppEng.instance(), guiElements.get(gui), world, x, y, z);
	}

	@Nullable
	@Override
	public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z){
		return commonElements.get(id).getGuiElement(player, world, x, y, z);
	}

	@Nullable
	@Override
	public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z){
		//System.out.print("ASDADS: "+clientElements.get(id));
		return clientElements.get(id).getGuiElement(player, world, x, y, z);
	}

}
