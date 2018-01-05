package com.spicejet.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spicejet.dto.BookingDetailDto;
import com.spicejet.dto.PassengerDetail;
import com.spicejet.kiosk.webservices.bookingManager.BookingManagerStub.Booking;
import com.spicejet.service.inter.EmailService;
import com.spicejet.util.EmailApiRequestBody;
import com.spicejet.util.EmailContent;
import com.spicejet.util.RestHandler;

@Service
public class EmailServiceImpl implements EmailService {

	Logger log = Logger.getLogger(EmailServiceImpl.class);

	@Autowired
	Environment env;

	@Override
	public void sendEmail(Booking booking, String errorMessage, boolean isSuccess, BookingDetailDto bookingDetailDto) {
		RestHandler restHandler = new RestHandler();
		HttpResponse response = null;
		EmailApiRequestBody emailApiRequestBody = new EmailApiRequestBody();
		String plainTextContent = " ";
		String seatNumber = " ";
		String passengerName = " ";
		for (PassengerDetail detail : bookingDetailDto.getPassengerDetails()) {
			for (String seat : detail.getAssignedSeats()) {
				seatNumber = seatNumber + seat + " ";
			}
			seatNumber = seatNumber + "||";
			passengerName = passengerName + detail.getTitle() + " " + detail.getFirstName() + " " + detail.getLastName()
					+ "||";
		}
		if (isSuccess) {
			plainTextContent = "Your CheckIn Request For PNR " + booking.getRecordLocator() + " Is SuccessFul For "
					+ passengerName + ". Your Seat Number " + seatNumber;
		} else {
			plainTextContent = "Your CheckIn Request For PNR " + booking.getRecordLocator() + " Is Failed As "
					+ errorMessage;
		}

		String[] to = new String[1];
		to[0] = booking.getBookingContacts().getBookingContact()[0].getEmailAddress();

		restHandler.setRequestType(RestHandler.RequestType.POST);
		log.info("URL : http://sg-azr-tom01-prod.centralindia.cloudapp.azure.com/netcore-api/sendEmailNoAttachment");
		restHandler
				.setUrl("http://sg-azr-tom01-prod.centralindia.cloudapp.azure.com/netcore-api/sendEmailNoAttachment");

		emailApiRequestBody.setFrom("nikita.sharma2@spicejet.com");
		emailApiRequestBody.setSubject("Twitter Checkin");
		emailApiRequestBody.setTo(to);
		emailApiRequestBody.setApplicationKey("qwertyuiop");
		emailApiRequestBody.setPlainTextContent(plainTextContent);
		ObjectMapper mapper = new ObjectMapper();
		try {
			String jsonInString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(emailApiRequestBody);
			log.info(jsonInString);
			restHandler.setBodyContentMap(jsonInString);

			response = restHandler.sendRequest();
			String json = EntityUtils.toString(response.getEntity(), "UTF-8");
			log.info("Response Code getBooking : " + response.getStatusLine().getStatusCode());
			log.info("Response :" + json);
		} catch (Exception ex) {
			log.error("Unpredictable exception occurs ", ex);
		}

	}

	@Override
	public void sendEmailAttachment(Booking booking, String errorMessage, boolean isSuccess,
			BookingDetailDto bookingDetailDto, List<String> boardingPassList) throws IOException {
		RestHandler restHandler = new RestHandler();
		String directoryPath = env.getProperty("app.save.boarding.pass");
		HttpResponse response = null;
		EmailApiRequestBody emailApiRequestBody = new EmailApiRequestBody();
		String plainTextContent = " ";
		String seatNumber = " ";
		String passengerName = " ";
		List<EmailContent> emailContents = new ArrayList<EmailContent>();
		for (String fileName : boardingPassList) {
			byte[] bytesArray = null;
			EmailContent emailContent = new EmailContent();
			File file = new File(directoryPath + fileName);
			bytesArray = new byte[(int) file.length()];
			FileInputStream fis1 = new FileInputStream(file);
			fis1.read(bytesArray); // read file into bytes[]
			fis1.close();
			emailContent.setAttachment(bytesArray);
			emailContent.setFileName(fileName);
			emailContents.add(emailContent);
		}
		EmailContent[] emailContentList =  new EmailContent[emailContents.size()];
		int index=0;
		for(EmailContent content: emailContents){
			emailContentList[index]=content;
			index++;
		}
		for (PassengerDetail detail : bookingDetailDto.getPassengerDetails()) {
			for (String seat : detail.getAssignedSeats()) {
				seatNumber = seatNumber + seat + " ";
			}
			seatNumber = seatNumber + "||";
			passengerName = passengerName + detail.getTitle() + " " + detail.getFirstName() + " " + detail.getLastName()
					+ "||";
		}
		if (isSuccess) {
			plainTextContent = "Your CheckIn Request For PNR " + booking.getRecordLocator() + " Is SuccessFul For "
					+ passengerName + ". Your Seat Number " + seatNumber;
		} else {
			plainTextContent = "Your CheckIn Request For PNR " + booking.getRecordLocator() + " Is Failed As "
					+ errorMessage;
		}

		String[] to = new String[1];
		//to[0] = booking.getBookingContacts().getBookingContact()[0].getEmailAddress();
		to[0] = "pprakash@xebia.com";
		restHandler.setRequestType(RestHandler.RequestType.POST);
		log.info("URL : http://sg-azr-tom01-prod.centralindia.cloudapp.azure.com/netcore-api/sendEmail");
		restHandler.setUrl("http://sg-azr-tom01-prod.centralindia.cloudapp.azure.com/netcore-api/sendEmail");

		emailApiRequestBody.setFrom("nikita.sharma2@spicejet.com");
		emailApiRequestBody.setSubject("Twitter Checkin");
		emailApiRequestBody.setTo(to);
		emailApiRequestBody.setApplicationKey("qwertyuiop");
		emailApiRequestBody.setPlainTextContent(plainTextContent);
		emailApiRequestBody.setAttachmentContent(emailContentList);
		ObjectMapper mapper = new ObjectMapper();
		try {
			String jsonInString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(emailApiRequestBody);
			log.info(jsonInString);
			restHandler.setBodyContentMap(jsonInString);

			response = restHandler.sendRequest();
			String json = EntityUtils.toString(response.getEntity(), "UTF-8");
			log.info("Response Code getBooking : " + response.getStatusLine().getStatusCode());
			log.info("Response :" + json);
		} catch (Exception ex) {
			log.error("Unpredictable exception occurs ", ex);
		}

	}

}
