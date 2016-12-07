package com.iblue.model.db;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.iblue.model.SpotDAOInterface;
import com.iblue.model.SpotInterface;

public class SpotDAO implements SpotDAOInterface {

	private Session session;

	public SpotDAO() {

	}

	private void open() {
		session = HibernateUtil.getSessionFactory().openSession();
		session.beginTransaction();
	}

	private void closeTx() {
		session.getTransaction().commit();
		session.close();
	}

	public Spot persist(SpotInterface spotI) {
		Spot spot;

		try {
			spot = (Spot) spotI;
		} catch (Exception e) {
			// in the case that the spotI is from another kind of class than
			// Spot
			spot = new Spot();
			spot.setLatLong(spotI.getLatitude(), spotI.getLongitude());
			spot.setMac(spotI.getMac());
			spot.setStatus(spotI.getStatus());
		}

		associateToStreet(spot);

		open();
		session.save(spot);
		closeTx();

		return spot;
	}

	private void associateToStreet(Spot spot) {
		ParkingAlloc alloc = new ParkingAlloc();
		spot.setStreetId(alloc.getNearestStreetId(spot));
	}

	public Spot update(SpotInterface spotI) {
		if (spotI == null) {
			return null;
		}

		Spot spot;

		try {
			spot = (Spot) spotI;
		} catch (Exception e) {
			// in the case that the spotI is from another kind of class than
			// Spot
			spot = getSpot(spotI.getId());
			if (spot == null) {
				return null;
			}

			// Latitude and Longitude are read only
			// spot.setLatLong(spotI.getLatitude(), spotI.getLongitude());
			// spot.setMac(spot.getMac());
			spot.setStatus(spotI.getStatus());
		}

		if (spot.getStreetId() == 0) {
			associateToStreet(spot);
		}

		open();
		session.update(spot);
		closeTx();

		return spot;
	}

	public void delete(SpotInterface spotI) {
		if (spotI == null) {
			return;
		}

		Spot spot;

		try {
			spot = (Spot) spotI;
		} catch (Exception e) {
			// in the case that the spotI is from another kind of class than
			// Spot
			spot = getSpot(spotI.getId());
			if (spot == null) {
				return;
			}
		}

		open();
		session.delete(spot);
		closeTx();
	}

	public Spot getSpot(int id) {
		open();
		Query<Spot> query = session.createQuery("from Spot where id = :id", Spot.class);
		query.setParameter("id", id);
		List<Spot> spots = query.getResultList();
		closeTx();
		if (spots.isEmpty()) {
			return null;
		}
		return spots.get(0);
	}

	public Spot getSpot(SpotInterface spotI) {
		open();
		Query<Spot> query = session.createQuery(
				"from Spot where latitude = :lat and longitude = :lon and status = 1 and mac = :mac", Spot.class);
		query.setParameter("lat", spotI.getLatitude().setScale(Spot.LAT_LON_SCALE, BigDecimal.ROUND_HALF_DOWN));
		query.setParameter("lon", spotI.getLongitude().setScale(Spot.LAT_LON_SCALE, BigDecimal.ROUND_HALF_DOWN));
		query.setParameter("mac", spotI.getMac());
		List<Spot> spots = query.getResultList();
		closeTx();
		if (spots.isEmpty()) {
			return null;
		}
		return spots.get(0);
	}

	public List<Spot> findAll() {
		open();
		Query<Spot> query = session.createQuery("from Spot", Spot.class);
		List<Spot> spots = query.getResultList();
		closeTx();
		return spots;
	}

	public List<Spot> findAllActive() {
		open();
		Query<Spot> query = session.createQuery("from Spot where status = :status", Spot.class);
		query.setParameter("status", 1);
		List<Spot> spots = query.getResultList();
		closeTx();
		return spots;
	}

	public List<Spot> findAllActive(long fromTime) {
		open();
		Query<Spot> query = session.createQuery("from Spot where status = :status and updateTs >= :ts", Spot.class);
		query.setParameter("status", 1);
		query.setParameter("ts", new Date(fromTime));
		List<Spot> spots = query.getResultList();
		closeTx();
		return spots;
	}

	public List<Spot> findAllRelease(long fromTime) {
		open();
		Query<Spot> query = session.createQuery("from Spot where status < :status and updateTs >= :ts", Spot.class);
		query.setParameter("status", 1);
		query.setParameter("ts", new Timestamp(fromTime));
		List<Spot> spots = query.getResultList();
		closeTx();
		return spots;
	}

}
