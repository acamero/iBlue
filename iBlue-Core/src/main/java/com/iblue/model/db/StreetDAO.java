package com.iblue.model.db;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.iblue.model.StreetDAOInterface;
import com.iblue.model.StreetInterface;

public class StreetDAO implements StreetDAOInterface {

	private Session session;

	private void open() {
		session = HibernateUtil.getSessionFactory().openSession();
		session.beginTransaction();
	}

	private void closeTx() {
		session.getTransaction().commit();
		session.close();
	}

		public Street persist(StreetInterface streetI) {
		Street street;

		try {
			street = (Street) streetI;
		} catch (Exception e) {
			street = new Street();
			street.setCapacity(streetI.getCapacity());
			street.setLatLong1(streetI.getLatitude1(), streetI.getLongitude1());
			street.setLatLong2(streetI.getLatitude2(), streetI.getLongitude2());
			street.setStatus(streetI.getStatus());
		}

		open();
		session.save(street);
		closeTx();
		
		return street;
	}

	
	public Street update(StreetInterface streetI) {
		if(streetI==null) {
			return null;
		}
		Street street;

		try {
			street = (Street) streetI;
		} catch (Exception e) {
			street = (Street) getStreet(streetI.getId());
			if(street==null) {
				return null;
			}
			street.setCapacity(streetI.getCapacity());
			street.setLatLong1(streetI.getLatitude1(), streetI.getLongitude1());
			street.setLatLong2(streetI.getLatitude2(), streetI.getLongitude2());
			street.setStatus(streetI.getStatus());
		}

		open();
		session.update(street);
		closeTx();
		
		return street;
	}

	
	public void delete(StreetInterface streetI) {
		Street street;

		try {
			street = (Street) streetI;
		} catch (Exception e) {
			street = (Street) getStreet(streetI.getId());
		}
		
		open();
		session.delete(street);
		closeTx();
	}

	
	public StreetInterface getStreet(int id) {
		open();
		Query<Street> query = session.createQuery("from Street where id = :id", Street.class);
		query.setParameter("id", id);
		List<Street> streets = query.getResultList();
		closeTx();
		if (streets.isEmpty()) {
			return null;
		}
		return streets.get(0);
	}

	
	public List<Street> findAll() {
		open();
		Query<Street> query = session.createQuery("from Street", Street.class);
		List<Street> streets = query.getResultList();
		closeTx();
		return streets;
	}

	
	public List<Street> findAllActive() {
		open();
		Query<Street> query = session.createQuery("from Street where status = :status", Street.class);
		query.setParameter("status", 1);
		List<Street> streets = query.getResultList();
		closeTx();
		return streets;
	}

}
