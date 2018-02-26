package appeng.core.me.config;

import appeng.core.me.api.parts.PartColor;
import code.elix_x.excomms.color.RGBA;

import java.util.HashMap;
import java.util.Map;

public class MEConfig {

	private Map<String, RGBA> partColors = new HashMap<>();

	public MEConfig(){
	}

	public void registerColors(){
		partColors.forEach((name, ref) -> PartColor.createNewColor(name, ref, PartColor.defaultCompatibility(ref)));
	}

}
