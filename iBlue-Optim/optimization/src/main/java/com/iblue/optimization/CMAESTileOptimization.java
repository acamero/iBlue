package com.iblue.optimization;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.analysis.MultivariateFunction;
import org.apache.commons.math3.optim.ConvergenceChecker;
import org.apache.commons.math3.optim.InitialGuess;
import org.apache.commons.math3.optim.MaxEval;
import org.apache.commons.math3.optim.OptimizationData;
import org.apache.commons.math3.optim.PointValuePair;
import org.apache.commons.math3.optim.SimpleBounds;
import org.apache.commons.math3.optim.SimpleValueChecker;
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType;
import org.apache.commons.math3.optim.nonlinear.scalar.ObjectiveFunction;
import org.apache.commons.math3.optim.nonlinear.scalar.noderiv.CMAESOptimizer;
import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomGenerator;

import com.iblue.model.GeoStreetInterface;
import com.iblue.model.IntersectionInterface;
import com.iblue.model.db.service.TileService;
import com.iblue.optimization.MapScale.MapPartition;
import com.iblue.path.AlgorithmInterface;
import com.iblue.path.Dijkstra;
import com.iblue.path.GraphInterface;
import com.iblue.utils.Log;
import com.iblue.utils.Pair;

public class CMAESTileOptimization implements TileOptimizerInterface {
	private static final long PENALTY = 20000;
	public static final int BIGDECIMAL_SCALE = 7;
	public static final MathContext MATH_CONTEXT = new MathContext(BIGDECIMAL_SCALE, RoundingMode.HALF_DOWN);

	private CMAESOptimizer optimizer;
	private OptimizationData sigma;
	private OptimizationData popSize;
	private SimpleBounds bounds;
	private ObjectiveFunction objectiveFunction;
	private MapScale mapScale;
	private int maxPartitions;
	private List<Pair<GeoStreetInterface, GeoStreetInterface>> originDestinations;
	private Map<String, Double> cache;
	private double relativeThreshold = 1e-8;
	private double absoluteThreshold = 1e-10;
	private GoalType goal = GoalType.MINIMIZE;
	private int maxIterations = 1000;
	private double stopFitness = 0.0d;
	private boolean isActiveCMA = true;
	private int diagonalOnly = 1;
	private int checkFeasableCount = 0;
	private RandomGenerator random;
	private boolean generateStatistics = true;
	private List<String> history = new ArrayList<String>();

	public CMAESTileOptimization(int seed, int maxPartitions, MapScale mapScale,
			List<Pair<GeoStreetInterface, GeoStreetInterface>> od, Map<String, Double> cache) {
		random = new MersenneTwister(seed);
		this.maxPartitions = maxPartitions;
		this.mapScale = mapScale;
		this.originDestinations = od;
		this.cache = cache;

		ConvergenceChecker<PointValuePair> checker = new SimpleValueChecker(relativeThreshold, absoluteThreshold);
		optimizer = new CMAESOptimizer(maxIterations, stopFitness, isActiveCMA, diagonalOnly, checkFeasableCount,
				random, generateStatistics, checker);

		double[] sigmaDbl = new double[this.maxPartitions * 2];
		for (int i = 0; i < this.maxPartitions * 2; i++) {
			sigmaDbl[i] = 0.5d;
		}
		sigma = new CMAESOptimizer.Sigma(sigmaDbl);
		popSize = new CMAESOptimizer.PopulationSize((int) (4 + Math.floor(3 * Math.log(this.maxPartitions * 2))));
		objectiveFunction = new ObjectiveFunction(fitnessFunction());

		double[] minBounds = new double[this.maxPartitions * 2];
		double[] maxBounds = new double[this.maxPartitions * 2];
		for (int i = 0; i < this.maxPartitions * 2; i++) {
			minBounds[i] = 0.0d;
			maxBounds[i] = 1.0d;
		}
		Log.debug("Bounds min=" + Arrays.toString(minBounds) + " max=" + Arrays.toString(maxBounds));
		bounds = new SimpleBounds(minBounds, maxBounds);
	}

	private MultivariateFunction fitnessFunction() {
		MultivariateFunction function = new MultivariateFunction() {
			@Override
			public double value(double[] point) {
				MapPartition partitions = mapScale.scale(Arrays.copyOfRange(point, 0, maxPartitions),
						Arrays.copyOfRange(point, maxPartitions, maxPartitions * 2));
				Log.info("Evaluate " + partitions.key());
				TileService tileService = new TileService();
				double fitness = Double.MAX_VALUE;
				// fitness cache
				if (!cache.containsKey(partitions.key())) {
					long beginComputingTiles = System.currentTimeMillis();
					String resp = tileService.computeMap(partitions.latPartitions, partitions.lonPartitions);
					long time = System.currentTimeMillis() - beginComputingTiles;
					Log.info("Time for computing tiles " + time + "(" + resp + ")");
					// calculate fitness as the mean time for resolving n routes
					fitness = routingTime();
					cache.put(partitions.key(), fitness);
					Log.info("Fitness " + fitness);
				} else {
					fitness = cache.get(partitions.key());
				}
				history.add(partitions.key() + "|" + Arrays.toString(point) + "|" + fitness);
				return fitness;
			}

			private double routingTime() {
				long aggFitnessTime = 0l;
				TileService tileService = new TileService();
				for (int i = 0; i < originDestinations.size(); i++) {
					Pair<GeoStreetInterface, GeoStreetInterface> p = originDestinations.get(i);
					Log.debug("Setting graph " + System.currentTimeMillis());
					long begin = System.currentTimeMillis();
					GraphInterface graph = tileService.getTile(p.getFirst().getLatitude1(),
							p.getFirst().getLongitude1(), p.getSecond().getLatitude1(), p.getSecond().getLongitude1());
					AlgorithmInterface alg = new Dijkstra();
					alg.setGraph(graph);

					long beginSearch = System.currentTimeMillis();
					LinkedList<IntersectionInterface> path = alg.getPath(p.getFirst().getFromIntersection(),
							p.getSecond().getFromIntersection());
					long end = System.currentTimeMillis();
					long time = end - begin;
					long searchTime = end - beginSearch;
					long fitnessTime = time;

					if (path.isEmpty()) {
						Log.debug("Trying againg with enlarged tile map");
						long penBeg = System.currentTimeMillis();
						// graph = tileService.getEnlargedTile(p.getFirst().getLatitude1(),
						// p.getFirst().getLongitude1(),
						// p.getSecond().getLatitude1(), p.getSecond().getLongitude1());
						Pair<Pair<BigDecimal, BigDecimal>, Pair<BigDecimal, BigDecimal>> mapBounds = tileService
								.getMapBoundaries();
						graph = tileService.getTile(mapBounds.getFirst().getFirst(), mapBounds.getSecond().getFirst(),
								mapBounds.getFirst().getSecond(), mapBounds.getSecond().getSecond());
						alg = new Dijkstra();
						alg.setGraph(graph);
						path = alg.getPath(p.getFirst().getFromIntersection(), p.getSecond().getFromIntersection());
						long penEnd = System.currentTimeMillis();
						searchTime = searchTime + (penEnd - penBeg);
						fitnessTime = fitnessTime + (penEnd - penBeg);
					}

					boolean found = !path.isEmpty();
					if (!found) {
						fitnessTime += PENALTY;
					}
					aggFitnessTime += fitnessTime;

					Log.debug("Route " + i + " time " + time + " found " + found);
				}

				return (double) (aggFitnessTime / originDestinations.size());
			}
		};

		return function;
	}

	public PointValuePair optimize(int maxEval) {
		double[] guessDbl = new double[this.maxPartitions * 2];
		for (int i = 0; i < this.maxPartitions * 2; i++) {
			guessDbl[i] = random.nextDouble();
		}
		Log.debug("Initial guess=" + Arrays.toString(guessDbl));
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
