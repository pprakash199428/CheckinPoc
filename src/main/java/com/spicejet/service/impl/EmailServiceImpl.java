package com.spicejet.service.impl;

import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spicejet.kiosk.webservices.bookingManager.BookingManagerStub.Booking;
import com.spicejet.service.inter.EmailService;
import com.spicejet.util.EmailApiRequestBody;
import com.spicejet.util.RestHandler;

@Service
public class EmailServiceImpl implements EmailService {

	Logger log = Logger.getLogger(EmailServiceImpl.class);

	@Autowired
	Environment env;

	@Override
	public void sendEmail(Booking booking, String errorMessage, boolean isSuccess) {
		RestHandler restHandler = new RestHandler();
		HttpResponse response = null;
		EmailApiRequestBody emailApiRequestBody = new EmailApiRequestBody();
		String plainTextContent = " ";
		if (isSuccess) {
			plainTextContent = "Your CheckIn Request For PNR " + booking.getRecordLocator() + " Is SuccessFul";
		} else {
			plainTextContent = "Your CheckIn Request For PNR " + booking.getRecordLocator() + " Is Failed As "
					+ errorMessage;
		}

		String[] to = new String[1];
		to[0] = "pprakash@xebia.com";

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
