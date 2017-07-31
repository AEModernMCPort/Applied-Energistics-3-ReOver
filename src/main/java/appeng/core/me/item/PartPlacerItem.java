package appeng.core.me.item;

import appeng.core.core.AppEngCore;
import appeng.core.lib.raytrace.RayTraceHelper;
import appeng.core.me.AppEngME;
import appeng.core.me.api.parts.PartPositionRotation;
import appeng.core.me.api.parts.PartRotation;
import appeng.core.me.api.parts.VoxelPosition;
import appeng.core.me.api.parts.container.*;
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
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;

import static appeng.core.me.api.parts.container.GlobalVoxelsInfo.*;

public class PartPlacerItem extends Item {

	protected PartPlacementLogic partPlacementLogic;

	public PartPlacerItem(PartPlacementLogic partPlacementLogic){
		setCreativeTab(CreativeTabs.REDSTONE);
		this.partPlacementLogic = partPlacementLogic;
	}

	public Part getPPart(){
		return AppEngME.INSTANCE.getPartRegistry().getValue(getRegistryName());
	}

	public Part.State getSPart(){
		return getPPart().createNewState();
	}

	public PartPlacementLogic getPartPlacementLogic(){
		return partPlacementLogic;
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ){
		//FIXME Client-server desync. Possibly due to partial ticks between frames on client
		IWorldPartsAccess partsAccess = world.getCapability(PartsHelper.worldPartsAccessCapability, null);
		PartPositionRotation positionRotation = partPlacementLogic.getPlacementPosition(player, RayTraceHelper.rayTrace(player));
		if(partsAccess.canPlace(positionRotation, getSPart().getPart())){
			partsAccess.setPart(positionRotation, getSPart());
			return EnumActionResult.SUCCESS;
		} else return EnumActionResult.FAIL;
	}
}
