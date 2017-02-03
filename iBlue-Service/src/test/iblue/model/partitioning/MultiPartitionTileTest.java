package test.iblue.model.partitioning;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import com.iblue.model.partitioning.MultiPartitionTile;
import com.iblue.model.partitioning.MultiPartitionTileFactory;
import com.iblue.utils.Log;
import com.iblue.utils.Log.LogLevel;
import com.iblue.utils.Pair;

public class MultiPartitionTileTest {

	private static Pair<BigDecimal, BigDecimal> latBoundaries;
	private static Pair<BigDecimal, BigDecimal> lonBoundaries;
	private static MultiPartitionTileFactory factory;

	@BeforeClass
	public static void beforeClass() {
		Log.setLogLevel(LogLevel.DEBUG);
		latBoundaries = new Pair<BigDecimal, BigDecimal>(new BigDecimal(0), new BigDecimal(10));
		lonBoundaries = new Pair<BigDecimal, BigDecimal>(new BigDecimal(0), new BigDecimal(10));
		factory = new MultiPartitionTileFactory();
	}

	@Test
	public void single() {
		List<BigDecimal> latRanges = new ArrayList<BigDecimal>();
		List<BigDecimal> lonRanges = new ArrayList<BigDecimal>();

		// Latitude sizes
		latRanges.add(new BigDecimal(5));
		// Longitude sizes
		lonRanges.add(new BigDecimal(5));


		MultiPartitionTile mpt = factory.loadFromConfiguration(latRanges, lonRanges, latBoundaries, lonBoundaries);
		Pair<Long, Long> tileId = mpt.getTileId(new BigDecimal(0), new BigDecimal(0));
		assertEquals(0l, tileId.getFirst().longValue());
		assertEquals(0l, tileId.getSecond().longValue());

		tileId = mpt.getTileId(new BigDecimal(10), new BigDecimal(10));
		assertEquals(1l, tileId.getFirst().longValue());
		assertEquals(1l, tileId.getSecond().longValue());

		Pair<Pair<BigDecimal, BigDecimal>, Pair<BigDecimal, BigDecimal>> bounds = mpt
				.getBounds(new Pair<Long, Long>(0l, 0l));
		// lat
		assertEquals(-90l, bounds.getFirst().getFirst().longValue());
		assertEquals(5l, bounds.getSecond().getFirst().longValue());
		// lon
		assertEquals(-90l, bounds.getFirst().getSecond().longValue());		
		assertEquals(5l, bounds.getSecond().getSecond().longValue());
		
		bounds = mpt
				.getBounds(new Pair<Long, Long>(1l, 0l));
		// lat
		assertEquals(5, bounds.getFirst().getFirst().longValue());
		assertEquals(90l, bounds.getSecond().getFirst().longValue());
		// lon
		assertEquals(-90l, bounds.getFirst().getSecond().longValue());		
		assertEquals(5l, bounds.getSecond().getSecond().longValue());
		
		bounds = mpt
				.getBounds(new Pair<Long, Long>(0l, 1l));
		// lat
		assertEquals(-90l, bounds.getFirst().getFirst().longValue());
		assertEquals(5l, bounds.getSecond().getFirst().longValue());
		// lon
		assertEquals(5l, bounds.getFirst().getSecond().longValue());		
		assertEquals(90l, bounds.getSecond().getSecond().longValue());
		
		bounds = mpt
				.getBounds(new Pair<Long, Long>(1l, 1l));
		// lat
		assertEquals(5, bounds.getFirst().getFirst().longValue());
		assertEquals(90l, bounds.getSecond().getFirst().longValue());
		// lon
		assertEquals(5l, bounds.getFirst().getSecond().longValue());		
		assertEquals(90l, bounds.getSecond().getSecond().longValue());
		
		List<Pair<Long, Long>> tiles = mpt.getListTileId(new BigDecimal(1), new BigDecimal(1), new BigDecimal(6), new BigDecimal(3));
		assertEquals(2, tiles.size());
	}

	@Test
	public void twoByOne() {
		List<BigDecimal> latRanges = new ArrayList<BigDecimal>();
		List<BigDecimal> lonRanges = new ArrayList<BigDecimal>();

		// Latitude sizes
		latRanges.add(new BigDecimal(2));
		latRanges.add(new BigDecimal(5));
		// Longitude sizes
		lonRanges.add(new BigDecimal(5));

		MultiPartitionTile mpt = factory.loadFromConfiguration(latRanges, lonRanges, latBoundaries, lonBoundaries);
		
		Pair<Long, Long> tileId = mpt.getTileId(new BigDecimal(0), new BigDecimal(0));
		assertEquals(0l, tileId.getFirst().longValue());
		assertEquals(0l, tileId.getSecond().longValue());
		
		tileId = mpt.getTileId(new BigDecimal(3), new BigDecimal(0));
		assertEquals(1l, tileId.getFirst().longValue());
		assertEquals(0l, tileId.getSecond().longValue());
		
		tileId = mpt.getTileId(new BigDecimal(8), new BigDecimal(0));
		assertEquals(2l, tileId.getFirst().longValue());
		assertEquals(0l, tileId.getSecond().longValue());
	}
	
	@Test
	public void twoByTwo() {
		List<BigDecimal> latRanges = new ArrayList<BigDecimal>();
		List<BigDecimal> lonRanges = new ArrayList<BigDecimal>();

		// Latitude sizes
		latRanges.add(new BigDecimal(2));
		latRanges.add(new BigDecimal(5));
		// Longitude sizes
		lonRanges.add(new BigDecimal(5));
		lonRanges.add(new BigDecimal(3));

		MultiPartitionTile mpt = factory.loadFromConfiguration(latRanges, lonRanges, latBoundaries, lonBoundaries);
		
		Pair<Long, Long> tileId = mpt.getTileId(new BigDecimal(0), new BigDecimal(0));
		assertEquals(0l, tileId.getFirst().longValue());
		assertEquals(0l, tileId.getSecond().longValue());
		
		tileId = mpt.getTileId(new BigDecimal(3), new BigDecimal(0));
		assertEquals(1l, tileId.getFirst().longValue());
		assertEquals(0l, tileId.getSecond().longValue());
		
		tileId = mpt.getTileId(new BigDecimal(8), new BigDecimal(0));
		assertEquals(2l, tileId.getFirst().longValue());
		assertEquals(0l, tileId.getSecond().longValue());
		
		//
		tileId = mpt.getTileId(new BigDecimal(0), new BigDecimal(7));
		assertEquals(0l, tileId.getFirst().longValue());
		assertEquals(1l, tileId.getSecond().longValue());
		
		tileId = mpt.getTileId(new BigDecimal(3), new BigDecimal(7));
		assertEquals(1l, tileId.getFirst().longValue());
		assertEquals(1l, tileId.getSecond().longValue());
		
		tileId = mpt.getTileId(new BigDecimal(8), new BigDecimal(7));
		assertEquals(2l, tileId.getFirst().longValue());
		assertEquals(1l, tileId.getSecond().longValue());
		
		//
		tileId = mpt.getTileId(new BigDecimal(0), new BigDecimal(10));
		assertEquals(0l, tileId.getFirst().longValue());
		assertEquals(2l, tileId.getSecond().longValue());
		
		tileId = mpt.getTileId(new BigDecimal(3), new BigDecimal(10));
		assertEquals(1l, tileId.getFirst().longValue());
		assertEquals(2l, tileId.getSecond().longValue());
		
		tileId = mpt.getTileId(new BigDecimal(8), new BigDecimal(10));
		assertEquals(2l, tileId.getFirst().longValue());
		assertEquals(2l, tileId.getSecond().longValue());
	}
}
