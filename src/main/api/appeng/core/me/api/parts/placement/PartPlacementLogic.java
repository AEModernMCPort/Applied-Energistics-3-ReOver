package appeng.core.me.api.parts.placement;

import appeng.core.me.api.parts.PartPositionRotation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.RayTraceResult;

public interface PartPlacementLogic {

	PartPositionRotation getPlacementPosition(EntityPlayer player, RayTraceResult rayTrace);

}
