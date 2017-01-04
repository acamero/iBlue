package test.iblue.model.db.service;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import com.iblue.model.db.service.TileHelper;
import com.iblue.utils.Log;
import com.iblue.utils.Pair;
import com.iblue.utils.Log.LogLevel;

public class TileHelperTest {
	
	@BeforeClass
	public static void initialize() {
		Log.setLogLevel(LogLevel.DEBUG);
	}

	@Test
	public void load() {
		TileHelper h = TileHelper.getInstance();
		assertNotNull(h);
	}
		
	@Test
	public void id1() {
		TileHelper h = TileHelper.getInstance();
		BigDecimal latRange = new BigDecimal(1d);
		BigDecimal lonRange = new BigDecimal(1d);
		
		h.setRange(latRange,lonRange);
		BigDecimal lat = new BigDecimal(0.1234567f);
		BigDecimal lon = new BigDecimal(36.7654321f);
		Pair<Long,Long> id = h.getTileId(lat, lon);
		assertEquals(0l,id.getFirst().longValue());
		assertEquals(36l,id.getSecond().longValue());
		
	}
	
	@Test
	public void id01() {
				TileHelper h = TileHelper.getInstance();
		BigDecimal latRange = new BigDecimal(0.1d);
		BigDecimal lonRange = new BigDecimal(0.1d);
		
		h.setRange(latRange,lonRange);
		BigDecimal lat = new BigDecimal(0.1234567f);
		BigDecimal lon = new BigDecimal(36.7654321f);
		Pair<Long,Long> id = h.getTileId(lat, lon);
		assertEquals(1l,id.getFirst().longValue());
		assertEquals(367l,id.getSecond().longValue());		
	}
	
	@Test
	public void id001() {
				TileHelper h = TileHelper.getInstance();
		BigDecimal latRange = new BigDecimal(0.01d);
		BigDecimal lonRange = new BigDecimal(0.01d);
		
		h.setRange(latRange,lonRange);
		BigDecimal lat = new BigDecimal(0.1234567f);
		BigDecimal lon = new BigDecimal(36.7654321f);
		Pair<Long,Long> id = h.getTileId(lat, lon);
		assertEquals(12l,id.getFirst().longValue());
		assertEquals(3676l,id.getSecond().longValue());		
	}
	
	@Test
	public void zero() {
		
		TileHelper h = TileHelper.getInstance();
		BigDecimal latRange = new BigDecimal(0.1d);
		BigDecimal lonRange = new BigDecimal(0.1d);
		
		h.setRange(latRange,lonRange);
		BigDecimal lat = new BigDecimal(0.0234567f);
		BigDecimal lon = new BigDecimal(36.7654321f);
		Pair<Long,Long> id = h.getTileId(lat, lon);
		assertEquals(0l,id.getFirst().longValue());
		assertEquals(367l,id.getSecond().longValue());		
	}
	
	@Test
	public void neg1() {
		TileHelper h = TileHelper.getInstance();
		BigDecimal latRange = new BigDecimal(0.1d);
		BigDecimal lonRange = new BigDecimal(0.1d);
		
		h.setRange(latRange,lonRange);
		BigDecimal lat = new BigDecimal(-0.0234567f);
		BigDecimal lon = new BigDecimal(36.7654321f);
		Pair<Long,Long> id = h.getTileId(lat, lon);
		assertEquals(-1l,id.getFirst().longValue());
		assertEquals(367l,id.getSecond().longValue());		
	}
	
	@Test
	public void neg2() {
		TileHelper h = TileHelper.getInstance();
		BigDecimal latRange = new BigDecimal(0.1d);
		BigDecimal lonRange = new BigDecimal(0.1d);
		
		h.setRange(latRange,lonRange);
		BigDecimal lat = new BigDecimal(-0.1234567f);
		BigDecimal lon = new BigDecimal(36.7654321f);
		Pair<Long,Long> id = h.getTileId(lat, lon);
		assertEquals(-2l,id.getFirst().longValue());
		assertEquals(367l,id.getSecond().longValue());		
	}
	
	@Test
	public void id4() {
		TileHelper h = TileHelper.getInstance();
		BigDecimal latRange = new BigDecimal(4d);
		BigDecimal lonRange = new BigDecimal(4d);
		
		h.setRange(latRange,lonRange);
		BigDecimal lat = new BigDecimal(0.1234567f);
		BigDecimal lon = new BigDecimal(36.7654321f);
		Pair<Long,Long> id = h.getTileId(lat, lon);
		assertEquals(0l,id.getFirst().longValue());
		assertEquals(9l,id.getSecond().longValue());
		
	}
	
	@Test
	public void id012() {
		TileHelper h = TileHelper.getInstance();
		BigDecimal latRange = new BigDecimal(0.12d);
		BigDecimal lonRange = new BigDecimal(4d);
		
		h.setRange(latRange,lonRange);
		BigDecimal lat = new BigDecimal(0.1234567f);
		BigDecimal lon = new BigDecimal(36.7654321f);
		Pair<Long,Long> id = h.getTileId(lat, lon);
		assertEquals(1l,id.getFirst().longValue());
		assertEquals(9l,id.getSecond().longValue());
		
	}
	
	@Test
	public void bounds1() {
		TileHelper h = TileHelper.getInstance();
		BigDecimal latRange = new BigDecimal(1d);
		BigDecimal lonRange = new BigDecimal(1d);		
		h.setRange(latRange,lonRange);
		
		Pair<Long,Long> tileId = new Pair<Long,Long>(0l,0l);
		Pair<Pair<BigDecimal, BigDecimal>, Pair<BigDecimal, BigDecimal>> bounds = h.getBounds(tileId);
		assertEquals(0.0d, bounds.getFirst().getFirst().doubleValue(), 0.000001d);
		assertEquals(0.0d, bounds.getFirst().getSecond().doubleValue(), 0.000001d);
		assertEquals(1.0d, bounds.getSecond().getFirst().doubleValue(), 0.000001d);
		assertEquals(1.0d, bounds.getSecond().getSecond().doubleValue(), 0.000001d);
	}
	
	@Test
	public void boundsNeg() {
		TileHelper h = TileHelper.getInstance();
		BigDecimal latRange = new BigDecimal(1d);
		BigDecimal lonRange = new BigDecimal(1d);		
		h.setRange(latRange,lonRange);
		
		Pair<Long,Long> tileId = new Pair<Long,Long>(-1l,-1l);
		Pair<Pair<BigDecimal, BigDecimal>, Pair<BigDecimal, BigDecimal>> bounds = h.getBounds(tileId);
		assertEquals(-1.0d, bounds.getFirst().getFirst().doubleValue(), 0.000001d);
		assertEquals(-1.0d, bounds.getFirst().getSecond().doubleValue(), 0.000001d);
		assertEquals(0.0d, bounds.getSecond().getFirst().doubleValue(), 0.000001d);
		assertEquals(0.0d, bounds.getSecond().getSecond().doubleValue(), 0.000001d);
	}
	
	@Test
	public void boundsComp() {
		TileHelper h = TileHelper.getInstance();
		BigDecimal latRange = new BigDecimal(0.12d);
		BigDecimal lonRange = new BigDecimal(0.12d);		
		h.setRange(latRange,lonRange);
		
		Pair<Long,Long> tileId = new Pair<Long,Long>(1l,-1l);
		Pair<Pair<BigDecimal, BigDecimal>, Pair<BigDecimal, BigDecimal>> bounds = h.getBounds(tileId);
		assertEquals(0.12d, bounds.getFirst().getFirst().doubleValue(), 0.000001d);
		assertEquals(-0.12d, bounds.getFirst().getSecond().doubleValue(), 0.000001d);
		assertEquals(0.24d, bounds.getSecond().getFirst().doubleValue(), 0.000001d);
		assertEquals(0.0d, bounds.getSecond().getSecond().doubleValue(), 0.000001d);
	}
	
	@Test
	public void negList() {
		TileHelper h = TileHelper.getInstance();
		BigDecimal latRange = new BigDecimal(1d);
		BigDecimal lonRange = new BigDecimal(1d);
		
		h.setRange(latRange,lonRange);
		BigDecimal lat = new BigDecimal(0d);
		BigDecimal lon = new BigDecimal(-0.01f);
		Pair<Long,Long> id = h.getTileId(lat, lon);
		System.out.println("id: "+id);	
		System.out.println(Math.floor(-1d/1d));
	}
	
	@Test
	public void boundsList() {
		TileHelper h = TileHelper.getInstance();
		BigDecimal latRange = new BigDecimal(0.1d);
		BigDecimal lonRange = new BigDecimal(0.1d);		
		h.setRange(latRange,lonRange);
		
		BigDecimal latFrom = new BigDecimal(36.4717d);
		BigDecimal lonFrom = new BigDecimal(-4.4611231d);
		BigDecimal latTo = new BigDecimal(36.8635d);
		BigDecimal lonTo = new BigDecimal(-4.4611231d);
		List<Pair<Long, Long>> l = h.getListTileId(latFrom, lonFrom, latTo, lonTo);
		for(Pair<Long,Long> p : l ) {
			System.out.println(p.toString());
		}
	}
}
