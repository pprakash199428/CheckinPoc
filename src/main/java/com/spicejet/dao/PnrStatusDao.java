package com.spicejet.dao;

import com.spicejet.dto.Status;

public interface PnrStatusDao {
	
	void savePnrStatus(Status status);
	void updatePnrStatus(Status status);
	Status fetchPnrStatus(String pnr, String status);
	

}
