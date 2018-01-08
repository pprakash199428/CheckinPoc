package com.spicejet.dao.impl;

import java.util.Date;

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

	@Override
	public void updatePnrStatus(Status status) {
		sessionfactory.getCurrentSession().update(status);

	}

	@Override
	public Status fetchPnrStatus(String pnr, String statusText, Date createdDate) {

		return (Status) sessionfactory.getCurrentSession()
				.createQuery(" from Status where createdDate=modifiedDate and status = :statusText and pnr=:pnr")
				.setParameter("statusText", statusText).setParameter("pnr", pnr).list().get(0);

	}

}
