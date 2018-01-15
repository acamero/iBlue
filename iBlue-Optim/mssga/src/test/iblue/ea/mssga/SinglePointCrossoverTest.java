package test.iblue.ea.mssga;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

import com.iblue.ea.mssga.Individual;
import com.iblue.ea.mssga.SinglePointCrossover;
import com.iblue.ea.mssga.Chromosome.ChromosomeException;
import com.iblue.ea.mssga.CrossoverInterface.CrossoverException;
import com.iblue.utils.Log;
import com.iblue.utils.Log.LogLevel;

public class SinglePointCrossoverTest {
	
	@BeforeClass
	public static void before() {
		Log.setLogLevel(LogLevel.DEBUG);
	}
	
	@Test
	public void noSpx() throws ChromosomeException, CrossoverException {
		SinglePointCrossover spx = new SinglePointCrossover();
		Individual a = new Individual(10,1);
		Individual b = new Individual(1,10);
		Individual t = spx.crossover(0.0d, a, b);
		
		assertEquals(13,t.getNumberOfGenes());
		if(t.getChromosome().getNumberLatGenes()==10) {
			assertEquals(1, t.getChromosome().getNumberLonGenes());
		} else {
			assertEquals(10, t.getChromosome().getNumberLonGenes());
		}
	}
	
	@Test
	public void spx() throws ChromosomeException, CrossoverException {
		SinglePointCrossover spx = new SinglePointCrossover();
		Individual a = new Individual(10,1);
		Individual b = new Individual(1,10);
		Individual t = spx.crossover(0.0d, a, b);	
		Log.debug(t.toString());
	}

}
