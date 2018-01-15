package com.iblue.ea.mssga;

import com.iblue.ea.mssga.Chromosome.ChromosomeException;

/**
 * 
 * @author Andrés Camero Unzueta
 *
 */
public interface CrossoverInterface {

	public Individual crossover(double crossoverProb, Individual a, Individual b) throws CrossoverException, ChromosomeException;
	
	public class CrossoverException extends Exception {
		private static final long serialVersionUID = -2848266054128928656L;
		public CrossoverException(String message) {
			super(message);
		}

		public CrossoverException(Throwable cause) {
			super(cause);
		}

		public CrossoverException(String message, Throwable cause) {
			super(message, cause);
		}
	}
}
