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
import hall.collin.christopher.math.random.CoordinateRandom2D;


/**
 * Specialized class for 2D noise generators (such as 2D perlin noise) that wraps
 * the dimensions (common for making tileable textures).
 * @author Christopher Collin Hall
 */
public class WrappedCoordinateNoiseGenerator2D extends CoordinateNoiseGenerator2D{
	/**
	 * Implementation of an Abstract3CoordinatePRNG to generate random numbers
	 */
	protected final CoordinateRandom2D prng;
	
	private final boolean wrapX;
	private final boolean wrapY;
	private final int width;
	private final int height;
	
	/**
	 * Creates a general use, one frequency perlin noise type of interpolated 
	 * random number generator for 3D noise.
	 * @param coordprng Implementation of an Abstract3CoordinatePRNG to generate 
	 * random numbers from coordinates
	 * @param width The wrapped size of the noise space in the X dimension, in 
	 * number of grid units. A 0 or negative number disables wrapping.
	 * @param height The wrapped size of the noise space in the Y dimension, in 
	 * number of grid units. A 0 or negative number disables wrapping.
	 */
	public WrappedCoordinateNoiseGenerator2D(CoordinateRandom2D coordprng, int width, int height){
		this.prng = coordprng;
		this.width = width;
		this.height = height;
		this.wrapX = width > 0;
		this.wrapY = height > 0;
	}
	
	/**
	 * This method will generate a Perlin Noise type of value 
	 * interpolated at the provided coordinate, wrapping the axese as indicated 
	 * in the constructor.
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
	public double getValue(double x, double y) throws ArrayIndexOutOfBoundsException{
		double[][] grid = new double[4][4];// [x][y]
		long xn1 = floor(x);
		long yn1 = floor(y);
		
		for (int dy = -1; dy <= 2; dy++) {
			for (int dx = -1; dx <= 2; dx++) {
				grid[dx + 1][dy + 1] = (2*prng.valueAt(
						wrapIndex(xn1 + dx,width,wrapX), 
						wrapIndex(yn1 + dy,height,wrapY)
				)-1);
			}
		}
		return CubicInterpolator.interpolate2d(x,y,grid);
	}
	
	private static int wrapIndex(long raw, int wrap, boolean doWrap){
		if(doWrap) return ((int)raw) % wrap;
		return (int)raw;
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
