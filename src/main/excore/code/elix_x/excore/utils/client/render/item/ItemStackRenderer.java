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
package code.elix_x.excore.utils.client.render.item;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;

public class ItemStackRenderer {

	public static void renderItemStack(Minecraft minecraft, int xPosition, int yPosition, ItemStack itemstack){
		minecraft.getRenderItem().renderItemAndEffectIntoGUI(null, itemstack, xPosition, yPosition);
		minecraft.getRenderItem().renderItemOverlayIntoGUI(getFontRenderer(minecraft, itemstack), itemstack, xPosition, yPosition, null);
	}

	public static List<String> getTooltip(Minecraft minecraft, ItemStack itemstack){
		List<String> list = itemstack.getTooltip(minecraft.player, minecraft.gameSettings.advancedItemTooltips ? ITooltipFlag.TooltipFlags.ADVANCED : ITooltipFlag.TooltipFlags.NORMAL);
		for(int k = 0; k < list.size(); ++k){
			if(k == 0){
				list.set(k, itemstack.getRarity().color + list.get(k));
			} else{
				list.set(k, TextFormatting.GRAY + list.get(k));
			}
		}
		return list;
	}

	public static FontRenderer getFontRenderer(Minecraft minecraft, ItemStack itemstack){
		FontRenderer fontRenderer = itemstack.getItem().getFontRenderer(itemstack);
		if(fontRenderer == null){
			fontRenderer = minecraft.fontRenderer;
		}
		return fontRenderer;
	}

}
