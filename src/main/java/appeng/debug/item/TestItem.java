package appeng.debug.item;

import appeng.core.lib.world.MutableBlockAccessWorldDelegate;
import appeng.core.lib.world.OriginTransformingMutableBlockAccess;
import appeng.core.lib.world.TransformingMutableBlockAccess;
import appeng.core.skyfall.api.generator.MutableBlockAccess;
import hall.collin.christopher.math.noise.SphericalSurfaceFractalNoiseGenerator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.commons.lang3.RandomUtils;
import org.joml.AxisAngle4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.Random;

public class TestItem extends Item {

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World oldworld, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ){
		if(oldworld.isRemote) return EnumActionResult.SUCCESS;
		MutableBlockAccess wworld = new MutableBlockAccessWorldDelegate(oldworld);
		Random random = new Random(/*523*/);
		float radius = (float) Math.sqrt(RandomUtils.nextFloat(25, 100/*2500/*15625*/));
		float radiusX = radius - radius * 0.25f + RandomUtils.nextFloat(0, radius * 0.5f);
		float radiusY = radius - radius * 0.25f + RandomUtils.nextFloat(0, radius * 0.5f);
		float radiusZ = radius - radius * 0.25f + RandomUtils.nextFloat(0, radius * 0.5f);
		float corruption = RandomUtils.nextFloat(0.65f, 0.95f);
		SphericalSurfaceFractalNoiseGenerator noiseGenerator = new SphericalSurfaceFractalNoiseGenerator(random.nextLong());
		TransformingMutableBlockAccess world = new OriginTransformingMutableBlockAccess(wworld, pos.add(0, radius * 1.5, 0));
		{
			for(float x = -radius*1.5f; x < radius*1.5f; x++){
				for(float y = -radius*1.5f; y < radius*1.5f; y++){
					for(float z = -radius*1.5f; z < radius*1.5f; z++){
						BlockPos next = new BlockPos(x, y, z);
						Quaternionf quaternion = new Quaternionf().rotateTo(new Vector3f(1, 0, 0), new Vector3f(x, y, z).normalize());
						AxisAngle4f angle4f = quaternion.get(new AxisAngle4f());
						if(x*x/(radiusX*radiusX) + y*y/(radiusY*radiusY) + z*z/(radiusZ*radiusZ) <= 1f + noiseGenerator.valueAt(0.1f, angle4f.angle * angle4f.x, angle4f.angle * angle4f.y)){
							if(random.nextFloat() < corruption) world.setBlockState(next, Blocks.STONE.getDefaultState());
						}
					}
				}
			}
		}
		return EnumActionResult.SUCCESS;
	}

}