package com.iblue.optim;

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
import org.apache.commons.math3.optim.InitialGuess;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.singleobjective.evolutionstrategy.EvolutionStrategyBuilder;
import org.uma.jmetal.algorithm.singleobjective.evolutionstrategy.EvolutionStrategyBuilder.EvolutionStrategyVariant;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.impl.mutation.SimpleRandomMutation;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.AlgorithmRunner;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

import com.iblue.model.GeoStreetInterface;
import com.iblue.model.db.service.TileService;
import com.iblue.optim.pso.StandardPSO2011;
import com.iblue.utils.Log;
import com.iblue.utils.Pair;
import com.iblue.utils.Log.LogLevel;

public class Main {

	private static enum Algorithms {
		MuLambdaES, PSO
	};

	private static final int seeds[] = { 2902, 5235, 357, 6058, 4846, 8280, 1295, 181, 3264, 7285, 8806, 2344, 9203,
			6806, 1511, 2172, 843, 4697, 3348, 1866, 5800, 4094, 2751, 64, 7181, 9167, 5579, 9461, 3393, 4602, 1796,
			8174, 1691, 8854, 5902, 4864, 5488, 1129, 1111, 7597, 5406, 2134, 7280, 6465, 4084, 8564, 2593, 9954, 4731,
			1347, 8984, 5057, 3429, 7635, 1323, 1146, 5192, 6547, 343, 7584, 3765, 8660, 9318, 5098, 5185, 9253, 4495,
			892, 5080, 5297, 9275, 7515, 9729, 6200, 2138, 5480, 860, 8295, 8327, 9629, 4212, 3087, 5276, 9250, 1835,
			9241, 1790, 1947, 8146, 8328, 973, 1255, 9733, 4314, 6912, 8007, 8911, 6802, 5102, 5451, 1026, 8029, 6628,
			8121, 5509, 3603, 6094, 4447, 683, 6996, 3304, 3130, 2314, 7788, 8689, 3253, 5920, 3660, 2489, 8153, 2822,
			6132, 7684, 3032, 9949, 59, 6669, 6334 };

	public static int getSeed(int seedPos) {
		int seedPosition = Math.abs(seedPos) % seeds.length;
		Log.info("Set seed position=" + seedPosition + ", value=" + seeds[seedPosition]);
		return seeds[seedPosition];
	}

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
		options.addOption(
				Option.builder().longOpt("max-evals").hasArg().desc("set the maximum number of evaluations").build());

		options.addOption(Option.builder().longOpt("algorithm").hasArg()
				.desc("set the optimization algorithm (" + Arrays.toString(Algorithms.values()) + ")").build());
		
		options.addOption("i", "initial", false, "fixed initial solution (10x10 regular partition)");

		return options;
	}

	private static Map<String, Double> loadCache(String fileName) {
		Map<String, Double> cache = new HashMap<String, Double>();
		if (fileName != null) {
			try {
				File file = new File(fileName);
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
				Log.error("Something happen with file " + fileName);
				e.printStackTrace();
			}
		}
		return cache;
	}

	private static void storeCache(String fileName, Map<String, Double> cache) {
		try {
			File file = new File(fileName);
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
	}

	private static void storeSolution(String fileName, String solution, double fitness) {
		try {
			File file = new File(fileName);
			if (!file.exists()) {
				file.createNewFile();
			}
			FileWriter fw = new FileWriter(file, false);
			BufferedWriter cw = new BufferedWriter(fw);
			cw.write("solution|fitness\n");

			cw.write(solution + "|" + fitness + "\n");

			cw.flush();
			cw.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void storeHistory(String fileName, List<String> history) {
		try {
			File file = new File(fileName);
			if (!file.exists()) {
				file.createNewFile();
			}
			FileWriter fw = new FileWriter(file, false);
			BufferedWriter cw = new BufferedWriter(fw);
			cw.write("solution|fitness\n");
			for (String line : history) {
				cw.write(line + "\n");
			}
			cw.flush();
			cw.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static Algorithm<DoubleSolution> getAlgorithm(String algorithmStr, MultiTileProblem problem) {
		Algorithm<DoubleSolution> algorithm = null;
		if (algorithmStr.equals(Algorithms.MuLambdaES.toString())) {
			double mutationProb = 1.0d / (2.0d * (double) problem.getMaxPartitions());
			int mu = 1;
			int lambda = 1;
			Log.info("Mu=" + mu + " Lambda=" + lambda + " Mutation Prob=" + mutationProb);
			MutationOperator<DoubleSolution> mutation = new SimpleRandomMutation(mutationProb);
			algorithm = new EvolutionStrategyBuilder<DoubleSolution>(problem, mutation,
					EvolutionStrategyVariant.ELITIST).setLambda(lambda).setMu(mu)
							.setMaxEvaluations(problem.getMaxEvals()).build();
		} else if (algorithmStr.equals(Algorithms.PSO.toString())) {
			int maxIterations = 2;
			int swarmSize = 3;
			int numberOfParticlesToInform = (int) ((double) swarmSize
					* (1.0d - Math.pow(1.0d - 1.0d / (double) swarmSize, 3.0d)));
			if (problem.getMaxEvals() > swarmSize) {
				maxIterations = problem.getMaxEvals() / swarmSize + 1;
			}
			if(numberOfParticlesToInform<1) {
				numberOfParticlesToInform=1;
			}
			Log.info("Swarm size=" + swarmSize + " Particles to inform=" + numberOfParticlesToInform + " Iterations="
					+ maxIterations);
			SolutionListEvaluator<DoubleSolution> evaluator = new SequentialSolutionListEvaluator<DoubleSolution>();
			algorithm = new StandardPSO2011(problem, 0, swarmSize, maxIterations, numberOfParticlesToInform, evaluator);
		} else {
			Log.error("Algorithm '" + algorithmStr + "' not found");
		}

		return algorithm;
	}

	public static void main(String[] args) throws IOException {
		Log.setLogLevel(LogLevel.INFO);
		String cacheFileName = "cache.txt";
		String solutionFileName = "solution.txt";
		String fitnessHistoryFileName = "fitness-history.txt";

		// parameters
		int seedPosition = 0;
		int maxPartitions = 10;
		int maxEvals = 4;
		String algorithmStr = Algorithms.PSO.toString();
		//String algorithmStr = Algorithms.MuLambdaES.toString();
		double[] guessDbl = null;

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
				guessDbl = new double[maxPartitions * 2];
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
				seedPosition = Integer.valueOf(seedLine);
			}

			if (line.hasOption("max-evals")) {
				maxEvals = Integer.valueOf(line.getOptionValue("max-evals"));
				Log.info("Maximum number of evaluations set to " + maxEvals);
			}

			if (line.hasOption("algorithm")) {
				algorithmStr = line.getOptionValue("algorithm");
				Log.info("Algorithm set to " + algorithmStr);
			}

		} catch (ParseException e) {
			Log.error("Unexpected exception:" + e.getMessage());
			return;
		}
		
		Map<String, Double> cache = loadCache(cacheFileName);
		// update output file names using the position of the seed selected
		solutionFileName = seedPosition + "-" + solutionFileName;
		fitnessHistoryFileName = seedPosition + "-" + fitnessHistoryFileName;

		// auxiliary function to scale a solution into the map boundaries
		TileService serv = new TileService();
		Pair<Pair<BigDecimal, BigDecimal>, Pair<BigDecimal, BigDecimal>> bounds = serv.getMapBoundaries();
		BigDecimal latR = bounds.getFirst().getFirst().subtract(bounds.getFirst().getSecond()).abs();
		BigDecimal lonR = bounds.getSecond().getFirst().subtract(bounds.getSecond().getSecond()).abs();
		Log.debug("Map Size latitude=" + latR + " longitude=" + lonR);
		MapScale mapScale = new MapScale(latR, lonR);

		// generate the set of origin-destination points
		RoutesGenerator rg = new RoutesGenerator();

		List<Pair<GeoStreetInterface, GeoStreetInterface>> od = rg.getRoutes("spots-malaga.txt");
		// reproducibility
		JMetalRandom.getInstance().setSeed(getSeed(seedPosition));

		MultiTileProblem problem = new MultiTileProblem(maxPartitions, cache, od, mapScale, maxEvals);
		if(guessDbl!=null) {
			problem.setInitialSolution(guessDbl);
		}

		Algorithm<DoubleSolution> algorithm = getAlgorithm(algorithmStr, problem);
		AlgorithmRunner algorithmRunner = new AlgorithmRunner.Executor(algorithm).execute();

		DoubleSolution solution = algorithm.getResult();
		Log.info("Solution fitness" + solution.getObjective(0));
		storeSolution(solutionFileName, solution.toString(), solution.getObjective(0));

		// store all history
		storeHistory(fitnessHistoryFileName, problem.getHistory());
		storeCache(cacheFileName, cache);
		Log.info("Run ended [time " + algorithmRunner.getComputingTime() + "]");
	}
}
