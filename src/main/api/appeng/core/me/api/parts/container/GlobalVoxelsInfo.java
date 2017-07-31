package appeng.core.me.api.parts.container;

import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public final class GlobalVoxelsInfo {

	public static final int VOXELSPERBLOCKAXISI = 16;
	public static final float VOXELSPERBLOCKAXISF = VOXELSPERBLOCKAXISI;
	public static final double VOXELSPERBLOCKAXISD = VOXELSPERBLOCKAXISI;
	public static final int VOXELSPERBLOCKAXISIBITCOUNT = 4;

	public static final float VOXELSIZEF = 1 / VOXELSPERBLOCKAXISF;
	public static final double VOXELSIZED = 1 / VOXELSPERBLOCKAXISD;

	public static final float VOXELSIZEF2 = VOXELSIZEF / 2;
	public static final double VOXELSIZED2 = VOXELSIZED / 2;

	public static final float VOXELSIZEF4 = VOXELSIZEF2 / 2;
	public static final double VOXELSIZED4 = VOXELSIZED2 / 2;

	public static final AxisAlignedBB VOXELBB = new AxisAlignedBB(0, 0, 0, VOXELSIZED, VOXELSIZED, VOXELSIZED);

	private GlobalVoxelsInfo(){}

	public static Iterable<BlockPos> allVoxelsInABlock(){
		return BlockPos.getAllInBox(BlockPos.ORIGIN, new BlockPos(VOXELSPERBLOCKAXISI - 1, VOXELSPERBLOCKAXISI - 1, VOXELSPERBLOCKAXISI - 1));
	}

	public static Stream<BlockPos> allVoxelsInABlockStream(){
		return StreamSupport.stream(allVoxelsInABlock().spliterator(), false);
	}

	public static AxisAlignedBB getVoxelBB(BlockPos voxel){
		return VOXELBB.offset(voxel.getX() / VOXELSPERBLOCKAXISD, voxel.getY() / VOXELSPERBLOCKAXISD, voxel.getZ() / VOXELSPERBLOCKAXISD);
	}
}
