package com.spicejet.controller;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.spicejet.dao.PnrStatusDao;
import com.spicejet.dto.Status;
import com.spicejet.util.Constants;

@RestController
public class TwitController {
	
	@Autowired
	PnrStatusDao pd;

	@RequestMapping(value = "/savetwitterpnr", method = RequestMethod.POST)
	void TwitC(@RequestBody Status status) {
		Date currentdate = new Date();
		status.setCreatedDate(currentdate);
		status.setModifiedDate(currentdate);
		status.setStatus(Constants.INPROGRESS);
		pd.savePnrStatus(status);
		//pd.saveReason(status);

	}

}
