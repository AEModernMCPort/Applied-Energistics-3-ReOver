package appeng.debug.proxy;

import net.minecraftforge.fml.relauncher.Side;

public class DebugServerProxy extends DebugProxy {

	public DebugServerProxy(){
		super(Side.SERVER);
	}
}
