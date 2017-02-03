package com.iblue.model.partitioning;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.iblue.utils.Log;
import com.iblue.utils.Pair;

public class UniformPartitionTile implements PartitionTileInterface {

	protected static final int LAT_LON_SCALE = 7;
	private BigDecimal rangeLat;
	private BigDecimal rangeLon;

	protected UniformPartitionTile(BigDecimal latRange, BigDecimal lonRange) {
		rangeLat = latRange.setScale(LAT_LON_SCALE, BigDecimal.ROUND_HALF_DOWN);
		rangeLon = lonRange.setScale(LAT_LON_SCALE, BigDecimal.ROUND_HALF_DOWN);
	}


	/**
	 * Given a coordinate, returns the correspondent tile id
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
	 * Returns the latitude and longitude boundaries of the region covered by
	 * the given tile <Lat1,Lon1>,<Lat2,Lon2>
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

	/**
	 * Returns the list of tiles needed for computing the P2PSP from the two
	 * points (from and to)
	 * 
	 * @param latFrom
	 * @param lonFrom
	 * @param latTo
	 * @param lonTo
	 * @return
	 */
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
	 * Returns the list of tiles (ids) that partitions the region described
	 * by the input when partitioned by the actual size of the partition
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


	@Override
	public Pair<List<BigDecimal>, List<BigDecimal>> getRanges() {
		List<BigDecimal> latRanges = new ArrayList<BigDecimal>();
		List<BigDecimal> lonRanges = new ArrayList<BigDecimal>();
		latRanges.add(rangeLat);
		lonRanges.add(rangeLon);
		
		return new Pair<List<BigDecimal>,List<BigDecimal>>(latRanges,lonRanges);
	}
}
