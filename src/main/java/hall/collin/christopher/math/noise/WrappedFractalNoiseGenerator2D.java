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

import hall.collin.christopher.math.random.CoordinateRandom2D;
import hall.collin.christopher.math.random.LCGCoordinateRandom2D;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Class for 2D noise generation (perlin noise) that uses 
 * multiple "octaves" of noise to automatically provide arbitrary granularity and 
 * wraps the X and/or Y axese. 
 * @author Christopher Collin Hall
 */
public class WrappedFractalNoiseGenerator2D extends FractalNoiseGenerator2D{
	private final Random seeder;
	private final ArrayList<CoordinateNoiseGenerator2D> octaves = new ArrayList<>(8);
	private final ArrayList<Double> octavePrecisions = new ArrayList<>(8);
	private final ArrayList<Double> octaveMagnitudes = new ArrayList<>(8);
	private int lastLayerIndex = -1;
	private int lastLayerWidth = 1;
	private int lastLayerHeight = 1;
	
	private final double octaveScaleMultiplier = 0.5; // always double frquency for each new octave
	private final double octaveMagnitudeMultiplier;
	
	private final Lock layerGenerationLock = new ReentrantLock();
	
	/**
	 * Constructs a fractal noise generator using standard psuedo-random number 
	 * generators.
	 * @param width The wrapped size of the noise space in the X dimension, in 
	 * number of grid units. A 0 or negative number disables wrapping.
	 * @param height The wrapped size of the noise space in the Y dimension, in 
	 * number of grid units. A 0 or negative number disables wrapping.
	 * @param initialMagnitude The range of values for the lowest frequency noise octave
	 * @param octaveMagnitudeMultiplier How to change the range of values when 
	 * generating noise octaves. Typically 0.5
	 * @param seed The seed for the random number generator
	 */
	public WrappedFractalNoiseGenerator2D(int width, int height, double initialMagnitude, double octaveMagnitudeMultiplier, long seed){
		this.seeder = new Random(seed);
		this.octaveMagnitudeMultiplier = octaveMagnitudeMultiplier;
		addNoiseLayer(width,height,1.0,initialMagnitude);
	}
	
	/**
	 * This method will generate a Perlin Noise type of value 
	 * interpolated at the provided coordinate.<p/>
	 * Thread Safe!
	 * @param precision Spacial resolution (finest noise "octave" will have a 
	 * grid size smaller than this value)
	 * @param x X coordinate
	 * @param y Y coordinate
	 * @return A value interpolated from random control points, such that the 
	 * same coordinate always results in the same output value and a coordinate 
	 * very close to another will have a similar, but not necessarily the same, 
	 * value as the other coordinate.
	 * @throws ArrayIndexOutOfBoundsException May be thrown if the provided 
	 * coordinates exceed the allowable range of the underlying algorithm.
	 */
	@Override
	public double valueAt(double precision, double x, double y) throws ArrayIndexOutOfBoundsException{
		if(octavePrecisions.get(lastLayerIndex) > precision){
			layerGenerationLock.lock();
			try{
				while(octavePrecisions.get(lastLayerIndex) > precision){
					// will need to add new layers
					addNoiseLayer(
							lastLayerWidth * 2,
							lastLayerHeight * 2,
							octavePrecisions.get(lastLayerIndex) * octaveScaleMultiplier,
							octaveMagnitudes.get(lastLayerIndex) * octaveMagnitudeMultiplier
					);
				}
			}finally{
				layerGenerationLock.unlock();
			}
		}
		double sum = 0;
		double dimensionScaler = 1.0;
		for(int i = 0; i <= lastLayerIndex; i++){
			sum += octaves.get(i).getValue(x*dimensionScaler, y*dimensionScaler) * octaveMagnitudes.get(i);
			if(octavePrecisions.get(i) <= precision) break;
			dimensionScaler *= 2;
		}
		return sum;
	}

	private synchronized void addNoiseLayer(int width, int height, double precision, double magnitude) {
		CoordinateRandom2D prng = new LCGCoordinateRandom2D(seeder.nextLong());
		CoordinateNoiseGenerator2D layer = new WrappedCoordinateNoiseGenerator2D(prng,width,height);
		octaves.add(layer);
		octavePrecisions.add(precision);
		octaveMagnitudes.add(magnitude);
		lastLayerWidth = width;
		lastLayerHeight = height;
		
		lastLayerIndex++;
	}
	
	@Deprecated // for testing and demonstration only
	public static void main(String[] a){
		
		int imgSize = 408;
		int tilesPerImageWidth = 4;
		int noiseSize = 3;
		double unitsPerPixel = (double)tilesPerImageWidth*(double)noiseSize/(double)imgSize;
		
		java.awt.image.BufferedImage bimg 
				= new java.awt.image.BufferedImage(imgSize,imgSize,
						java.awt.image.BufferedImage.TYPE_INT_ARGB
				);
		FractalNoiseGenerator2D prng 
				= new WrappedFractalNoiseGenerator2D(
						noiseSize, // width
						noiseSize, // height
						0.9, // magnitude
						0.5, // magnitude scaling factor
						System.currentTimeMillis() // random number seed
				);
		
		for(int x = 0; x < imgSize; x++){
			for(int y = 0; y < imgSize; y++){
				float v = (float)prng.valueAt(
							unitsPerPixel,
							x*unitsPerPixel, 
							y*unitsPerPixel
				);
				v += 1;
				v *= 0.5f;
				if(v < 0) v = 0f;//0.5f;
				if(v > 1) v = 1f;//0.5f;
				bimg.setRGB(x, y, java.awt.Color.HSBtoRGB(0f, 0f, v));
			}
		}
		javax.swing.JOptionPane.showMessageDialog(
				null, 
				new javax.swing.JLabel(new javax.swing.ImageIcon(bimg))
		);
	}
}
