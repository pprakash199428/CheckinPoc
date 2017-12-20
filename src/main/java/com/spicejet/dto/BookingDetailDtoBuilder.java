package com.spicejet.dto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang.WordUtils;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;


import com.spicejet.kiosk.webservices.bookingManager.BookingManagerStub;
import com.spicejet.kiosk.webservices.bookingManager.BookingManagerStub.Booking;
import com.spicejet.kiosk.webservices.bookingManager.BookingManagerStub.Leg;
import com.spicejet.kiosk.webservices.bookingManager.BookingManagerStub.PaxSSR;
import com.spicejet.util.Constants;

/**
 * Builder to build Booking Detail.
 */
@Component
public class BookingDetailDtoBuilder {

	@Autowired
	Environment env;

	Logger log = Logger.getLogger(BookingDetailDtoBuilder.class);
	short passengerCount;

	boolean isValidBooking;
	boolean isCheckInAllowed;
	String checkInNotAllowedReason;
	String bookingId;
	String pnr;
	String currencyCode;
	List<Integer> notAvailableJourney; // = new ArrayList<>();
	List<PassengerDetail> passengerDetails;
	List<JourneyDetail> journeyDetails;
	BookingManagerStub.Booking booking;

	List<Boolean> checkinAllowedHourFlag; // = new ArrayList<Boolean>();

	List<JourneyListOfPnr> noOfJourneyListWithoutSegment;

	public List<JourneyListOfPnr> getNoOfJourneyListWithoutSegment() {
		return noOfJourneyListWithoutSegment;
	}

	public void setNoOfJourneyListWithoutSegment(List<JourneyListOfPnr> noOfJourneyListWithoutSegment) {
		this.noOfJourneyListWithoutSegment = noOfJourneyListWithoutSegment;
	}

	public BookingDetailDtoBuilder() {
		super();
	}

	public BookingDetailDtoBuilder initBookingStub(Booking booking) {
		this.booking = booking;
		return this;
	}

	/**
	 * Prepares Basic BookingDetailDtoBuilder object.
	 *
	 * @return - BookingDetailDtoBuilder @BookingDetailDtoBuilder
	 */
	public BookingDetailDtoBuilder buildBasicBookingDetails() {
		this.bookingId = String.valueOf(booking.getBookingID());
		this.pnr = booking.getRecordLocator();
		this.currencyCode = booking.getCurrencyCode();
		this.passengerCount = booking.getPaxCount();
		this.isValidBooking = true;
		this.isCheckInAllowed = true;
		this.checkInNotAllowedReason = null;
		return this;
	}

	/**
	 * Builds BookingDetailDto .
	 *
	 * @param bookingDetailDto
	 *            - BookingDetailDto @BookingDetailDto
	 * @return - Prepared BookingDetailDto Object.
	 */
	public BookingDetailDto build(BookingDetailDto bookingDetailDto) {
		return populateBookingDetailDto(bookingDetailDto, this);
	}

	private BookingDetailDto populateBookingDetailDto(BookingDetailDto bookingDetailDto,
			BookingDetailDtoBuilder bookingDetailDtoBuilder) {
		bookingDetailDto.setPnr(bookingDetailDtoBuilder.getPnr());
		bookingDetailDto.setPassengerCount(bookingDetailDtoBuilder.getPassengerCount());
		bookingDetailDto.setBookingId(bookingDetailDtoBuilder.getBookingId());
		bookingDetailDto.setPassengerDetails(bookingDetailDtoBuilder.getPassengerDetails());
		bookingDetailDto.setJourneyDetails(bookingDetailDtoBuilder.getJourneyDetails());
		bookingDetailDto.setValidBooking(bookingDetailDtoBuilder.isValidBooking);
		bookingDetailDto.setCheckInAllowed(bookingDetailDtoBuilder.isCheckInAllowed);
		bookingDetailDto.setCheckInNotAllowedReason(bookingDetailDtoBuilder.checkInNotAllowedReason);
		bookingDetailDto.setNoOfJourneyListWithoutSegment(bookingDetailDtoBuilder.noOfJourneyListWithoutSegment);
		return bookingDetailDto;
	}

	/**
	 * Build Passenger Details and Populates to BookingDetailDtoBuilder object.
	 *
	 * @return - BookingDetailDtoBuilder : Object populated with Passenger
	 *         Details.
	 */
	public BookingDetailDtoBuilder buildPassengerDetails() {
		List<PassengerDetail> passengerDetailList;
		/* Passenger Information */
		BookingManagerStub.ArrayOfPassenger arrayOfPassenger = booking.getPassengers();
		BookingManagerStub.ArrayOfJourney arrayOfJourney = booking.getJourneys();
		passengerDetailList = populatePassengerDetails(Arrays.asList(arrayOfPassenger.getPassenger()),
				Arrays.asList(arrayOfJourney.getJourney()));
		this.passengerDetails = passengerDetailList;
		return this;
	}

	private List<PassengerDetail> populatePassengerDetails(List<BookingManagerStub.Passenger> passengerList,
			List<BookingManagerStub.Journey> journeyList) {
		/* Passenger Information */
		List<PassengerDetail> passengerDetails = passengerList.stream().map(this::setPassengerDetails)
				.collect(Collectors.toList());

		passengerDetails.stream().filter(p -> Constants.ADULT_TYPE.equalsIgnoreCase(p.getPaxType())).findFirst()
				.ifPresent(p -> p.setPrimaryLeader(true));

		for (BookingManagerStub.Journey journey : journeyList) {
			BookingManagerStub.ArrayOfSegment arrayOfSegment = journey.getSegments();
			for (BookingManagerStub.Segment segment : Arrays.asList(arrayOfSegment.getSegment())) {
				// TODO validate segment w.r.t time
				populateSSRCode(passengerDetails, segment);
				populateUnitDesignator(passengerDetails, segment);
				populateLiftStatus(passengerDetails, segment);
			}

		}

		return passengerDetails;

	}

	private void populateLiftStatus(List<PassengerDetail> passengerDetails, BookingManagerStub.Segment segment) {
		if (segment.getPaxSegments() != null) {
			for (BookingManagerStub.PaxSegment paxSegment : Arrays.asList(segment.getPaxSegments().getPaxSegment())) {
				if (passengerDetails.get(paxSegment.getPassengerNumber()) != null) {
					List<Boolean> lift = passengerDetails.get(paxSegment.getPassengerNumber()).getLiftStatus();
					lift.add(paxSegment.getLiftStatus().getValue().equals(Constants.CHECKED_IN));

					passengerDetails.get(paxSegment.getPassengerNumber())
							.setSequenceNumber(paxSegment.getBoardingSequence());

				}
			}
		}
	}

	private void populateSSRCode(List<PassengerDetail> passengerDetails, BookingManagerStub.Segment segment) {
		if (segment.getPaxSSRs() != null && segment.getPaxSSRs().getPaxSSR() != null) {
			for (PaxSSR paxSSR : Arrays.asList(segment.getPaxSSRs().getPaxSSR())) {
				String ssrCode = paxSSR.getSSRCode();

				paxSSR.getPassengerNumber();
				PassengerDetail passengerDetail = passengerDetails.stream()
						.filter(p -> p.getPassengerNumber() == paxSSR.getPassengerNumber()).findAny().get();
				if (passengerDetail != null) {
					passengerDetail.getPaxSSRList().add(ssrCode);

				}
			}
		}

	}

	private void populateUnitDesignator(List<PassengerDetail> passengerDetails, BookingManagerStub.Segment segment) {
		if (segment.getPaxSeats() != null && (segment.getPaxSeats().getPaxSeat() != null)) {
			for (BookingManagerStub.PaxSeat paxSeat : Arrays.asList(segment.getPaxSeats().getPaxSeat())) {
				if (passengerDetails.get(paxSeat.getPassengerNumber()) != null) {
					List<String> assignedSeats = passengerDetails.get(paxSeat.getPassengerNumber()).getAssignedSeats();
					assignedSeats.add(paxSeat.getUnitDesignator());
					Map<String, String> passengerJourneyMap = passengerDetails.get(paxSeat.getPassengerNumber())
							.getPassengerJourney();
					passengerJourneyMap.put(segment.getDepartureStation() + "-" + segment.getArrivalStation(),
							paxSeat.getUnitDesignator());

				}
			}
		}
	}

	private PassengerDetail setPassengerDetails(BookingManagerStub.Passenger passenger) {
		List<BookingManagerStub.BookingName> bookingNames = Arrays.asList(passenger.getNames().getBookingName());

		Optional<PassengerDetail> passengerDetailOptional = bookingNames.stream().map(this::setBookingName)
				.collect(Collectors.toList()).stream().findFirst();

		if (passengerDetailOptional.isPresent()) {
			passengerDetailOptional.get().setPassengerId(passenger.getPassengerID());
			passengerDetailOptional.get().setPassengerNumber(passenger.getPassengerNumber());
		}

		Optional<PassengerDetail> passengerDetail = Arrays.asList(passenger.getPassengerTypeInfos()).stream()
				.map(passengerTypeInfo -> setPaxType(Arrays.asList(passengerTypeInfo.getPassengerTypeInfo()),
						passengerDetailOptional.get()))
				.collect(Collectors.toList()).stream().findFirst();
		return passengerDetail.get();
	}

	private PassengerDetail setPaxType(List<BookingManagerStub.PassengerTypeInfo> passengerTypeInfos,
			PassengerDetail passengerDetail) {
		BookingManagerStub.PassengerTypeInfo passengerTypeInfo = passengerTypeInfos.stream().findFirst().get();
		if (passengerTypeInfo.getPaxType() != null) {
			passengerDetail.setPaxType(Constants.ADULT_TYPE);
		}
		return passengerDetail;
	}

	private PassengerDetail setBookingName(BookingManagerStub.BookingName bookingName) {
		PassengerDetail passengerDetail = new PassengerDetail();
		passengerDetail.setFirstName(WordUtils.capitalizeFully(bookingName.getFirstName()));
		passengerDetail.setLastName(WordUtils.capitalizeFully(bookingName.getLastName()));
		passengerDetail.setMiddleName(WordUtils.capitalizeFully(bookingName.getMiddleName()));
		passengerDetail.setTitle(bookingName.getTitle());
		passengerDetail.setSuffix(bookingName.getSuffix());
		return passengerDetail;
	}

	/**
	 * Builds Journey Details.
	 *
	 * @param currentDateTime
	 *            - To check whether passed journey is valid for Check-in
	 *            Window.
	 * @return - BookingDetailDtoBuilder : Object with Journey Details
	 *         Populated.
	 */
	public BookingDetailDtoBuilder buildJourneyDetails(Calendar currentDateTime) {
		List<JourneyDetail> journeyDetailList = null;
		/* Journey Details */
		if ((this.passengerDetails != null) && (!this.passengerDetails.isEmpty())) {
			BookingManagerStub.ArrayOfJourney arrayOfJourney = booking.getJourneys();
			journeyDetailList = populateJourneyDetails(Arrays.asList(arrayOfJourney.getJourney()), currentDateTime);
		}
		if ((journeyDetailList != null) && (journeyDetailList.isEmpty())) {
			if (this.passengerDetails != null) {
				this.passengerDetails.clear();
			}
			if (checkinAllowedHourFlag.size() > 0) {
				if (!checkinAllowedHourFlag.contains(true)) {
					this.setValidBooking(false);
					this.bookingId = null;
					this.pnr = null;
					this.setCheckInAllowed(false);
					this.setCheckInNotAllowedReason(env.getProperty("app.check.in.allowed.hours.error.msg"));
				}
			}
			if (this.pnr != null) {
				this.passengerCount = 0;
				this.setValidBooking(false);
				this.bookingId = null;
				this.pnr = null;
				this.setCheckInAllowed(true);
				this.setCheckInNotAllowedReason(env.getProperty("app.pnr.not.found.message"));
			}

		} else {
			updatePassengerDetails(journeyDetailList, this.passengerDetails);
		}
		this.journeyDetails = journeyDetailList;
		return this;
	}

	

	private void updatePassengerDetails(List<JourneyDetail> journeyDetailList, List<PassengerDetail> passengerDetails) {
		for (PassengerDetail passengerDetail : passengerDetails) {

			int noOfJourney = journeyDetailList.size();

			int noOfLiftStatus = 0;
			if (passengerDetail.getLiftStatus() != null)
				noOfLiftStatus = passengerDetail.getLiftStatus().size();

			int noOfAssignedSeats = 0;
			if (passengerDetail.getAssignedSeats() != null)
				noOfAssignedSeats = passengerDetail.getAssignedSeats().size();

			int noOfSSR = 0;
			if (passengerDetail.getPaxSSRList() != null)
				noOfSSR = passengerDetail.getPaxSSRList().size();

			java.util.Collections.sort(notAvailableJourney, java.util.Collections.reverseOrder());
			if (noOfLiftStatus > 0 && noOfLiftStatus > noOfJourney) {
				for (int index : notAvailableJourney) {
					passengerDetail.getLiftStatus().remove(index);
				}
			}
			if (noOfAssignedSeats > 0 && noOfAssignedSeats > noOfJourney) {
				passengerDetail.getAssignedSeats().subList(noOfJourney, noOfAssignedSeats).clear();
			}
			if (noOfSSR > 0 && noOfSSR > noOfJourney) {
				passengerDetail.getPaxSSRList().subList(noOfJourney, noOfSSR).clear();
			}
		}
	}

	private List<JourneyDetail> populateJourneyDetails(List<BookingManagerStub.Journey> journeyList,
			Calendar currentDateTime) {
		List<JourneyDetail> journeyDetailList = new ArrayList<>();
		noOfJourneyListWithoutSegment = new ArrayList<>();
		notAvailableJourney = new ArrayList<>();
		checkinAllowedHourFlag = new ArrayList<Boolean>();

		int checkInHour = 24;
		String hours = env.getProperty("app.check.in.allowed.hours");
		if (hours != null) {
			checkInHour = Integer.valueOf(hours);
		}

		int index = 0;
		for (BookingManagerStub.Journey journey : journeyList) {
			BookingManagerStub.ArrayOfSegment arrayOfSegment = journey.getSegments();
			if (arrayOfSegment.getSegment().length > 1) {
				JourneyListOfPnr journeyListOfPnr = new JourneyListOfPnr();
				List<String> halfJourney = new ArrayList<>();
				halfJourney.add(arrayOfSegment.getSegment()[0].getDepartureStation() + "-"
						+ arrayOfSegment.getSegment()[0].getArrivalStation());
				halfJourney.add(arrayOfSegment.getSegment()[1].getDepartureStation() + "-"
						+ arrayOfSegment.getSegment()[1].getArrivalStation());
				journeyListOfPnr.setSingleJourney(halfJourney);
				noOfJourneyListWithoutSegment.add(journeyListOfPnr);

			} else {
				JourneyListOfPnr journeyListOfPnr = new JourneyListOfPnr();
				List<String> halfJourney = new ArrayList<>();
				halfJourney.add(arrayOfSegment.getSegment()[0].getDepartureStation() + "-"
						+ arrayOfSegment.getSegment()[0].getArrivalStation());
				journeyListOfPnr.setSingleJourney(halfJourney);
				noOfJourneyListWithoutSegment.add(journeyListOfPnr);
			}
			for (BookingManagerStub.Segment segment : Arrays.asList(arrayOfSegment.getSegment())) {
				JourneyDetail journeyDetail = new JourneyDetail();
				Calendar sta = null;
				Calendar std = null;

				Calendar etd = null;

				boolean isCheckedInAllowed = false;

				Leg[] legs = segment.getLegs().getLeg();
				int noOfLeg = legs.length;

				if (noOfLeg > 0) {
					if (legs[0].getOperationsInfo() != null) {
						/* Standard Time */
						std = legs[0].getOperationsInfo().getSTD();
						/* Estimate Time */
						etd = legs[0].getOperationsInfo().getETD();

						DateTime etdJoda = new DateTime(etd.getTime());
						DateTime stdJoda = new DateTime(std.getTime());
						Duration duration = new Duration(stdJoda, etdJoda);

						boolean isDelayed = checkFlightDelayed(etd);

						if ((isDelayed) && (Long.valueOf(duration.getStandardMinutes())
								.compareTo(Long.valueOf(env.getProperty("app.check.in.delated.allowed.time"))) > 0)) {
							log.info("..........Flight Delayed By : " + duration.getStandardMinutes());
							isCheckedInAllowed = validateCheckedInDateTime(etd, currentDateTime, checkInHour);
							if (isCheckedInAllowed) {
								journeyDetail.setArrivalDateTime(segment.getSTA());
								journeyDetail.setDepartureDateTime(etd);
								journeyDetail.setOnTime(false);
							}
						} else {
							log.info("..........Flight on time : " + duration.getStandardMinutes());
							isCheckedInAllowed = validateCheckedInDateTime(std, currentDateTime, checkInHour);
							if (isCheckedInAllowed) {
								journeyDetail.setArrivalDateTime(segment.getSTA());
								journeyDetail.setDepartureDateTime(std);
								journeyDetail.setOnTime(true);
							}
						}
						if (isCheckedInAllowed) {
							journeyDetail.setArrivalGate(legs[0].getOperationsInfo().getArrivalGate());
							journeyDetail.setDepartureGate(legs[noOfLeg - 1].getOperationsInfo().getDepartureGate());
						}

					} else {
						std = segment.getSTD();
						sta = segment.getSTA();
						isCheckedInAllowed = validateCheckedInDateTime(std, currentDateTime, checkInHour);
						if (isCheckedInAllowed) {
							journeyDetail.setArrivalDateTime(sta);
							journeyDetail.setDepartureDateTime(std);
							journeyDetail.setOnTime(true);
						}
					}

					if (isCheckedInAllowed) {
						journeyDetail.setDepartureTerminal(legs[0].getLegInfo().getDepartureTerminal());
						journeyDetail.setArrivalTerminal(legs[noOfLeg - 1].getLegInfo().getArrivalTerminal());
					}
				}

				if (isCheckedInAllowed) {
					if (noOfLeg > 1) {
						journeyDetail.setVia(true);
						journeyDetail.setViaStation(legs[0].getArrivalStation());
					} else {
						journeyDetail.setVia(false);
					}
					journeyDetail.setArrivalStation(segment.getArrivalStation());
					journeyDetail.setDepartureStation(segment.getDepartureStation());
					journeyDetail.setFlightNumber(segment.getFlightDesignator().getFlightNumber());
					journeyDetail.setCarrierCode(segment.getFlightDesignator().getCarrierCode());

					journeyDetailList.add(journeyDetail);
				} else {
					{
						int journeyIndex = getJourneyIndex(noOfJourneyListWithoutSegment,
								segment.getDepartureStation() + "-" + segment.getArrivalStation());
						int segmentIndex = getJourneySegmentIndex(noOfJourneyListWithoutSegment.get(journeyIndex),
								segment.getDepartureStation() + "-" + segment.getArrivalStation());
						boolean addJourney = false;
						if (segmentIndex == 1) {
							String srcDestPreviousLeg = noOfJourneyListWithoutSegment.get(journeyIndex)
									.getSingleJourney().get(segmentIndex - 1);

							for (JourneyDetail journeyDetails : journeyDetailList) {
								String srcDest = journeyDetails.getDepartureStation() + "-"
										+ journeyDetails.getArrivalStation();
								if (srcDest.equalsIgnoreCase(srcDestPreviousLeg)) {
									journeyDetail.setArrivalDateTime(sta);
									journeyDetail.setDepartureDateTime(std);
									journeyDetail.setOnTime(true);
									journeyDetail.setArrivalStation(segment.getArrivalStation());
									journeyDetail.setDepartureStation(segment.getDepartureStation());
									journeyDetail.setFlightNumber(segment.getFlightDesignator().getFlightNumber());
									journeyDetail.setCarrierCode(segment.getFlightDesignator().getCarrierCode());
									journeyDetail.setDepartureTerminal(legs[0].getLegInfo().getDepartureTerminal());
									journeyDetail
											.setArrivalTerminal(legs[noOfLeg - 1].getLegInfo().getArrivalTerminal());
									addJourney = true;
									break;
								}
							}

						}
						if (addJourney) {
							journeyDetailList.add(journeyDetail);
						} else {
							this.notAvailableJourney.add(index);
						}
					}
				}

				index++;
			}

		}
		log.info(".....................Number of journey : " + journeyDetailList.size());
		return journeyDetailList;
	}

	private int getJourneyIndex(List<JourneyListOfPnr> noOfJourneyListWithoutSegment, String srcDest) {
		int journeyIndex = 0;
		for (JourneyListOfPnr singleJourney : noOfJourneyListWithoutSegment) {
			if (singleJourney.getSingleJourney().contains(srcDest)) {
				break;
			}
			journeyIndex++;
		}
		return journeyIndex;
	}

	private int getJourneySegmentIndex(JourneyListOfPnr noOfSegmentList, String srcDest) {
		int journeySegmentIndex = 0;
		for (String singleJourneySegment : noOfSegmentList.getSingleJourney()) {
			if (singleJourneySegment.contains(srcDest)) {
				break;
			}
			journeySegmentIndex++;
		}
		return journeySegmentIndex;
	}

	private boolean checkFlightDelayed(Calendar etd) {
		boolean isDelayed = true;
		if (etd.get(Calendar.YEAR) == Constants.DEFAULT_YEAR)
			isDelayed = false;
		return isDelayed;
	}

	private boolean validateCheckedInDateTime(Calendar etd, Calendar currentDateTime, int checkInHour) {
		log.info(".........validateCheckedInDateTime............");
		log.info("Departure Date Time : " + etd.get(Calendar.DAY_OF_MONTH) + "-" + etd.get(Calendar.MONTH) + "-"
				+ etd.get(Calendar.YEAR) + "-" + etd.get(Calendar.HOUR) + "-" + etd.get(Calendar.MINUTE) + "-"
				+ etd.get(Calendar.SECOND));
		log.info("Current Date Time : " + currentDateTime.get(Calendar.DAY_OF_MONTH) + "-"
				+ currentDateTime.get(Calendar.MONTH) + "-" + currentDateTime.get(Calendar.YEAR) + "-"
				+ currentDateTime.get(Calendar.HOUR) + "-" + currentDateTime.get(Calendar.MINUTE) + "-"
				+ currentDateTime.get(Calendar.SECOND));
		boolean isCheckedInAllowed = false;
		log.info(currentDateTime.compareTo(etd));

		long currentMilli = currentDateTime.getTimeInMillis();
		long etdMilli = etd.getTimeInMillis();

		log.info("Current times in milli : " + currentMilli);
		log.info("ETD times in milli : " + etdMilli);

		if (currentDateTime.compareTo(etd) < 0) {

			long timeDiff = (etdMilli - currentMilli) / 60000;

			// checkin allowed to particular time before departure
			if (timeDiff > Long.parseLong(env.getProperty("app.last.check.in.allowd.minutes"))) {

				Calendar currentDateTimeAfter24 = Calendar.getInstance();
				currentDateTimeAfter24.setTimeInMillis(currentDateTime.getTimeInMillis());
				currentDateTimeAfter24.add(Calendar.HOUR, checkInHour);
				log.info("Current Date Time After Adding 24 hours: " + currentDateTimeAfter24.get(Calendar.DAY_OF_MONTH)
						+ "-" + currentDateTimeAfter24.get(Calendar.MONTH) + "-"
						+ currentDateTimeAfter24.get(Calendar.YEAR) + "-" + currentDateTimeAfter24.get(Calendar.HOUR)
						+ "-" + currentDateTimeAfter24.get(Calendar.MINUTE) + "-"
						+ currentDateTimeAfter24.get(Calendar.SECOND));
				log.info(currentDateTimeAfter24.compareTo(etd));
				if (currentDateTimeAfter24.compareTo(etd) >= 0) {
					isCheckedInAllowed = true;
					checkinAllowedHourFlag.add(true);
				} else {
					log.info("checkin is not yet started: ");
					isCheckedInAllowed = false;
					checkinAllowedHourFlag.add(false);
				}
			} else {
				log.info("checkin is closed before : ");
				isCheckedInAllowed = false;
				checkinAllowedHourFlag.add(false);
			}

		}
		log.info("isCheckedInAllowed: " + isCheckedInAllowed);
		return isCheckedInAllowed;
	}

	public short getPassengerCount() {
		return passengerCount;
	}

	public void setPassengerCount(final short passengerCount) {
		this.passengerCount = passengerCount;
	}

	public String getBookingId() {
		return bookingId;
	}

	public void setBookingId(final String bookingId) {
		this.bookingId = bookingId;
	}

	public String getPnr() {
		return pnr;
	}

	public void setPnr(final String pnr) {
		this.pnr = pnr;
	}

	public List<PassengerDetail> getPassengerDetails() {
		return passengerDetails;
	}

	public void setPassengerDetails(final List<PassengerDetail> passengerDetails) {
		this.passengerDetails = passengerDetails;
	}

	public List<JourneyDetail> getJourneyDetails() {
		return journeyDetails;
	}

	public void setJourneyDetails(final List<JourneyDetail> journeyDetails) {
		this.journeyDetails = journeyDetails;
	}

	public boolean isValidBooking() {
		return isValidBooking;
	}

	public void setValidBooking(boolean isValidBooking) {
		this.isValidBooking = isValidBooking;
	}

	public boolean isCheckInAllowed() {
		return isCheckInAllowed;
	}

	public void setCheckInAllowed(boolean isCheckInAllowed) {
		this.isCheckInAllowed = isCheckInAllowed;
	}

	public String getCheckInNotAllowedReason() {
		return checkInNotAllowedReason;
	}

	public void setCheckInNotAllowedReason(String checkInNotAllowedReason) {
		this.checkInNotAllowedReason = checkInNotAllowedReason;
	}

}