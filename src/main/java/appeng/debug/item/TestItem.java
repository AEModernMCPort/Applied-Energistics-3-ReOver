package appeng.debug.item;

import appeng.core.lib.world.MutableBlockAccessWorldDelegate;
import appeng.core.lib.world.OriginTransformingMutableBlockAccess;
import appeng.core.skyfall.api.generator.MutableBlockAccess;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TestItem extends Item {

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World oldworld, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ){
		MutableBlockAccess world = new MutableBlockAccessWorldDelegate(oldworld);
		world = new OriginTransformingMutableBlockAccess(world, pos);
		for(int x = 0; x < 3; x++){
			for(int y = 0; y < 3; y++){
				for(int z = 0; z < 3; z++){
					world.setBlockState(new BlockPos(x, y, z), Blocks.STONE.getDefaultState());
				}
			}
		}
		System.out.println(pos);
		System.out.println(new OriginTransformingMutableBlockAccess(world, pos).transform(pos));
		return EnumActionResult.SUCCESS;
	}

}