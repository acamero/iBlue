package com.iblue.ea;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import com.iblue.ea.mssga.Algorithm;
import com.iblue.ea.mssga.BinaryTournament;
import com.iblue.ea.mssga.Chromosome.ChromosomeException;
import com.iblue.ea.mssga.CrossoverInterface;
import com.iblue.ea.mssga.CrossoverInterface.CrossoverException;
import com.iblue.ea.mssga.IndividualInitInterface.IndividualInitInterfaceException;
import com.iblue.ea.mssga.MapScale;
import com.iblue.ea.mssga.MutationInterface;
import com.iblue.ea.mssga.MutationInterface.MutationException;
import com.iblue.ea.mssga.Population.PopulationException;
import com.iblue.ea.mssga.Problem;
import com.iblue.ea.mssga.ScaleInterface;
import com.iblue.ea.mssga.SelectionInterface;
import com.iblue.ea.mssga.SinglePointCrossover;
import com.iblue.ea.mssga.SizeMutation;
import com.iblue.ea.utils.RandomGenerator;
import com.iblue.model.GeoStreetInterface;
import com.iblue.model.db.service.TileService;
import com.iblue.utils.Log;
import com.iblue.utils.Pair;
import com.iblue.utils.Log.LogLevel;

public class MultiTileMapMain {

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
	private static Map<String, Double> cache;

	private static Options setOptions() {
		// create the Options
		Options options = new Options();
		// help menu
		options.addOption("h", "help", false, "print help information");

		// set the logging level
		options.addOption(Option.builder().longOpt("log-level").hasArg()
				.desc("set the logging level (" + Arrays.toString(Log.LogLevel.values()) + ")").build());

		// select the seed to be used in the pseudo-random number generation
		options.addOption(
				Option.builder().longOpt("seed").hasArg().desc("set the number of the seed to be used").build());

		// maximum number of evaluations
		options.addOption(Option.builder().longOpt("max-evals").hasArg()
				.desc("set the maximum number of evaluations for each time").build());

		// population size
		options.addOption(Option.builder().longOpt("pop-size").hasArg().desc("set the size of the population").build());

		// number of times
		options.addOption(
				Option.builder().longOpt("times").hasArg().desc("set the number of times to repeat SSGA").build());

		// initial number of partitions (max)
		options.addOption(Option.builder().longOpt("partitions").hasArg()
				.desc("set the max initial number of partitions").build());

		options.addOption("i", "initial", false, "fixed initial solution (10x10 regular partition)");

		return options;
	}

	private static void before() throws IOException {

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
			statsBw.write("bestFitness;bestFitnessEver;medianFitness;meanFitness;stDev\n");
			statsBw.flush();

		} catch (IOException e) {
			e.printStackTrace();
		}

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
					System.out.println(prts[1]);
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
	}

	private static void after() {
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

	public static void main(String[] args) throws IOException, PopulationException, ChromosomeException,
			IndividualInitInterfaceException, CrossoverException, MutationException {
		int popSize = 5;
		int maxInitialPartitions = 10;
		int microEvals = 20;
		int times = 30;
		int seed = 1;
		double[] guessDbl = null;

		Log.setLogLevel(LogLevel.INFO);

		CommandLineParser parser = new DefaultParser();
		Options options = setOptions();

		try {
			// parse the command line arguments
			CommandLine line = parser.parse(options, args);

			if (line.hasOption("help")) {
				HelpFormatter formatter = new HelpFormatter();
				formatter.printHelp("mssga", options, true);
				// stop processing and print help
				return;
			}

			if (line.hasOption("initial")) {
				guessDbl = new double[maxInitialPartitions * 2];
				for (int i = 0; i < guessDbl.length; i++) {
					guessDbl[i] = 0.5d;
				}
				Log.info("Fixed initial guess " + Arrays.toString(guessDbl));
			}

			if (line.hasOption("log-level")) {
				Log.setLogLevel(LogLevel.getLogLevel(line.getOptionValue("log-level")));
				Log.info("Set log level to " + line.getOptionValue("log-level"));
			}

			if (line.hasOption("seed")) {
				String seedLine = line.getOptionValue("seed");
				Log.info("Seed number argument parsed (seed '" + seedLine + "' selected)");
				seed = Integer.valueOf(seedLine);
			}

			if (line.hasOption("pop-size")) {
				popSize = Integer.valueOf(line.getOptionValue("pop-size"));
				Log.info("Population size set to " + popSize);
			}

			if (line.hasOption("max-evals")) {
				microEvals = Integer.valueOf(line.getOptionValue("max-evals"));
				Log.info("Maximum number of evaluations set to " + microEvals);
			}

			if (line.hasOption("partitions")) {
				maxInitialPartitions = Integer.valueOf(line.getOptionValue("partitions"));
				Log.info("Maximum number of partitions set to " + maxInitialPartitions);
			}

			if (line.hasOption("times")) {
				times = Integer.valueOf(line.getOptionValue("times"));
				Log.info("Number of times " + times);
			}

		} catch (ParseException e) {
			Log.error("Unexpected exception:" + e.getMessage());
			return;
		}

		RandomGenerator.setSeed(seed);

		before();

		// setup the problem
		Problem problem = new Problem(originDestinations, routeBw, tileComputingBw, fitnessBw);
		Log.info("Fitness cache size=" + cache.size());
		problem.addFitnessCache(cache);

		// the operators
		CrossoverInterface crossover = new SinglePointCrossover();
		MutationInterface mutation = new SizeMutation();
		SelectionInterface selection = new BinaryTournament();

		TileService serv = new TileService();
		Pair<Pair<BigDecimal, BigDecimal>, Pair<BigDecimal, BigDecimal>> bounds = serv.getMapBoundaries();
		BigDecimal latR = bounds.getFirst().getFirst().subtract(bounds.getFirst().getSecond()).abs();
		BigDecimal lonR = bounds.getSecond().getFirst().subtract(bounds.getSecond().getSecond()).abs();
		Log.debug("Map Size latitude=" + latR + " longitude=" + lonR);
		ScaleInterface mapScale = new MapScale(latR, lonR);

		Algorithm alg = new Algorithm(problem, popSize, crossover, mutation, mapScale, selection, maxInitialPartitions,
				guessDbl, statsBw);

		alg.evaluate(microEvals, times);
		cache = problem.getFitnessCache();

		after();

	}
}
