package test.iblue.model.service;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.BeforeClass;
import org.junit.Test;

import com.iblue.model.Tile;
import com.iblue.model.db.TileContainer;
import com.iblue.model.db.TileRange;
import com.iblue.model.db.dao.TileDAO;
import com.iblue.model.db.dao.TileRangeDAO;
import com.iblue.model.db.service.TileHelper;
import com.iblue.model.db.service.TileService;
import com.iblue.utils.Log;
import com.iblue.utils.Pair;
import com.iblue.utils.Log.LogLevel;

public class TileServiceTest {
	private static BigDecimal lat, lon;
	private static Pair<Long, Long> tileId;
	private static BigDecimal latTo, lonTo;

	@BeforeClass
	public static void beforeClass() {
		Log.setLogLevel(LogLevel.DEBUG);
		lat = new BigDecimal(36.50f);
		lon = new BigDecimal(-4.75f);
		latTo = new BigDecimal(36.538671f);
		lonTo = new BigDecimal(-4.625363f);
		tileId = TileHelper.getInstance().getTileId(lat, lon);
		// System.out.println("Tile id:" + tileId.getFirst() + " " +
		// tileId.getSecond());
	}

	@Test
	public void buildTile() {
		TileService serv = new TileService();
		Tile tile = serv.buildTile(tileId);
		System.out.println("Size: " + tile.getAdjacencyMatrix().size());
		System.out.println("Intersections: " + tile.getIntersections().size());
		System.out.println("Weights: " + tile.getWeightsMatrix().size());

		TileDAO dao = new TileDAO();
		TileContainer tileCont = new TileContainer();
		tileCont.setTileId(tileId);
		tileCont.setTile(tile);
		dao.persist(tileCont);
		System.out.println("Byte size: " + tileCont.getByteTile().length);
	}

	@Test
	public void updateTile() {
		TileService serv = new TileService();
		Tile tile = serv.buildTile(tileId);
		System.out.println("Size: " + tile.getAdjacencyMatrix().size());
		System.out.println("Intersections: " + tile.getIntersections().size());
		System.out.println("Weights: " + tile.getWeightsMatrix().size());

		TileDAO dao = new TileDAO();
		TileContainer tileCont = new TileContainer();
		tileCont.setTileId(tileId);
		tileCont.setTile(tile);
		dao.update(tileCont);
		System.out.println("Byte size: " + humanReadableByteCount(tileCont.getByteTile().length, true));
	}

	@Test
	public void retrieveTile() {
		TileDAO dao = new TileDAO();
		TileContainer tileCont = dao.getTile(tileId);
		Tile tile = tileCont.getTile();
		System.out.println("Size: " + tile.getAdjacencyMatrix().size());
		System.out.println("Intersections: " + tile.getIntersections().size());
		System.out.println("Weights: " + tile.getWeightsMatrix().size());
		System.out.println("Byte size: " + humanReadableByteCount(tileCont.getByteTile().length, true));
	}

	@Test
	public void updateMap() {
		TileService serv = new TileService();
		serv.updateMap();
	}

	public static String humanReadableByteCount(long bytes, boolean si) {
		int unit = si ? 1000 : 1024;
		if (bytes < unit)
			return bytes + " B";
		int exp = (int) (Math.log(bytes) / Math.log(unit));
		String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + (si ? "" : "i");
		return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
	}

	@Test
	public void getTiles() {
		TileService serv = new TileService();
		Tile tile = serv.getTile(lat, lon, latTo, lonTo);
		System.out.println("Size: " + tile.getAdjacencyMatrix().size());
		System.out.println("Intersections: " + tile.getIntersections().size());
		System.out.println("Weights: " + tile.getWeightsMatrix().size());
		Map<Long, Float> m = tile.getWeightsMatrix().row(8166l);
		for (Entry<Long, Float> e : m.entrySet()) {
			System.out.println(e.getKey() + " " + e.getValue());
		}
	}

	@Test
	public void tileIds() {
		Pair<BigDecimal, BigDecimal> latBounds, lonBounds;
		latBounds = new Pair<BigDecimal, BigDecimal>(new BigDecimal(36.4717154f), new BigDecimal(36.8635631f));
		lonBounds = new Pair<BigDecimal, BigDecimal>(new BigDecimal(-4.990535504f), new BigDecimal(-4.0402347f));
		List<Pair<Long, Long>> ids = TileHelper.getInstance().getBoundariesTileId(latBounds, lonBounds);
		for (Pair<Long, Long> id : ids) {
			System.out.println("Id " + id.getFirst() + " " + id.getSecond());
		}
	}

	@Test
	public void computeMap05() {
		BigDecimal latRange = new BigDecimal(0.5d);
		BigDecimal lonRange = new BigDecimal(0.5d);
		TileService serv = new TileService();
		serv.computeMap(latRange, lonRange);
	}
	
	@Test
	public void noChange() {
		TileRangeDAO dao = new TileRangeDAO();
		TileRange range = dao.getTileRange();
		TileService serv = new TileService();
		assertEquals("No changes",serv.computeMap(range.getLatitudeRange(), range.getLongitudeRange()));
	}
	
	@Test
	public void computeMap01() {
		BigDecimal latRange = new BigDecimal(0.1d);
		BigDecimal lonRange = new BigDecimal(0.1d);
		TileService serv = new TileService();
		serv.computeMap(latRange, lonRange);
	}
	
	@Test
	public void computeMap() {
		BigDecimal latRange = new BigDecimal(1.1219258d);
		BigDecimal lonRange = new BigDecimal(4.9364468d);
		TileService serv = new TileService();
		serv.computeMap(latRange, lonRange);
	}

}
