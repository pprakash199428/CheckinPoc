package com.spicejet.service.inter;

import java.util.Date;

import com.spicejet.dto.Status;

public interface PnrStatusService {
	
	Status savePnrStatus(String status);
	void updatePnrStatus(String pnr, String status,String reason, Date date);
	
}
