/*
 * The MIT License
 *
 * Copyright 2016 Christopher Collin Hall 
 * <a href="mailto:explosivegnome@yahoo.com">explosivegnome@yahoo.com</a>.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package hall.collin.christopher.math.noise;

import hall.collin.christopher.math.AbstractNumberGenerator;
import hall.collin.christopher.math.random.CoordinateRandom3D;
import hall.collin.christopher.math.random.DefaultRandomNumberGenerator;
import hall.collin.christopher.math.random.LCGCoordinateRandom3D;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Class for 3D noise generation (perlin noise) that uses 
 * multiple "octaves" of noise to automatically provide arbitrary granularity. 
 * @author Christopher Collin Hall
 */
public class DefaultFractalNoiseGenerator3D extends FractalNoiseGenerator3D{
	private final AbstractNumberGenerator seeder;
	private final ArrayList<CoordinateNoiseGenerator3D> octaves = new ArrayList<>(8);
	private final ArrayList<Double> octavePrecisions = new ArrayList<>(8);
	private final ArrayList<Double> octaveMagnitudes = new ArrayList<>(8);
	private int lastLayerIndex = -1;
	
	private final double octaveScaleMultiplier;
	private final double octaveMagnitudeMultiplier;
	
	private final Lock layerGenerationLock = new ReentrantLock();
	
	/**
	 * Constructs a fractal noise generator using standard psuedo-random number 
	 * generators.
	 * @param initialScale The initial grid spacing of the lowest frequency noise octave
	 * @param initialMagnitude The range of values for the lowest frequency noise octave
	 * @param octaveScaleMultiplier How much to change the grid size for each successive 
	 * layer of noise (must be between 0 and 1). Typically 0.5
	 * @param octaveMagnitudeMultiplier How to change the range of values when 
	 * generating noise octaves. Typically 0.5
	 * @param seeder random number generator for seeding
	 */
	public DefaultFractalNoiseGenerator3D(double initialScale, double initialMagnitude, double octaveScaleMultiplier, double octaveMagnitudeMultiplier, AbstractNumberGenerator seeder){
		if(octaveScaleMultiplier >= 1 || octaveScaleMultiplier < 0) throw new IllegalArgumentException("Octave scale multiplier must be in range of (0,1)");
		this.seeder = seeder;
		this.octaveScaleMultiplier = octaveScaleMultiplier;
		this.octaveMagnitudeMultiplier = octaveMagnitudeMultiplier;
		addNoiseLayer(initialScale,initialMagnitude);
	}
	/**
	 * Constructs a fractal noise generator using standard psuedo-random number 
	 * generators using typical perlin noise starting values.
	 * @param seed seed for random number generators
	 */
	public DefaultFractalNoiseGenerator3D(long seed){
		this(1.0,1.0,0.5,0.5,new DefaultRandomNumberGenerator(seed));
	}
	
	/**
	 * This method will generate a Perlin Noise type of value 
	 * interpolated at the provided coordinate.<p/>
	 * Thread Safe!
	 * @param precision Spacial resolution (finest noise "octave" will have a 
	 * grid size smaller than this value)
	 * @param x X coordinate
	 * @param y Y coordinate
	 * @param z Z coordinate
	 * @return A value interpolated from random control points, such that the 
	 * same coordinate always results in the same output value and a coordinate 
	 * very close to another will have a similar, but not necessarily the same, 
	 * value as the other coordinate.
	 * @throws ArrayIndexOutOfBoundsException May be thrown if the provided 
	 * coordinates exceed the allowable range of the underlying algorithm.
	 */
	@Override
	public double valueAt(double precision, double x, double y, double z) throws ArrayIndexOutOfBoundsException{
		if(octavePrecisions.get(lastLayerIndex) > precision){
			layerGenerationLock.lock();
			try{
				while(octavePrecisions.get(lastLayerIndex) > precision){
					// will need to add new layers
					addNoiseLayer(
							octavePrecisions.get(lastLayerIndex) * octaveScaleMultiplier,
							octaveMagnitudes.get(lastLayerIndex) * octaveMagnitudeMultiplier
					);
				}
			}finally{
				layerGenerationLock.unlock();
			}
		}
		double sum = 0;
		for(int i = 0; i <= lastLayerIndex; i++){
			sum += octaves.get(i).getValue(x, y, z) * octaveMagnitudes.get(i);
			if(octavePrecisions.get(i) <= precision) break;
		}
		return sum;
	}

	private synchronized void addNoiseLayer(double scale, double magnitude) {
		CoordinateRandom3D prng = new LCGCoordinateRandom3D(seeder.nextLong());
		CoordinateNoiseGenerator3D layer = new DefaultCoordinateNoiseGenerator3D(prng,magnitude);
		octaves.add(layer);
		octavePrecisions.add(scale);
		octaveMagnitudes.add(magnitude);
		
		lastLayerIndex++;
	}
	
	@Deprecated // for testing and demonstration only
	public static void main(String[] a){
		int size = 400;
		java.awt.image.BufferedImage bimg = new java.awt.image.BufferedImage(size,size,java.awt.image.BufferedImage.TYPE_INT_ARGB);
		FractalNoiseGenerator3D prng = new DefaultFractalNoiseGenerator3D(System.currentTimeMillis());
		final double p = 0.2;
		final double s = 0.025;
		for(int x = 0; x < size; x++){
			for(int y = 0; y < size; y++){
				float v = (float)prng.valueAt(p,(x-100)*s, (y-100)*s, 0) + 1;
				v *= 0.5f;
				if(v < 0) v = 0f;//0.5f;
				if(v > 1) v = 1f;//0.5f;
				bimg.setRGB(x, y, java.awt.Color.HSBtoRGB(0, 0f, v));
			}
		}
		javax.swing.JOptionPane.showMessageDialog(null, new javax.swing.JLabel(new javax.swing.ImageIcon(bimg)));
	}
}
