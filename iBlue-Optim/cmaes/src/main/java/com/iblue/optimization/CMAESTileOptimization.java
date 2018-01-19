package com.iblue.optimization;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.optim.ConvergenceChecker;
import org.apache.commons.math3.optim.InitialGuess;
import org.apache.commons.math3.optim.MaxEval;
import org.apache.commons.math3.optim.OptimizationData;
import org.apache.commons.math3.optim.PointValuePair;
import org.apache.commons.math3.optim.SimpleBounds;
import org.apache.commons.math3.optim.SimpleValueChecker;
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType;
import org.apache.commons.math3.optim.nonlinear.scalar.noderiv.CMAESOptimizer;

import com.iblue.model.GeoStreetInterface;
import com.iblue.utils.Log;
import com.iblue.utils.Pair;

public class CMAESTileOptimization extends TileOptimization {

	private CMAESOptimizer optimizer;
	private OptimizationData sigma;
	private OptimizationData popSize;
	private SimpleBounds bounds;
	//private ObjectiveFunction objectiveFunction;
	//private MapScale mapScale;
	//private int maxPartitions;
	//private List<Pair<GeoStreetInterface, GeoStreetInterface>> originDestinations;
	//private Map<String, Double> cache;
	private double relativeThreshold = 1e-8;
	private double absoluteThreshold = 1e-10;
	private GoalType goal = GoalType.MINIMIZE;
	private int maxIterations = 1000;
	private double stopFitness = 0.0d;
	private boolean isActiveCMA = true;
	private int diagonalOnly = 1;
	private int checkFeasableCount = 0;
	//private RandomGenerator random;
	private boolean generateStatistics = true;
	// private List<String> history = new ArrayList<String>();

	public CMAESTileOptimization(int seed, int maxPartitions, MapScale mapScale,
			List<Pair<GeoStreetInterface, GeoStreetInterface>> od, Map<String, Double> cache) {
		super(seed, maxPartitions, mapScale, od, cache);
		//random = new MersenneTwister(seed);
		//this.maxPartitions = maxPartitions;
		//this.mapScale = mapScale;
		//this.originDestinations = od;
		//this.cache = cache;
		//this.objectiveFunction = new ObjectiveFunction(fitnessFunction());

		ConvergenceChecker<PointValuePair> checker = new SimpleValueChecker(relativeThreshold, absoluteThreshold);
		optimizer = new CMAESOptimizer(maxIterations, stopFitness, isActiveCMA, diagonalOnly, checkFeasableCount,
				random, generateStatistics, checker);

		double[] sigmaDbl = new double[this.maxPartitions * 2];
		for (int i = 0; i < this.maxPartitions * 2; i++) {
			sigmaDbl[i] = 0.5d;
		}
		sigma = new CMAESOptimizer.Sigma(sigmaDbl);
		popSize = new CMAESOptimizer.PopulationSize((int) (4 + Math.floor(3 * Math.log(this.maxPartitions * 2))));
		

		double[] minBounds = new double[this.maxPartitions * 2];
		double[] maxBounds = new double[this.maxPartitions * 2];
		for (int i = 0; i < this.maxPartitions * 2; i++) {
			minBounds[i] = 0.0d;
			maxBounds[i] = 1.0d;
		}
		Log.debug("Bounds min=" + Arrays.toString(minBounds) + " max=" + Arrays.toString(maxBounds));
		bounds = new SimpleBounds(minBounds, maxBounds);
	}

	

	public PointValuePair optimize(int maxEval) {
		double[] guessDbl = new double[this.maxPartitions * 2];
		int tempPart = random.nextInt(this.maxPartitions);
		for (int i = 0; i < tempPart; i++) {
			guessDbl[i] = random.nextDouble();
		}
		for (int i = tempPart; i < this.maxPartitions; i++) {
			guessDbl[i] = 0.0d;
		}
		tempPart = this.maxPartitions + random.nextInt(this.maxPartitions);
		for (int i = this.maxPartitions; i < tempPart; i++) {
			guessDbl[i] = random.nextDouble();
		}
		for (int i = tempPart; i < this.maxPartitions*2; i++) {
			guessDbl[i] = 0.0d;
		}
		Log.info("Initial guess=" + Arrays.toString(guessDbl));
		InitialGuess guess = new InitialGuess(guessDbl);
		return optimize(maxEval, guess);
	}

	public PointValuePair optimize(int maxEval, InitialGuess initialGuess) {
		PointValuePair solution = optimizer.optimize(objectiveFunction, sigma, popSize, goal, bounds,
				new MaxEval(maxEval), initialGuess);
		return solution;
	}

	public List<String> getHistory() {
		return history;
	}
}
