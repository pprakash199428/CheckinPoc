package com.spicejet.controller;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.spicejet.dao.PnrStatusDao;
import com.spicejet.dto.Status;
import com.spicejet.util.Constants;

@Controller
public class TwitController {
	
	@Autowired
	PnrStatusDao pd;

	@RequestMapping(name = "/savetwitterpnr", method = RequestMethod.POST)
	void TwitC(@RequestBody Status status) {
		Date currentdate = new Date();
		status.setCreatedDate(currentdate.toString());
		status.setModifiedDate(currentdate.toString());
		status.setStatus(Constants.INPROGRESS);
		pd.savePnrStatus(status);

	}

}
