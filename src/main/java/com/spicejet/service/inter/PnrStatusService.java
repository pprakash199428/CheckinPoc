package com.spicejet.service.inter;

import com.spicejet.dto.Status;

public interface PnrStatusService {
	
	void savePnrStatus(String status);
	void updatePnrStatus(String pnr, String status,String reason);
	
}
