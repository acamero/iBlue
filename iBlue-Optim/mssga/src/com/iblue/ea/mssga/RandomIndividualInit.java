package com.iblue.ea.mssga;

import java.math.BigDecimal;

import com.iblue.ea.mssga.Chromosome.ChromosomeException;
import com.iblue.ea.utils.RandomGenerator;

public class RandomIndividualInit implements IndividualInitInterface {

	public Individual nextIndividual(int numberLatGenes, int numberLonGenes)
			throws ChromosomeException, IndividualInitInterfaceException {
		Individual individual = new Individual(numberLatGenes, numberLonGenes);
		for (int i = 0; i < numberLatGenes; i++) {
			individual.getChromosome().getLatGenes().set(i,
					new BigDecimal(RandomGenerator.nextDouble(), Problem.MATH_CONTEXT));
		}
		for (int i = 0; i < numberLonGenes; i++) {
			individual.getChromosome().getLonGenes().set(i,
					new BigDecimal(RandomGenerator.nextDouble(), Problem.MATH_CONTEXT));
		}
		return individual;
	}

	public Individual nextIndividual(int maxPartitions) throws ChromosomeException, IndividualInitInterfaceException {
		int latSize = (int) Math.max(1.0d, RandomGenerator.nextDouble(true, false) * ((double) maxPartitions));
		int lonSize = (int) Math.max(1.0d, RandomGenerator.nextDouble(true, false) * ((double) maxPartitions));

		return nextIndividual(latSize, lonSize);
	}

}
