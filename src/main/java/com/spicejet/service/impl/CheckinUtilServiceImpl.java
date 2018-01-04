package com.spicejet.service.impl;

import java.io.File;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.spicejet.service.inter.CheckinUtilService;

@Service
public class CheckinUtilServiceImpl implements CheckinUtilService{

	Logger log = Logger.getLogger(CheckinUtilServiceImpl.class);

	@Autowired
	Environment env;

	public void deleteTempFiles() {
		try {
			File file = new File(env.getProperty("app.save.boarding.pass"));
			File[] listOfFiles = file.listFiles();
			for (File dirFile : listOfFiles) {
				if (!dirFile.isDirectory()) {
					if (dirFile.delete())
						log.info(dirFile.getName() + " is deleted!");
					else
						log.info(dirFile.getName() + " is not able to deleted!");
				} else {
					log.info(dirFile.getName() + " is not able to deleted!");
				}
			}
		} catch (Exception e) {
			log.error("Error Deleting Temp File :" + e);
		}
	}
}
