package com.spicejet.dto;

import java.util.ArrayList;
import java.util.List;


public class BookingDetailDto {

	boolean isValidBooking;

	boolean isCheckInAllowed;

	String checkInNotAllowedReason;

	short passengerCount;

	String bookingId;

	String pnr;

	String currencyCode;

	List<PassengerDetail> passengerDetails;

	List<JourneyDetail> journeyDetails;

	private boolean handBaggage;

	
	List<JourneyListOfPnr> noOfJourneyListWithoutSegment = new ArrayList<JourneyListOfPnr>();

	public List<JourneyListOfPnr> getNoOfJourneyListWithoutSegment() {
		return noOfJourneyListWithoutSegment;
	}

	public void setNoOfJourneyListWithoutSegment(List<JourneyListOfPnr> noOfJourneyListWithoutSegment) {
		this.noOfJourneyListWithoutSegment = noOfJourneyListWithoutSegment;
	}

	public short getPassengerCount() {
		return passengerCount;
	}

	

	public void setPassengerCount(short passengerCount) {
		this.passengerCount = passengerCount;
	}

	public String getBookingId() {
		return bookingId;
	}

	public void setBookingId(String bookingId) {
		this.bookingId = bookingId;
	}

	public String getPnr() {
		return pnr;
	}

	public void setPnr(String pnr) {
		this.pnr = pnr;
	}

	public List<PassengerDetail> getPassengerDetails() {
		return passengerDetails;
	}

	public void setPassengerDetails(List<PassengerDetail> passengerDetails) {
		this.passengerDetails = passengerDetails;
	}

	public List<JourneyDetail> getJourneyDetails() {
		return journeyDetails;
	}

	public void setJourneyDetails(List<JourneyDetail> journeyDetails) {
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

	public String getCurrencyCode() {
		return currencyCode;
	}

	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}

	public BookingDetailDto(BookingDetailDtoBuilder dtoBuilder) {
		this.isValidBooking = dtoBuilder.isValidBooking();
		this.pnr = dtoBuilder.getPnr();
		this.passengerCount = dtoBuilder.getPassengerCount();
		this.bookingId = dtoBuilder.getBookingId();
		this.passengerDetails = dtoBuilder.getPassengerDetails();
		this.journeyDetails = dtoBuilder.getJourneyDetails();
		this.checkInNotAllowedReason = dtoBuilder.getCheckInNotAllowedReason();
	}

	public BookingDetailDto() {
	}

	public void setHandBaggage(boolean handBaggage) {
		this.handBaggage = handBaggage;
	}

	public boolean isHandBaggage() {
		return handBaggage;
	}
}