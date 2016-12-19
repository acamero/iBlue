package com.iblue.model.db.dao;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import org.hibernate.query.Query;

import com.iblue.model.SpotDAOInterface;
import com.iblue.model.SpotInterface;
import com.iblue.model.db.Spot;
import com.iblue.model.db.service.ParkingAlloc;

public class SpotDAO extends MasterDAO  implements SpotDAOInterface {

	

	public SpotDAO() {

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

		saveTx(spot);
		
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

		updateTx(spot);
		
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

		deleteTx(spot);
	}

	public Spot getSpot(long id) {
		openTx();
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
		openTx();
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
		openTx();
		Query<Spot> query = session.createQuery("from Spot", Spot.class);
		List<Spot> spots = query.getResultList();
		closeTx();
		return spots;
	}

	public List<Spot> findAllActive() {
		openTx();
		Query<Spot> query = session.createQuery("from Spot where status = :status", Spot.class);
		query.setParameter("status", 1);
		List<Spot> spots = query.getResultList();
		closeTx();
		return spots;
	}

	public List<Spot> findAllActive(long fromTime) {
		openTx();
		Query<Spot> query = session.createQuery("from Spot where status = :status and updateTs >= :ts", Spot.class);
		query.setParameter("status", 1);
		query.setParameter("ts", new Date(fromTime));
		List<Spot> spots = query.getResultList();
		closeTx();
		return spots;
	}

	public List<Spot> findAllRelease(long fromTime) {
		openTx();
		Query<Spot> query = session.createQuery("from Spot where status < :status and updateTs >= :ts", Spot.class);
		query.setParameter("status", 1);
		query.setParameter("ts", new Timestamp(fromTime));
		List<Spot> spots = query.getResultList();
		closeTx();
		return spots;
	}

}
