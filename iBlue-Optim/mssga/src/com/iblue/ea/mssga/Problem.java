package com.iblue.ea.mssga;

import java.io.BufferedWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.iblue.model.GeoStreetInterface;
import com.iblue.model.IntersectionInterface;
import com.iblue.model.db.service.TileService;
import com.iblue.path.AlgorithmInterface;
import com.iblue.path.Dijkstra;
import com.iblue.path.GraphInterface;
import com.iblue.utils.Log;
import com.iblue.utils.Pair;

/**
 * 
 * @author Andrés Camero Unzueta
 *
 */
public class Problem {

	private static final long PENALTY = 20000;
	public static final int BIGDECIMAL_SCALE = 7;
	public static final MathContext MATH_CONTEXT = new MathContext(BIGDECIMAL_SCALE, RoundingMode.HALF_DOWN);

	private List<Pair<GeoStreetInterface, GeoStreetInterface>> originDestinations;
	private BufferedWriter routeBw;
	private BufferedWriter tileComputingBw;
	private BufferedWriter fitnessBw;
	private Map<String, Double> fitnessCache;
	private boolean cache = true;
	private Pair<Pair<BigDecimal, BigDecimal>, Pair<BigDecimal, BigDecimal>> bounds;

	public Problem(List<Pair<GeoStreetInterface, GeoStreetInterface>> od, BufferedWriter routeBw,
			BufferedWriter tileComputingBw, BufferedWriter fitnessBw) {

		this.originDestinations = od;
		this.routeBw = routeBw;
		this.tileComputingBw = tileComputingBw;
		this.fitnessBw = fitnessBw;
		this.fitnessCache = new HashMap<String, Double>();
		TileService serv = new TileService();
		bounds = serv.getMapBoundaries();
	}

	public void setFitnessCache(boolean cache) {
		this.cache = cache;
	}

	public boolean isFitnessCache() {
		return cache;
	}

	public void addFitnessCache(Map<String, Double> fitCache) {
		fitnessCache.putAll(fitCache);
	}

	public Map<String, Double> getFitnessCache() {
		return fitnessCache;
	}

	public void evaluate(Individual individual) {

		TileService tileService = new TileService();
		Log.info("Evaluate solution " + individual.getChromosome().toString());

		double fitness;
		if (cache && fitnessCache.containsKey(individual.getChromosome().toString())) {
			// due to rounding, solution has been already evaluated
			fitness = fitnessCache.get(individual.getChromosome().toString());
			Log.info("Solution already computed");
		} else {
			// new solution
			long beginComputingTiles = System.currentTimeMillis();
			String resp = tileService.computeMap(individual.getChromosome().getLatGenes(),
					individual.getChromosome().getLonGenes());
			long time = System.currentTimeMillis() - beginComputingTiles;
			Log.info("Time for computing tiles " + time + "(" + resp + ")");

			try {
				tileComputingBw.write(individual.getChromosome().toString() + ";" + time + "\n");
				tileComputingBw.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}

			// calculate fitness as the mean time for resolving n routes
			fitness = routingTime(individual.getChromosome().toString());

			if (cache) {
				fitnessCache.put(individual.getChromosome().toString(), fitness);
			}
		}

		try {
			fitnessBw.write(individual.getChromosome().toString() + ";" + fitness + "\n");
			fitnessBw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}

		Log.info("Fitness=" + fitness);
		individual.setFitness(fitness);
	}

	private double routingTime(String solutionStr) {
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
			
			if(path.isEmpty()) {
				Log.debug("Trying againg with enlarged tile map");
				long penBeg = System.currentTimeMillis();
				//graph = tileService.getEnlargedTile(p.getFirst().getLatitude1(), p.getFirst().getLongitude1(),
				//		p.getSecond().getLatitude1(), p.getSecond().getLongitude1());
				graph = tileService.getTile(bounds.getFirst().getFirst(), bounds.getSecond().getFirst(), bounds.getFirst().getSecond(), bounds.getSecond().getSecond());
				alg = new Dijkstra();
				alg.setGraph(graph);
				path = alg.getPath(p.getFirst().getFromIntersection(),
						p.getSecond().getFromIntersection());
				long penEnd = System.currentTimeMillis();
				searchTime = searchTime + (penEnd - penBeg);
				fitnessTime = fitnessTime + (penEnd - penBeg);
				Log.debug("Path found="+!path.isEmpty());
			}

			boolean found = !path.isEmpty();
			if (!found) {
				fitnessTime += PENALTY;
			}
			aggFitnessTime += fitnessTime;

			Log.debug("Route " + i + " time " + time + " found " + found);
			try {
				routeBw.write(
						solutionStr + ";" + i + ";" + time + ";" + searchTime + ";" + fitnessTime + ";" + found + "\n");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		try {
			routeBw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return (double) (aggFitnessTime / originDestinations.size());
	}

	public class ProblemException extends Exception {
		private static final long serialVersionUID = -3722473717296968691L;

		public ProblemException(String message) {
			super(message);
		}

		public ProblemException(Throwable cause) {
			super(cause);
		}

		public ProblemException(String message, Throwable cause) {
			super(message, cause);
		}
	}

}
