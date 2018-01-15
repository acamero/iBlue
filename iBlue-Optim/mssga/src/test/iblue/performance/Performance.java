package test.iblue.performance;

import static org.junit.Assert.*;

import java.io.BufferedWriter;
import java.io.File;

import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import java.util.LinkedList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import com.iblue.ea.RoutesGenerator;
import com.iblue.model.GeoStreetInterface;
import com.iblue.model.IntersectionInterface;
import com.iblue.model.db.service.TileService;
import com.iblue.path.AlgorithmInterface;
import com.iblue.path.Dijkstra;
import com.iblue.path.GraphInterface;
import com.iblue.utils.Log;
import com.iblue.utils.Pair;
import com.iblue.utils.Log.LogLevel;

public class Performance {
	private static final long PENALTY = 20000;
	public static final int BIGDECIMAL_SCALE = 7;
	public static final MathContext MATH_CONTEXT = new MathContext(BIGDECIMAL_SCALE, RoundingMode.HALF_DOWN);
	private static List<Pair<GeoStreetInterface, GeoStreetInterface>> originDestinations;
	private static BufferedWriter routeBw;
	private static BufferedWriter fitnessBw;
	private static final String OUTPUT_FILE_ROUTE = "routing-results.csv";
	private static final String OUTPUT_FILE_FITNESS = "fitness-results.csv";
	private static Pair<Pair<BigDecimal, BigDecimal>, Pair<BigDecimal, BigDecimal>> bounds;

	@BeforeClass
	public static void before() throws IOException {
		Log.setLogLevel(LogLevel.INFO);
		RoutesGenerator rg = new RoutesGenerator();
		originDestinations = rg.getRoutes("spots-mex.txt");

		try {
			File file = new File(OUTPUT_FILE_ROUTE);
			if (!file.exists()) {
				file.createNewFile();
			}
			FileWriter fw = new FileWriter(file, false);
			routeBw = new BufferedWriter(fw);
			routeBw.write("latN;lonN;latG;lonG;route;totaltime;searchtime;fitnesstime;found\n");
			routeBw.flush();

			file = new File(OUTPUT_FILE_FITNESS);
			if (!file.exists()) {
				file.createNewFile();
			}
			fw = new FileWriter(file, false);
			fitnessBw = new BufferedWriter(fw);
			fitnessBw.write("latN;lonN;latG;lonG;fitness\n");
			fitnessBw.flush();

		} catch (IOException e) {
			e.printStackTrace();
		}

		TileService serv = new TileService();
		serv.computeMap(new BigDecimal(100), new BigDecimal(100));
		bounds = serv.getMapBoundaries();
	}

	@Test
	public void computeSP() {
		int numRoutes = originDestinations.size();
		// numRoutes = 1;
		double fitness = fitness(numRoutes);
		Log.info("Fitness=" + fitness);
		try {
			fitnessBw.write("PERFORMANCE" + ";" + fitness + "\n");
			fitnessBw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		assertNotEquals(0.0d, fitness, 0.0d);
	}

	public double fitness(int numRoutes) {
		long aggFitnessTime = 0l;
		TileService tileService = new TileService();
		for (int i = 0; i < numRoutes; i++) {
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
				// graph =
				// tileService.getEnlargedTile(p.getFirst().getLatitude1(),
				// p.getFirst().getLongitude1(),
				// p.getSecond().getLatitude1(), p.getSecond().getLongitude1());
				graph = tileService.getTile(bounds.getFirst().getFirst(), bounds.getSecond().getFirst(),
						bounds.getFirst().getSecond(), bounds.getSecond().getSecond());
				alg = new Dijkstra();
				alg.setGraph(graph);
				path = alg.getPath(p.getFirst().getFromIntersection(), p.getSecond().getFromIntersection());
				long penEnd = System.currentTimeMillis();
				searchTime = searchTime + (penEnd - penBeg);
				fitnessTime = fitnessTime + (penEnd - penBeg);
				Log.debug("Path found=" + !path.isEmpty());
			}

			boolean found = !path.isEmpty();
			if (!found) {
				fitnessTime += PENALTY;
			}
			aggFitnessTime += fitnessTime;

			Log.info("Route " + i + " time " + time + " found " + found);
			try {
				routeBw.write("PERFORMANCE" + ";" + i + ";" + time + ";" + searchTime + ";" + fitnessTime + ";" + found
						+ "\n");
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

}
