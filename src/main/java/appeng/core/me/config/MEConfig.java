package appeng.core.me.config;

import appeng.core.me.api.parts.PartColor;
import code.elix_x.excomms.color.RGBA;
import com.google.common.base.Predicates;

import java.util.HashMap;
import java.util.Map;

public class MEConfig {

	private Map<String, RGBA> partColors = new HashMap<>();
	private String nocolorColor = "NOCOLOR";

	public MEConfig(){
		partColors.put("NOCOLOR", new RGBA(189, 169, 216));
	}

	public void registerColors(){
		partColors.forEach((name, ref) -> PartColor.createNewColor(name, ref, name.equals(nocolorColor) ? other -> true : PartColor.defaultCompatibility(ref)));
	}

}
