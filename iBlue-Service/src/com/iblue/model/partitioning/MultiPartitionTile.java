package com.iblue.model.partitioning;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Range;
import com.iblue.utils.Log;
import com.iblue.utils.Pair;

public class MultiPartitionTile implements PartitionTileInterface {

	protected static final int LAT_LON_SCALE = 7;
	private Pair<List<BigDecimal>, List<BigDecimal>> ranges;
	private BigDecimal minLat;
	private BigDecimal minLon;
	// TODO this may be improved by using a binary tree
	private List<Range<BigDecimal>> latRanges;
	private List<Range<BigDecimal>> lonRanges;

	protected MultiPartitionTile(Pair<List<BigDecimal>, List<BigDecimal>> ranges,
			Pair<BigDecimal, BigDecimal> latBoundaries, Pair<BigDecimal, BigDecimal> lonBoundaries) {

		this.ranges = ranges;
		this.minLat = latBoundaries.getFirst().min(latBoundaries.getSecond());
		this.minLon = lonBoundaries.getFirst().min(lonBoundaries.getSecond());
		latRanges = new ArrayList<Range<BigDecimal>>();
		lonRanges = new ArrayList<Range<BigDecimal>>();

		// Generate latitude ranges
		BigDecimal mark = minLat.add(ranges.getFirst().get(0).setScale(LAT_LON_SCALE, BigDecimal.ROUND_HALF_DOWN));
		Range<BigDecimal> temp = Range.lessThan(mark);
		latRanges.add(temp);
		Log.debug("Lat range parittion added "+temp.toString());
		for (int i = 1; i < ranges.getFirst().size() ; i++) {
			temp = Range.closedOpen(mark,
					mark.add(ranges.getFirst().get(i).setScale(LAT_LON_SCALE, BigDecimal.ROUND_HALF_DOWN)));
			latRanges.add(temp);
			Log.debug("Lat range parittion added "+temp.toString());
			mark = mark.add(ranges.getFirst().get(i).setScale(LAT_LON_SCALE, BigDecimal.ROUND_HALF_DOWN));
		}

		temp = Range.atLeast(mark);
		latRanges.add(temp);
		Log.debug("Lat range parittion added "+temp.toString());

		// Generate latitude ranges
		mark = minLon.add(ranges.getSecond().get(0).setScale(LAT_LON_SCALE, BigDecimal.ROUND_HALF_DOWN));
		temp = Range.lessThan(mark);
		Log.debug("Lon range parittion added "+temp.toString());
		lonRanges.add(temp);
		for (int i = 1; i < ranges.getSecond().size(); i++) {
			temp = Range.closedOpen(mark,
					mark.add(ranges.getSecond().get(i).setScale(LAT_LON_SCALE, BigDecimal.ROUND_HALF_DOWN)));
			lonRanges.add(temp);
			Log.debug("Lat range parittion added "+temp.toString());
			mark = mark.add(ranges.getSecond().get(i).setScale(LAT_LON_SCALE, BigDecimal.ROUND_HALF_DOWN));
		}

		temp = Range.atLeast(mark);
		lonRanges.add(temp);
		Log.debug("Lat range parittion added "+temp.toString());

	}

	/**
	 * Returns the size of the partitions
	 */
	@Override
	public Pair<List<BigDecimal>, List<BigDecimal>> getRanges() {
		return ranges;
	}

	/**
	 * Given a coordinate, returns the correspondent tile id
	 */
	@Override
	public Pair<Long, Long> getTileId(BigDecimal lat, BigDecimal lon) {
		long latId = -1l, lonId = -1l;

		// TODO improve by using binary search
		for (int i = 0; i < latRanges.size(); i++) {
			if (latRanges.get(i).contains(lat)) {
				latId = (long) i;
			}
		}

		for (int i = 0; i < lonRanges.size(); i++) {
			if (lonRanges.get(i).contains(lon)) {
				lonId = (long) i;
			}
		}

		return new Pair<Long, Long>(latId, lonId);
	}

	/**
	 * Returns the latitude and longitude boundaries of the region covered by
	 * the given tile <Lat1,Lon1>,<Lat2,Lon2>
	 */
	@Override
	public Pair<Pair<BigDecimal, BigDecimal>, Pair<BigDecimal, BigDecimal>> getBounds(Pair<Long, Long> tileId) {
		BigDecimal lat1 = new BigDecimal(-90).setScale(LAT_LON_SCALE, BigDecimal.ROUND_HALF_DOWN);
		BigDecimal lat2 = new BigDecimal(90).setScale(LAT_LON_SCALE, BigDecimal.ROUND_HALF_DOWN);
		BigDecimal lon1 = new BigDecimal(-90).setScale(LAT_LON_SCALE, BigDecimal.ROUND_HALF_DOWN);
		BigDecimal lon2 = new BigDecimal(90).setScale(LAT_LON_SCALE, BigDecimal.ROUND_HALF_DOWN);

		Range<BigDecimal> temp = latRanges.get(tileId.getFirst().intValue());

		if (temp.hasLowerBound()) {
			lat1 = temp.lowerEndpoint();
		}

		if (temp.hasUpperBound()) {
			lat2 = temp.upperEndpoint();
		}

		temp = lonRanges.get(tileId.getSecond().intValue());

		if (temp.hasLowerBound()) {
			lon1 = temp.lowerEndpoint();
		}

		if (temp.hasUpperBound()) {
			lon2 = temp.upperEndpoint();
		}

		Pair<BigDecimal, BigDecimal> p1 = new Pair<BigDecimal, BigDecimal>(lat1, lon1);
		Pair<BigDecimal, BigDecimal> p2 = new Pair<BigDecimal, BigDecimal>(lat2, lon2);
		return new Pair<Pair<BigDecimal, BigDecimal>, Pair<BigDecimal, BigDecimal>>(p1, p2);
	}

	/**
	 * Returns the list of tiles needed for computing the P2PSP from the two
	 * points (from and to)
	 */
	@Override
	public List<Pair<Long, Long>> getListTileId(BigDecimal latFrom, BigDecimal lonFrom, BigDecimal latTo,
			BigDecimal lonTo) {
		List<Pair<Long, Long>> tiles = new ArrayList<Pair<Long, Long>>();
		Pair<Long, Long> fromTile = getTileId(latFrom, lonFrom);
		Pair<Long, Long> toTile = getTileId(latTo, lonTo);

		int minLat = fromTile.getFirst().intValue();
		int maxLat = toTile.getFirst().intValue();
		if (fromTile.getFirst() > toTile.getFirst()) {
			minLat = toTile.getFirst().intValue();
			maxLat = fromTile.getFirst().intValue();
		}
		
		int minLon = fromTile.getSecond().intValue();
		int maxLon= toTile.getSecond().intValue();
		if (fromTile.getSecond() > toTile.getSecond()) {
			minLon = toTile.getSecond().intValue();
			maxLon = fromTile.getSecond().intValue();
		}
		Log.debug("Latitude=["+minLat+","+maxLat+"]\t Longitude=["+minLon+","+maxLon+"]");
		for(int i=minLat;i<=maxLat;i++) {
			for(int j=minLon;j<=maxLon;j++) {
				tiles.add(new Pair<Long,Long>((long)i,(long)j));
			}
		}

		return tiles;
	}

	/**
	 * Returns the list of tiles (ids) that partitions the region described by
	 * the input when partitioned by the actual size of the partition
	 */
	@Override
	public List<Pair<Long, Long>> getBoundariesTileId(Pair<BigDecimal, BigDecimal> latBounds,
			Pair<BigDecimal, BigDecimal> lonBounds) {
		List<Pair<Long, Long>> tiles = new ArrayList<Pair<Long, Long>>();
		
		for(int i=0; i<latRanges.size();i++) {
			for(int j=0; j<lonRanges.size();j++) {
				tiles.add(new Pair<Long,Long>((long)i,(long)(j)));
			}
		}
		return tiles;
	}

}
