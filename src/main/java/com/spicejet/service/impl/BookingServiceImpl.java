package com.spicejet.service.impl;

import java.rmi.RemoteException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.spicejet.dto.BookingDetailDto;
import com.spicejet.dto.JourneyDetail;
import com.spicejet.dto.PassengerDetail;
import com.spicejet.kiosk.webservices.bookingManager.BookingManagerStub;
import com.spicejet.kiosk.webservices.bookingManager.BookingManagerStub.ArrayOfBookingHistory;
import com.spicejet.kiosk.webservices.bookingManager.BookingManagerStub.ArrayOfPaxSSR;
import com.spicejet.kiosk.webservices.bookingManager.BookingManagerStub.ArrayOfSegmentSSRRequest;
import com.spicejet.kiosk.webservices.bookingManager.BookingManagerStub.ArrayOfSegmentSeatRequest;
import com.spicejet.kiosk.webservices.bookingManager.BookingManagerStub.ArrayOflong;
import com.spicejet.kiosk.webservices.bookingManager.BookingManagerStub.ArrayOfshort;
import com.spicejet.kiosk.webservices.bookingManager.BookingManagerStub.BookingCommitRequestData;
import com.spicejet.kiosk.webservices.bookingManager.BookingManagerStub.BookingHistory;
import com.spicejet.kiosk.webservices.bookingManager.BookingManagerStub.FlightDesignator;
import com.spicejet.kiosk.webservices.bookingManager.BookingManagerStub.MessageState;
import com.spicejet.kiosk.webservices.bookingManager.BookingManagerStub.PaxSSR;
import com.spicejet.kiosk.webservices.bookingManager.BookingManagerStub.SSRRequest;
import com.spicejet.kiosk.webservices.bookingManager.BookingManagerStub.SeatAssignmentMode;
import com.spicejet.kiosk.webservices.bookingManager.BookingManagerStub.SeatSellRequest;
import com.spicejet.kiosk.webservices.bookingManager.BookingManagerStub.SegmentSSRRequest;
import com.spicejet.kiosk.webservices.bookingManager.BookingManagerStub.SegmentSeatRequest;
import com.spicejet.kiosk.webservices.bookingManager.BookingManagerStub.SellBy;
import com.spicejet.kiosk.webservices.bookingManager.BookingManagerStub.SellRequestData;
import com.spicejet.kiosk.webservices.bookingManager.BookingManagerStub.SellSSR;
import com.spicejet.service.inter.IBookingManagerService;
import com.spicejet.util.Constants;

@Service
public class BookingServiceImpl implements IBookingManagerService {

	@Autowired
	private Environment env;

	public BookingManagerStub.Booking getBooking(String sign, String pnr) throws RemoteException {
		BookingManagerStub bookingManagerStub = new BookingManagerStub(env.getProperty(Constants.BOOKING_MANAGER));
		BookingManagerStub.GetBookingRequest getBookingRequest = new BookingManagerStub.GetBookingRequest();
		BookingManagerStub.GetBookingRequestData getBookingRequestData = new BookingManagerStub.GetBookingRequestData();
		getBookingRequestData.setGetBookingBy(BookingManagerStub.GetBookingBy.RecordLocator);
		BookingManagerStub.GetByRecordLocator getByRecordLocator = new BookingManagerStub.GetByRecordLocator();
		getByRecordLocator.setRecordLocator(pnr);
		getBookingRequestData.setGetByRecordLocator(getByRecordLocator);
		getBookingRequest.setGetBookingReqData(getBookingRequestData);
		BookingManagerStub.ContractVersion cv = new BookingManagerStub.ContractVersion();
		cv.setContractVersion(Constants.CONTRACT_VERSION);
		BookingManagerStub.Signature signature = new BookingManagerStub.Signature();
		signature.setSignature(sign);
		return bookingManagerStub.getBooking(getBookingRequest, cv, signature).getBooking();
	}

	public BookingHistory[] bookingHistory(String sign, long bookingId) throws RemoteException {
		BookingManagerStub bookingManagerStub = new BookingManagerStub(env.getProperty(Constants.BOOKING_MANAGER));
		BookingManagerStub.GetBookingHistoryRequest bookingHistoryRequest = new BookingManagerStub.GetBookingHistoryRequest();
		BookingManagerStub.GetBookingHistoryRequestData bookingHistoryRequestData = new BookingManagerStub.GetBookingHistoryRequestData();
		bookingHistoryRequestData.setBookingID(bookingId);
		bookingHistoryRequestData.setHistoryCode(env.getProperty(Constants.BOOKING_HISTORY_CODE));
		bookingHistoryRequestData.setGetTotalCount(true);
		bookingHistoryRequestData.setLastID(0l);
		bookingHistoryRequestData.setPageSize((short) 0);
		bookingHistoryRequestData.setRetrieveFromArchive(false);
		bookingHistoryRequest.setGetBookingHistoryReqData(bookingHistoryRequestData);
		BookingManagerStub.ContractVersion cv = new BookingManagerStub.ContractVersion();
		cv.setContractVersion(Constants.CONTRACT_VERSION);
		BookingManagerStub.Signature signature = new BookingManagerStub.Signature();
		signature.setSignature(sign);
		ArrayOfBookingHistory arrayOfBookingHistory = bookingManagerStub
				.getBookingHistory(bookingHistoryRequest, cv, signature).getGetBookingHistoryResponseData()
				.getBookingHistories();
		if (arrayOfBookingHistory == null) {
			return null;
		} else {
			return arrayOfBookingHistory.getBookingHistory();
		}
	}

	public boolean autoSeatAssign(BookingDetailDto bookingDetailDto, String sign, String pnr) throws RemoteException {

		boolean isSeatAssign = false;

		BookingManagerStub bookingManagerStub = new BookingManagerStub(env.getProperty(Constants.BOOKING_MANAGER));
		BookingManagerStub.AssignSeatsRequest assignSeatsRequest = new BookingManagerStub.AssignSeatsRequest();
		SeatSellRequest seatSellRequest = new SeatSellRequest();
		seatSellRequest.setWaiveFee(true);
		seatSellRequest.setSeatAssignmentMode(SeatAssignmentMode.PreSeatAssignment);

		ArrayOfSegmentSeatRequest arrayOfSegmentSeatRequest = new ArrayOfSegmentSeatRequest();
		for (JourneyDetail journeyDetail : bookingDetailDto.getJourneyDetails()) {
			List<PassengerDetail> nonAssignedPassenger = bookingDetailDto.getPassengerDetails().stream().filter(p -> {
				return (p.getPassengerJourney().isEmpty() || p.getPassengerJourney()
						.get(journeyDetail.getDepartureStation() + "-" + journeyDetail.getArrivalStation()) == null);
			}).collect(Collectors.toList());

			if (!nonAssignedPassenger.isEmpty()) {
				SegmentSeatRequest segmentSeatRequest = new SegmentSeatRequest();
				segmentSeatRequest.setArrivalStation(journeyDetail.getArrivalStation());
				segmentSeatRequest.setDepartureStation(journeyDetail.getDepartureStation());
				segmentSeatRequest.setSTD(journeyDetail.getDepartureDateTime());
				FlightDesignator flightDesignator = new FlightDesignator();
				flightDesignator.setFlightNumber(journeyDetail.getFlightNumber());
				flightDesignator.setCarrierCode(journeyDetail.getCarrierCode());
				segmentSeatRequest.setFlightDesignator(flightDesignator);

				ArrayOflong arrayOfLong = new ArrayOflong();
				arrayOfLong.set_long(getPassengerIds(nonAssignedPassenger));
				segmentSeatRequest.setPassengerIDs(arrayOfLong);
				ArrayOfshort arrayOfShort = new ArrayOfshort();
				int[] passengerNumbers = getPassengerNumbers(nonAssignedPassenger);
				short[] passengerNumber = convertToShort(passengerNumbers);
				arrayOfShort.set_short(passengerNumber);
				segmentSeatRequest.setPassengerNumbers(arrayOfShort);
				arrayOfSegmentSeatRequest.addSegmentSeatRequest(segmentSeatRequest);
			}
		}

		if (arrayOfSegmentSeatRequest.getSegmentSeatRequest() != null) {
			isSeatAssign = true;
			seatSellRequest.setSegmentSeatRequests(arrayOfSegmentSeatRequest);
			assignSeatsRequest.setSellSeatRequest(seatSellRequest);
			BookingManagerStub.ContractVersion cv = new BookingManagerStub.ContractVersion();
			cv.setContractVersion(Constants.CONTRACT_VERSION);
			BookingManagerStub.Signature signature = new BookingManagerStub.Signature();
			signature.setSignature(sign);
			bookingManagerStub.assignSeats(assignSeatsRequest, cv, signature);
			bookingCommit(bookingDetailDto, sign, pnr, bookingManagerStub, cv);
		}

		return isSeatAssign;

	}

	public void sellSSR(BookingDetailDto bookingDetailDto, String sign, String pnr) throws RemoteException {

		BookingManagerStub bookingManagerStub = new BookingManagerStub(env.getProperty(Constants.BOOKING_MANAGER));
		BookingManagerStub.SellRequest sellRequest = new BookingManagerStub.SellRequest();
		SellRequestData sellRequestData = new SellRequestData();
		sellRequestData.setSellBy(SellBy.SSR);
		SellSSR sellSSR = new SellSSR();
		SSRRequest ssrRequest = new SSRRequest();
		ArrayOfSegmentSSRRequest segmentSSRRequests = new ArrayOfSegmentSSRRequest();

		int noOfJouney = bookingDetailDto.getJourneyDetails().size();

		JourneyDetail journeyDetail = bookingDetailDto.getJourneyDetails().get(noOfJouney - 1);
		for (PassengerDetail passengerDetail : bookingDetailDto.getPassengerDetails()) {
			if (!passengerDetail.getPaxSSRList().contains(Constants.RCISSR)) {
				SegmentSSRRequest segmentSSRRequest = new SegmentSSRRequest();
				segmentSSRRequest.setArrivalStation(journeyDetail.getArrivalStation());
				segmentSSRRequest.setSTD(journeyDetail.getDepartureDateTime());
				segmentSSRRequest.setDepartureStation(journeyDetail.getDepartureStation());
				FlightDesignator flightDesignator = new FlightDesignator();
				flightDesignator.setFlightNumber(journeyDetail.getFlightNumber());
				flightDesignator.setCarrierCode(journeyDetail.getCarrierCode());
				segmentSSRRequest.setFlightDesignator(flightDesignator);
				ArrayOfPaxSSR arrayOfPaxSSR = new ArrayOfPaxSSR();
				PaxSSR paxSSR = new PaxSSR();
				paxSSR.setState(MessageState.New);
				paxSSR.setActionStatusCode("NN");
				paxSSR.setArrivalStation(journeyDetail.getArrivalStation());
				paxSSR.setDepartureStation(journeyDetail.getDepartureStation());
				paxSSR.setPassengerNumber(passengerDetail.getPassengerNumber());
				paxSSR.setSSRCode(Constants.RCISSR);
				arrayOfPaxSSR.addPaxSSR(paxSSR);
				segmentSSRRequest.setPaxSSRs(arrayOfPaxSSR);
				segmentSSRRequests.addSegmentSSRRequest(segmentSSRRequest);
			}
		}

		ssrRequest.setSegmentSSRRequests(segmentSSRRequests);
		ssrRequest.setCurrencyCode(bookingDetailDto.getCurrencyCode());
		sellSSR.setSSRRequest(ssrRequest);
		sellRequestData.setSellSSR(sellSSR);
		sellRequest.setSellRequestData(sellRequestData);
		BookingManagerStub.ContractVersion cv = new BookingManagerStub.ContractVersion();
		cv.setContractVersion(Constants.CONTRACT_VERSION);
		BookingManagerStub.Signature signature = new BookingManagerStub.Signature();
		signature.setSignature(sign);
		bookingManagerStub.sell(sellRequest, cv, signature);
		bookingCommit(bookingDetailDto, sign, pnr, bookingManagerStub, cv);

	}
	
	public void sellSSRWEBC(BookingDetailDto bookingDetailDto, String sign, String pnr) throws RemoteException {

		BookingManagerStub bookingManagerStub = new BookingManagerStub(env.getProperty(Constants.BOOKING_MANAGER));
		BookingManagerStub.SellRequest sellRequest = new BookingManagerStub.SellRequest();
		SellRequestData sellRequestData = new SellRequestData();
		sellRequestData.setSellBy(SellBy.SSR);
		SellSSR sellSSR = new SellSSR();
		SSRRequest ssrRequest = new SSRRequest();
		ArrayOfSegmentSSRRequest segmentSSRRequests = new ArrayOfSegmentSSRRequest();
		
		int noOfJouney = bookingDetailDto.getJourneyDetails().size();

		JourneyDetail journeyDetail = bookingDetailDto.getJourneyDetails().get(noOfJouney-1);
		for (PassengerDetail passengerDetail : bookingDetailDto.getPassengerDetails()) {
			if (!passengerDetail.getPaxSSRList().contains(Constants.WEBCSSR)) {
				SegmentSSRRequest segmentSSRRequest = new SegmentSSRRequest();
				segmentSSRRequest.setArrivalStation(journeyDetail.getArrivalStation());
				segmentSSRRequest.setSTD(journeyDetail.getDepartureDateTime());
				segmentSSRRequest.setDepartureStation(journeyDetail.getDepartureStation());
				FlightDesignator flightDesignator = new FlightDesignator();
				flightDesignator.setFlightNumber(journeyDetail.getFlightNumber());
				flightDesignator.setCarrierCode(journeyDetail.getCarrierCode());
				segmentSSRRequest.setFlightDesignator(flightDesignator);
				ArrayOfPaxSSR arrayOfPaxSSR = new ArrayOfPaxSSR();
				PaxSSR paxSSR = new PaxSSR();
				paxSSR.setState(MessageState.New);
				paxSSR.setActionStatusCode("NN");
				paxSSR.setArrivalStation(journeyDetail.getArrivalStation());
				paxSSR.setDepartureStation(journeyDetail.getDepartureStation());
				paxSSR.setPassengerNumber(passengerDetail.getPassengerNumber());
				paxSSR.setSSRCode(Constants.WEBCSSR);
				arrayOfPaxSSR.addPaxSSR(paxSSR);
				segmentSSRRequest.setPaxSSRs(arrayOfPaxSSR);
				segmentSSRRequests.addSegmentSSRRequest(segmentSSRRequest);
			}
		}

		ssrRequest.setSegmentSSRRequests(segmentSSRRequests);
		ssrRequest.setCurrencyCode(bookingDetailDto.getCurrencyCode());
		sellSSR.setSSRRequest(ssrRequest);
		sellRequestData.setSellSSR(sellSSR);
		sellRequest.setSellRequestData(sellRequestData);
		BookingManagerStub.ContractVersion cv = new BookingManagerStub.ContractVersion();
		cv.setContractVersion(Constants.CONTRACT_VERSION);
		BookingManagerStub.Signature signature = new BookingManagerStub.Signature();
		signature.setSignature(sign);
		bookingManagerStub.sell(sellRequest, cv, signature);
		bookingCommit(bookingDetailDto, sign, pnr, bookingManagerStub, cv);

	}

	private void bookingCommit(BookingDetailDto bookingDetailDto, String sign, String pnr,
			BookingManagerStub bookingManagerStub, BookingManagerStub.ContractVersion cv) throws RemoteException {
		BookingManagerStub.Signature signature = new BookingManagerStub.Signature();
		signature.setSignature(sign);
		BookingManagerStub.BookingCommitRequest bookingCommitRequest = new BookingManagerStub.BookingCommitRequest();
		BookingCommitRequestData bookingCommitRequestData = new BookingCommitRequestData();
		bookingCommitRequestData.setState(MessageState.Modified);
		bookingCommitRequestData.setRecordLocator(pnr);
		bookingCommitRequestData.setPaxCount(bookingDetailDto.getPassengerCount());
		bookingCommitRequestData.setCurrencyCode(bookingDetailDto.getCurrencyCode());
		bookingCommitRequest.setBookingCommitRequestData(bookingCommitRequestData);
		bookingManagerStub.bookingCommit(bookingCommitRequest, cv, signature);
	}

	private short[] convertToShort(int[] passengerNumbers) {
		short inShort[] = new short[passengerNumbers.length];

		for (int i = 0; i < passengerNumbers.length; i++) {
			inShort[i] = (short) passengerNumbers[i];
		}
		return inShort;

	}

	private int[] getPassengerNumbers(List<PassengerDetail> nonAssignedPassenger) {
		return nonAssignedPassenger.stream().mapToInt(p -> p.getPassengerNumber()).toArray();

	}

	private long[] getPassengerIds(List<PassengerDetail> nonAssignedPassenger) {
		return nonAssignedPassenger.stream().mapToLong(p -> p.getPassengerId()).toArray();
	}

	

}