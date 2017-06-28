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

import hall.collin.christopher.math.CubicInterpolator;
import hall.collin.christopher.math.random.CoordinateRandom3D;


/**
 * Standard class for 3D noise generators (such as 3D perlin noise). Extending 
 * classes will use some method to generate an interpolated random value for the 
 * given coordinate.
 * @author Christopher Collin Hall
 */
public class DefaultCoordinateNoiseGenerator3D extends CoordinateNoiseGenerator3D{
	/**
	 * Implementation of an Abstract3CoordinatePRNG to generate random numbers
	 */
	protected final CoordinateRandom3D prng;
	/** distance between noise points */
	private final double resolution;
	private final double inverseResolution;
	/**
	 * Creates a general use, one frequency perlin noise type of interpolated 
	 * random number generator for 3D noise.
	 * @param coordprng Implementation of an Abstract3CoordinatePRNG to generate 
	 * random numbers from coordinates
	 * @param gridSpacing The noise resolution (distance between the noise control points)
	 */
	public DefaultCoordinateNoiseGenerator3D(CoordinateRandom3D coordprng, double gridSpacing){
		this.prng = coordprng;
		resolution = gridSpacing;
		inverseResolution = 1.0/resolution;
	}
	
	/**
	 * This method will generate a Perlin Noise type of value 
	 * interpolated at the provided coordinate.
	 * @param x X coordinate
	 * @param y Y coordinate
	 * @param z Z coordinate
	 * @return A value interpolated from random control points, such that the 
	 * same coordinate always results in the same output value and a coordinate 
	 * very close to another will have a similar, but not necessarily the same, 
	 * value as the other coordinate. Ranges from -1 to 1
	 * @throws ArrayIndexOutOfBoundsException May be thrown if the provided 
	 * coordinates exceed the allowable range of the underlying algorithm.
	 */
	@Override
	public double getValue(double x, double y, double z) throws ArrayIndexOutOfBoundsException{
		x *= inverseResolution;
		y *= inverseResolution;
		z *= inverseResolution;
		double[][][] grid = new double[4][4][4];// [x][y][z]
		long xn1 = floor(x);
		long yn1 = floor(y);
		long zn1 = floor(z);
		
		for (int dz = -1; dz <= 2; dz++) {
			for (int dy = -1; dy <= 2; dy++) {
				for (int dx = -1; dx <= 2; dx++) {
					grid[dx + 1][dy + 1][dz + 1] = (2*prng.valueAt(xn1 + dx, yn1 + dy, zn1 + dz)-1);
				}
			}
		}
		return CubicInterpolator.interpolate3d(x,y,z,grid);
	}

	/**
	 * Faster implementation than Math.floor(x). 
	 * @param x
	 * @return The greatest integer value less than x. 
	 */
	private static long floor(double x) {
		if(x < 0){
			return (long)x - 1;
		} else {
			return (long)x;
		}
	}
	
	
	
	
}
