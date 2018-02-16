package appeng.core.me.config;

import appeng.core.me.api.network.block.DeviceColor;
import code.elix_x.excomms.color.RGBA;

import java.util.HashMap;
import java.util.Map;

public class MEConfig {

	private Map<String, RGBA> partColors = new HashMap<>();
	private String nocolorColor = "NOCOLOR";

	public MEConfig(){
		partColors.put("NOCOLOR", new RGBA(189, 169, 216));
		partColors.put("GREEN", new RGBA(0f, 1f, 0f));
	}

	public void registerColors(){
		partColors.forEach((name, ref) -> DeviceColor.createNewColor(name, ref, name.equals(nocolorColor) ? other -> true : DeviceColor.defaultCompatibility(ref)));
	}

}
