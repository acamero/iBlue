package com.iblue.optim;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.uma.jmetal.problem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.solution.impl.DefaultDoubleSolution;

import com.iblue.model.GeoStreetInterface;
import com.iblue.model.IntersectionInterface;
import com.iblue.model.db.service.TileService;
import com.iblue.optim.MapScale.MapPartition;
import com.iblue.path.AlgorithmInterface;
import com.iblue.path.Dijkstra;
import com.iblue.path.GraphInterface;
import com.iblue.utils.Log;
import com.iblue.utils.Pair;

public class MultiTileProblem extends AbstractDoubleProblem {
	private static final long serialVersionUID = 3185727386852544024L;
	private static final long PENALTY = 20000;
	public static final int BIGDECIMAL_SCALE = 7;
	public static final MathContext MATH_CONTEXT = new MathContext(BIGDECIMAL_SCALE, RoundingMode.HALF_DOWN);

	private Map<String, Double> cache;
	private List<String> history = new ArrayList<String>();
	private List<Pair<GeoStreetInterface, GeoStreetInterface>> originDestinations;
	private MapScale mapScale;
	private int maxPartitions;
	private double upper = 1.0d;
	private double lower = 0.0d;
	private int maxEvals;

	public MultiTileProblem(int maxPartitions, Map<String, Double> cache,
			List<Pair<GeoStreetInterface, GeoStreetInterface>> od, MapScale mapScale, int maxEvals) {
		this.cache = cache;
		this.originDestinations = od;
		this.mapScale = mapScale;
		this.maxPartitions = maxPartitions;
		this.setNumberOfObjectives(1);
		this.setNumberOfVariables(this.maxPartitions * 2);
		this.maxEvals = maxEvals;
	}

	@Override
	public void evaluate(DoubleSolution solution) {
		// TODO this could be improved
		double[] point = new double[getNumberOfVariables()];
		for (int i = 0; i < solution.getNumberOfVariables(); i++) {
			point[i] = solution.getVariableValue(i);
		}

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
		solution.setObjective(0, fitness);
	}

	private double routingTime() {
		long aggFitnessTime = 0l;
		TileService tileService = new TileService();
		for (int i = 0; i < originDestinations.size(); i++) {
			Pair<GeoStreetInterface, GeoStreetInterface> p = originDestinations.get(i);
			Log.debug("Setting graph " + System.currentTimeMillis());
			long begin = System.currentTimeMillis();
			GraphInterface graph = tileService.getTile(p.getFirst().getLatitude1(), p.getFirst().getLongitude1(),
					p.getSecond().getLatitude1(), p.getSecond().getLongitude1());
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

	@Override
	public Double getLowerBound(int index) {
		return lower;
	}

	@Override
	public Double getUpperBound(int index) {
		return upper;
	}
	
	public List<String> getHistory() {
		return history;
	}
	
	public DoubleSolution createSolution() {
		return new DefaultDoubleSolution(this);
	}
	
	public int getMaxPartitions() {
		return this.maxPartitions;
	}
	
	public int getMaxEvals() {
		return this.maxEvals;
	}

}
