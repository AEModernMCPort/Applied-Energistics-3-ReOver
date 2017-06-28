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
package hall.collin.christopher.math.random;

import java.util.Random;

/**
 * Simple implementation of the CoordinateRandom2D using 
 linear congruence generator as the random number generator.
 * @author Christopher Collin Hall
 */
public class LCGCoordinateRandom2D extends CoordinateRandom2D {
	private final long seed;
	private static final long prime1 = 0x5DEECE66DL, prime2 = 0xBL;
    private static final long mask = (1L << 48) - 1;
	private static final long doubleMask = 0x3FFFFFFFFFFFL;
	private static final double longToDouble = 1.0 /(double)doubleMask;
	/**
	 * Instantiate with the provided seed.
	 * @param seed Seed to use for the random number generator.
	 */
	public LCGCoordinateRandom2D(long seed){
		this.seed = seed;
	}
	/**
	 * Implementations of this method will return a psuedo-random 
	 * double-precision number using the provided coordinates. The 
	 * implementation must ensure that the same instance will always return the 
	 * same value for the same input coordinates.<p/>
	 * @param x coordinate
	 * @param y coordinate
	 * @param z coordinate
	 * @return A psuedorandom number that will always be the same for the same 
	 * coordinate, in the range from 0 to 1 
	 */
	public double valueAt(long x, long y){
		return toDouble(combine(combine(seed,x),y));
	}
	/**
	 * Converts a randomly generated long into a double
	 * @param randomLong a random long
	 * @return a random double in range from 0 to 1
	 */
	private static double toDouble(long randomLong){
		return ((randomLong >>> 7) & doubleMask) * longToDouble;
	}
	/**
	 * generates a psuedorandom number from two input numbers
	 * @param a a number
	 * @param b another number
	 * @return a psuedorandom number
	 */
	private static long combine(long a, long b){
		return next(next(a) ^ next(b));
	}
	/**
	 * Uses a linear-congruence generator to make a random number from the input 
	 * number
	 * @param seed seed number
	 * @return a psuedorandom number
	 */
	private static long next(long seed){
		long x2 = ((seed * prime1) + prime2) & mask;
		long x3 = ((x2 * prime1) + prime2) & mask;
		return ((x2 & 0xFFFFFFFF) << 32) | (x3 & 0xFFFFFFFF);
	}
	
	@Deprecated // for testing and demonstration only
	public static void main(String[] a){
		int size = 400;
		java.awt.image.BufferedImage bimg = new java.awt.image.BufferedImage(size,size,java.awt.image.BufferedImage.TYPE_INT_ARGB);
		LCGCoordinateRandom2D prng = new LCGCoordinateRandom2D(System.currentTimeMillis());
		for(int x = 0; x < size; x++){
			for(int y = 0; y < size; y++){
				bimg.setRGB(x, y, java.awt.Color.HSBtoRGB(0, 0, (float)prng.valueAt(x, y)));
			}
		}
		javax.swing.JOptionPane.showMessageDialog(null, new javax.swing.JLabel(new javax.swing.ImageIcon(bimg)));
		bimg = new java.awt.image.BufferedImage(size,size,java.awt.image.BufferedImage.TYPE_INT_ARGB);
		for(int x = 0; x < size; x++){
			for(int y = 0; y < size; y++){
				bimg.setRGB(x, y, java.awt.Color.HSBtoRGB(0, 0, (float)prng.valueAt( y*x, x)));
			}
		}
		javax.swing.JOptionPane.showMessageDialog(null, new javax.swing.JLabel(new javax.swing.ImageIcon(bimg)));
	}
	
	
}
