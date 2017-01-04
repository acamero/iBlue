package com.iblue.model.db.dao;

import java.math.BigDecimal;
import java.util.List;

import org.hibernate.query.Query;

import com.iblue.model.StreetDAOInterface;
import com.iblue.model.SimpleStreetInterface;
import com.iblue.model.db.GeoStreet;
import com.iblue.model.db.Intersection;
import com.iblue.model.db.service.TileHelper;
import com.iblue.utils.Log;
import com.iblue.utils.Pair;

public class GeoStreetDAO extends MasterDAO implements StreetDAOInterface {

	private StreetTypeDAO std;

	public GeoStreetDAO() {
		std = new StreetTypeDAO();
	}

	public GeoStreet persist(SimpleStreetInterface streetI) {
		GeoStreet street;

		try {
			street = (GeoStreet) streetI;
		} catch (Exception e) {
			street = new GeoStreet();
			street.setFromIntersection(new Intersection(streetI.getLatitude1(), streetI.getLongitude1()));
			street.setToIntersection(new Intersection(streetI.getLatitude2(), street.getLongitude2()));
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

	public GeoStreet update(SimpleStreetInterface streetI) {
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

	public void delete(SimpleStreetInterface streetI) {
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

	public GeoStreet getStreet(long id) {
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

	public List<GeoStreet> getTileBounded(Pair<Long, Long> tileId) {
		openTx();
		Query<GeoStreet> query = session.createQuery(
				"from GeoStreet where status = 1 and routable = 1 and fromIntersection.latitude >= :lat1 and fromIntersection.latitude < :lat2 and fromIntersection.longitude >= :lon1 and fromIntersection.longitude < :lon2",
				GeoStreet.class);
		Pair<Pair<BigDecimal, BigDecimal>, Pair<BigDecimal, BigDecimal>> bounds = TileHelper.getInstance().getBounds(tileId);
		Log.debug(
				"Get GeoStreets lat>=" + bounds.getFirst().getFirst() + " and lat<" + bounds.getSecond().getFirst()
						+ " lon>=" + bounds.getFirst().getSecond() + " lon<" + bounds.getSecond().getSecond());
		query.setParameter("lat1", bounds.getFirst().getFirst());
		query.setParameter("lat2", bounds.getSecond().getFirst());
		query.setParameter("lon1", bounds.getFirst().getSecond());
		query.setParameter("lon2", bounds.getSecond().getSecond());
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

	public Pair<BigDecimal, BigDecimal> getLatitudeBoundaries() {
		Pair<BigDecimal, BigDecimal> bounds = null;
		openTx();
		@SuppressWarnings("rawtypes")
		Query query = session.createQuery("select min(latitude), max(latitude) from Intersection");
		Object[] res = null;
		try {
			res = (Object[]) query.getSingleResult();
			bounds = new Pair<BigDecimal, BigDecimal>((BigDecimal) res[0], (BigDecimal) res[1]);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bounds;
	}

	public Pair<BigDecimal, BigDecimal> getLongitudeBoundaries() {
		Pair<BigDecimal, BigDecimal> bounds = null;
		openTx();
		@SuppressWarnings("rawtypes")
		Query query = session.createQuery("select min(longitude), max(longitude) from Intersection");
		Object[] res = null;
		try {
			res = (Object[]) query.getSingleResult();
			bounds = new Pair<BigDecimal, BigDecimal>((BigDecimal) res[0], (BigDecimal) res[1]);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bounds;
	}

}
