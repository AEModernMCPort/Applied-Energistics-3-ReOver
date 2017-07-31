package appeng.core.me.proxy;

import appeng.core.lib.proxy.BaseProxy;
import appeng.core.me.client.part.ClientPartHelper;
import com.owens.oobjloader.parser.ResourceHelper;
import net.minecraftforge.fml.relauncher.Side;

import java.util.Optional;

public abstract class MEProxy extends BaseProxy {

	public MEProxy(Side side){
		super(side);
	}

	public abstract ResourceHelper getResourceHelper();

	public abstract Optional<ClientPartHelper> clientPartHelper();

}
