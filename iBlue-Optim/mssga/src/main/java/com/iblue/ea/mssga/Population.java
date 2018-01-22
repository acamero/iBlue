package com.iblue.ea.mssga;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.iblue.ea.mssga.Chromosome.ChromosomeException;
import com.iblue.ea.mssga.IndividualInitInterface.IndividualInitInterfaceException;
import com.iblue.utils.Log;

/**
 * 
 * @author Andrés Camero Unzueta
 *
 */
public class Population {

	private int populationSize;
	private List<Individual> population;
	private double bestFitnessEver;
	private double bestFitness;
	private int worstPosition;
	private int bestPosition;
	private int medianPosition;
	private double medianFitness;
	private double stDev;
	private double meanFitness;

	public Population(List<Individual> individuals)
			throws PopulationException, ChromosomeException, IndividualInitInterfaceException {

		if (individuals.isEmpty()) {
			Log.error("Population size should be greater than zero");
			throw new PopulationException("Population size should be greater than zero");
		} else {
			this.populationSize = individuals.size();
			population = new ArrayList<Individual>(individuals);
		}

		// initialize fitness
		bestFitness = Double.MAX_VALUE;
		bestFitnessEver = Double.MAX_VALUE;
		worstPosition = populationSize - 1;
		bestPosition = 0;
		medianPosition = populationSize / 2;
		stDev = 0;
		meanFitness = Double.MAX_VALUE;
	}

	public int getPopulationSize() {
		return populationSize;
	}

	public double getBestFitnessEver() {
		return bestFitnessEver;
	}

	public double getBestFitness() {
		return bestFitness;
	}

	public double getMedianFitness() {
		return medianFitness;
	}

	public int getWorstPosition() {
		return worstPosition;
	}

	public int getBestPosition() {
		return bestPosition;
	}

	public double getStDev() {
		return stDev;
	}

	public double getMeanFitness() {
		return meanFitness;
	}

	public Individual getBestIndividual() {
		return population.get(bestPosition);
	}

	public void computeStats() {
		// sort by fitness
		Collections.sort(population);

		bestFitness = population.get(0).getFitness();
		if (bestFitness < bestFitnessEver) {
			bestFitnessEver = bestFitness;
		}
		medianFitness = population.get(medianPosition).getFitness();
		double accum = 0.0d;
		for (Individual ind : population) {
			accum = accum + ind.getFitness();
		}
		meanFitness = accum / (double) populationSize;

		double diff = 0.0d;
		for (Individual ind : population) {
			diff = diff + Math.pow(ind.getFitness() - meanFitness, 2.0d);
		}

		stDev = Math.sqrt(diff / (double) populationSize);

		Log.info("\tBestFitness=" + bestFitness + "\tEver=" + bestFitnessEver + "\tMedianFitness=" + medianFitness
				+ "\tMeanFitness=" + meanFitness + "\tStDev=" + stDev);
	}

	/**
	 * Replace the 'n' worst individuals of the population with the offsprings,
	 * where 'n' is the number of offsprings
	 * 
	 * @param offsprings
	 * @throws PopulationException
	 */
	public void replaceWorst(Individual offspring) throws PopulationException {
		if (offspring == null) {
			// something bad happen
			Log.error("Null offspring");
			throw new PopulationException("Null offspring");
		}

		Log.debug("Worst individual (" + worstPosition + ") replaced");
		population.set(worstPosition, offspring);

		// update population statistics
		computeStats();
	}

	public List<Individual> getPopulation() {
		return population;
	}

	public class PopulationException extends Exception {
		private static final long serialVersionUID = -8630050463815306413L;

		public PopulationException(String message) {
			super(message);
		}

		public PopulationException(Throwable cause) {
			super(cause);
		}

		public PopulationException(String message, Throwable cause) {
			super(message, cause);
		}
	}
}
