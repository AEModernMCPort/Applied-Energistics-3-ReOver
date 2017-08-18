package appeng.core.skyfall.config;

import appeng.api.bootstrap.InitializationComponent;
import appeng.api.config.ConfigCompilable;
import appeng.core.AppEng;
import appeng.core.lib.util.BlockState2String;
import appeng.core.skyfall.AppEngSkyfall;
import appeng.core.skyfall.api.skyobject.Skyobject;
import appeng.core.skyfall.api.skyobject.SkyobjectProvider;
import com.google.common.collect.Lists;
import hall.collin.christopher.math.noise.DefaultFractalNoiseGenerator2D;
import hall.collin.christopher.math.noise.FractalNoiseGenerator2D;
import hall.collin.christopher.math.random.DefaultRandomNumberGenerator;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.WeightedRandom;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class SkyfallConfig implements ConfigCompilable, InitializationComponent.Init {

	private Map<ResourceLocation, Integer> weights = new HashMap<>();

	public SpawnNoise day = new SpawnNoise(0.866, 0.269, 0.909, 0.763, 1.183);
	public SpawnNoise tick = new SpawnNoise(0.091, 0.86, 0.054, 0.065, 5);

	public Meteorite meteorite = new Meteorite();

	public SkyfallConfig(){

	}

	@Override
	public void compile(){

	}

	@Override
	public void init(){
		for(SkyobjectProvider generator : AppEngSkyfall.INSTANCE.getSkyobjectProvidersRegistry()) if(!weights.containsKey(generator.getRegistryName())) weights.put(generator.getRegistryName(), generator.getDefaultWeight());
		meteorite.init();
	}

	@Override
	public void decompile(){
		meteorite.decompile();
	}

	public float getWeight(ResourceLocation gen){
		return weights.get(gen);
	}

	public Supplier<Double> skyobjectFallingSupplierForWorld(World world){
		FractalNoiseGenerator2D day = this.day.getNoise(world.getSeed());
		FractalNoiseGenerator2D tick = this.tick.getNoise(world.getSeed());
		return () -> day.valueAt(0.1, (double) Math.floorDiv(world.getTotalWorldTime(), 24000), 0) * tick.valueAt(0.1, (double) world.getTotalWorldTime(), 0);
	}

	public <S extends Skyobject<S, P>, P extends SkyobjectProvider<S, P>> P getNextWeightedSkyobjectProvider(Random random){
		return WeightedRandom.getRandomItem(random, weights.entrySet().stream().map(WeightedSkyobject::new).collect(Collectors.toList())).asSkyobjectProvider();
	}

	@Override
	public boolean equals(Object o){
		if(this == o) return true;
		if(!(o instanceof SkyfallConfig)) return false;

		SkyfallConfig that = (SkyfallConfig) o;

		if(!weights.equals(that.weights)) return false;
		return meteorite.equals(that.meteorite);
	}

	@Override
	public int hashCode(){
		int result = weights.hashCode();
		result = 31 * result + meteorite.hashCode();
		return result;
	}

	public static class SpawnNoise {

		public double initialScale = 1;
		public double initialMagnitude = 1;
		public double scaleMultiplier = 0.5;
		public double magnitudeMultiplier = 0.5;
		public double exponent = 1;

		public SpawnNoise(){
		}

		public SpawnNoise(double initialScale, double initialMagnitude, double scaleMultiplier, double magnitudeMultiplier, double exponent){
			this.initialScale = initialScale;
			this.initialMagnitude = initialMagnitude;
			this.scaleMultiplier = scaleMultiplier;
			this.magnitudeMultiplier = magnitudeMultiplier;
			this.exponent = exponent;
		}

		public void compile(){
			scaleMultiplier = Math.min(Math.max(scaleMultiplier, 0), 1);
		}

		public FractalNoiseGenerator2D getNoise(long seed){
			return new DefaultFractalNoiseGenerator2D(initialScale, initialMagnitude, scaleMultiplier, magnitudeMultiplier, new DefaultRandomNumberGenerator(seed));
		}

	}

	public static class Meteorite {

		public float minRadius = 5;
		public float maxRadius = 110;
		public double distributionExponent = 1.25;
		//Caching these values to maybe improve performance
		public transient float radiusDelta = minRadius - maxRadius;
		public transient double eToDisExp = Math.exp(distributionExponent);
		public List<ResourceLocation> allowedBlocks = Lists.newArrayList(new ResourceLocation(AppEng.MODID,"skystone"), new ResourceLocation("minecraft:stone"), new ResourceLocation("minecraft:cobblestone"), new ResourceLocation("minecraft:ice"), new ResourceLocation("minecraft:obsidian"));

		public Meteorite(){

		}

		public float fractToRadius(double fract){
			return (float) (minRadius + (Math.exp(fract*distributionExponent)-1) * radiusDelta/(eToDisExp-1));
		}

		private void compile(){
			minRadius = Math.min(minRadius, maxRadius);
			maxRadius = Math.max(minRadius, maxRadius);
			minRadius = Math.max(minRadius, 1);
			maxRadius = Math.min(maxRadius, 110);

			distributionExponent = Math.max(distributionExponent, 0.001);

			radiusDelta = minRadius-maxRadius;
			eToDisExp = Math.exp(distributionExponent);
		}

		public void init(){
			allowedBlocks = allowedBlocks.stream().filter(BlockState2String::isValidBlock).collect(Collectors.toList());
		}

		private void decompile(){

		}

		@Override
		public boolean equals(Object o){
			if(this == o) return true;
			if(!(o instanceof Meteorite)) return false;

			Meteorite meteorite = (Meteorite) o;

			if(Float.compare(meteorite.minRadius, minRadius) != 0) return false;
			if(Float.compare(meteorite.maxRadius, maxRadius) != 0) return false;
			if(Double.compare(meteorite.distributionExponent, distributionExponent) != 0) return false;
			return allowedBlocks.equals(meteorite.allowedBlocks);
		}

		@Override
		public int hashCode(){
			int result;
			long temp;
			result = (minRadius != +0.0f ? Float.floatToIntBits(minRadius) : 0);
			result = 31 * result + (maxRadius != +0.0f ? Float.floatToIntBits(maxRadius) : 0);
			temp = Double.doubleToLongBits(distributionExponent);
			result = 31 * result + (int) (temp ^ (temp >>> 32));
			result = 31 * result + allowedBlocks.hashCode();
			return result;
		}

	}

	public static class WeightedSkyobject extends WeightedRandom.Item {

		public ResourceLocation id;

		public WeightedSkyobject(Map.Entry<ResourceLocation, Integer> entry){
			super(entry.getValue());
			this.id = entry.getKey();
		}

		public <S extends Skyobject<S, P>, P extends SkyobjectProvider<S, P>> P asSkyobjectProvider(){
			return AppEngSkyfall.INSTANCE.<S, P>getSkyobjectProvidersRegistry().getValue(id);
		}

	}

}
