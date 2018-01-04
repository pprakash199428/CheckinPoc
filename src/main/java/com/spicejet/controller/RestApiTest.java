package com.spicejet.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.itextpdf.text.DocumentException;
import com.spicejet.service.inter.JasperGeneratorService;

import net.sf.jasperreports.engine.JRException;

@RestController
public class RestApiTest {

	
	@Autowired
	JasperGeneratorService jasperGeneratorService;
	
	@RequestMapping(name = "/testDummyBP", method = RequestMethod.GET)
	void generateAndMailDummyBP(){
		try {
			jasperGeneratorService.replaceAndCreatePdf(null);
		} catch (JRException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
