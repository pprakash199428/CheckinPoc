package com.spicejet.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.orm.jdo.support.StandardPersistenceManagerProxyBean;
import org.springframework.stereotype.Service;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BarcodePDF417;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfTemplate;
import com.spicejet.dto.BoardingPass;
import com.spicejet.dto.PassSetup;
import com.spicejet.service.impl.AbstractReportingService;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.export.ExporterInput;
import net.sf.jasperreports.export.OutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.view.JasperViewer;
import com.spicejet.controller.*;


@Service
public class JasperGeneratorService extends AbstractReportingService {

	@Autowired
	Environment env;
	
	public void replaceAndCreatePdf()throws JRException, IOException, DocumentException{

		String[] jasperList = { "BoardingPass" };
		Path path1 = FileSystems.getDefault().getPath(env.getProperty("app.save.boarding.pass"));
		
		try {
			Files.delete(path1);
		} catch (NoSuchFileException x) {
			System.err.println(path1 + "no exists");
		}
		System.out.print("file deleted");

		JasperGeneratorService generator = new JasperGeneratorService();
		String name=" ";
		// for (String str : jasperList) {
		/*
		 * try { JasperCompileManager.compileReportToFile(filePath +
		 * "BoardingPass.jrxml", filePath + "BoardingPass.jasper"); } catch
		 * (JRException e1) { // TODO Auto-generated catch block
		 * e1.printStackTrace(); }
		 */
		try {
			BoardingPass graph = new BoardingPass();
			graph.setFirstName("shubham" + " " + "");
			graph.setLastName("gupta");
			graph.setFlightNumber("SG161");
			graph.setArrivalDateTime(DateUtil.formatTimeForBoardingPass(Calendar.getInstance()));
			graph.setArrivalStation("mumbai");
			graph.setCarrierCode("adsf");
			graph.setArrivalStationAbbr("mum");
			graph.setBoardingDateTime(DateUtil.formatDateForBoardingPass(Calendar.getInstance()));
			graph.setSequenceNumber("12");
			List<String> ssrs = new ArrayList<String>();
			ssrs.add("WEB");
			ssrs.add("TEQ");
			graph.setSsrs(ssrs);
			graph.setUnitDesignator("7b");
			graph.setDepartureDateTime(DateUtil.formatTimeForBoardingPass(Calendar.getInstance()));
			graph.setDepartureStation("newdelhi");
			graph.setDepartureStationAbbr("ndls");
			graph.setGate("1");
			graph.setRecordLocator("x44lky");
			PassSetup set = new PassSetup();
			set.setPassengerName(graph.getFirstName() + " " + graph.getLastName());
			set.setFlight(graph.getFlightNumber() + " " + graph.getDepartureStation() + " " + "("
					+ graph.getDepartureStationAbbr() + ") TO" + graph.getArrivalStation() + " " + "("
					+ graph.getArrivalStationAbbr() + ")");
			set.setArrival(DateUtil.formatTimeForBoardingPass(Calendar.getInstance()) + ", "
					+ DateUtil.formatDateForBoardingPass(Calendar.getInstance()));
			set.setDepart(DateUtil.formatTimeForBoardingPass(Calendar.getInstance()) + ", "
					+ DateUtil.formatDateForBoardingPass(Calendar.getInstance()));
			set.setGate("VERIFY AT THE GATE");
			set.setGate1("Verify");
			set.setFlight1(graph.getFlightNumber() + " " + graph.getDepartureStationAbbr() + "-"
					+ graph.getArrivalStationAbbr() + " " + DateUtil.formatTimeForBoardingPass(Calendar.getInstance())
					+ ", " + DateUtil.formatDateForBoardingPass(Calendar.getInstance()));
			set.setPnr(graph.getRecordLocator());
			set.setSeat(graph.getUnitDesignator());
			set.setSeq(graph.getSequenceNumber());

			List<PassSetup> jPrintbean = new ArrayList<PassSetup>();
			jPrintbean.add(set);
			List<JasperPrint> jPrintSinglePilot = new ArrayList<JasperPrint>();
			JasperPrint jprint = generator.getJasperPrint(env.getProperty("app.save.boarding.pass")+"BoardingPass.jasper", new HashMap<>(),
					new JRBeanCollectionDataSource(jPrintbean));
			jPrintSinglePilot.add(jprint);
			System.out.println("JasperPrint Done");
			name = "boardingpass"+graph.getFirstName();
			JasperViewer jasperViewer = new JasperViewer(jprint);
			OutputStream outStream = new FileOutputStream(new File(env.getProperty("app.save.boarding.pass")+name + ".pdf"));
			final JRPdfExporter exporter = new JRPdfExporter();
			final ExporterInput exporterInput = SimpleExporterInput.getInstance(jPrintSinglePilot);
			final OutputStreamExporterOutput outputStreamExporterOutput = new SimpleOutputStreamExporterOutput(
					outStream);
			exporter.setExporterInput(exporterInput);
			exporter.setExporterOutput(outputStreamExporterOutput);
			exporter.exportReport();
			outStream.flush();
			outStream.close();

			jasperViewer.setVisible(true);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.print("file regenerate");
		CreateAndStampBarcode cs = new CreateAndStampBarcode();
		cs.stampBarCode("Hello Success", env.getProperty("app.save.boarding.pass")+name + ".pdf",env.getProperty("app.save.boarding.pass") + name+"Final" + ".pdf");
		

	}

	

	

	
	

}
