package com.spicejet.service.inter;

import com.spicejet.kiosk.webservices.bookingManager.BookingManagerStub.Booking;

public interface EmailService {
	
	void sendEmail(Booking booking,String errorMessage,boolean isSuccess);

}
