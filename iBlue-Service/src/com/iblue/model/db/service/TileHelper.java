package com.iblue.model.db.service;

import java.math.BigDecimal;

import com.iblue.model.Pair;


public class TileHelper {

	
	private static BigDecimal HUNDRED = new BigDecimal(100);
	private static BigDecimal CENT = new BigDecimal(0.01f);
	
	
	public static Pair<Long,Long> getTileId(BigDecimal lat, BigDecimal lon) {
		lat.multiply(HUNDRED);
		lon.multiply(HUNDRED);
		return new Pair<Long,Long>(lat.longValue(), lon.longValue());		
	}
	
	/**
	 * <Lat1,Lon1>,<Lat2,Lon2>
	 * @param tileId
	 * @return
	 */
	public static Pair<Pair<BigDecimal,BigDecimal>,Pair<BigDecimal,BigDecimal>> getBounds(Pair<Long,Long> tileId) {
		BigDecimal lat1 = new BigDecimal(tileId.getFirst()).divide(HUNDRED).setScale(2,BigDecimal.ROUND_HALF_DOWN);
		BigDecimal lon1 = new BigDecimal(tileId.getSecond()).divide(HUNDRED).setScale(2,BigDecimal.ROUND_HALF_DOWN);
		BigDecimal lat2 = lat1.add(CENT).setScale(2,BigDecimal.ROUND_HALF_DOWN);
		BigDecimal lon2 = lon1.add(CENT).setScale(2,BigDecimal.ROUND_HALF_DOWN);
		Pair<BigDecimal,BigDecimal> latLon1 = new Pair<BigDecimal,BigDecimal>(lat1,lon1);
		Pair<BigDecimal,BigDecimal> latLon2 = new Pair<BigDecimal,BigDecimal>(lat2,lon2);
		return new Pair<Pair<BigDecimal,BigDecimal>,Pair<BigDecimal,BigDecimal>>(latLon1, latLon2);
	}

}
