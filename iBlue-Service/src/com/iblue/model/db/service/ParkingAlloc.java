package com.iblue.model.db.service;

import java.math.BigInteger;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.iblue.model.ParkingAllocInterface;
import com.iblue.model.SpotInterface;
import com.iblue.model.StreetAvailabilityInterface;
import com.iblue.model.db.Spot;
import com.iblue.model.db.StreetAvailability;
import com.iblue.model.db.dao.SpotDAO;

public class ParkingAlloc implements ParkingAllocInterface {

	private Session session;

	private void open() {
		session = HibernateUtil.getSessionFactory().openSession();
		session.beginTransaction();
	}

	private void closeTx() {
		session.getTransaction().commit();
		session.close();
	}

	public long getNearestStreetId(SpotInterface spotI) {
		Spot spot;
		try {
			spot = (Spot) spotI;
		} catch (Exception e) {
			SpotDAO dao = new SpotDAO();
			spot = dao.getSpot(spotI.getId());
		}
		open();
		String queryString = "CALL " + DbSchema.DB_SCHEMA
				+ ".get_nearest_street(:latitude, :longitude, :northing, :easting)";
		@SuppressWarnings("rawtypes")
		Query query = session.createNativeQuery(queryString).setParameter("latitude", spot.getLatitude())
				.setParameter("longitude", spot.getLongitude()).setParameter("northing", spot.getNorthing())
				.setParameter("easting", spot.getEasting());
		Object[] res = null;
		try {
			res = (Object[]) query.getSingleResult();
		} catch (Exception e) {

		}
		closeTx();
		if (res != null && res.length >= 2) {
			System.out.println("Street id=" + res[0].toString() + " distance=" + res[1].toString());
			BigInteger id = (BigInteger)res[0];
			return id.longValue();
		}
		// if no street found
		return 0;
	}

	public List<StreetAvailability> getNearStreetAvailability(SpotInterface spot) {
		open();
		String queryString = "CALL " + DbSchema.DB_SCHEMA + ".get_near_area_map(:latitude, :longitude)";
		@SuppressWarnings("rawtypes")
		Query query = session.createNativeQuery(queryString, "AreaMapResultSet")
				.setParameter("latitude", spot.getLatitude()).setParameter("longitude", spot.getLongitude());
		@SuppressWarnings("unchecked")
		List<StreetAvailability> area = query.getResultList();
		closeTx();
		return area;
	}
	
	public StreetAvailabilityInterface parkMeClosest(SpotInterface spot) {
		open();
		String queryString = "CALL " + DbSchema.DB_SCHEMA + ".get_closest_parking(:latitude, :longitude)";
		@SuppressWarnings("rawtypes")
		Query query = session.createNativeQuery(queryString, "AreaMapResultSet")
				.setParameter("latitude", spot.getLatitude()).setParameter("longitude", spot.getLongitude());
		@SuppressWarnings("unchecked")
		List<StreetAvailability> area = query.getResultList();
		closeTx();
		if(area.isEmpty()) {
			return null;
		}
		return area.get(0);
	}

}
