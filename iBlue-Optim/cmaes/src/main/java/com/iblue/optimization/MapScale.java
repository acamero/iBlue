package com.iblue.optimization;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

import com.iblue.utils.Log;

public class MapScale {

	public class MapPartition {
		public List<BigDecimal> latPartitions;
		public List<BigDecimal> lonPartitions;

		public MapPartition(List<BigDecimal> latPartitions, List<BigDecimal> lonPartitions) {
			this.latPartitions = latPartitions;
			this.lonPartitions = lonPartitions;
		}

		public String key() {
			String key = latPartitions.stream().map(Object::toString).collect(Collectors.joining(", "));
			key = key + ", " + lonPartitions.stream().map(Object::toString).collect(Collectors.joining(", "));
			return key;
		}
	}

	private BigDecimal latRange;
	private BigDecimal lonRange;

	public MapScale(BigDecimal latRange, BigDecimal lonRange) {
		this.latRange = latRange;
		this.lonRange = lonRange;
	}

	public MapPartition scale(double[] latDivision, double[] lonDivision) {
		Log.debug("Scaling lat=" + Arrays.toString(latDivision) + " lon=" + Arrays.toString(lonDivision));
		List<BigDecimal> latPartitions = new ArrayList<BigDecimal>();
		List<BigDecimal> lonPartitions = new ArrayList<BigDecimal>();

		BigDecimal latSum = new BigDecimal(DoubleStream.of(latDivision).sum(), CMAESTileOptimization.MATH_CONTEXT);
		BigDecimal lonSum = new BigDecimal(DoubleStream.of(lonDivision).sum(), CMAESTileOptimization.MATH_CONTEXT);

		if (latSum.compareTo(BigDecimal.ZERO) == 0) {
			latPartitions.add(new BigDecimal(0, CMAESTileOptimization.MATH_CONTEXT));
		} else {
			// latitude scaling
			for (int i = 0; i < latDivision.length; i++) {
				BigDecimal temp = latRange.multiply(new BigDecimal(latDivision[i], CMAESTileOptimization.MATH_CONTEXT))
						.divide(latSum, CMAESTileOptimization.MATH_CONTEXT);				
				if (temp.compareTo(BigDecimal.ZERO) != 0) {
					latPartitions.add(temp);
				}
			}
		}

		// longitude scaling
		if (lonSum.compareTo(BigDecimal.ZERO) == 0) {
			lonPartitions.add(new BigDecimal(0, CMAESTileOptimization.MATH_CONTEXT));
		} else {
			for (int i = 0; i < lonDivision.length; i++) {
				BigDecimal temp = lonRange.multiply(new BigDecimal(lonDivision[i], CMAESTileOptimization.MATH_CONTEXT))
						.divide(lonSum, CMAESTileOptimization.MATH_CONTEXT);
				if (temp.compareTo(BigDecimal.ZERO) != 0) {
					lonPartitions.add(temp);
				}
			}
		}

		return new MapPartition(latPartitions, lonPartitions);
	}
}
