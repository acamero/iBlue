package com.iblue.model.db.dao;

import org.hibernate.Session;

import com.iblue.model.db.service.HibernateUtil;

public abstract class MasterDAO {
	
	protected Session session;

	protected void openTx() {
		session = HibernateUtil.getSessionFactory().openSession();
		session.beginTransaction();
	}

	protected void closeTx() {
		session.getTransaction().commit();		
		session.close();
	}
	
	protected void saveTx(Object obj) {
		openTx();
		session.save(obj);
		closeTx();
	}
	
	protected void updateTx(Object obj) {
		openTx();
		session.update(obj);
		closeTx();
	}
	
	protected void deleteTx(Object obj) {
		openTx();
		session.delete(obj);
		closeTx();
	}
}
