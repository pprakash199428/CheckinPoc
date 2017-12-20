package com.spicejet.service.impl;

import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.spicejet.dto.BookingDetailDto;
import com.spicejet.kiosk.webservices.bookingManager.BookingManagerStub;
import com.spicejet.kiosk.webservices.bookingManager.BookingManagerStub.Booking;
import com.spicejet.kiosk.webservices.bookingManager.BookingManagerStub.BookingQueueInfo;
import com.spicejet.kiosk.webservices.bookingManager.BookingManagerStub.BookingStatus;
import com.spicejet.kiosk.webservices.bookingManager.BookingManagerStub.Fare;
import com.spicejet.kiosk.webservices.bookingManager.BookingManagerStub.Journey;
import com.spicejet.kiosk.webservices.bookingManager.BookingManagerStub.Passenger;
import com.spicejet.kiosk.webservices.bookingManager.BookingManagerStub.PaxSSR;
import com.spicejet.kiosk.webservices.bookingManager.BookingManagerStub.Segment;
import com.spicejet.service.inter.BusinessRuleConfigurator;

@Service
public class BusinessRuleConfiguratorImpl implements BusinessRuleConfigurator {

	@Autowired
	Environment env;

	Logger log = Logger.getLogger(BusinessRuleConfiguratorImpl.class);

	private BookingDetailDto bookingDetailDto;

	@Override
	public BusinessRuleConfigurator initBookingDetailDto(BookingDetailDto bookingDetailDto) {
		this.bookingDetailDto = bookingDetailDto;
		this.bookingDetailDto.setValidBooking(true);
		this.bookingDetailDto.setCheckInAllowed(true);
		return this;
	}

	@Override
	public BookingDetailDto applyRules(Booking booking) {
		log.info(".....Applying Business Rules.....");
		boolean isValidBooking = true;
		BookingManagerStub.ArrayOfPassenger arrayOfPassenger = booking.getPassengers();
		BookingManagerStub.ArrayOfJourney arrayOfJourney = booking.getJourneys();
		if (((arrayOfPassenger != null) && (arrayOfPassenger.getPassenger() != null))
				&& ((arrayOfJourney != null) && (arrayOfJourney.getJourney() != null))) {

			List<Journey> journeyList = Arrays.asList(arrayOfJourney.getJourney());
			List<Passenger> passengerList = Arrays.asList(arrayOfPassenger.getPassenger());

            if ((!passengerList.isEmpty()) && (!journeyList.isEmpty())) {
            	/* check for fraud */
				isValidBooking = !applyFraudRules(booking);

				/* check for booking status */
				if (isValidBooking) {
					isValidBooking = applyBookingStatusRules(booking);
				}

				/* check for international flight rule */
				if (isValidBooking) {
					isValidBooking = applyInternationFlightRules(booking);
				}

				/* check for pax count */
				if (isValidBooking) {
					isValidBooking = applyGroupCheckinRule(booking);
				}

				/* check for promo codes */
				if (isValidBooking) {
					isValidBooking = applyPromoCodeRules(booking);
				}

				/* check for valid ssr code */
				if (isValidBooking) {
					isValidBooking = applySSRRules(booking);
				}

				/* check for valid fare basis code */
				if (isValidBooking) {
					isValidBooking = applyFareBasisCodeRules(booking);
				}
			} else {
				isValidBooking = false;
				this.bookingDetailDto.setCheckInNotAllowedReason(env.getProperty("app.pnr.not.found.message"));
			}
		} else {
			isValidBooking = false;
			this.bookingDetailDto.setCheckInNotAllowedReason(env.getProperty("app.pnr.not.found.message"));
		}

		if (!isValidBooking) {
			this.bookingDetailDto.setValidBooking(isValidBooking);
		}
		return this.bookingDetailDto;

	}

	private boolean applyFraudRules(Booking booking) {
		boolean isFraudCase = false;
		BookingManagerStub.ArrayOfBookingQueueInfo arrayOfBookingQueueInfo = booking.getBookingQueueInfos();
		if (arrayOfBookingQueueInfo.getBookingQueueInfo() != null) {
			for (BookingQueueInfo bookingQueueInfo : arrayOfBookingQueueInfo.getBookingQueueInfo()) {
				if (Arrays.asList(env.getProperty("app.checkin.Fraud.code").split(",")).contains(bookingQueueInfo.getQueueCode())) {
					isFraudCase = true;
					break;
				}

			}
		}
		if (isFraudCase) {
			this.bookingDetailDto.setCheckInAllowed(false);
			this.bookingDetailDto.setCheckInNotAllowedReason(env.getProperty("app.checkin.Fraud.message"));
			log.info("Fraud Case Failed");
		}
		return isFraudCase;
	}

	private boolean applyInternationFlightRules(Booking booking) {
		boolean isFlightAllowed = true;
		BookingManagerStub.ArrayOfJourney arrayOfJourney = booking.getJourneys();
		for (BookingManagerStub.Journey journey : Arrays.asList(arrayOfJourney.getJourney())) {
			BookingManagerStub.ArrayOfSegment arrayOfSegment = journey.getSegments();
			if ((arrayOfSegment != null) && (arrayOfSegment.getSegment() != null)) {
				for (BookingManagerStub.Segment segment : Arrays.asList(arrayOfSegment.getSegment())) {
					if (segment.getInternational()) {
						isFlightAllowed = false;
						break;
					}
				}
			}
			if (!isFlightAllowed) {
				break;
			}
		}

		if (!isFlightAllowed) {
			this.bookingDetailDto.setCheckInAllowed(false);
			this.bookingDetailDto.setCheckInNotAllowedReason(env.getProperty("app.checkin.international.message"));
			log.info("International Flight Rule Failed");
		}

		return isFlightAllowed;
	}

	private boolean applySSRRules(Booking booking) {
		boolean isValidSSR = true;
		for (BookingManagerStub.Journey journey : Arrays.asList(booking.getJourneys().getJourney())) {
			BookingManagerStub.ArrayOfSegment arrayOfSegment = journey.getSegments();
			if ((arrayOfSegment != null) && (arrayOfSegment.getSegment() != null)) {
				for (BookingManagerStub.Segment segment : Arrays.asList(arrayOfSegment.getSegment())) {
					if (segment.getPaxSSRs() != null && segment.getPaxSSRs().getPaxSSR() != null) {
						for (PaxSSR paxSSR : Arrays.asList(segment.getPaxSSRs().getPaxSSR())) {
							isValidSSR = validateSSRForCheckIn(paxSSR.getSSRCode());
							if (!isValidSSR) {
								break;
							}
						}
					}
					if (!isValidSSR) {
						log.info("SSR Rule Failed");
						break;
					}
				}
			}
			if (!isValidSSR) {
				log.info("SSR Rule Failed");
				break;
			}

		}
		return isValidSSR;
	}

	private boolean validateSSRForCheckIn(String ssrCode) {
		boolean isValidCheckIn = true;
		List<String> nonAllowedSSRList = null;
		if (env.getProperty("app.kiosk.checkin.not.allowed.ssr") != null) {
			nonAllowedSSRList = Arrays.asList((env.getProperty("app.kiosk.checkin.not.allowed.ssr").split(",")));
			if (nonAllowedSSRList.contains(ssrCode)) {
				this.bookingDetailDto.setCheckInAllowed(false);
				this.bookingDetailDto.setCheckInNotAllowedReason(env.getProperty("app.checkin.not.allowed.message"));
				isValidCheckIn = false;
			}

		}
		if (isValidCheckIn && env.getProperty("app.kiosk.checkin.infant.not.allowed.ssr") != null) {
			nonAllowedSSRList = Arrays.asList((env.getProperty("app.kiosk.checkin.infant.not.allowed.ssr").split(",")));
			if (nonAllowedSSRList.contains(ssrCode)) {
				this.bookingDetailDto.setCheckInAllowed(false);
				this.bookingDetailDto.setCheckInNotAllowedReason(env.getProperty("app.checkin.infant.message"));
				isValidCheckIn = false;
			}
		}
		if (isValidCheckIn && env.getProperty("app.kiosk.checkin.unaccompanied.minor.not.allowed.ssr") != null) {
			nonAllowedSSRList = Arrays
					.asList((env.getProperty("app.kiosk.checkin.unaccompanied.minor.not.allowed.ssr").split(",")));
			if (nonAllowedSSRList.contains(ssrCode)) {
				this.bookingDetailDto.setCheckInAllowed(false);
				this.bookingDetailDto.setCheckInNotAllowedReason(env.getProperty("app.unaccompanied.minor.message"));
				isValidCheckIn = false;
			}
		}
		if (isValidCheckIn && env.getProperty("app.kiosk.checkin.excess.baggage.not.allowed.ssr") != null) {
			nonAllowedSSRList = Arrays.asList((env.getProperty("app.kiosk.checkin.excess.baggage.not.allowed.ssr").split(",")));
			if (nonAllowedSSRList.contains(ssrCode)) {
				this.bookingDetailDto.setCheckInAllowed(false);
				this.bookingDetailDto.setCheckInNotAllowedReason(env.getProperty("app.checkin.excess.baggage.message"));
				isValidCheckIn = false;
			}
		}
		if (isValidCheckIn && env.getProperty("app.kiosk.checkin.weapon.not.allowed.ssr") != null) {
			nonAllowedSSRList = Arrays.asList((env.getProperty("app.kiosk.checkin.weapon.not.allowed.ssr").split(",")));
			if (nonAllowedSSRList.contains(ssrCode)) {
				this.bookingDetailDto.setCheckInAllowed(false);
				this.bookingDetailDto.setCheckInNotAllowedReason(env.getProperty("app.checkin.weapon.message"));
				isValidCheckIn = false;
			}
		}

		return isValidCheckIn;
	}

	private boolean applyBookingStatusRules(Booking booking) {
		boolean isValidStatus = true;
		if ((booking.getBookingInfo() != null)) {
			BookingStatus bookingStatus = booking.getBookingInfo().getBookingStatus();
			if (bookingStatus != null && bookingStatus.getValue().equals(BookingStatus.Hold.getValue())) {
				isValidStatus = false;
				this.bookingDetailDto.setCheckInAllowed(false);
				this.bookingDetailDto.setCheckInNotAllowedReason(env.getProperty("app.checkin.hold.message"));
				log.info("Booking Status Rule Failed");
			}
		}
		return isValidStatus;
	}

	private boolean applyPromoCodeRules(Booking booking) {

		boolean isValidPromoCode = true;

		if ((booking.getTypeOfSale() != null)) {
			String bookingPromoCode = booking.getTypeOfSale().getPromotionCode();
			String defencePromoCode = env.getProperty("app.kiosk.checkin.defence.not.allowed.promocode");
			String studentPromocode = env.getProperty("app.kiosk.checkin.student.not.allowed.promocode");

			if (bookingPromoCode != null) {
				if (defencePromoCode != null && bookingPromoCode.equals(defencePromoCode)) {
					isValidPromoCode = false;
					this.bookingDetailDto.setCheckInAllowed(false);
					this.bookingDetailDto.setCheckInNotAllowedReason(env.getProperty("app.checkin.defence.message"));
					log.info("Promo Code Rule Failed");
				} else if (studentPromocode != null && bookingPromoCode.equals(studentPromocode)) {
					isValidPromoCode = false;
					this.bookingDetailDto.setCheckInAllowed(false);
					this.bookingDetailDto.setCheckInNotAllowedReason(env.getProperty("app.checkin.student.message"));
					log.info("Promo Code Rule Failed");
				}
			}
		}

		return isValidPromoCode;
	}

	private boolean applyFareBasisCodeRules(Booking booking) {
		boolean isValidFareBasisCode = true;
		if (String.valueOf(booking.getBookingSum().getBalanceDue()).equals("0")) {
			for (BookingManagerStub.Journey journey : Arrays.asList(booking.getJourneys().getJourney())) {
				BookingManagerStub.ArrayOfSegment arrayOfSegment = journey.getSegments();
				if (arrayOfSegment != null && arrayOfSegment.getSegment() != null) {
					for (BookingManagerStub.Segment segment : Arrays.asList(arrayOfSegment.getSegment())) {

						if ((segment.getFares() != null) && (segment.getFares().getFare() != null)
								&& (!Arrays.asList(segment.getFares().getFare()).isEmpty())) {
							isValidFareBasisCode = validateFareBasisCode(segment);
						}
						if (!isValidFareBasisCode) {
							log.info("Fare Basis Code Rule Failed");
							break;
						}
					}

				}
			}
		} else {
			this.bookingDetailDto.setCheckInAllowed(false);
			this.bookingDetailDto.setCheckInNotAllowedReason(env.getProperty("app.checkin.payment.due.message"));
			isValidFareBasisCode = false;
		}
		return isValidFareBasisCode;

	}

	private boolean validateFareBasisCode(Segment segment) {
		boolean isValidFareBasisCode = true;
		for (Fare fare : Arrays.asList(segment.getFares().getFare())) {
			List<String> notAllowdFreeBasisCodes = Arrays
					.asList(env.getProperty("app.kiosk.checkin.not.allowed.farebasiscode").split(","));
			if (notAllowdFreeBasisCodes.contains(fare.getFareBasisCode())) {
				this.bookingDetailDto.setCheckInAllowed(false);
				this.bookingDetailDto.setCheckInNotAllowedReason(env.getProperty("app.checkin.farebasiscode"));
				isValidFareBasisCode = false;
				break;
			} else if (!segment.getActionStatusCode().contains(env.getProperty("app.checkin.sod.code"))) {
				this.bookingDetailDto.setCheckInAllowed(false);
				this.bookingDetailDto.setCheckInNotAllowedReason(env.getProperty("app.checkin.sod.message"));
				isValidFareBasisCode = false;
				break;
			}
		}
		for (Fare fare : Arrays.asList(segment.getFares().getFare())) {
			if (fare.getProductClass() != null
					&& fare.getProductClass().equalsIgnoreCase(env.getProperty("app.checkin.handbaggage.code"))) {
				this.bookingDetailDto.setHandBaggage(true);
				log.info("passanger with hand baggage found for PNR: " + bookingDetailDto.getPnr());
			}
		}
		return isValidFareBasisCode;
	}

	private boolean applyGroupCheckinRule(Booking booking) {
		boolean isValidPaxCount = true;
		/* default pax count allowed */
		short paxCountAllowed = 9;
		if (env.getProperty("app.group.checkin.allowed.pax.count") != null) {
			paxCountAllowed = Short.parseShort(env.getProperty("app.group.checkin.allowed.pax.count"));
		}
		if (booking.getPaxCount() > paxCountAllowed) {
			isValidPaxCount = false;
			this.bookingDetailDto.setCheckInAllowed(false);
			this.bookingDetailDto.setCheckInNotAllowedReason(env.getProperty("app.group.checkin.not.allowed.message"));
		}

		return isValidPaxCount;
	}
}
