package com.spicejet.controller;

import java.rmi.RemoteException;
import java.util.Date;

import org.apache.axis2.AxisFault;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.spicejet.dao.PnrStatusDao;
import com.spicejet.dto.AuthenticationResponseDto;
import com.spicejet.dto.BookingDetailDto;
import com.spicejet.dto.ResponseDto;
import com.spicejet.dto.Status;
import com.spicejet.kiosk.webservices.bookingManager.BookingManagerStub.Booking;
import com.spicejet.resources.BookingManagerResource;
import com.spicejet.resources.OperationManagerResource;
import com.spicejet.resources.SessionManagerResource;
import com.spicejet.service.impl.BookingServiceImpl;
import com.spicejet.service.inter.EmailService;
import com.spicejet.service.inter.MessageService;
import com.spicejet.service.inter.PnrStatusService;
import com.spicejet.util.Constants;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
public class TwitterCheckinController {

	@Autowired
	BookingManagerResource bookingManagerResource;

	@Autowired
	SessionManagerResource sessionManagerResources;

	@Autowired
	OperationManagerResource operationManagerResources;

	@Autowired
	EmailService emailService;

	@Autowired
	MessageService messageService;

	@Autowired
	private BookingServiceImpl bookingService;
	
	@Autowired
	PnrStatusService pnrStatusService;

	

	private static AuthenticationResponseDto authenticationResponseDto;

	Logger log = Logger.getLogger(TwitterCheckinController.class);

	@RequestMapping(value = "/checkin", method = RequestMethod.GET)
	String CheckinPax(String pnr) {
		new Thread(() -> {
			pnrStatusService.savePnrStatus(pnr);
			BookingDetailDto bookingDetailDto = null;
			ResponseDto responseDto = null;
			try {
				if (authenticationResponseDto == null) {
					authenticationResponseDto = sessionManagerResources.logon();
				}
				Booking booking = bookingService.getBooking(authenticationResponseDto.getSignature(), pnr);
				bookingDetailDto = bookingManagerResource.getBookingDetails(authenticationResponseDto.getSignature(),
						pnr, new Date().toString(), booking);
				if (bookingDetailDto.isValidBooking()) {
					responseDto = operationManagerResources.checkIn(authenticationResponseDto.getSignature(), pnr,
							bookingDetailDto);
				} else {
					pnrStatusService.updatePnrStatus(pnr, Constants.FAILED, bookingDetailDto.getCheckInNotAllowedReason());
					emailService.sendEmail(booking, bookingDetailDto.getCheckInNotAllowedReason(), false,
							bookingDetailDto);
					messageService.sendMessage(booking, bookingDetailDto.getCheckInNotAllowedReason(), false,
							bookingDetailDto);
				}

				if (responseDto != null) {
					if (responseDto.isValidResponse()) {
						pnrStatusService.updatePnrStatus(pnr, Constants.SUCCESS,"Success");
						emailService.sendEmail(booking, " ", true, bookingDetailDto);
						messageService.sendMessage(booking, " ", true, bookingDetailDto);
					} else {
						pnrStatusService.updatePnrStatus(pnr, Constants.FAILED, responseDto.getErrorMessage());
						emailService.sendEmail(booking, responseDto.getErrorMessage(), false, bookingDetailDto);
						messageService.sendMessage(booking, bookingDetailDto.getCheckInNotAllowedReason(), false,
								bookingDetailDto);
					}
				}

			} catch (AxisFault e) {
				authenticationResponseDto = null;
				log.error("Axis Fault for PNR : ", e);
			} catch (RemoteException e) {
				authenticationResponseDto = null;
				log.error("RemoteException for PNR : ", e);
			}
		}).start();
		return "Success";
	}

	private void savePnrCheckinRequest(String pnr) {
		// TODO Auto-generated method stub
		
	}

	@RequestMapping(value = "/test", method = RequestMethod.GET)
	String TestApi(String pnr) {
		return "OK";
	}

}
