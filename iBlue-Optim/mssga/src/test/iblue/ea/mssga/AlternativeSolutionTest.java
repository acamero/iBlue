package test.iblue.ea.mssga;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.iblue.ea.RoutesGenerator;
import com.iblue.ea.mssga.Individual;
import com.iblue.ea.mssga.Problem;
import com.iblue.ea.mssga.Chromosome.ChromosomeException;
import com.iblue.model.GeoStreetInterface;
import com.iblue.model.db.service.TileService;
import com.iblue.utils.Log;
import com.iblue.utils.Pair;
import com.iblue.utils.Log.LogLevel;

public class AlternativeSolutionTest {

	private static List<Pair<GeoStreetInterface, GeoStreetInterface>> originDestinations;
	private static BufferedWriter routeBw;
	private static BufferedWriter tileComputingBw;
	private static BufferedWriter fitnessBw;
	private static BufferedWriter statsBw;
	private static final String OUTPUT_FILE_RANGE = "tile-comp-results.csv";
	private static final String OUTPUT_FILE_ROUTE = "routing-results.csv";
	private static final String OUTPUT_FILE_FITNESS = "fitness-results.csv";
	private static final String STATS_FILE = "population-stats.csv";
	private static final String CACHE_FILE = "cache.txt";
	// private static ScaleInterface mapScale;
	private static Map<String, Double> cache;

	@BeforeClass
	public static void before() throws IOException {
		Log.setLogLevel(LogLevel.INFO);
		RoutesGenerator rg = new RoutesGenerator();
		originDestinations = rg.getRoutes("spots-malaga-4.txt");

		try {
			File file = new File(OUTPUT_FILE_RANGE);
			if (!file.exists()) {
				file.createNewFile();
			}

			FileWriter fw = new FileWriter(file, false);
			tileComputingBw = new BufferedWriter(fw);
			tileComputingBw.write("latN;lonN;latG;lonG;buildtime\n");
			tileComputingBw.flush();

			file = new File(OUTPUT_FILE_ROUTE);
			if (!file.exists()) {
				file.createNewFile();
			}
			fw = new FileWriter(file, false);
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

			file = new File(STATS_FILE);
			if (!file.exists()) {
				file.createNewFile();
			}
			fw = new FileWriter(file, false);
			statsBw = new BufferedWriter(fw);
			statsBw.write("bestFitness;bestFitnessEver;medianFitness;meanFitness;stDev\n");
			statsBw.flush();

		} catch (IOException e) {
			e.printStackTrace();
		}

		TileService serv = new TileService();
		Pair<Pair<BigDecimal, BigDecimal>, Pair<BigDecimal, BigDecimal>> bounds = serv.getMapBoundaries();
		BigDecimal latR = bounds.getFirst().getFirst().subtract(bounds.getFirst().getSecond()).abs();
		BigDecimal lonR = bounds.getSecond().getFirst().subtract(bounds.getSecond().getSecond()).abs();
		Log.debug("Map Size latitude=" + latR + " longitude=" + lonR);
		// mapScale = new MapScale(latR, lonR);

		cache = new HashMap<String, Double>();
		try {
			File file = new File(CACHE_FILE);
			if (file.exists()) {
				FileReader fr = new FileReader(file);
				BufferedReader br = new BufferedReader(fr);
				String line;
				// skip first line
				br.readLine();
				while ((line = br.readLine()) != null) {
					Log.debug(line);
					String[] prts = line.split("\\|");
					Double fitness = new Double(prts[1]);
					cache.put(prts[0], fitness);
					Log.debug("Added to cache key=" + prts[0] + " value=" + fitness);
				}
				br.close();
			}

		} catch (IOException e) {
			Log.error("Something happen with file " + OUTPUT_FILE_RANGE);
			e.printStackTrace();
		}
		Log.debug("Cache loaded (" + cache.size() + " entries)");
	}

	@AfterClass
	public static void after() {
		try {
			File file = new File(CACHE_FILE);
			if (!file.exists()) {
				file.createNewFile();
			}
			FileWriter fw = new FileWriter(file, false);
			BufferedWriter cw = new BufferedWriter(fw);
			cw.write("solution|fitness\n");
			for (Entry<String, Double> e : cache.entrySet()) {
				cw.write(e.getKey() + "|" + e.getValue() + "\n");
			}
			cw.flush();
			cw.close();
			Log.debug("Cache writted");
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			routeBw.close();
			tileComputingBw.close();
			fitnessBw.close();
			statsBw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void bestSolutions() throws ChromosomeException {
		List<String> solutions = new ArrayList<String>();

		solutions.add(
				"9;9;[0.02433493, 0.06870006, 0.05315750, 0.02403321, 0.05924977, 0.02554867, 0.05289105, 0.02017778, 0.06372486];[0.1470352, 0.2094769, 0.1591029, 0.04245527, 0.06698109, 0.05440936, 0.03368176, 0.1327351, 0.1044227];232");

		for (String sol : solutions) {
			String[] parts = sol.split(";");
			String latGene = parts[2];
			latGene = latGene.substring(1, latGene.length() - 1);

			String lonGene = parts[3];
			lonGene = lonGene.substring(1, lonGene.length() - 1);

			String[] lats = latGene.split(",");
			String[] lons = lonGene.split(",");
			Individual individual = new Individual(lats.length, lons.length);
			for (int i = 0; i < lats.length; i++) {
				individual.getChromosome().setLatGene(i, new BigDecimal(lats[i].trim(), Problem.MATH_CONTEXT));
			}

			for (int i = 0; i < lons.length; i++) {
				individual.getChromosome().setLonGene(i, new BigDecimal(lons[i].trim(), Problem.MATH_CONTEXT));
			}
			Log.info(individual.toString());

			Problem problem = new Problem(originDestinations, routeBw, tileComputingBw, fitnessBw);
			problem.setFitnessCache(false);

			for (int i = 0; i < 31; i++) {
				problem.evaluate(individual);
			}

		}
	}

	@Test
	public void uniformSolutions() throws ChromosomeException {
		Individual individual = new Individual(1, 1);
		//Log.setLogLevel(LogLevel.DEBUG);
		individual.getChromosome().setLatGene(0, new BigDecimal(0.2372263d, Problem.MATH_CONTEXT));
		individual.getChromosome().setLonGene(0, new BigDecimal(0.0289512d, Problem.MATH_CONTEXT));

		Log.info(individual.toString());

		Problem problem = new Problem(originDestinations, routeBw, tileComputingBw, fitnessBw);
		problem.setFitnessCache(false);

		for (int i = 0; i < 31; i++) {
			problem.evaluate(individual);
		}

	}

}
