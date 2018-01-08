package com.spicejet.service.impl;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.spicejet.dao.PnrStatusDao;
import com.spicejet.dto.Status;
import com.spicejet.service.inter.PnrStatusService;
import com.spicejet.util.Constants;

@Service
public class PnrStatusServiceImpl implements PnrStatusService {

	@Autowired
	PnrStatusDao pd;

	@Override
	public Status savePnrStatus(String pnr) {
		Status status = new Status();
		status = TwitC(pnr);
		pd.savePnrStatus(status);
		return status;
	}

	Status TwitC(String pnr) {
		Status status = new Status();
		Date currentdate = new Date();
		status.setPnr(pnr);
		status.setCreatedDate(currentdate);
		status.setStatus(Constants.INPROGRESS);
		status.setUserid("dummy user");
		return status;

	}

	@Override
	public void updatePnrStatus(String pnr, String status, String reason, Date createdDate) {
		Status pnrStatus = pd.fetchPnrStatus(pnr, Constants.INPROGRESS, createdDate);
		if (pnrStatus != null) {
			pnrStatus.setStatus(status);
			pnrStatus.setReason(reason);
			pnrStatus.setModifiedDate(new Date());
			pd.updatePnrStatus(pnrStatus);
		}

	}

}
