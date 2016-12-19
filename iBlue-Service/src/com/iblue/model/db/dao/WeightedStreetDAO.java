package com.iblue.model.db.dao;

import java.math.BigDecimal;
import java.util.List;

import org.hibernate.query.Query;

import com.iblue.model.SpotInterface;
import com.iblue.model.db.WeightedStreet;

public class WeightedStreetDAO extends MasterDAO {
	// 0.015 ~ 1.5km
	private static final BigDecimal DELTA = new BigDecimal(0.015f);

	public List<WeightedStreet> getGreatCircle(SpotInterface origin, SpotInterface destination) {
		BigDecimal meanLat = origin.getLatitude().subtract(destination.getLatitude()).abs();	
		BigDecimal meanLon = origin.getLongitude().subtract(destination.getLongitude()).abs();
		
		BigDecimal diffLat = origin.getLatitude().subtract(meanLat).abs();
		BigDecimal diffLon = origin.getLongitude().subtract(meanLon).abs();
		BigDecimal radius = diffLat.max(diffLon).add(DELTA);
		
		openTx();
		Query<WeightedStreet> query = session.createQuery(
				"from WeightedStreet where latitudeFrom between :lat1 and :lat2 and longitudeFrom between :lon1 and :lon2", WeightedStreet.class);
		query.setParameter("lat1", meanLat.subtract(radius));
		query.setParameter("lat2", meanLat.add(radius));
		query.setParameter("lon1", meanLon.subtract(radius));
		query.setParameter("lon2", meanLon.add(radius));
		List<WeightedStreet> streets = query.getResultList();
		closeTx();
		return streets;
	}
}
