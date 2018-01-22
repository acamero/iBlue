package com.iblue.ea.mssga;


import com.iblue.ea.mssga.Chromosome.ChromosomeException;

/**
 * 
 * @author Andrés Camero Unzueta
 *
 */
public class Individual implements Comparable<Individual> {

	private Chromosome chromosome;
	private double fitness;

	public Individual(int numberLatGenes, int numberLonGenes) throws ChromosomeException {
		chromosome = new Chromosome(numberLatGenes, numberLonGenes);
		fitness = Double.MAX_VALUE;
	}

	public double getFitness() {
		return fitness;
	}

	public void setFitness(double fitness) {
		this.fitness = fitness;
	}

	public Chromosome getChromosome() {
		return chromosome;
	}

	public int getNumberOfGenes() {
		return chromosome.getNumberOfGenes();
	}

	public String toString() {
		return "Fitness=" + fitness + ", Chromosome=(" + chromosome.toString() + ")";
	}

	/**
	 * Returns a copy of the Individual
	 * @return
	 * @throws ChromosomeException
	 */
	public Individual copy() throws ChromosomeException {
		Individual copy = new Individual(chromosome.getNumberLatGenes(), chromosome.getNumberLonGenes());
		for (int i = 0; i < copy.getChromosome().getNumberLatGenes(); i++) {
			copy.getChromosome().setLatGene(i, chromosome.getLatGenes().get(i));
		}
		for (int i = 0; i < copy.getChromosome().getNumberLonGenes(); i++) {
			copy.getChromosome().setLonGene(i, chromosome.getLonGenes().get(i));
		}
		copy.setFitness(this.getFitness());
		return copy;
	}


	public int compareTo(Individual arg0) {
		// ascending
		double diff = this.fitness - arg0.getFitness();
		if (diff < 0.0d) {
			return -1;
		} else if (diff == 0.0d) {
			return 0;
		} else {
			return 1;
		}
	}
}
