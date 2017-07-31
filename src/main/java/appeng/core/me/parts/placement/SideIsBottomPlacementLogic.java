package appeng.core.me.parts.placement;

import appeng.core.me.api.parts.part.Part;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

public class SideIsBottomPlacementLogic<P extends Part<P, S>, S extends Part.State<P, S>> extends DefaultPartPlacementLogic<P, S> {

	public SideIsBottomPlacementLogic(P part){
		super(part);
	}

	@Override
	protected Pair<EnumFacing, EnumFacing> getForwardUp(EntityPlayer player, EnumFacing sideHit){
		EnumFacing up = sideHit;
		EnumFacing forward = up == EnumFacing.UP ? player.getHorizontalFacing() : up == EnumFacing.DOWN ? player.getHorizontalFacing().getOpposite() : EnumFacing.UP;
		return new ImmutablePair<>(forward, up);
	}
}
