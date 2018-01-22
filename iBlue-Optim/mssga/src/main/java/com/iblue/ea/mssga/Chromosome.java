package com.iblue.ea.mssga;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.iblue.utils.Log;

/**
 * 
 * @author Andrés Camero Unzueta
 *
 */
public class Chromosome {
	private List<BigDecimal> latGenes;
	private List<BigDecimal> lonGenes;
	private int numberLatGenes;
	private int numberLonGenes;

	public Chromosome(int numberLatGenes, int numberLonGenes) throws ChromosomeException {
		if (numberLatGenes > 0 && numberLonGenes > 0) {
			this.numberLatGenes = numberLatGenes;
			this.numberLonGenes = numberLonGenes;
			this.latGenes = new ArrayList<BigDecimal>();
			for(int i=0;i<numberLatGenes;i++) {
				latGenes.add(BigDecimal.ONE);
			}
			this.lonGenes = new ArrayList<BigDecimal>();
			for(int i=0;i<numberLonGenes;i++) {
				lonGenes.add(BigDecimal.ONE);
			}
		} else {
			Log.error("The number of genes should be greater than zero");
			throw new ChromosomeException("The number of genes should be greater than zero");
		}
	}

	public int getNumberOfGenes() {
		return 2 + numberLatGenes + numberLonGenes;
	}

	public int getNumberLatGenes() {
		return numberLatGenes;
	}

	public int getNumberLonGenes() {
		return numberLonGenes;
	}

	public void setLatGene(int geneNumber, BigDecimal geneValue) throws ChromosomeException {
		if (geneNumber < 0 || geneNumber >= numberLatGenes) {
			Log.error("Gene number " + geneNumber + " out of range");
			throw new ChromosomeException("Gene number " + geneNumber + " out of range");
		} else {
			latGenes.set(geneNumber, geneValue);
		}
	}

	public void setLonGene(int geneNumber, BigDecimal geneValue) throws ChromosomeException {
		if (geneNumber < 0 || geneNumber >= numberLonGenes) {
			Log.error("Gene number " + geneNumber + " out of range");
			throw new ChromosomeException("Gene number " + geneNumber + " out of range");
		} else {
			lonGenes.set(geneNumber, geneValue);
		}
	}

	public void setNumberLatGenes(int number) {
		if (number > 0) {
			this.numberLatGenes = number;
		}
	}

	public void setNumberLonGenes(int number) {
		if (number > 0) {
			this.numberLonGenes = number;
		}
	}

	public List<BigDecimal> getLatGenes() {
		return latGenes;
	}

	public List<BigDecimal> getLonGenes() {
		return lonGenes;
	}

	public String toString() {
		return numberLatGenes + ";" + numberLonGenes + ";" + Arrays.toString(latGenes.toArray()) + ";"
				+ Arrays.toString(lonGenes.toArray());
	}

	public class ChromosomeException extends Exception {
		private static final long serialVersionUID = 1345015079346216448L;

		public ChromosomeException(String message) {
			super(message);
		}

		public ChromosomeException(Throwable cause) {
			super(cause);
		}

		public ChromosomeException(String message, Throwable cause) {
			super(message, cause);
		}
	}

}
