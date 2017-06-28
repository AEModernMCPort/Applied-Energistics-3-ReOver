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

/**
 * Abstract class for Psuedo-Random Number Generators (PRNGs) that generate a 
 * value based on a 3-dimensional coordinate. Used in 3D Perlin Noise 
 * generators.
 * @author Christopher Collin Hall
 */
public abstract class CoordinateRandom2D {
	/**
	 * Implementations of this method will return a psuedo-random 
	 * double-precision number using the provided coordinates. The 
	 * implementation must ensure that the same instance will always return the 
	 * same value for the same input coordinates.<p/>
	 * @param x coordinate
	 * @param y coordinate
	 * @return A psuedorandom number that will always be the same for the same 
	 * coordinate, in the range from 0 to 1 
	 */
	public abstract double valueAt(long x, long y);
	
}
