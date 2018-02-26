package appeng.core.me.parts.part.connected;

import appeng.core.AppEng;
import appeng.core.me.api.parts.PartColor;
import net.minecraft.util.ResourceLocation;

public class PartRecerticFiber extends PartFiber.Micro {

	public PartRecerticFiber(){
		super(PartColor.NOCOLOR);
		meshes = new ResourceLocation[]{new ResourceLocation(AppEng.MODID, "me/fiber/recertic/recertic_node.obj"), new ResourceLocation(AppEng.MODID, "me/fiber/recertic/recertic_line.obj")};
	}

}
