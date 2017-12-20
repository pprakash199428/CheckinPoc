package com.spicejet.controller;

import java.rmi.RemoteException;
import java.util.Date;

import org.apache.axis2.AxisFault;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.spicejet.dto.AuthenticationResponseDto;
import com.spicejet.dto.BookingDetailDto;
import com.spicejet.resources.BookingManagerResource;
import com.spicejet.resources.OperationManagerResource;
import com.spicejet.resources.SessionManagerResource;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
public class TwitterCheckinController {

	@Autowired
	BookingManagerResource bookingManagerResource;

	@Autowired
	SessionManagerResource sessionManagerResources;

	@Autowired
	OperationManagerResource operationManagerResources;

	static AuthenticationResponseDto authenticationResponseDto;

	@RequestMapping(value = "/checkin", method = RequestMethod.GET)
	String CheckinPax(AuthenticationResponseDto pnr) {
		
		new Runnable() {
			public void run() {
				BookingDetailDto bookingDetailDto = null;
				try {
					if (authenticationResponseDto == null) {
						authenticationResponseDto = sessionManagerResources.logon();
					}
					bookingDetailDto = bookingManagerResource.getBookingDetails(authenticationResponseDto.getSignature(), pnr,
							new Date().toString());
					operationManagerResources.checkIn(authenticationResponseDto.getSignature(), pnr, bookingDetailDto);

				} catch (AxisFault e) {
					e.printStackTrace();
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		}.run();
		return "Success";
	}
	@RequestMapping(value = "/test", method = RequestMethod.GET)
	String TestApi(String pnr) {
		return "OK";
	}
}
