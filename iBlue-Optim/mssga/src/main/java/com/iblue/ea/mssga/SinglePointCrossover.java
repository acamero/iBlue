package com.iblue.ea.mssga;

import com.iblue.ea.mssga.Chromosome.ChromosomeException;
import com.iblue.ea.utils.RandomGenerator;
import com.iblue.utils.Log;

/**
 * 
 * @author Andrés Camero Unzueta
 *
 */
public class SinglePointCrossover implements CrossoverInterface {

	public Individual crossover(double crossoverProb, Individual a, Individual b)
			throws CrossoverException, ChromosomeException {
		if (a == null || b == null) {
			Log.error("Individuals should not be null");
			throw new CrossoverException("Individuals should not be null");
		}

		if (RandomGenerator.nextDouble() > crossoverProb) {
			// No crossover, then return a or b
			if (RandomGenerator.nextDouble() > 0.5d) {
				return a.copy();
			} else {
				return b.copy();
			}
		}

		// Crossover
		int latSize, lonSize;
		// choose the number of latitude partitions
		if (RandomGenerator.nextDouble() > 0.5d) {
			latSize = a.getChromosome().getNumberLatGenes();
		} else {
			latSize = b.getChromosome().getNumberLatGenes();
		}

		// choose the number of longitude partitions
		if (RandomGenerator.nextDouble() > 0.5d) {
			lonSize = a.getChromosome().getNumberLonGenes();
		} else {
			lonSize = b.getChromosome().getNumberLonGenes();
		}

		Individual individual = new Individual(latSize, lonSize);

		// mix both parents
		int minLatSize = Math.min(a.getChromosome().getNumberLatGenes(), b.getChromosome().getNumberLatGenes());
		for (int i = 0; i < minLatSize; i++) {
			if (RandomGenerator.nextDouble() > 0.5d) {
				individual.getChromosome().setLatGene(i, a.getChromosome().getLatGenes().get(i));
			} else {
				individual.getChromosome().setLatGene(i, b.getChromosome().getLatGenes().get(i));
			}
		}
		if (a.getChromosome().getNumberLatGenes() > minLatSize) {
			for (int i = minLatSize; i < latSize; i++) {
				individual.getChromosome().setLatGene(i, a.getChromosome().getLatGenes().get(i));
			}
		} else if (b.getChromosome().getNumberLatGenes() > minLatSize) {
			for (int i = minLatSize; i < latSize; i++) {
				individual.getChromosome().setLatGene(i, b.getChromosome().getLatGenes().get(i));
			}
		}
		
		int minLonSize = Math.min(a.getChromosome().getNumberLonGenes(), b.getChromosome().getNumberLonGenes());
		for (int i = 0; i < minLonSize; i++) {
			if (RandomGenerator.nextDouble() > 0.5d) {
				individual.getChromosome().setLonGene(i, a.getChromosome().getLonGenes().get(i));
			} else {
				individual.getChromosome().setLonGene(i, b.getChromosome().getLonGenes().get(i));
			}
		}
		if (a.getChromosome().getNumberLonGenes() > minLonSize) {
			for (int i = minLonSize; i < lonSize; i++) {
				individual.getChromosome().setLonGene(i, a.getChromosome().getLonGenes().get(i));
			}
		} else if (b.getChromosome().getNumberLonGenes() > minLonSize) {
			for (int i = minLonSize; i < lonSize; i++) {
				individual.getChromosome().setLonGene(i, b.getChromosome().getLonGenes().get(i));
			}
		}

		return individual;
	}

}
