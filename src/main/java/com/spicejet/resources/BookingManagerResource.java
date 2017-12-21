package com.spicejet.resources;

import java.rmi.RemoteException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import org.apache.axis2.AxisFault;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.spicejet.dto.BookingDetailDto;
import com.spicejet.dto.BookingDetailDtoBuilder;
import com.spicejet.dto.JourneyDetail;
import com.spicejet.dto.PassengerDetail;
import com.spicejet.dto.SeatAvailabilityResponseDto;
import com.spicejet.dto.SeatColumn;
import com.spicejet.dto.SeatRow;
import com.spicejet.kiosk.webservices.bookingManager.BookingManagerStub.Booking;
import com.spicejet.service.impl.BookingServiceImpl;
import com.spicejet.service.impl.BusinessRuleConfiguratorImpl;
import com.spicejet.service.inter.BusinessRuleConfigurator;
import com.spicejet.service.inter.IOperationManagerService;

/**
 * Resource to expose all the endpoints required by a client for all the booking
 * related APIs of Navitaire.
 *
 * To Contact : http://<hostname or ip>:<port>/session
 *
 * This is Secured Resource , a valid session token will be needed to contact
 * this resource.
 */

@Service
public class BookingManagerResource {

	private BookingServiceImpl bookingService;

	@Autowired
	Environment env;

	@Autowired
	public BookingManagerResource(BookingServiceImpl bookingService) {
		this.bookingService = bookingService;
	}

	@Autowired
	private BookingDetailDtoBuilder bookingDetailDtoBuilder;

	@Autowired
	private BusinessRuleConfigurator businessRuleConfigurator;

	@Autowired
	private IOperationManagerService operationManagerService;

	/**
	 * Endpoint to get a valid Booking from Navitaire against passed PNR.
	 *
	 * Consumes : "application/x-www-form-urlencoded" Produces :
	 * "application/xml" Method Type Supported: POST Path : "/getBooking"
	 *
	 * @param sign
	 *            - Signature which was obtained at the time of Logon from
	 *            BookingManagerResource @BookingManagerResource.
	 * @param pnr
	 *            - PNR for which we need to fetch bookings.
	 * @param currentDateTime
	 *            - Boooking will be fetched in the time frame of this passed
	 *            time.
	 * @return - BookingDetailDto @BookingDetailDto.
	 */

	Logger log = Logger.getLogger(BookingManagerResource.class);
	
	public BookingDetailDto getBookingDetails(String sign,String pnr,String currentDateTime,Booking booking) {

		log.info("Signature : " + sign);
		log.info("PNR : " + pnr);
		log.info("currentDateTime : " + currentDateTime);

		// Setting current time to business layer current time
		Calendar currentCalendar = Calendar.getInstance();

		// Setting current time to kiosk machine time.
		// calendar.setTimeInMillis(Long.valueOf(currentDateTime));

		log.info("Current Date Time : " + currentCalendar.get(Calendar.DAY_OF_MONTH) + "-"
				+ currentCalendar.get(Calendar.MONTH) + "-" + currentCalendar.get(Calendar.YEAR) + "-"
				+ currentCalendar.get(Calendar.HOUR) + "-" + currentCalendar.get(Calendar.MINUTE) + "-"
				+ currentCalendar.get(Calendar.SECOND));

		BookingDetailDto bookingDetailDto = new BookingDetailDto();
		bookingDetailDto.setPassengerDetails(new ArrayList<PassengerDetail>());
		bookingDetailDto.setJourneyDetails(new ArrayList<JourneyDetail>());
		try {
			if (booking != null && (booking.getJourneys() != null)) {
				bookingDetailDto = businessRuleConfigurator.initBookingDetailDto(bookingDetailDto).applyRules(booking);
				if (bookingDetailDto.isValidBooking()) {
					bookingDetailDtoBuilder.initBookingStub(booking).buildBasicBookingDetails().buildPassengerDetails()
							.buildJourneyDetails(currentCalendar).build(bookingDetailDto);

					if (!bookingDetailDto.getJourneyDetails().isEmpty()
							&& Boolean.valueOf(env.getProperty("app.auto.assign"))) {
						boolean autoSeatAssign = bookingService.autoSeatAssign(bookingDetailDto, sign, pnr);
						
						if (autoSeatAssign) {

							booking = bookingService.getBooking(sign, pnr);

							bookingDetailDtoBuilder.initBookingStub(booking).buildBasicBookingDetails()
									.buildPassengerDetails().buildJourneyDetails(currentCalendar)
									.build(bookingDetailDto);

						}
						
						boolean isValidSeatAssigned=true;
						
						if(autoSeatAssign){
							isValidSeatAssigned = validateAutoAssignedSeat(bookingDetailDto, sign);
						} else {
							isValidSeatAssigned = validateAssignedSeat(bookingDetailDto, sign);
						}
						
						if (!isValidSeatAssigned) {
							bookingDetailDto.setCheckInNotAllowedReason(env.getProperty("error.seat.full.message"));
							bookingDetailDto.setValidBooking(false);
							bookingDetailDto.setCheckInAllowed(false);
						} else {
							if (isRoundTrip(bookingDetailDto)) {
								if (!isTripOnSameDay(bookingDetailDto)) {
									updateSameDayJourney(bookingDetailDto.getJourneyDetails());
									updateSameDayPassengerDetail(bookingDetailDto.getPassengerDetails());
								}
							}
						}

					}
				}
			} else {
				bookingDetailDto.setCheckInNotAllowedReason(env.getProperty("app.invalid.pnr.message"));
				bookingDetailDto.setValidBooking(false);
				bookingDetailDto.setCheckInAllowed(true);
			}
		} catch (AxisFault axisFault) {
			bookingDetailDto.setCheckInNotAllowedReason(env.getProperty("app.axis.fault.message"));
			bookingDetailDto.setValidBooking(false);
			bookingDetailDto.setCheckInAllowed(false);
			log.error("Axis Fault", axisFault);
		} catch (Exception exception) {
			bookingDetailDto.setCheckInNotAllowedReason(env.getProperty("app.axis.fault.message"));
			bookingDetailDto.setValidBooking(false);
			bookingDetailDto.setCheckInAllowed(false);
			log.error("RemoteException", exception);
		}

		return bookingDetailDto;
	}

	private void updateSameDayPassengerDetail(List<PassengerDetail> passengerDetails) {
		for (PassengerDetail passengerDetail : passengerDetails) {
			log.info("Assigned Seats " + passengerDetail.getAssignedSeats());
			passengerDetail.getAssignedSeats().remove(passengerDetail.getAssignedSeats().size() - 1);
			passengerDetail.getLiftStatus().remove(passengerDetail.getLiftStatus().size() - 1);
		}

	}

	private void updateSameDayJourney(List<JourneyDetail> journeyDetails) {
		journeyDetails.remove(journeyDetails.size() - 1);

	}

	private boolean isRoundTrip(BookingDetailDto bookingDetailDto) {
		int noOfJourney = bookingDetailDto.getJourneyDetails().size();

		boolean roundTrip = false;

		if ((noOfJourney > 1) && bookingDetailDto.getJourneyDetails().get(0).getDepartureStation()
				.equals(bookingDetailDto.getJourneyDetails().get(noOfJourney - 1).getArrivalStation())) {

			roundTrip = true;
		}

		log.info(".............isRoundTrip : " + roundTrip);
		return roundTrip;

	}

	private boolean isTripOnSameDay(BookingDetailDto bookingDetailDto) {
		int noOfJourney = bookingDetailDto.getJourneyDetails().size();
		log.info("Number of journes ......................................: " + noOfJourney);
		boolean isOnSameDay = true;
		Calendar startJourneyDateTime = bookingDetailDto.getJourneyDetails().get(0).getDepartureDateTime();
		Calendar returnJourneyDateTime = bookingDetailDto.getJourneyDetails().get(noOfJourney - 1)
				.getDepartureDateTime();

		LocalDate startJouneyDate = LocalDate.of(startJourneyDateTime.get(Calendar.YEAR),
				startJourneyDateTime.get(Calendar.MONTH), startJourneyDateTime.get(Calendar.DAY_OF_MONTH));
		log.info("...........start Journey Date" + startJouneyDate);
		LocalDate returnJouneyDate = LocalDate.of(returnJourneyDateTime.get(Calendar.YEAR),
				returnJourneyDateTime.get(Calendar.MONTH), returnJourneyDateTime.get(Calendar.DAY_OF_MONTH));
		log.info("...........start Journey Date" + returnJouneyDate);

		if (!startJouneyDate.equals(returnJouneyDate)) {
			isOnSameDay = false;
		}

		log.info("......................isJourneyOnSameDay : " + isOnSameDay);
		return isOnSameDay;
	}

	private boolean validateAssignedSeat(BookingDetailDto bookingDetailDto, String sign) throws RemoteException {
		boolean isValidSeatAssigned = true;
		int journeyIndex = 0;
		for (JourneyDetail journeyDetail : bookingDetailDto.getJourneyDetails()) {
			SeatAvailabilityResponseDto availabilityResponseDto = operationManagerService.fetchSeatDetails(sign,
					String.valueOf(journeyDetail.getDepartureDateTime().getTime().getTime()),
					journeyDetail.getDepartureStation(), journeyDetail.getArrivalStation(),
					journeyDetail.getFlightNumber(), journeyDetail.getCarrierCode());

			isValidSeatAssigned = checkAssignedSeat(availabilityResponseDto, bookingDetailDto.getPassengerDetails(),
					journeyIndex);
			if (!isValidSeatAssigned) {
				break;
			}
			journeyIndex++;
		}

		/* un-assign seat if no valid seats are available */
		if (!isValidSeatAssigned) {
			operationManagerService.unAssign(sign, bookingDetailDto.getPnr(), bookingDetailDto);
		}
		return isValidSeatAssigned;
	}

	private boolean validateAutoAssignedSeat(BookingDetailDto bookingDetailDto, String sign) throws RemoteException {
		boolean isValidSeatAssigned = true;
		boolean flag=true;
		int journeyIndex = 0;
		
		for (JourneyDetail journeyDetail : bookingDetailDto.getJourneyDetails()) {
			SeatAvailabilityResponseDto availabilityResponseDto = operationManagerService.fetchSeatDetails(sign,
					String.valueOf(journeyDetail.getDepartureDateTime().getTime().getTime()),
					journeyDetail.getDepartureStation(), journeyDetail.getArrivalStation(),
					journeyDetail.getFlightNumber(), journeyDetail.getCarrierCode());

			isValidSeatAssigned = checkAutoAssignedSeat(availabilityResponseDto, bookingDetailDto.getPassengerDetails(),
					journeyIndex);
			if (!isValidSeatAssigned) {
				flag=false;
			}
			journeyIndex++;
		}

		/* un-assign seat if no valid seats are available */
		if (!flag) {
			operationManagerService.unAssign(sign, bookingDetailDto.getPnr(), bookingDetailDto);
		}
		
		return flag;
	}
	private boolean checkAssignedSeat(SeatAvailabilityResponseDto availabilityResponseDto,
			List<PassengerDetail> passengerDetails, int journeyIndex) {
		boolean isValidSeatAssigned = true;

		for (PassengerDetail passengerDetail : passengerDetails) {
			if (passengerDetail.getAssignedSeats().size() > journeyIndex) {
				for (SeatRow seatRow : availabilityResponseDto.getSeatRows()) {
					for (SeatColumn seatColumn : seatRow.getAvailableSeat()) {
						if ((seatRow.getGroupNumber() + seatColumn.getSeatName())
								.equals(passengerDetail.getAssignedSeats().get(journeyIndex))) {
							if (Arrays.asList(env.getProperty("not.available.seat.group").split(","))
									.contains(String.valueOf(seatColumn.getSeatGroup()))) {
								isValidSeatAssigned = false;
							}
						}
						if (!isValidSeatAssigned) {
							break;
						}
					}
					if (!isValidSeatAssigned) {
						break;
					}
				}
				if (!isValidSeatAssigned) {
					break;
				}
			}
		}

		return isValidSeatAssigned;
	}

	private boolean checkAutoAssignedSeat(SeatAvailabilityResponseDto availabilityResponseDto,
			List<PassengerDetail> passengerDetails, int journeyIndex) {
		boolean flag = true;
		for (PassengerDetail passengerDetail : passengerDetails) {
			if (passengerDetail.getAssignedSeats().size() <= journeyIndex)
				continue;
			
			for (SeatRow seatRow : availabilityResponseDto.getSeatRows()) {
				for (SeatColumn seatColumn : seatRow.getAvailableSeat()) {
					if ((seatRow.getGroupNumber() + seatColumn.getSeatName())
							.equals(passengerDetail.getAssignedSeats().get(journeyIndex))) {
						if (Arrays.asList(env.getProperty("not.available.seat.group").split(","))
								.contains(String.valueOf(seatColumn.getSeatGroup()))) {
							flag = false;
							passengerDetail.getAssignedSeats().set(journeyIndex,
									passengerDetail.getAssignedSeats().get(journeyIndex) + "/U");
						}
					}
				}
			}
		}

		return flag;
	}
}