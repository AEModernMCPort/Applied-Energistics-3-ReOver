package appeng.core.crafting.proxy;

import net.minecraftforge.fml.relauncher.Side;

public class CraftingClientProxy extends CraftingProxy {

	public CraftingClientProxy(){
		super(Side.SERVER);
	}
}
