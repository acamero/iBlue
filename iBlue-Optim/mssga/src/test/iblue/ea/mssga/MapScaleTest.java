package test.iblue.ea.mssga;

import static org.junit.Assert.*;

import java.math.BigDecimal;

import org.junit.BeforeClass;
import org.junit.Test;

import com.iblue.ea.mssga.Individual;
import com.iblue.ea.mssga.MapScale;
import com.iblue.ea.mssga.Problem;
import com.iblue.ea.mssga.Chromosome.ChromosomeException;
import com.iblue.utils.Log;
import com.iblue.utils.Log.LogLevel;

public class MapScaleTest {
	@BeforeClass
	public static void before(){
		Log.setLogLevel(LogLevel.DEBUG);
	}

	@Test
	public void uniform() throws ChromosomeException {
		Individual ind = new Individual(10,20);
		// Log.info(ind.toString());
		MapScale scale = new MapScale(BigDecimal.ONE, BigDecimal.ONE);
		scale.scale(ind);
		// Log.info(ind.toString());
		BigDecimal latVal = new BigDecimal(0.1d,Problem.MATH_CONTEXT);
		for(BigDecimal d : ind.getChromosome().getLatGenes()) {
			assertEquals(0, d.compareTo(latVal));
		}
		
		BigDecimal lonVal = new BigDecimal(0.05d,Problem.MATH_CONTEXT);
		for(BigDecimal d : ind.getChromosome().getLonGenes()) {
			assertEquals(0, d.compareTo(lonVal));
		}
	}
	
	@Test
	public void nonUniform() throws ChromosomeException {
		Individual ind = new Individual(3,4);
		ind.getChromosome().setLatGene(0, new BigDecimal(2,Problem.MATH_CONTEXT));
		ind.getChromosome().setLatGene(1, new BigDecimal(3,Problem.MATH_CONTEXT));
		ind.getChromosome().setLatGene(2, new BigDecimal(5,Problem.MATH_CONTEXT));
		
		ind.getChromosome().setLonGene(0, new BigDecimal(1,Problem.MATH_CONTEXT));
		ind.getChromosome().setLonGene(1, new BigDecimal(2,Problem.MATH_CONTEXT));
		ind.getChromosome().setLonGene(2, new BigDecimal(3,Problem.MATH_CONTEXT));
		ind.getChromosome().setLonGene(3, new BigDecimal(4,Problem.MATH_CONTEXT));
		
		MapScale scale = new MapScale(BigDecimal.ONE, BigDecimal.ONE);
		scale.scale(ind);
		assertEquals( 0, ind.getChromosome().getLatGenes().get(0).compareTo( new BigDecimal(0.2d, Problem.MATH_CONTEXT)));
		assertEquals( 0, ind.getChromosome().getLatGenes().get(1).compareTo( new BigDecimal(0.3d, Problem.MATH_CONTEXT)));
		assertEquals( 0, ind.getChromosome().getLatGenes().get(2).compareTo( new BigDecimal(0.5d, Problem.MATH_CONTEXT)));
		
		assertEquals( 0, ind.getChromosome().getLonGenes().get(0).compareTo( new BigDecimal(0.1d, Problem.MATH_CONTEXT)));
		assertEquals( 0, ind.getChromosome().getLonGenes().get(1).compareTo( new BigDecimal(0.2d, Problem.MATH_CONTEXT)));
		assertEquals( 0, ind.getChromosome().getLonGenes().get(2).compareTo( new BigDecimal(0.3d, Problem.MATH_CONTEXT)));
		assertEquals( 0, ind.getChromosome().getLonGenes().get(3).compareTo( new BigDecimal(0.4d, Problem.MATH_CONTEXT)));
		
		
	}
	
}
