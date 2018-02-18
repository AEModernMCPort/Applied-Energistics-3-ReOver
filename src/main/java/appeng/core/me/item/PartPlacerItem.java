package appeng.core.me.item;

import appeng.core.lib.raytrace.RayTraceHelper;
import appeng.core.me.AppEngME;
import appeng.core.me.api.parts.PartPositionRotation;
import appeng.core.me.api.parts.container.PartsAccess;
import appeng.core.me.api.parts.part.Part;
import appeng.core.me.api.parts.placement.PartPlacementLogic;
import appeng.core.me.parts.part.PartsHelper;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class PartPlacerItem<P extends Part<P, S>, S extends Part.State<P, S>> extends Item {

	protected PartPlacementLogic partPlacementLogic;

	public PartPlacerItem(PartPlacementLogic partPlacementLogic){
		setCreativeTab(CreativeTabs.REDSTONE);
		this.partPlacementLogic = partPlacementLogic;
	}

	public P getPPart(){
		return AppEngME.INSTANCE.<P, S>getPartRegistry().getValue(getRegistryName());
	}

	public S getSPart(){
		return getPPart().createNewState();
	}

	public PartPlacementLogic getPartPlacementLogic(){
		return partPlacementLogic;
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ){
		//FIXME Client-server desync. Possibly due to partial ticks between frames on client
		PartsAccess.Mutable partsAccess = world.getCapability(PartsHelper.worldPartsAccessCapability, null);
		PartPositionRotation positionRotation = partPlacementLogic.getPlacementPosition(player, RayTraceHelper.rayTrace(player));
		if(partsAccess.canPlace(positionRotation, getPPart())){
			S state = getSPart();
			partsAccess.setPart(positionRotation, state).ifPresent(uuid -> getPPart().onPlaced(state, partsAccess, world, player, hand));
			return EnumActionResult.SUCCESS;
		} else return EnumActionResult.FAIL;
	}
}
