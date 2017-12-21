package com.spicejet.service.impl;

import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spicejet.kiosk.webservices.bookingManager.BookingManagerStub.Booking;
import com.spicejet.service.inter.MessageService;
import com.spicejet.util.MessageRequest;
import com.spicejet.util.RestHandler;

@Service
public class MessageServiceImpl implements MessageService {

	Logger log = Logger.getLogger(EmailServiceImpl.class);

	@Override
	public void sendMessage(Booking booking, String errorMessage, boolean isSuccess) {
		RestHandler restHandler = new RestHandler();
		HttpResponse response = null;
		MessageRequest messageRequest = new MessageRequest();

		String plainTextContent = " ";
		if (isSuccess) {
			plainTextContent = "Your CheckIn Request For PNR " + booking.getRecordLocator() + " Is SuccessFul";
		} else {
			plainTextContent = "Your CheckIn Request For PNR " + booking.getRecordLocator() + " Is Failed As "
					+ errorMessage;
		}
		String[] to = new String[1];
		to[0] = "8513866908";

		restHandler.setRequestType(RestHandler.RequestType.POST);
		log.info("URL : http://sg-azr-tom01-prod.centralindia.cloudapp.azure.com/netcore-api/sendMessage");
		restHandler
				.setUrl("http://sg-azr-tom01-prod.centralindia.cloudapp.azure.com/netcore-api/sendMessage");
		messageRequest.setMessageText(plainTextContent);
		messageRequest.setReceiver(to);
		ObjectMapper mapper = new ObjectMapper();
		try {
			String jsonInString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(messageRequest);
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
