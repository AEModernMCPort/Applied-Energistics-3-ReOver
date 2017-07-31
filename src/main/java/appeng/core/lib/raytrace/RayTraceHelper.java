package appeng.core.lib.raytrace;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.RayTraceResult;

public class RayTraceHelper {
	
	public static RayTraceResult rayTrace(EntityPlayer player){
		return rayTrace(player, false, false, true);
	}

	public static RayTraceResult rayTrace(EntityPlayer player,  boolean stopOnLiquid, boolean ignoreBlockWithoutBoundingBox, boolean returnLastUncollidableBlock){
		return player.world.rayTraceBlocks(player.getPositionEyes(1), player.getPositionEyes(1).add(player.getLookVec().scale(player.isCreative() ? 5 : 4.5)), stopOnLiquid, ignoreBlockWithoutBoundingBox, returnLastUncollidableBlock);
	}

}
