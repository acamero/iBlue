package com.iblue.ea.mssga;

/**
 * 
 * @author Andrés Camero Unzueta
 *
 */
public interface MutationInterface {

	public void mutate(double sizeMutationProb, double perturbationProb, Individual individual) throws MutationException;
	
	public class MutationException extends Exception {
		private static final long serialVersionUID = -5027599450369654201L;
		public MutationException(String message) {
			super(message);
		}

		public MutationException(Throwable cause) {
			super(cause);
		}

		public MutationException(String message, Throwable cause) {
			super(message, cause);
		}
	}
}
