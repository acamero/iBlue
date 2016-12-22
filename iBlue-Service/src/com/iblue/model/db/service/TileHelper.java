package com.iblue.model.db.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.iblue.utils.Pair;

public class TileHelper {

	private static BigDecimal MULTIPLY_BY = new BigDecimal(10f);
	private static BigDecimal ADD_TO_DECIMAL = new BigDecimal(0.1f);
	

	public static Pair<Long, Long> getTileId(BigDecimal lat, BigDecimal lon) {
		Long idLat = lat.multiply(MULTIPLY_BY).longValue();
		Long idLon = lon.multiply(MULTIPLY_BY).longValue();
		if(idLat<0l) {
			idLat = idLat - 1l;
		}
		
		if(idLon<0l) {
			idLon = idLon -1l;
		}
		return new Pair<Long, Long>(idLat, idLon);
	}

	/**
	 * <Lat1,Lon1>,<Lat2,Lon2>
	 * 
	 * @param tileId
	 * @return
	 */
	public static Pair<Pair<BigDecimal, BigDecimal>, Pair<BigDecimal, BigDecimal>> getBounds(Pair<Long, Long> tileId) {
		BigDecimal lat1 = new BigDecimal(tileId.getFirst()).divide(MULTIPLY_BY).setScale(2, BigDecimal.ROUND_HALF_DOWN);
		BigDecimal lon1 = new BigDecimal(tileId.getSecond()).divide(MULTIPLY_BY).setScale(2,
				BigDecimal.ROUND_HALF_DOWN);
		BigDecimal lat2 = lat1.add(ADD_TO_DECIMAL).setScale(2, BigDecimal.ROUND_HALF_DOWN);
		BigDecimal lon2 = lon1.add(ADD_TO_DECIMAL).setScale(2, BigDecimal.ROUND_HALF_DOWN);
		Pair<BigDecimal, BigDecimal> latLon1 = new Pair<BigDecimal, BigDecimal>(lat1, lon1);
		Pair<BigDecimal, BigDecimal> latLon2 = new Pair<BigDecimal, BigDecimal>(lat2, lon2);
		return new Pair<Pair<BigDecimal, BigDecimal>, Pair<BigDecimal, BigDecimal>>(latLon1, latLon2);
	}

	public static List<Pair<Long, Long>> getListTileId(BigDecimal latFrom, BigDecimal lonFrom, BigDecimal latTo,
			BigDecimal lonTo) {
		Pair<BigDecimal, BigDecimal> latBounds = new Pair<BigDecimal, BigDecimal>(latFrom.min(latTo),
				latFrom.max(latTo));
		Pair<BigDecimal, BigDecimal> lonBounds = new Pair<BigDecimal, BigDecimal>(lonFrom.min(lonTo),
				lonFrom.max(lonTo));
		return getBoundariesTileId(latBounds, lonBounds);
	}

	public static List<Pair<Long, Long>> getBoundariesTileId(Pair<BigDecimal, BigDecimal> latBounds,
			Pair<BigDecimal, BigDecimal> lonBounds) {
		List<Pair<Long, Long>> tileIds = new ArrayList<Pair<Long, Long>>();

		int latSteps = (int) ((latBounds.getSecond().floatValue() - latBounds.getFirst().floatValue())
				/ ADD_TO_DECIMAL.floatValue()) + 1;
		int lonSteps = (int) ((lonBounds.getSecond().floatValue() - lonBounds.getFirst().floatValue())
				/ ADD_TO_DECIMAL.floatValue()) + 1;

		BigDecimal tempLat = latBounds.getFirst();//.subtract(ADD_TO_DECIMAL);
		BigDecimal tempLon = lonBounds.getFirst();//.subtract(ADD_TO_DECIMAL);
		for (int i = 0; i <= latSteps; i++) {
			for (int j = 0; j <= lonSteps; j++) {
				Pair<Long, Long> p = getTileId(tempLat, tempLon);
				if (!tileIds.contains(p)) {
					tileIds.add(p);
					// System.out.println("Adding id" + p.getFirst()+" "+p.getSecond());
				}
				tempLon = tempLon.add(ADD_TO_DECIMAL);
			}
			tempLat = tempLat.add(ADD_TO_DECIMAL);
			tempLon = lonBounds.getFirst();
		}

		return tileIds;
	}

}
