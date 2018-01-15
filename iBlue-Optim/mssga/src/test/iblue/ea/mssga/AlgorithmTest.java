package test.iblue.ea.mssga;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.iblue.ea.RoutesGenerator;
import com.iblue.ea.mssga.Algorithm;
import com.iblue.ea.mssga.BinaryTournament;
import com.iblue.ea.mssga.CrossoverInterface;
import com.iblue.ea.mssga.MapScale;
import com.iblue.ea.mssga.MutationInterface;
import com.iblue.ea.mssga.Problem;
import com.iblue.ea.mssga.ScaleInterface;
import com.iblue.ea.mssga.SelectionInterface;
import com.iblue.ea.mssga.SinglePointCrossover;
import com.iblue.ea.mssga.SizeMutation;
import com.iblue.ea.mssga.Chromosome.ChromosomeException;
import com.iblue.ea.mssga.CrossoverInterface.CrossoverException;
import com.iblue.ea.mssga.Individual;
import com.iblue.ea.mssga.IndividualInitInterface.IndividualInitInterfaceException;
import com.iblue.ea.mssga.MutationInterface.MutationException;
import com.iblue.ea.mssga.Population.PopulationException;
import com.iblue.model.GeoStreetInterface;
import com.iblue.model.db.service.TileService;
import com.iblue.utils.Log;
import com.iblue.utils.Pair;
import com.iblue.utils.Log.LogLevel;

public class AlgorithmTest {
	
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
	private static ScaleInterface mapScale;
	private static Map<String,Double> cache;
	
	
	
	
	@BeforeClass
	public static void before() throws IOException {
		Log.setLogLevel(LogLevel.DEBUG);
		RoutesGenerator rg = new RoutesGenerator();
		originDestinations = rg.getRoutes("spots-malaga.txt");
		
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
			statsBw.write("bestFitness;bestFitnessEver;medianFitness\n");
			statsBw.flush();

		} catch (IOException e) {			
			e.printStackTrace();
		}
		
		TileService serv = new TileService();
		Pair<Pair<BigDecimal,BigDecimal>,Pair<BigDecimal,BigDecimal>> bounds = serv.getMapBoundaries();
		BigDecimal latR = bounds.getFirst().getFirst().subtract(bounds.getFirst().getSecond()).abs();
		BigDecimal lonR = bounds.getSecond().getFirst().subtract(bounds.getSecond().getSecond()).abs();
		Log.debug("Map Size latitude="+latR+" longitude="+lonR);
		mapScale = new MapScale(latR, lonR);
		
		cache = new HashMap<String,Double>();
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
		Log.debug("Cache loaded ("+cache.size()+" entries)");
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
			for(Entry<String,Double> e : cache.entrySet()) {
				cw.write(e.getKey()+"|"+e.getValue()+"\n");
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
	public void test() throws PopulationException, ChromosomeException, IndividualInitInterfaceException, CrossoverException, MutationException, IOException {
		Problem problem = new Problem(originDestinations, routeBw, tileComputingBw, fitnessBw);
		Log.info("Fitness cache size="+cache.size());
		problem.addFitnessCache(cache);
		
		int popSize = 2;
		CrossoverInterface crossover = new SinglePointCrossover();
		MutationInterface mutation = new SizeMutation();
		
		
		SelectionInterface selection = new BinaryTournament();
		int maxInitialPartitions = 10;
		
		Algorithm alg = new Algorithm(problem, popSize, crossover, mutation,
				 mapScale,  selection, maxInitialPartitions, statsBw);
		
		alg.evaluate(3, 1);
		cache = problem.getFitnessCache();
		
	}
	
	@Test
	public void solution() throws ChromosomeException {
		// 1 missing
		// 6;7;[0.05176526, 0.03114841, 0.04602222, 0.1086907, 0.02387407, 0.1303172];[0.1386007, 0.3572897, 0.008154917, 0.03908844, 0.03153798, 0.1207874, 0.2548415];440.0
		// 5;7;[0.2237446, 0.02297405, 0.008506904, 0.01182063, 0.1247717];[0.1026446, 0.1071688, 0.08834229, 0.1024760, 0.2079177, 0.08215889, 0.2595919];203.0
		// 8;4;[0.04391260, 0.07143665, 0.02869467, 0.07747304, 0.03881302, 0.04451067, 0.06421130, 0.02276585];[0.2634636, 0.3004880, 0.01987738, 0.3664712];223.0
		String latGene = "0.2237446, 0.02297405, 0.008506904, 0.01182063, 0.1247717";
		String lonGene = "0.1026446, 0.1071688, 0.08834229, 0.1024760, 0.2079177, 0.08215889, 0.2595919";		

		String[] lats = latGene.split(",");
		String[] lons = lonGene.split(",");
		Individual individual = new Individual(lats.length, lons.length);
		for(int i=0;i<lats.length;i++) {
			individual.getChromosome().setLatGene(i, new BigDecimal(lats[i].trim(),Problem.MATH_CONTEXT));
		}
		
		for(int i=0;i<lons.length;i++) {
			individual.getChromosome().setLonGene(i, new BigDecimal(lons[i].trim(),Problem.MATH_CONTEXT));
		}
		Log.info(individual.toString());

		
		Problem problem = new Problem(originDestinations, routeBw, tileComputingBw, fitnessBw);
		Log.info("Fitness cache size="+cache.size());
		problem.addFitnessCache(cache);
		
		problem.evaluate(individual);
		
		cache = problem.getFitnessCache();
		
	}
}
