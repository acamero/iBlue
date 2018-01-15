package com.iblue.ea.mssga;

import com.iblue.ea.mssga.Chromosome.ChromosomeException;

/**
 * 
 * @author Andrés Camero Unzueta
 *
 */
public interface IndividualInitInterface {

	// initialize an individual without a fitness value
	public Individual nextIndividual(int numberLatGenes, int numberLonGenes) throws ChromosomeException, IndividualInitInterfaceException;
	public Individual nextIndividual(int maxPartitions) throws ChromosomeException, IndividualInitInterfaceException;
	
	public class IndividualInitInterfaceException extends Exception {
		private static final long serialVersionUID = 536348314796786425L;

		public IndividualInitInterfaceException(String message) {
			super(message);
		}

		public IndividualInitInterfaceException(Throwable cause) {
			super(cause);
		}

		public IndividualInitInterfaceException(String message, Throwable cause) {
			super(message, cause);
		}

	}

}
