package test.iblue.ea.mssga;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

import com.iblue.ea.mssga.Individual;
import com.iblue.ea.mssga.SizeMutation;
import com.iblue.ea.mssga.Chromosome.ChromosomeException;
import com.iblue.ea.mssga.MutationInterface.MutationException;
import com.iblue.utils.Log;
import com.iblue.utils.Log.LogLevel;

public class SizeMutationTest {

	@BeforeClass
	public static void before() {
		Log.setLogLevel(LogLevel.DEBUG);
	}

	@Test
	public void noMutation() throws ChromosomeException, MutationException {
		Individual a = new Individual(1, 1);
		Individual b = a.copy();
		SizeMutation mutation = new SizeMutation();
		mutation.mutate(0.0d, 0.0d, a);
		assertEquals(a.toString(), b.toString());
	}

	@Test
	public void mutationBoth() throws ChromosomeException, MutationException {
		Individual a = new Individual(1, 1);
		Individual b = a.copy();
		SizeMutation mutation = new SizeMutation();
		mutation.mutate(1.0d, 1.0d, a);
		assertNotEquals(a.toString(), b.toString());
	}

	@Test
	public void mutation() throws ChromosomeException, MutationException {
		Individual a = new Individual(10, 10);
		Log.info(a.toString());

		SizeMutation mutation = new SizeMutation();
		for (int i = 0; i < 10; i++) {
			mutation.mutate(0.5d, 0.1d, a);
			Log.info(a.toString());
		}
	}

	@Test
	public void size() throws ChromosomeException, MutationException {
		Individual a = new Individual(2, 1);
		for (int i = 0; i < 100; i++) {
			SizeMutation mutation = new SizeMutation();
			Log.debug(a.toString());
			mutation.mutate(1.0d, 0.0d, a);
		}
	}
}
