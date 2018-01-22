package com.iblue.ea.mssga;

import java.math.BigDecimal;

import com.iblue.ea.utils.RandomGenerator;
import com.iblue.utils.Log;

public class SizeMutation implements MutationInterface {

	private static double standardDeviation = 1.0d;

	public void mutate(double sizeMutationProb, double perturbationProb, Individual individual)
			throws MutationException {

		if (RandomGenerator.nextDouble() <= sizeMutationProb) {
			// vary lat size
			int latSize = (int) Math.max(1.0d,
					RandomGenerator.nextGaussian(standardDeviation, individual.getChromosome().getNumberLatGenes()));
			//Log.debug("Lat size="+latSize);
			if (individual.getChromosome().getNumberLatGenes() > latSize) {				
				int times = individual.getChromosome().getNumberLatGenes() - latSize;
				Log.debug("Lat size reduced in "+times);
				for (int i = 0; i < times; i++) {
					int pos = (int) (RandomGenerator.nextDouble(true, false)
							* ((double) individual.getChromosome().getNumberLatGenes()));
					individual.getChromosome().getLatGenes().remove(pos);
					individual.getChromosome().setNumberLatGenes(individual.getChromosome().getNumberLatGenes()-1);
				}
			} else if (individual.getChromosome().getNumberLatGenes() < latSize) {
				int times = latSize - individual.getChromosome().getNumberLatGenes();
				Log.debug("Lat size augmented in "+times);
				for (int i = 0; i < times; i++) {
					int pos = (int) (RandomGenerator.nextDouble(true, false)
							* ((double) individual.getChromosome().getNumberLatGenes()));
					individual.getChromosome().getLatGenes().add(pos,
							new BigDecimal(RandomGenerator.nextDouble(), Problem.MATH_CONTEXT));
					individual.getChromosome().setNumberLatGenes(individual.getChromosome().getNumberLatGenes()+1);
				}
			}
		}

		if (RandomGenerator.nextDouble() <= sizeMutationProb) {
			// vary lon size
			int lonSize = (int) Math.max(1.0d,
					RandomGenerator.nextGaussian(standardDeviation, individual.getChromosome().getNumberLonGenes()));
			//Log.debug("Lon size="+lonSize);
			if (individual.getChromosome().getNumberLonGenes() > lonSize) {
				int times = individual.getChromosome().getNumberLonGenes() - lonSize;
				Log.debug("Lon size reduced in "+times);
				for (int i = 0; i < times; i++) {
					int pos = (int) (RandomGenerator.nextDouble(true, false)
							* ((double) individual.getChromosome().getNumberLonGenes()));
					individual.getChromosome().getLonGenes().remove(pos);
					individual.getChromosome().setNumberLonGenes(individual.getChromosome().getNumberLonGenes()-1);
				}
			} else if (individual.getChromosome().getNumberLonGenes() < lonSize) {
				int times = lonSize - individual.getChromosome().getNumberLonGenes() ;
				Log.debug("Lon size augmented in "+times);
				for (int i = 0; i < times; i++) {
					int pos = (int) (RandomGenerator.nextDouble(true, false)
							* ((double) individual.getChromosome().getNumberLonGenes()));
					individual.getChromosome().getLonGenes().add(pos,
							new BigDecimal(RandomGenerator.nextDouble(), Problem.MATH_CONTEXT));
					individual.getChromosome().setNumberLonGenes(individual.getChromosome().getNumberLonGenes()+1);
				}
			}
		}

		// perturb genes
		for (int i = 0; i < individual.getChromosome().getNumberLatGenes(); i++) {
			if (RandomGenerator.nextDouble() <= perturbationProb) {
				individual.getChromosome().getLatGenes().set(i,
						new BigDecimal(
								Math.abs(RandomGenerator.nextGaussian(standardDeviation,
										individual.getChromosome().getLatGenes().get(i).doubleValue())),
								Problem.MATH_CONTEXT));
			}
		}

		for (int i = 0; i < individual.getChromosome().getNumberLonGenes(); i++) {
			if (RandomGenerator.nextDouble() <= perturbationProb) {
				individual.getChromosome().getLonGenes().set(i,
						new BigDecimal(
								Math.abs(RandomGenerator.nextGaussian(standardDeviation,
										individual.getChromosome().getLonGenes().get(i).doubleValue())),
								Problem.MATH_CONTEXT));
			}
		}
	}

}
