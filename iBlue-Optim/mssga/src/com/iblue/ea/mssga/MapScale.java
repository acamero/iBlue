package com.iblue.ea.mssga;

import java.math.BigDecimal;

public class MapScale implements ScaleInterface {
	private BigDecimal latRange;
	private BigDecimal lonRange;

	public MapScale(BigDecimal latRange, BigDecimal lonRange) {
		this.latRange = latRange;
		this.lonRange = lonRange;
	}

	public void scale(Individual individual) {

		if (individual.getChromosome().getNumberLatGenes() > 1) {
			BigDecimal accum = BigDecimal.ZERO;
			for (BigDecimal d : individual.getChromosome().getLatGenes()) {
				accum = accum.add(d, Problem.MATH_CONTEXT);
			}
			for (int i = 0; i < individual.getChromosome().getNumberLatGenes(); i++) {
				individual.getChromosome().getLatGenes().set(i,
						latRange.multiply(individual.getChromosome().getLatGenes().get(i), Problem.MATH_CONTEXT)
								.divide(accum, Problem.MATH_CONTEXT));
			}
		}

		if (individual.getChromosome().getNumberLonGenes() > 1) {
			BigDecimal accum = BigDecimal.ZERO;
			for (BigDecimal d : individual.getChromosome().getLonGenes()) {
				accum = accum.add(d, Problem.MATH_CONTEXT);
			}
			for (int i = 0; i < individual.getChromosome().getNumberLonGenes(); i++) {
				individual.getChromosome().getLonGenes().set(i,
						lonRange.multiply(individual.getChromosome().getLonGenes().get(i), Problem.MATH_CONTEXT)
								.divide(accum, Problem.MATH_CONTEXT));
			}
		}
	}

}
