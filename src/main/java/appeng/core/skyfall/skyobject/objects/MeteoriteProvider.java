package appeng.core.skyfall.skyobject.objects;

import appeng.core.lib.world.ExpandleMutableBlockAccess;
import appeng.core.skyfall.AppEngSkyfall;
import appeng.core.skyfall.block.CertusInfusedBlock;
import appeng.core.skyfall.config.SkyfallConfig;
import code.elix_x.excore.utils.world.MutableBlockAccess;
import hall.collin.christopher.math.noise.DefaultFractalNoiseGenerator3D;
import hall.collin.christopher.math.noise.SphericalSurfaceFractalNoiseGenerator;
import hall.collin.christopher.math.random.DefaultRandomNumberGenerator;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import org.apache.commons.lang3.RandomUtils;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class MeteoriteProvider extends SkyobjectFallingProvider<Meteorite, MeteoriteProvider> {

	public MeteoriteProvider(int defaultWeight){
		super(Meteorite::new, defaultWeight);
	}

	@Override
	public Meteorite generate(long seed){
		Meteorite meteorite = get();
		MutableBlockAccess world = meteorite.world;

		SkyfallConfig.Meteorite config = AppEngSkyfall.INSTANCE.config.meteorite;
		Random random = new Random(seed);
		float radius = config.fractToRadius(random.nextDouble());
		AppEngSkyfall.logger.info("Meteorite radius - " + radius);
		List<IBlockState> allowed = config.allowedBlocks.stream().map(Block.REGISTRY::getObject).map(Block::getDefaultState).collect(Collectors.toList());
		Collections.shuffle(allowed, random);
		int count = RandomUtils.nextInt(Math.min(allowed.size(), 2), allowed.size() + 1);
		AppEngSkyfall.logger.info("Meteorite layers - " + count);
		for(int i = 0; i < count; i++){
			IBlockState block = allowed.get(i);

			AppEngSkyfall.logger.info("Layer - " + block.getBlock());
			long ltime = System.currentTimeMillis();

			Random localRandom = new Random(random.nextLong());
			float localRadius = RandomUtils.nextFloat(radius * 0.75f, radius * 1.25f);
			float radiusX = localRadius - localRadius * 0.25f + RandomUtils.nextFloat(0, localRadius * 0.5f);
			float radiusY = localRadius - localRadius * 0.25f + RandomUtils.nextFloat(0, localRadius * 0.5f);
			float radiusZ = localRadius - localRadius * 0.25f + RandomUtils.nextFloat(0, localRadius * 0.5f);
			float corruption = RandomUtils.nextFloat(0.65f, 0.95f);
			SphericalSurfaceFractalNoiseGenerator noiseGenerator = new SphericalSurfaceFractalNoiseGenerator(localRandom.nextLong());
			{
				for(float x = -localRadius * 1.5f; x < localRadius * 1.5f; x++){
					for(float y = -localRadius * 1.5f; y < localRadius * 1.5f; y++){
						for(float z = -localRadius * 1.5f; z < localRadius * 1.5f; z++){
							BlockPos next = new BlockPos(x, y, z);
							if(x * x / (radiusX * radiusX) + y * y / (radiusY * radiusY) + z * z / (radiusZ * radiusZ) <= 1f + noiseGenerator.valueAt(1f / (2f * localRadius) , Math.atan(y/x), Math.acos(z/Math.sqrt(x*x+y*y+z*z))))
								if(localRandom.nextFloat() < corruption) world.setBlockState(next, block);
						}
					}
				}
			}

			AppEngSkyfall.logger.info("Layer took " + (System.currentTimeMillis() - ltime) + " ms");
		}

		long itime = System.currentTimeMillis();

		Random localRandom = new Random(random.nextLong());
		DefaultFractalNoiseGenerator3D infusionNoise = new DefaultFractalNoiseGenerator3D(500, 0.3, 0.9, 1, new DefaultRandomNumberGenerator(localRandom.nextLong()));
		final double p = 0.02;
		final double s = 0.05;
		final double threshold = 0.85;
		float corruption = RandomUtils.nextFloat(0.65f, 0.95f);
		for(float x = -radius * 1.5f; x < radius * 1.5f; x++){
			for(float y = -radius * 1.5f; y < radius * 1.5f; y++){
				for(float z = -radius * 1.5f; z < radius * 1.5f; z++){
					BlockPos next = new BlockPos(x, y, z);
					if(infusionNoise.valueAt(p, x * s, y * s, z * s) >= threshold)
						if(localRandom.nextFloat() < corruption)
							CertusInfusedBlock.getInfused(world.getBlockState(next).getBlock()).ifPresent(certusInfusedBlock -> world.setBlockState(next, certusInfusedBlock.getDefaultState()));
				}
			}
		}

		AppEngSkyfall.logger.info("Infusion took " + (System.currentTimeMillis() - itime) + " ms");

		return meteorite;
	}

}
