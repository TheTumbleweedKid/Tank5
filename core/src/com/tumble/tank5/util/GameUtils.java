package com.tumble.tank5.util;

public class GameUtils {
	
	private GameUtils() {
		// Private constructor.
	}
	
	/**
	 * Returns a pseudo-random <code>double</code> value of a given (maximum)
	 * magnitude.
	 * 
	 * @param magnitude - the maximum magnitude the value should take.
	 * 
	 * @return a pseudo-random number from the interval [-<code>magnitude</code>,
	 *         <code>magnitude</code>).
	 *         
	 * @see Math#random()
	 */
	public static double random(double magnitude) {
		return (Math.random() - 0.5)  * magnitude;
	}
}
