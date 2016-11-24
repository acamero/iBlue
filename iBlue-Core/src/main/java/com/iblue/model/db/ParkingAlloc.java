package com.iblue.model.db;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.iblue.model.ParkingAllocInterface;
import com.iblue.model.SpotInterface;

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

	public int getNearestStreetId(SpotInterface spotI) {
		Spot spot;
		try {
			spot = (Spot) spotI;
		} catch (Exception e) {
			SpotDAO dao = new SpotDAO();
			spot = dao.getSpot(spotI.getId());
		}
		open();
		@SuppressWarnings("rawtypes")
		Query query = session.createNativeQuery("CALL iblue.nearest_street(:latitude, :longitude, :northing, :easting)")
				.setParameter("latitude", spot.getLatitude()).setParameter("longitude", spot.getLongitude())
				.setParameter("northing", spot.getNorthing()).setParameter("easting", spot.getEasting());
		Object[] res = null;
		try {
			res = (Object[]) query.getSingleResult();
		} catch (Exception e) {

		}
		closeTx();
		if (res != null && res.length >= 2) {
			System.out.println("Street id=" + res[0].toString() + " distance=" + res[1].toString());
			return (Integer) res[0];
		}
		// if no street found
		return 0;
	}

	public List<StreetAvailability> getNearStreetAvailability(SpotInterface spot) {
		open();
		@SuppressWarnings("rawtypes")
		Query query = session.createNativeQuery("CALL iblue.area_map(:latitude, :longitude)", "AreaMapResultSet")
				.setParameter("latitude", spot.getLatitude()).setParameter("longitude", spot.getLongitude());
		@SuppressWarnings("unchecked")
		List<StreetAvailability> area = query.getResultList();
		closeTx();
		return area;
	}

}
