package com.iblue.ea.mssga;

import com.iblue.ea.utils.RandomGenerator;

public class BinaryTournament implements SelectionInterface {
	
	public Individual selectIndividual(Population population) {
		int p1, p2;

		p1 = (int) (RandomGenerator.nextDouble() * (double) population.getPopulationSize() + 0.5d);
		if (p1 > population.getPopulationSize() - 1) {
			p1 = population.getPopulationSize() - 1;
		}

		do {
			p2 = (int) (RandomGenerator.nextDouble() * (double) population.getPopulationSize() + 0.5d);
			if (p2 > population.getPopulationSize() - 1) {
				p2 = population.getPopulationSize() - 1;
			}
		} while (p1 == p2);

		if (population.getPopulation().get(p1).getFitness() < population.getPopulation().get(p2).getFitness()) {
			return population.getPopulation().get(p1);
		} else {
			return population.getPopulation().get(p2);
		}
	}
}
