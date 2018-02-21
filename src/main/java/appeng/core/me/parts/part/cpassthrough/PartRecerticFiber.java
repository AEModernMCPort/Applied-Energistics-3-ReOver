package appeng.core.me.parts.part.cpassthrough;

import appeng.core.AppEng;
import appeng.core.me.api.parts.PartColor;
import net.minecraft.util.ResourceLocation;

import java.util.Map;

public class PartRecerticFiber extends PartFiber.Micro {

	public PartRecerticFiber(Map<ResourceLocation, Comparable<?>> connectionParams){
		super(PartColor.values()[0], connectionParams);
		meshes = new ResourceLocation[]{new ResourceLocation(AppEng.MODID, "me/fiber/recertic/recertic_node.obj"), new ResourceLocation(AppEng.MODID, "me/fiber/recertic/recertic_line.obj")};
	}

}
