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
package code.elix_x.excore.utils.net.gui;

import code.elix_x.excore.utils.net.gui.SmartGuiHandler.SmartGuiHandlerElement;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;

public class SmartGuiHandler<T extends Enum & SmartGuiHandlerElement> implements IGuiHandler {

	public final Class<T> clas;
	public final Object mod;

	public SmartGuiHandler(Class<T> clas){
		this.clas = clas;
		this.mod = Loader.instance().activeModContainer().getMod();
		NetworkRegistry.INSTANCE.registerGuiHandler(mod, this);
	}

	public void display(T element, EntityPlayer player, World world, int x, int y, int z){
		player.openGui(mod, element.ordinal(), world, x, y, z);
	}

	@Override
	public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z){
		return clas.getEnumConstants()[id].getServerGuiElement(player, world, x, y, z);
	}

	@Override
	public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z){
		return clas.getEnumConstants()[id].getClientGuiElement(player, world, x, y, z);
	}

	public static interface SmartGuiHandlerElement {

		public Object getServerGuiElement(EntityPlayer player, World world, int x, int y, int z);

		public Object getClientGuiElement(EntityPlayer player, World world, int x, int y, int z);

	}

}
