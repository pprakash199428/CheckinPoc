package com.spicejet.service.inter;

import com.spicejet.dto.BookingDetailDto;
import com.spicejet.kiosk.webservices.bookingManager.BookingManagerStub.Booking;

public interface MessageService {
	
	void sendMessage(Booking booking,String errorMessage,boolean isSuccess,BookingDetailDto bookingDetailDto);
	
}
