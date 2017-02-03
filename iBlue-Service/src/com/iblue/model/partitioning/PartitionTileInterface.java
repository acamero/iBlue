package com.iblue.model.partitioning;

import java.math.BigDecimal;
import java.util.List;

import com.iblue.utils.Pair;

public interface PartitionTileInterface {
	
	/**
	 * Returns the size of the partitions
	 */
	public Pair<List<BigDecimal>,List<BigDecimal>> getRanges();
	/**
	 * Given a coordinate, returns the correspondent tile id
	 */
	public Pair<Long, Long> getTileId(BigDecimal lat, BigDecimal lon);
	/**
	 * Returns the latitude and longitude boundaries of the region covered by
	 * the given tile <Lat1,Lon1>,<Lat2,Lon2>
	 */
	public Pair<Pair<BigDecimal, BigDecimal>, Pair<BigDecimal, BigDecimal>> getBounds(Pair<Long, Long> tileId);
	/**
	 * Returns the list of tiles needed for computing the P2PSP from the two
	 * points (from and to)
	 */
	public List<Pair<Long, Long>> getListTileId(BigDecimal latFrom, BigDecimal lonFrom, BigDecimal latTo,
			BigDecimal lonTo);
	/**
	 * Returns the list of tiles (ids) that partitions the region described
	 * by the input when partitioned by the actual size of the partition
	 */
	public List<Pair<Long, Long>> getBoundariesTileId(Pair<BigDecimal, BigDecimal> latBounds,
			Pair<BigDecimal, BigDecimal> lonBounds);
	
}
