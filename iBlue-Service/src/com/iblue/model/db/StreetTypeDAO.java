package com.iblue.model.db;

import java.util.List;

import org.hibernate.query.Query;


public class StreetTypeDAO extends MasterDAO {

	public StreetType getStreetType(long id) {
		openTx();
		Query<StreetType> query = session.createQuery("from StreetType where id = :id", StreetType.class);
		query.setParameter("id", id);
		List<StreetType> streets = query.getResultList();
		closeTx();
		if (streets.isEmpty()) {
			return null;
		}
		return streets.get(0);
	}
}
