package com.spicejet.service.inter;

import java.io.IOException;
import java.util.List;

import com.itextpdf.text.DocumentException;
import com.spicejet.dto.BoardingPass;

import net.sf.jasperreports.engine.JRException;

public interface JasperGeneratorService {
		
	public void replaceAndCreatePdf(List<BoardingPass> boardingPasses)throws JRException, IOException, DocumentException;
}
