package com.spicejet.service.inter;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import com.spicejet.dto.BookingDetailDto;
import com.spicejet.kiosk.webservices.bookingManager.BookingManagerStub.Booking;

public interface EmailService {
	
	void sendEmail(Booking booking,String errorMessage,boolean isSuccess,BookingDetailDto bookingDetailDto);

	void sendEmailAttachment(Booking booking, String string, boolean b, BookingDetailDto bookingDetailDto,
			List<String> boardingPassList) throws FileNotFoundException, IOException;

}
