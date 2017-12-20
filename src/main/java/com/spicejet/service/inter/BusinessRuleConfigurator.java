package com.spicejet.service.inter;

import com.spicejet.dto.BookingDetailDto;
import com.spicejet.kiosk.webservices.bookingManager.BookingManagerStub.Booking;

/**
 * Service to Apply Business Rules on Booking to validate if
 * Booking valid for Kiosk Checkin or not .
 *
 */
public interface BusinessRuleConfigurator {

    /**
     * Method will apply all the rules on the booking
     * step by step, and if on any step business rule breaks,
     * it sets valid booking as false in BookingDetailDto.
     *
     * @param booking - Booking element.
     * @return - BookingDetailDto @BookingDetailDto.
     */
	BookingDetailDto applyRules(Booking booking);

    /**
     * Initializes the BusinessRuleConfigurator.
     *
     * @param bookingDetailDto - BookingDetailDto.
     * @return - Object for BusinessRuleConfigurator.
     */

	BusinessRuleConfigurator initBookingDetailDto(BookingDetailDto bookingDetailDto);

}
