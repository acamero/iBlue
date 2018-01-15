package com.iblue.ea.mssga;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.iblue.ea.mssga.Chromosome.ChromosomeException;
import com.iblue.ea.mssga.CrossoverInterface.CrossoverException;
import com.iblue.ea.mssga.IndividualInitInterface.IndividualInitInterfaceException;
import com.iblue.ea.mssga.MutationInterface.MutationException;
import com.iblue.ea.mssga.Population.PopulationException;
import com.iblue.utils.Log;

public class Algorithm {

	private Problem problem;
	private CrossoverInterface crossover;
	private MutationInterface mutation;
	private ScaleInterface scale;
	private double crossoverProb = 0.8d;
	private double sizeMutationProb = 0.1d;
	private double perturbationProb = 0.1d;
	private SelectionInterface selection;
	private Population population;
	private int maxInitialPartitions;
	private BufferedWriter statsBw;

	public Algorithm(Problem problem, int popSize, CrossoverInterface crossover, MutationInterface mutation,
			ScaleInterface scale, SelectionInterface selection, int maxInitialPartitions, BufferedWriter statsBw)
			throws PopulationException, ChromosomeException, IndividualInitInterfaceException {

		this.problem = problem;
		this.crossover = crossover;
		this.mutation = mutation;
		this.scale = scale;
		this.selection = selection;
		this.maxInitialPartitions = maxInitialPartitions;
		this.statsBw = statsBw;
		List<Individual> individuals = new ArrayList<Individual>();
		RandomIndividualInit init = new RandomIndividualInit();
		for (int i = 0; i < popSize; i++) {
			Individual ind = init.nextIndividual(maxInitialPartitions);
			scale.scale(ind);
			individuals.add(ind);
		}
		Log.debug(individuals.size() + " individuals added to the initial population");
		population = new Population(individuals);
	}

	/**
	 * 
	 * @param microEvals
	 * @param times
	 * @throws ChromosomeException
	 * @throws CrossoverException
	 * @throws MutationException
	 * @throws PopulationException
	 * @throws IndividualInitInterfaceException
	 * @throws IOException
	 */
	public void evaluate(int microEvals, int times) throws CrossoverException, ChromosomeException, MutationException,
			PopulationException, IndividualInitInterfaceException {
		for (int t = 0; t < times; t++) {
			// run the ssga
			evaluateMssga(microEvals);

			// select the best individual
			List<Individual> individuals = new ArrayList<Individual>();
			individuals.add(population.getBestIndividual());
			// generate new individuals to complete the population
			RandomIndividualInit init = new RandomIndividualInit();
			for (int i = 1; i < population.getPopulationSize(); i++) {
				Individual ind = init.nextIndividual(maxInitialPartitions);
				scale.scale(ind);
				individuals.add(ind);
			}
			// create a new population to start over again
			population = new Population(individuals);

			Log.debug("New population generated for the next evolution");
			Log.debug("-----------------------------------------------------------------");
		}
	}

	private void evaluateMssga(int microEvals)
			throws CrossoverException, ChromosomeException, MutationException, PopulationException {
		int evals = 0;
		// evaluate initial population
		for (Individual i : population.getPopulation()) {
			problem.evaluate(i);
		}
		population.computeStats();
		evals = evals + population.getPopulationSize();
		Log.debug("Initial population evaluated");

		while (evals < microEvals) {
			// generate new individual
			Individual offspring = crossover.crossover(crossoverProb, selection.selectIndividual(population),
					selection.selectIndividual(population));
			mutation.mutate(sizeMutationProb, perturbationProb, offspring);
			scale.scale(offspring);
			problem.evaluate(offspring);
			population.replaceWorst(offspring);
			// populations statistics are computed automatically
			try {
				statsBw.write(population.getBestFitness() + ";" + population.getBestFitnessEver() + ";"
						+ population.getMedianFitness() + ";" + population.getMeanFitness() + ";"+ population.getStDev() + "\n");
				statsBw.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
			evals++;
		}
		Log.debug("SSGA evaluate " + microEvals + " solutions");
	}

}
