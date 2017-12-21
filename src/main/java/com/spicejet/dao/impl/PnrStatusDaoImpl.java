package com.spicejet.dao.impl;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.spicejet.dao.PnrStatusDao;
import com.spicejet.dto.Status;

@Transactional
@Repository
public class PnrStatusDaoImpl implements PnrStatusDao {

	@Autowired
	SessionFactory sessionfactory;
	@Override
	public void savePnrStatus(Status status) {
		sessionfactory.getCurrentSession().save(status);
		
	}

}
