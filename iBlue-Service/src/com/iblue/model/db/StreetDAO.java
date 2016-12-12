package com.iblue.model.db;

import java.util.List;

import org.hibernate.query.Query;

import com.iblue.model.StreetDAOInterface;
import com.iblue.model.StreetInterface;

public class StreetDAO extends MasterDAO implements StreetDAOInterface {
	
	private StreetTypeDAO std;
	
	public StreetDAO() {
		std = new StreetTypeDAO();
	}

	public GeoStreet persist(StreetInterface streetI) {
		GeoStreet street;

		try {
			street = (GeoStreet) streetI;
		} catch (Exception e) {
			street = new GeoStreet();
			street.setFromIntersection(new Intersection(streetI.getLatitude1(),streetI.getLongitude1()));
			street.setToIntersection(new Intersection(streetI.getLatitude2(),street.getLongitude2()));
			street.setLanesBackward(streetI.getLanesBackward());
			street.setLanesForward(streetI.getLanesForward());
			street.setNumberOfLanes(streetI.getNumberOfLanes());
			street.setOneway(streetI.isOneway());
			street.setParkingCapacity(streetI.getParkingCapacity());
			street.setRoutable(streetI.isRoutable());
			street.setStatus(streetI.getStatus());			
			street.setStreetType(std.getStreetType(streetI.getStreetTypeId()));		
		}

		saveTx(street);

		return street;
	}

	public GeoStreet update(StreetInterface streetI) {
		if (streetI == null) {
			return null;
		}
		GeoStreet street;

		try {
			street = (GeoStreet) streetI;
		} catch (Exception e) {
			street = (GeoStreet) getStreet(streetI.getId());
			if (street == null) {
				return null;
			}
			Intersection tmp = street.getFromIntersection();
			tmp.setLatLong(streetI.getLatitude1(), streetI.getLongitude1());
			street.setFromIntersection(tmp);
			tmp = street.getToIntersection();
			tmp.setLatLong(streetI.getLatitude2(), streetI.getLongitude2());
			street.setToIntersection(tmp);
			
			street.setLanesBackward(streetI.getLanesBackward());
			street.setLanesForward(streetI.getLanesForward());
			street.setNumberOfLanes(streetI.getNumberOfLanes());
			street.setOneway(streetI.isOneway());
			street.setParkingCapacity(streetI.getParkingCapacity());
			street.setRoutable(streetI.isRoutable());
			street.setStatus(streetI.getStatus());			
			street.setStreetType(std.getStreetType(streetI.getStreetTypeId()));
		}

		updateTx(street);

		return street;
	}

	public void delete(StreetInterface streetI) {
		GeoStreet street = null;

		try {
			street = (GeoStreet) streetI;
		} catch (Exception e) {
			street = (GeoStreet) getStreet(streetI.getId());
		}

		if (street != null) {
			deleteTx(street);
		}

	}

	public StreetInterface getStreet(long id) {
		openTx();
		Query<GeoStreet> query = session.createQuery("from GeoStreet where id = :id", GeoStreet.class);
		query.setParameter("id", id);
		List<GeoStreet> streets = query.getResultList();
		closeTx();
		if (streets.isEmpty()) {
			return null;
		}
		return streets.get(0);
	}

	public List<GeoStreet> findAll() {
		openTx();
		Query<GeoStreet> query = session.createQuery("from GeoStreet", GeoStreet.class);
		List<GeoStreet> streets = query.getResultList();
		closeTx();
		return streets;
	}

	public List<GeoStreet> findAllActive() {
		openTx();
		Query<GeoStreet> query = session.createQuery("from GeoStreet where status = :status", GeoStreet.class);
		query.setParameter("status", 1);
		List<GeoStreet> streets = query.getResultList();
		closeTx();
		return streets;
	}

}
