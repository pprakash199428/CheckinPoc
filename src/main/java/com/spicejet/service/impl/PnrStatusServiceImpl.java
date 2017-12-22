package com.spicejet.service.impl;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import com.spicejet.dao.PnrStatusDao;
import com.spicejet.dto.Status;
import com.spicejet.service.inter.PnrStatusService;
import com.spicejet.util.Constants;

@Service
public class PnrStatusServiceImpl implements PnrStatusService {

	@Autowired
	PnrStatusDao pd;
	
	@Override
	public void savePnrStatus(String pnr) {
		pd.savePnrStatus(TwitC(pnr));
	}
	
	
	Status TwitC(String pnr) {
		Status status = new Status();
		Date currentdate = new Date();
		status.setPnr(pnr);
		status.setCreatedDate(currentdate);
		status.setModifiedDate(currentdate);
		status.setStatus(Constants.INPROGRESS);
		status.setUserid("dummy user");
		pd.savePnrStatus(status);
		
		return status;
		
	}


	@Override
	public void updatePnrStatus(String pnr,String status,String reason) {
		Status pnrStatus = pd.fetchPnrStatus(pnr,Constants.INPROGRESS);
		if(pnrStatus!=null){
			pnrStatus.setStatus(status);
			pnrStatus.setReason(reason);
			pd.updatePnrStatus(pnrStatus);
		}
		
	}

}
