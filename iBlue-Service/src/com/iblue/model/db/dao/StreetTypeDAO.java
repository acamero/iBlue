package com.iblue.model.db.dao;

import java.util.List;

import org.hibernate.query.Query;

import com.iblue.model.db.StreetType;


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
