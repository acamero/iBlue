package com.iblue.model.db.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.iblue.model.db.TileRange;
import com.iblue.model.db.dao.TileRangeDAO;
import com.iblue.utils.Log;
import com.iblue.utils.Pair;

public class TileHelper {

	private static TileHelper INSTANCE = buildInstance();
	protected static final int LAT_LON_SCALE = 7;
	private BigDecimal rangeLat;
	private BigDecimal rangeLon;

	private static TileHelper buildInstance() {
		TileRangeDAO dao = new TileRangeDAO();
		TileRange range = dao.getTileRange();
		return new TileHelper(range.getLatitudeRange(),range.getLongitudeRange());
	}

	public static TileHelper getInstance() {
		return INSTANCE;
	}

	private TileHelper(BigDecimal latRange, BigDecimal lonRange) {
		setRange(latRange, lonRange);

	}

	public boolean setRange(BigDecimal latRange, BigDecimal lonRange) {
		BigDecimal tempLat = latRange.setScale(LAT_LON_SCALE, BigDecimal.ROUND_HALF_DOWN);
		BigDecimal tempLon = lonRange.setScale(LAT_LON_SCALE, BigDecimal.ROUND_HALF_DOWN);
		if(tempLat.equals(rangeLat) && tempLon.equals(rangeLon)) {
			return false;
		}
		rangeLat = latRange.setScale(LAT_LON_SCALE, BigDecimal.ROUND_HALF_DOWN);
		rangeLon = lonRange.setScale(LAT_LON_SCALE, BigDecimal.ROUND_HALF_DOWN);
		Log.debug("Range latitude=" + rangeLat + " longitude=" + rangeLon);
		
		TileRangeDAO dao = new TileRangeDAO();
		TileRange range = dao.getTileRange();
		range.setLatitudeRange(rangeLat);
		range.setLongitudeRange(rangeLon);
		dao.update(range);
		return true;
	}
	
	public Pair<BigDecimal,BigDecimal> getRange() {
		return new Pair<BigDecimal,BigDecimal>(rangeLat,rangeLon);
	}
	

	/**
	 * Given a coordinate, generate the correspondent tile id
	 * 
	 * @param lat
	 * @param lon
	 * @return
	 */
	public Pair<Long, Long> getTileId(BigDecimal lat, BigDecimal lon) {
		Long idLat = (long) Math.floor(lat.doubleValue() / rangeLat.doubleValue());
		Long idLon = (long) Math.floor(lon.doubleValue() / rangeLon.doubleValue());

		return new Pair<Long, Long>(idLat, idLon);
	}

	/**
	 * <Lat1,Lon1>,<Lat2,Lon2>
	 * 
	 * @param tileId
	 * @return
	 */
	public Pair<Pair<BigDecimal, BigDecimal>, Pair<BigDecimal, BigDecimal>> getBounds(Pair<Long, Long> tileId) {
		BigDecimal lat1 = new BigDecimal(tileId.getFirst()).multiply(rangeLat).setScale(LAT_LON_SCALE,
				BigDecimal.ROUND_HALF_DOWN);
		BigDecimal lon1 = new BigDecimal(tileId.getSecond()).multiply(rangeLon).setScale(LAT_LON_SCALE,
				BigDecimal.ROUND_HALF_DOWN);
		BigDecimal lat2 = lat1.add(rangeLat).setScale(LAT_LON_SCALE, BigDecimal.ROUND_HALF_DOWN);
		BigDecimal lon2 = lon1.add(rangeLon).setScale(LAT_LON_SCALE, BigDecimal.ROUND_HALF_DOWN);
		Pair<BigDecimal, BigDecimal> latLon1 = new Pair<BigDecimal, BigDecimal>(lat1, lon1);
		Pair<BigDecimal, BigDecimal> latLon2 = new Pair<BigDecimal, BigDecimal>(lat2, lon2);
		return new Pair<Pair<BigDecimal, BigDecimal>, Pair<BigDecimal, BigDecimal>>(latLon1, latLon2);
	}

	public List<Pair<Long, Long>> getListTileId(BigDecimal latFrom, BigDecimal lonFrom, BigDecimal latTo,
			BigDecimal lonTo) {
		Pair<BigDecimal, BigDecimal> latBounds = new Pair<BigDecimal, BigDecimal>(latFrom.min(latTo),
				latFrom.max(latTo));
		Pair<BigDecimal, BigDecimal> lonBounds = new Pair<BigDecimal, BigDecimal>(lonFrom.min(lonTo),
				lonFrom.max(lonTo));
		Log.debug("Lat bounds: " + latBounds + " \tLon bounds: " + lonBounds);
		return getBoundariesTileId(latBounds, lonBounds);
	}

	/**
	 * 
	 * @param latBounds
	 * @param lonBounds
	 * @return
	 */
	public List<Pair<Long, Long>> getBoundariesTileId(Pair<BigDecimal, BigDecimal> latBounds,
			Pair<BigDecimal, BigDecimal> lonBounds) {
		List<Pair<Long, Long>> tileIds = new ArrayList<Pair<Long, Long>>();

		int latSteps = 1 + (int) Math
				.abs((latBounds.getSecond().floatValue() - latBounds.getFirst().floatValue()) / rangeLat.floatValue());

		int lonSteps = 1 + (int) Math
				.abs((lonBounds.getSecond().floatValue() - lonBounds.getFirst().floatValue()) / rangeLon.floatValue());

		BigDecimal tempLat = latBounds.getFirst();
		BigDecimal tempLon = lonBounds.getFirst();
		Log.debug("Min lat: " + tempLat + " \tMin lon: " + tempLon);

		for (int i = 0; i <= latSteps; i++) {
			for (int j = 0; j <= lonSteps; j++) {
				Pair<Long, Long> p = getTileId(tempLat, tempLon);
				if (!tileIds.contains(p)) {
					tileIds.add(p);
				}
				tempLon = tempLon.add(rangeLon);
			}
			tempLat = tempLat.add(rangeLat);
			tempLon = lonBounds.getFirst();
		}

		return tileIds;
	}

	/*
	 * private int getNumberOfDecimalPlaces(BigDecimal bigDecimal) { String
	 * string = bigDecimal.stripTrailingZeros().toPlainString(); int index =
	 * string.indexOf("."); return index < 0 ? 0 : string.length() - index - 1;
	 * }
	 */

}
