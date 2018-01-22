package com.iblue.ea.utils;

import com.iblue.utils.Log;
import com.iblue.utils.MersenneTwister;

/**
 * 
 * @author Andrés Camero Unzueta
 *
 */
public class RandomGenerator {

	private static final int seeds[] = { 2902, 5235, 357, 6058, 4846, 8280, 1295, 181, 3264, 7285, 8806, 2344, 9203,
			6806, 1511, 2172, 843, 4697, 3348, 1866, 5800, 4094, 2751, 64, 7181, 9167, 5579, 9461, 3393, 4602, 1796,
			8174, 1691, 8854, 5902, 4864, 5488, 1129, 1111, 7597, 5406, 2134, 7280, 6465, 4084, 8564, 2593, 9954, 4731,
			1347, 8984, 5057, 3429, 7635, 1323, 1146, 5192, 6547, 343, 7584, 3765, 8660, 9318, 5098, 5185, 9253, 4495,
			892, 5080, 5297, 9275, 7515, 9729, 6200, 2138, 5480, 860, 8295, 8327, 9629, 4212, 3087, 5276, 9250, 1835,
			9241, 1790, 1947, 8146, 8328, 973, 1255, 9733, 4314, 6912, 8007, 8911, 6802, 5102, 5451, 1026, 8029, 6628,
			8121, 5509, 3603, 6094, 4447, 683, 6996, 3304, 3130, 2314, 7788, 8689, 3253, 5920, 3660, 2489, 8153, 2822,
			6132, 7684, 3032, 9949, 59, 6669, 6334 };

	private static int seedPosition = 0;
	private static MersenneTwister generator = new MersenneTwister(seeds[seedPosition]);

	public static void nextSeed() {
		seedPosition = (seedPosition + 1) % seeds.length;
		Log.info("Move to next seed (position=" + seedPosition + ")");
		generator = new MersenneTwister(seeds[seedPosition]);
	}

	public static void setSeed(int seedPos) {
		seedPosition = Math.abs(seedPos) % seeds.length;
		Log.info("Set seed (position=" + seedPosition + ")");
		generator = new MersenneTwister(seeds[seedPosition]);
	}

	public static int getActualSeed() {
		return seeds[seedPosition];
	}

	public static int getActualSeedPosition() {
		return seedPosition;
	}

	public static int getNumberOfSeeds() {
		return seeds.length;
	}

	/**
	 * Random double in the interval 0..1
	 * 
	 * @return
	 */
	public static double nextDouble() {
		return generator.nextDouble();
	}

	/**
	 * Random double in the interval 0..1
	 * 
	 * @return
	 */
	public static double nextDouble(boolean includeZero, boolean includeOne) {
		return generator.nextDouble(includeZero, includeOne);
	}

	public static int nextInt() {
		return generator.nextInt();
	}

	/**
	 * Returns the next pseudorandom, Gaussian ("normally") distributed double
	 * value with mean 0.0 and standard deviation 1.0 from this random number
	 * generator's sequence.
	 * 
	 * @return
	 */
	public static double nextGaussian() {
		return generator.nextGaussian();
	}

	public static double nextGaussian(double standardDeviation, double mean) {
		return generator.nextGaussian() * standardDeviation + mean;
	}

}
