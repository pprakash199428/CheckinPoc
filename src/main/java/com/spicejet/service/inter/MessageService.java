package com.spicejet.service.inter;

import com.spicejet.kiosk.webservices.bookingManager.BookingManagerStub.Booking;

public interface MessageService {
	
	void sendMessage(Booking booking,String errorMessage,boolean isSuccess);
	
}
