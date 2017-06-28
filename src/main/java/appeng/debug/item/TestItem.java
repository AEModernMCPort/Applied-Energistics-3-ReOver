package appeng.debug.item;

import appeng.core.lib.world.MutableBlockAccessWorldDelegate;
import appeng.core.lib.world.OriginTransformingMutableBlockAccess;
import appeng.core.lib.world.TransformedMutableBlockAccessM4f;
import appeng.core.lib.world.TransformingMutableBlockAccess;
import appeng.core.skyfall.api.generator.MutableBlockAccess;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.joml.Matrix4f;

public class TestItem extends Item {

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World oldworld, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ){
		MutableBlockAccess wworld = new MutableBlockAccessWorldDelegate(oldworld);
		TransformingMutableBlockAccess world = new OriginTransformingMutableBlockAccess(wworld, pos);
		world = new TransformedMutableBlockAccessM4f(world, new Matrix4f().translate(-2, -1, 0).scale(2, 3, 1));
		for(int x = 0; x < 5; x++){
			for(int y = 0; y < 3; y++){
				for(int z = 0; z < 3; z++){
					world.setBlockState(new BlockPos(x, y, z), Blocks.STONE.getDefaultState());
				}
			}
		}
		return EnumActionResult.SUCCESS;
	}

}