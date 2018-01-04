package com.spicejet.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.itextpdf.text.DocumentException;
import com.spicejet.dto.BoardingPass;
import com.spicejet.dto.BoardingPassDataBean;
import com.spicejet.service.impl.AbstractReportingService;
import com.spicejet.service.inter.CheckinUtilService;
import com.spicejet.service.inter.JasperGeneratorService;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.export.ExporterInput;
import net.sf.jasperreports.export.OutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.view.JasperViewer;

@Service
public class JasperGeneratorServiceImpl extends AbstractReportingService implements JasperGeneratorService {

	@Autowired
	Environment env;

	@Autowired
	CheckinUtilService checkinUtilService;

	Logger log = Logger.getLogger(JasperGeneratorServiceImpl.class);

	public void replaceAndCreatePdf(List<BoardingPass> boardingPasses)
			throws JRException, IOException, DocumentException {
		List<String> boardingPassFileName = new ArrayList<String>();
		String directoryPath = env.getProperty("app.save.boarding.pass");
		File file = new File(directoryPath);
		if (!file.exists()) {
			file.mkdir();
		}
		checkinUtilService.deleteTempFiles();
		log.info("Temp File Deleted");

		String boardingPassName = " ";
		String boardingPassStamped = " ";
		for (BoardingPass boardingPass : boardingPasses) {
			try {
				boardingPassName = "BoardingPass " + boardingPass.getFirstName() + " " + boardingPass.getLastName()
						+ " " + boardingPass.getDepartureStation();
				BoardingPassDataBean dataBean = extractBeanForBP(boardingPass);

				List<BoardingPassDataBean> jPrintbean = new ArrayList<BoardingPassDataBean>();
				jPrintbean.add(dataBean);
				List<JasperPrint> jPrintSinglePilot = new ArrayList<JasperPrint>();
				JasperPrint jprint = getJasperPrint("template/BoardingPass.jasper", new HashMap<>(),
						new JRBeanCollectionDataSource(jPrintbean));
				jPrintSinglePilot.add(jprint);
				System.out.println("JasperPrint Done");

				OutputStream outStream = new FileOutputStream(
						new File(env.getProperty("app.save.boarding.pass") + boardingPassName + ".pdf"));
				final JRPdfExporter exporter = new JRPdfExporter();
				final ExporterInput exporterInput = SimpleExporterInput.getInstance(jPrintSinglePilot);
				final OutputStreamExporterOutput outputStreamExporterOutput = new SimpleOutputStreamExporterOutput(
						outStream);
				exporter.setExporterInput(exporterInput);
				exporter.setExporterOutput(outputStreamExporterOutput);
				exporter.exportReport();
				outStream.flush();
				outStream.close();
				log.info("file regenerate");
				boardingPassStamped = boardingPassName+" Final"+ ".pdf" ;
				boardingPassFileName.add(boardingPassStamped);
				CreateAndStampBarcode cs = new CreateAndStampBarcode();
				cs.stampBarCode(boardingPass.getBarcodedString(), env.getProperty("app.save.boarding.pass") + boardingPassName+".pdf",
						env.getProperty("app.save.boarding.pass") + boardingPassStamped);

			} catch (FileNotFoundException e) {
				log.error("File Not Found :", e);
			}
		}
	}

	private BoardingPassDataBean extractBeanForBP(BoardingPass graph) {
		BoardingPassDataBean set = new BoardingPassDataBean();
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
		return set;
	}

}
