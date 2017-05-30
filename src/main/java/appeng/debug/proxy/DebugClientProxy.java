package appeng.debug.proxy;

import net.minecraftforge.fml.relauncher.Side;

public class DebugClientProxy extends DebugProxy {

	public DebugClientProxy(){
		super(Side.CLIENT);
	}
}
