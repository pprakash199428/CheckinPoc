package com.spicejet.service.impl;

import java.rmi.RemoteException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.spicejet.dto.BoardingPass;
import com.spicejet.dto.BookingDetailDto;
import com.spicejet.dto.ContentDataBuilder;
import com.spicejet.dto.IATABoardingPassBarcode;
import com.spicejet.dto.JourneyDetail;
import com.spicejet.dto.PassengerDetail;
import com.spicejet.dto.ResponseDto;
import com.spicejet.dto.SeatAvailabilityResponseDto;
import com.spicejet.dto.SeatColumn;
import com.spicejet.dto.SeatRow;
import com.spicejet.enums.Template;
import com.spicejet.kiosk.webservices.bookingManager.BookingManagerStub;
import com.spicejet.kiosk.webservices.bookingManager.BookingManagerStub.ArrayOfBookingHistory;
import com.spicejet.kiosk.webservices.bookingManager.BookingManagerStub.BookingHistory;
import com.spicejet.kiosk.webservices.operationManager.OperationManagerStub;
import com.spicejet.kiosk.webservices.operationManager.OperationManagerStub.ArrayOfBarCodedBoardingPass;
import com.spicejet.kiosk.webservices.operationManager.OperationManagerStub.ArrayOfBoardingPassRequest;
import com.spicejet.kiosk.webservices.operationManager.OperationManagerStub.ArrayOfCheckInMultiplePassengerRequest;
import com.spicejet.kiosk.webservices.operationManager.OperationManagerStub.ArrayOfCheckInPaxRequest;
import com.spicejet.kiosk.webservices.operationManager.OperationManagerStub.ArrayOfEquipmentInfo;
import com.spicejet.kiosk.webservices.operationManager.OperationManagerStub.ArrayOfSegmentSeatRequest;
import com.spicejet.kiosk.webservices.operationManager.OperationManagerStub.ArrayOfSegmentUnassignSeatRequest;
import com.spicejet.kiosk.webservices.operationManager.OperationManagerStub.ArrayOfshort;
import com.spicejet.kiosk.webservices.operationManager.OperationManagerStub.AssignSeatsAtCheckinRequestData;
import com.spicejet.kiosk.webservices.operationManager.OperationManagerStub.BarCodeType;
import com.spicejet.kiosk.webservices.operationManager.OperationManagerStub.BarCodedBoardingPass;
import com.spicejet.kiosk.webservices.operationManager.OperationManagerStub.BarCodedBoardingPassSegment;
import com.spicejet.kiosk.webservices.operationManager.OperationManagerStub.BoardingPassRequest;
import com.spicejet.kiosk.webservices.operationManager.OperationManagerStub.CheckInMultiplePassengerRequest;
import com.spicejet.kiosk.webservices.operationManager.OperationManagerStub.CheckInMultiplePassengerResponse;
import com.spicejet.kiosk.webservices.operationManager.OperationManagerStub.CheckInMultiplePassengersRequest;
import com.spicejet.kiosk.webservices.operationManager.OperationManagerStub.CheckInPassengersRequestData;
import com.spicejet.kiosk.webservices.operationManager.OperationManagerStub.CheckInPassengersResponse;
import com.spicejet.kiosk.webservices.operationManager.OperationManagerStub.CheckInPaxRequest;
import com.spicejet.kiosk.webservices.operationManager.OperationManagerStub.CheckInPaxResponse;
import com.spicejet.kiosk.webservices.operationManager.OperationManagerStub.CompartmentInfo;
import com.spicejet.kiosk.webservices.operationManager.OperationManagerStub.ContractVersion;
import com.spicejet.kiosk.webservices.operationManager.OperationManagerStub.EquipmentInfo;
import com.spicejet.kiosk.webservices.operationManager.OperationManagerStub.FlightDesignator;
import com.spicejet.kiosk.webservices.operationManager.OperationManagerStub.GetBarCodedBoardingPassesMultipleRequestData;
import com.spicejet.kiosk.webservices.operationManager.OperationManagerStub.GetBarCodedBoardingPassesMultipleResponse;
import com.spicejet.kiosk.webservices.operationManager.OperationManagerStub.GetBarCodedBoardingPassesMultipleResponseData;
import com.spicejet.kiosk.webservices.operationManager.OperationManagerStub.GetSeatAvailabilityAtCheckinRequest;
import com.spicejet.kiosk.webservices.operationManager.OperationManagerStub.GetSeatAvailabilityAtCheckinResponse;
import com.spicejet.kiosk.webservices.operationManager.OperationManagerStub.InventoryLegKey;
import com.spicejet.kiosk.webservices.operationManager.OperationManagerStub.Name;
import com.spicejet.kiosk.webservices.operationManager.OperationManagerStub.SeatAvailabilityRequest;
import com.spicejet.kiosk.webservices.operationManager.OperationManagerStub.SeatAvailabilityResponse;
import com.spicejet.kiosk.webservices.operationManager.OperationManagerStub.SeatInfo;
import com.spicejet.kiosk.webservices.operationManager.OperationManagerStub.SegmentSeatRequest;
import com.spicejet.kiosk.webservices.operationManager.OperationManagerStub.SegmentUnassignSeatRequest;
import com.spicejet.kiosk.webservices.operationManager.OperationManagerStub.Signature;
import com.spicejet.kiosk.webservices.operationManager.OperationManagerStub.UnassignSeatsAtCheckinRequestData;
import com.spicejet.kiosk.webservices.operationManager.OperationManagerStub.UnitHoldType;
import com.spicejet.service.inter.IBookingManagerService;
import com.spicejet.service.inter.IOperationManagerService;
import com.spicejet.util.Constants;

@Service
// @PropertySource("classpath:application-config.properties")
public class OperationManagerServiceImpl implements IOperationManagerService {

	@Autowired
	private Environment env;

	@Autowired
	private IBookingManagerService bookingService;

	Logger log = Logger.getLogger(OperationManagerServiceImpl.class);

	@Autowired
	ContentDataBuilder contentDataBuilder;

	@Override
	public SeatAvailabilityResponseDto fetchSeatDetails(String sign, String std, String dStation, String aStation,
			String flightNumber, String carrierCode) throws RemoteException {

		OperationManagerStub operationManagerStub = new OperationManagerStub(
				env.getProperty(Constants.OPERATION_MANAGER));
		log.info("Operational Manager URL" + env.getProperty(Constants.OPERATION_MANAGER));
		GetSeatAvailabilityAtCheckinRequest availabilityAtCheckinRequest = new GetSeatAvailabilityAtCheckinRequest();
		SeatAvailabilityRequest seatAvailabilityRequest = new SeatAvailabilityRequest();
		seatAvailabilityRequest.setCarrierCode(carrierCode);
		seatAvailabilityRequest.setFlightNumber(flightNumber);
		seatAvailabilityRequest.setArrivalStation(aStation);
		seatAvailabilityRequest.setDepartureStation(dStation);
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(Long.valueOf(std));
		seatAvailabilityRequest.setSTD(calendar);
		availabilityAtCheckinRequest.setSeatAvailabilityRequest(seatAvailabilityRequest);
		ContractVersion contractVersion = new ContractVersion();
		contractVersion.setContractVersion(Constants.CONTRACT_VERSION);
		Signature signature = new Signature();
		signature.setSignature(sign);
		GetSeatAvailabilityAtCheckinResponse getSeatAvailabilityAtCheckinResponse = operationManagerStub
				.getSeatAvailabilityAtCheckin(availabilityAtCheckinRequest, contractVersion, signature);
		SeatAvailabilityResponse seatAvailabilityResponse = getSeatAvailabilityAtCheckinResponse
				.getSeatAvailabilityResponse();

		SeatAvailabilityResponseDto seatAvailabilityResponseDto = buildSeatAvailabilityResponseDto(
				seatAvailabilityResponse);
		seatAvailabilityResponseDto.setFlightNumber(flightNumber);
		seatAvailabilityResponseDto.setCarrierCode(carrierCode);
		return seatAvailabilityResponseDto;
	}

	@Override
	public ResponseDto checkIn(String sign, String pnr, BookingDetailDto bookingDetailDto) throws RemoteException {

		OperationManagerStub operationManagerStub = new OperationManagerStub(
				env.getProperty(Constants.OPERATION_MANAGER));
		OperationManagerStub.CheckInPassengersRequest checkInPassengerRequest = new OperationManagerStub.CheckInPassengersRequest();
		CheckInMultiplePassengersRequest checkInMultiplePassengerRequest = new CheckInMultiplePassengersRequest();
		CheckInPassengersRequestData checkInPassengersRequestData = new CheckInPassengersRequestData();
		ArrayOfCheckInMultiplePassengerRequest checkInMultiplePassengerRequestList = new ArrayOfCheckInMultiplePassengerRequest();
		if (isRoundTrip(bookingDetailDto)) {
			if (isTripOnSameDay(bookingDetailDto)) {
				bookingService.sellSSR(bookingDetailDto, sign, pnr);
			} else {
				updateSameDayJourney(bookingDetailDto.getJourneyDetails());
				updateSameDayPassengerDetail(bookingDetailDto.getPassengerDetails());
			}
		}
		int journeyIndex = 0;
		boolean isCheckinPrintAllowed = Boolean.valueOf(env.getProperty("app.checkin.passenger.allowed.print"));
		for (JourneyDetail journeyDetail : bookingDetailDto.getJourneyDetails()) {
			CheckInMultiplePassengerRequest cheInMultiplePassengerRequest = new CheckInMultiplePassengerRequest();
			cheInMultiplePassengerRequest.setBySegment(true);
			cheInMultiplePassengerRequest.setRecordLocator(pnr);
			InventoryLegKey inventoryLegKey = new InventoryLegKey();
			inventoryLegKey.setCarrierCode(journeyDetail.getCarrierCode());
			inventoryLegKey.setFlightNumber(journeyDetail.getFlightNumber());
			inventoryLegKey.setArrivalStation(journeyDetail.getArrivalStation());
			inventoryLegKey.setDepartureStation(journeyDetail.getDepartureStation());
			inventoryLegKey.setDepartureDate(journeyDetail.getDepartureDateTime());
			cheInMultiplePassengerRequest.setInventoryLegKey(inventoryLegKey);
			cheInMultiplePassengerRequest.setLiftStatus(
					com.spicejet.kiosk.webservices.operationManager.OperationManagerStub.LiftStatus.CheckedIn);
			ArrayOfCheckInPaxRequest checkInPaxRequestList = new ArrayOfCheckInPaxRequest();
			for (PassengerDetail passengerDetail : bookingDetailDto.getPassengerDetails()) {
				if (passengerDetail.getAssignedSeats().size() > journeyIndex) {
					Boolean isCheckedIn = passengerDetail.getLiftStatus().get(journeyIndex);
					Boolean isWebCheckedIn = passengerDetail.getPaxSSRList()
							.contains(env.getProperty("app.kiosk.web.checkin.ssr"));
					if (!isCheckedIn || isWebCheckedIn || isCheckinPrintAllowed) {
						CheckInPaxRequest checkInPaxRequest = new CheckInPaxRequest();
						Name name = new Name();
						name.setTitle(passengerDetail.getTitle());
						name.setFirstName(passengerDetail.getFirstName());
						name.setMiddleName(passengerDetail.getMiddleName());
						name.setLastName(passengerDetail.getLastName());
						checkInPaxRequest.setName(name);
						checkInPaxRequestList.addCheckInPaxRequest(checkInPaxRequest);
					}
				}
			}
			if (checkInPaxRequestList.getCheckInPaxRequest() != null) {
				cheInMultiplePassengerRequest.setCheckInPaxRequestList(checkInPaxRequestList);
				checkInMultiplePassengerRequestList.addCheckInMultiplePassengerRequest(cheInMultiplePassengerRequest);
			}
			journeyIndex++;
		}
		if (checkInMultiplePassengerRequestList.getCheckInMultiplePassengerRequest() != null) {
			checkInPassengersRequestData.setCheckInMultiplePassengerRequestList(checkInMultiplePassengerRequestList);
			Signature signature = new Signature();
			signature.setSignature(sign);
			ContractVersion cv = new ContractVersion();
			cv.setContractVersion(Constants.CONTRACT_VERSION);
			checkInMultiplePassengerRequest.setCheckInMultiplePassengersRequest(checkInPassengersRequestData);
			checkInPassengerRequest.setCheckInMultiplePassengersRequest(checkInPassengersRequestData);
			CheckInPassengersResponse checkInPassengers = operationManagerStub
					.checkInPassengers(checkInPassengerRequest, cv, signature);
			return populateBoardingPass(checkInPassengers, pnr, sign, bookingDetailDto);
		}
		ResponseDto responseDtoNext = new ResponseDto();
		responseDtoNext.setValidResponse(false);
		responseDtoNext.setErrorMessage("All selected passenger(s) are checked in");
		return responseDtoNext;
	}

	private void updateSameDayPassengerDetail(List<PassengerDetail> passengerDetails) {
		for (PassengerDetail passengerDetail : passengerDetails) {
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

	private Map<String, Integer> extractDataFromBookingHistory(BookingHistory[] bookingHistories) {

		Map<String, Integer> extractedDataFromBookingHistory = new HashMap<String, Integer>();
		String historyDetail;
		String[] parsedHistoryDetail;
		BookingHistory[] histories = bookingHistories;
		for (int i = 0; i < histories.length; i++) {

			historyDetail = histories[i].getHistoryDetail();
			parsedHistoryDetail = parseHistoryDetails(historyDetail);

			if (parsedHistoryDetail.length > 0) {
				if (parsedHistoryDetail.length == 9) {
					String key = parsedHistoryDetail[2] + parsedHistoryDetail[8];
					if (extractedDataFromBookingHistory.containsKey(key)) {
						if (histories[i].getPointOfSale().getAgentCode()
								.equalsIgnoreCase(env.getProperty(Constants.BOOKING_HISTORY_AGENT_CODE))) {
							extractedDataFromBookingHistory.put(key, 1);
						} else {
							extractedDataFromBookingHistory.put(key, extractedDataFromBookingHistory.get(key) + 1);
						}
					} else {
						if (histories[i].getPointOfSale().getAgentCode()
								.equalsIgnoreCase(env.getProperty(Constants.BOOKING_HISTORY_AGENT_CODE))) {
							extractedDataFromBookingHistory.put(key, 1);
						} else {
							extractedDataFromBookingHistory.put(key, 1);
						}
					}
				} else {
					String key = parsedHistoryDetail[2] + parsedHistoryDetail[9];
					if (extractedDataFromBookingHistory.containsKey(key)) {
						if (histories[i].getPointOfSale().getAgentCode()
								.equalsIgnoreCase(env.getProperty(Constants.BOOKING_HISTORY_AGENT_CODE))) {
							extractedDataFromBookingHistory.put(key, 1);
						} else {
							extractedDataFromBookingHistory.put(key, extractedDataFromBookingHistory.get(key) + 1);
						}
					} else {
						if (histories[i].getPointOfSale().getAgentCode()
								.equalsIgnoreCase(env.getProperty(Constants.BOOKING_HISTORY_AGENT_CODE))) {
							extractedDataFromBookingHistory.put(key, 1);
						} else {
							extractedDataFromBookingHistory.put(key, 1);
						}
					}
				}
			}
		}
		return extractedDataFromBookingHistory;

	}

	private String[] parseHistoryDetails(String historyDetail) {
		String[] parsedHistoryDetails = new String[] {};
		try {
			parsedHistoryDetails = historyDetail.split("\\s+");
		} catch (PatternSyntaxException ex) {
			log.error("Unable to parse HistoryDetail tag in BookingHistoryResponse .");
		}
		return parsedHistoryDetails;
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

	@Override
	public ResponseDto unAssign(String sign, String pnr, BookingDetailDto bookingDetailDto) throws RemoteException {
		ResponseDto responseDto = new ResponseDto();

		OperationManagerStub operationManagerStub = new OperationManagerStub(
				env.getProperty(Constants.OPERATION_MANAGER));
		OperationManagerStub.UnassignSeatsAtCheckinRequestDataE unassignSeatsAtCheckinRequestDataE = new OperationManagerStub.UnassignSeatsAtCheckinRequestDataE();
		UnassignSeatsAtCheckinRequestData unassignSeatsAtCheckinRequestData = new UnassignSeatsAtCheckinRequestData();
		unassignSeatsAtCheckinRequestData.setRecordLocator(pnr);
		ArrayOfSegmentUnassignSeatRequest segmentUnassignSeatRequestList = new ArrayOfSegmentUnassignSeatRequest();
		for (JourneyDetail journeyDetail : bookingDetailDto.getJourneyDetails()) {
			int journeyIndex = 0;
			SegmentUnassignSeatRequest segmentUnassignSeatRequest = new SegmentUnassignSeatRequest();
			FlightDesignator flightDesignator = new FlightDesignator();
			flightDesignator.setCarrierCode(journeyDetail.getCarrierCode());
			flightDesignator.setFlightNumber(journeyDetail.getFlightNumber());
			segmentUnassignSeatRequest.setFlightDesignator(flightDesignator);
			segmentUnassignSeatRequest.setArrivalStation(journeyDetail.getArrivalStation());
			segmentUnassignSeatRequest.setDepartureStation(journeyDetail.getDepartureStation());
			segmentUnassignSeatRequest.setSTD(journeyDetail.getDepartureDateTime());
			ArrayOfshort arrayOfShort = new ArrayOfshort();
			List<Short> list = getPassengerNumbers(bookingDetailDto.getPassengerDetails(), journeyIndex);
			if (!list.isEmpty()) {
				short[] convertToShort = convertToShort(list);
				arrayOfShort.set_short(convertToShort);
				segmentUnassignSeatRequest.setPassengerNumbers(arrayOfShort);
				segmentUnassignSeatRequestList.addSegmentUnassignSeatRequest(segmentUnassignSeatRequest);
			}
			journeyIndex++;
		}
		if (segmentUnassignSeatRequestList.getSegmentUnassignSeatRequest() != null) {
			unassignSeatsAtCheckinRequestData.setSegmentUnassignSeatRequestList(segmentUnassignSeatRequestList);
			unassignSeatsAtCheckinRequestDataE.setUnassignSeatsAtCheckinRequestData(unassignSeatsAtCheckinRequestData);
			Signature signature = new Signature();
			signature.setSignature(sign);
			ContractVersion cv = new ContractVersion();
			cv.setContractVersion(Constants.CONTRACT_VERSION);
			operationManagerStub.unassignSeatsAtCheckin(unassignSeatsAtCheckinRequestDataE, cv, signature);
			responseDto.setValidResponse(true);
		} else {
			responseDto.setValidResponse(false);
			responseDto.setErrorMessage(env.getProperty("error.not.found.message"));
		}

		return responseDto;

	}

	@Override
	public ResponseDto assignSeats(String sign, String pnr, BookingDetailDto bookingDetailDto) throws RemoteException {

		OperationManagerStub operationManagerStub = new OperationManagerStub(
				env.getProperty(Constants.OPERATION_MANAGER));
		OperationManagerStub.AssignSeatsAtCheckinReqData assignSeatsAtCheckinReq = new OperationManagerStub.AssignSeatsAtCheckinReqData();
		AssignSeatsAtCheckinRequestData assignSeatsAtCheckinReqData = new AssignSeatsAtCheckinRequestData();
		assignSeatsAtCheckinReqData.setRecordLocator(pnr);
		assignSeatsAtCheckinReqData.setBlockType(UnitHoldType.Session);
		Signature signature = new Signature();
		signature.setSignature(sign);
		ContractVersion cv = new ContractVersion();
		cv.setContractVersion(Constants.CONTRACT_VERSION);

		for (PassengerDetail passengerDetail : bookingDetailDto.getPassengerDetails()) {

			ArrayOfSegmentSeatRequest arrayofSegmentRequest = new ArrayOfSegmentSeatRequest();
			int journeyIndex = 0;
			for (JourneyDetail journeyDetail : bookingDetailDto.getJourneyDetails()) {
				if (passengerDetail.getAssignedSeats().size() > journeyIndex) {
					String assignedSeat = passengerDetail.getAssignedSeats().get(journeyIndex);
					if (Arrays.asList(assignedSeat.split("/")).size() > 1) {
						SegmentSeatRequest segmentSeatRequest = new SegmentSeatRequest();
						FlightDesignator flightDesignator = new FlightDesignator();
						flightDesignator.setCarrierCode(journeyDetail.getCarrierCode());
						flightDesignator.setFlightNumber(journeyDetail.getFlightNumber());
						segmentSeatRequest.setFlightDesignator(flightDesignator);
						segmentSeatRequest.setArrivalStation(journeyDetail.getArrivalStation());
						segmentSeatRequest.setDepartureStation(journeyDetail.getDepartureStation());
						segmentSeatRequest.setSTD(journeyDetail.getDepartureDateTime());
						ArrayOfshort arrayOfShort = new ArrayOfshort();
						short[] passengerNumber = { passengerDetail.getPassengerNumber() };
						arrayOfShort.set_short(passengerNumber);
						segmentSeatRequest.setPassengerNumbers(arrayOfShort);
						segmentSeatRequest.setUnitDesignator(Arrays.asList(assignedSeat.split("/")).get(0));
						arrayofSegmentRequest.addSegmentSeatRequest(segmentSeatRequest);
					}
				}
				journeyIndex++;
			}
			if (arrayofSegmentRequest.getSegmentSeatRequest() != null) {
				assignSeatsAtCheckinReqData.setSegmentSeatRequests(arrayofSegmentRequest);
				assignSeatsAtCheckinReq.setAssignSeatsAtCheckinReqData(assignSeatsAtCheckinReqData);
				operationManagerStub.assignSeatsAtCheckin(assignSeatsAtCheckinReq, cv, signature);
			}
		}
		ResponseDto responseDto = new ResponseDto();
		responseDto.setValidResponse(true);
		return responseDto;

	}

	private short[] convertToShort(List<Short> list) {
		short[] passengerNumbers = new short[list.size()];
		for (Short passengerNumber : list) {
			int index = 0;
			passengerNumbers[index] = passengerNumber;
		}
		return passengerNumbers;

	}

	private List<Short> getPassengerNumbers(List<PassengerDetail> nonAssignedPassenger, int journeyIndex) {
		List<Short> list = new ArrayList<>();
		for (PassengerDetail passengerDetail : nonAssignedPassenger) {
			if (passengerDetail.getAssignedSeats().size() > journeyIndex) {
				if (!passengerDetail.getLiftStatus().get(journeyIndex)) {
					if ((passengerDetail.getAssignedSeats() != null)
							&& (passengerDetail.getAssignedSeats().size() > journeyIndex)) {
						String assignedSeat = passengerDetail.getAssignedSeats().get(journeyIndex);
						if (Arrays.asList(assignedSeat.split("/")).size() > 1)
							list.add(passengerDetail.getPassengerNumber());
					}
				}
			}

		}
		return list;

	}

	private List<String> populateSsrs(OperationManagerStub.ManifestLegSSR[] manifestLegSSR) {
		List<String> manifestSSRS = new ArrayList<>();
		for (OperationManagerStub.ManifestLegSSR manifestLegSSR2 : Arrays.asList(manifestLegSSR)) {
			manifestSSRS.add(manifestLegSSR2.getSSRCode());
		}
		return manifestSSRS;

	}

	private SeatAvailabilityResponseDto buildSeatAvailabilityResponseDto(
			SeatAvailabilityResponse seatAvailabilityResponse) {
		ArrayOfEquipmentInfo equipmentInfos = seatAvailabilityResponse.getEquipmentInfos();
		SeatAvailabilityResponseDto seatAvailabilityResponseDto = new SeatAvailabilityResponseDto();

		Map<Integer, Map<String, SeatColumn>> seatRows = new HashMap<>();

		Optional<EquipmentInfo> equipmentInfo = Arrays.asList(equipmentInfos.getEquipmentInfo()).stream().findFirst();

		if (equipmentInfo.isPresent()) {
			seatAvailabilityResponseDto.setArrivalStation(equipmentInfo.get().getArrivalStation());
			seatAvailabilityResponseDto.setDepartureStation(equipmentInfo.get().getDepartureStation());
			seatAvailabilityResponseDto.setAvailableUnits(equipmentInfo.get().getAvailableUnits());

			if (!equipmentInfo.get().getEquipmentTypeSuffix().isEmpty()) {
				seatAvailabilityResponseDto.setPlaneType(
						equipmentInfo.get().getEquipmentType() + "-" + equipmentInfo.get().getEquipmentTypeSuffix());
			} else {
				seatAvailabilityResponseDto.setPlaneType(equipmentInfo.get().getEquipmentType());
			}

			Optional<CompartmentInfo> compartmentInfo = Arrays
					.asList(equipmentInfo.get().getCompartments().getCompartmentInfo()).stream().findFirst();
			if (compartmentInfo.isPresent()) {
				List<SeatInfo> seatInfos = Arrays.asList(compartmentInfo.get().getSeats().getSeatInfo());
				log.info("UnknownSeat Removed");
				for (SeatInfo seatInfo : seatInfos.stream().filter(isUnknownSeat().negate())
						.collect(Collectors.toList())) {
					String seatDesignator = seatInfo.getSeatDesignator();
					Integer seatRow = Integer.valueOf(seatDesignator.substring(0, seatDesignator.length() - 1));
					String seatColumn = seatDesignator.substring(seatDesignator.length() - 1);
					if (seatRows.get(seatRow) != null) {
						Map<String, SeatColumn> seatMap = seatRows.get(seatRow);
						SeatColumn seat = new SeatColumn();
						seat.setSeatAvailabilityType(seatInfo.getSeatAvailability().getValue());
						seat.setAssignable(seatInfo.getAssignable() && isSeatGroupAssignable(seatInfo.getSeatGroup()));
						seat.setSeatName(seatColumn);
						seat.setSeatAvailable(true);
						seat.setSeatGroup(seatInfo.getSeatGroup());
						seatMap.put(seatColumn, seat);
					} else {
						SeatColumn seat = new SeatColumn();
						seat.setSeatAvailabilityType(seatInfo.getSeatAvailability().getValue());
						seat.setAssignable(seatInfo.getAssignable() && isSeatGroupAssignable(seatInfo.getSeatGroup()));
						seat.setSeatName(seatColumn);
						seat.setSeatAvailable(true);
						seat.setSeatGroup(seatInfo.getSeatGroup());
						Map<String, SeatColumn> seatMap = new HashMap<>();
						seatMap.put(seatColumn, seat);
						seatRows.put(seatRow, seatMap);
					}
				}
			}
		}
		List<SeatRow> populateSeatRow = null;
		if (Arrays.asList(env.getProperty("app.plane.type.3.3").split(","))
				.contains(seatAvailabilityResponseDto.getPlaneType())) {
			log.info("Plane Is Three X Three" + seatAvailabilityResponseDto.getPlaneType());
			populateSeatRow = populateSeatGroupThreeCrossThree(seatRows);
		} else if (Arrays.asList(env.getProperty("app.plane.type.2.2").split(","))
				.contains(seatAvailabilityResponseDto.getPlaneType())) {
			log.info("Plane Is Two X Two" + seatAvailabilityResponseDto.getPlaneType());
			populateSeatRow = populateSeatGroupTwoCrossTwo(seatRows);
		}
		seatAvailabilityResponseDto.setSeatRows(populateSeatRow);
		return seatAvailabilityResponseDto;

	}

	private boolean isSeatGroupAssignable(short seatGroup) {
		List<String> notAvailableSeatGroups = Arrays.asList(env.getProperty("not.available.seat.group").split(","));

		if (notAvailableSeatGroups.contains(String.valueOf(seatGroup))) {
			return false;
		} else {
			return true;
		}
	}

	private Predicate<SeatInfo> isUnknownSeat() {
		return seatInfo -> seatInfo.getSeatAvailability().equals(OperationManagerStub.SeatAvailability.Unknown);
	}

	private List<SeatRow> populateSeatGroupTwoCrossTwo(Map<Integer, Map<String, SeatColumn>> seatRows) {
		List<SeatRow> seatGroupList = new ArrayList<>();
		for (Map.Entry<Integer, Map<String, SeatColumn>> entry : seatRows.entrySet()) {
			SeatRow seatGroup = new SeatRow();
			seatGroup.setGroupNumber(entry.getKey());
			Map<String, SeatColumn> seatEntry = entry.getValue();
			List<SeatColumn> seatColumns = new ArrayList<>();
			if (seatEntry.get("A") != null) {
				seatColumns.add(seatEntry.get("A"));
			} else {
				SeatColumn seatColumn = new SeatColumn();
				seatColumn.setSeatName("A");
				seatColumn.setSeatAvailable(false);
				seatColumns.add(seatColumn);
			}
			if (seatEntry.get("B") != null) {
				seatColumns.add(seatEntry.get("B"));
			} else {
				SeatColumn seatColumn = new SeatColumn();
				seatColumn.setSeatName("B");
				seatColumn.setSeatAvailable(false);
				seatColumns.add(seatColumn);
			}
			if (seatEntry.get("C") != null) {
				seatColumns.add(seatEntry.get("C"));
			} else {
				SeatColumn seatColumn = new SeatColumn();
				seatColumn.setSeatName("C");
				seatColumn.setSeatAvailable(false);
				seatColumns.add(seatColumn);
			}
			if (seatEntry.get("D") != null) {
				seatColumns.add(seatEntry.get("D"));
			} else {
				SeatColumn seatColumn = new SeatColumn();
				seatColumn.setSeatName("D");
				seatColumn.setSeatAvailable(false);
				seatColumns.add(seatColumn);
			}
			seatGroup.setAvailableSeat(seatColumns);
			seatGroupList.add(seatGroup);
		}
		return seatGroupList;
	}

	private List<SeatRow> populateSeatGroupThreeCrossThree(Map<Integer, Map<String, SeatColumn>> seatRows) {
		List<SeatRow> seatRowList = new ArrayList<>();
		for (Map.Entry<Integer, Map<String, SeatColumn>> entry : seatRows.entrySet()) {
			SeatRow seatRow = new SeatRow();
			seatRow.setGroupNumber(entry.getKey());
			Map<String, SeatColumn> seatEntry = entry.getValue();
			List<SeatColumn> seatColumns = new ArrayList<>();
			if (seatEntry.get("A") != null) {
				seatColumns.add(seatEntry.get("A"));
			} else {
				SeatColumn seatColumn = new SeatColumn();
				seatColumn.setSeatName("A");
				seatColumn.setSeatAvailable(false);
				seatColumns.add(seatColumn);
			}
			if (seatEntry.get("B") != null) {
				seatColumns.add(seatEntry.get("B"));
			} else {
				SeatColumn seatColumn = new SeatColumn();
				seatColumn.setSeatName("B");
				seatColumn.setSeatAvailable(false);
				seatColumns.add(seatColumn);
			}
			if (seatEntry.get("C") != null) {
				seatColumns.add(seatEntry.get("C"));
			} else {
				SeatColumn seatColumn = new SeatColumn();
				seatColumn.setSeatName("C");
				seatColumn.setSeatAvailable(false);
				seatColumns.add(seatColumn);
			}
			if (seatEntry.get("D") != null) {
				seatColumns.add(seatEntry.get("D"));
			} else {
				SeatColumn seatColumn = new SeatColumn();
				seatColumn.setSeatName("D");
				seatColumn.setSeatAvailable(false);
				seatColumns.add(seatColumn);
			}
			if (seatEntry.get("E") != null) {
				seatColumns.add(seatEntry.get("E"));
			} else {
				SeatColumn seatColumn = new SeatColumn();
				seatColumn.setSeatName("E");
				seatColumn.setSeatAvailable(false);
				seatColumns.add(seatColumn);
			}
			if (seatEntry.get("F") != null) {
				seatColumns.add(seatEntry.get("F"));
			} else {
				SeatColumn seatColumn = new SeatColumn();
				seatColumn.setSeatName("F");
				seatColumn.setSeatAvailable(false);
				seatColumns.add(seatColumn);
			}
			seatRow.setAvailableSeat(seatColumns);
			seatRowList.add(seatRow);
		}
		return seatRowList;
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

	private ResponseDto populateBoardingPass(CheckInPassengersResponse checkInPassengers, String pnr, String sign,
			BookingDetailDto bookingDetailDto) throws RemoteException {
		OperationManagerStub operationManagerStub = new OperationManagerStub(
				env.getProperty(Constants.OPERATION_MANAGER));
		OperationManagerStub.GetBarCodedBoardingPassesMultipleRequest barCodedBoardingPassesMultipleRequest = new OperationManagerStub.GetBarCodedBoardingPassesMultipleRequest();
		GetBarCodedBoardingPassesMultipleRequestData getBarCodedBoardingPassesMultipleRequestData = new GetBarCodedBoardingPassesMultipleRequestData();
		ArrayOfBoardingPassRequest boardingPassRequestList = new ArrayOfBoardingPassRequest();
		CheckInMultiplePassengerResponse[] checkInMultiplePassengerResponse = checkInPassengers
				.getCheckInPassengersResponseData().getCheckInMultiplePassengerResponseList()
				.getCheckInMultiplePassengerResponse();
		ResponseDto responseDto = new ResponseDto();
		responseDto.setValidResponse(true);
		for (CheckInMultiplePassengerResponse checkInPassengerResponse : Arrays
				.asList(checkInMultiplePassengerResponse)) {
			for (CheckInPaxResponse checkInPaxResponse : Arrays
					.asList(checkInPassengerResponse.getCheckInPaxResponseList().getCheckInPaxResponse())) {
				if (checkInPaxResponse.getErrorList().getCheckInError() == null) {
					BoardingPassRequest boardingPassRequest = new BoardingPassRequest();
					boardingPassRequest.setActiveOnly(false);
					boardingPassRequest.setGetTotalCount(false);
					boardingPassRequest.setBarCodeType(BarCodeType.S2D);
					boardingPassRequest.setLastID(0);
					boardingPassRequest.setPageSize((short) 0);
					boardingPassRequest.setRecordLocator(pnr);
					boardingPassRequest.setName(checkInPaxResponse.getName());
					for (JourneyDetail journeyDetail : bookingDetailDto.getJourneyDetails()) {
						if (journeyDetail.getDepartureStation().equalsIgnoreCase(
								checkInPassengerResponse.getInventoryLegKey().getDepartureStation())) {
							if (journeyDetail.isVia()) {
								checkInPassengerResponse.getInventoryLegKey()
										.setArrivalStation(journeyDetail.getViaStation());
							}
						}
					}
					boardingPassRequest.setInventoryLegKey(checkInPassengerResponse.getInventoryLegKey());
					boardingPassRequest.setBySegment(true);
					boardingPassRequest.setPrintSameDayReturn(false);
					boardingPassRequest.setInitial(false);
					boardingPassRequest.setCurrentTime(Calendar.getInstance());
					boardingPassRequest.setInventoryLegKeyDepartureDateTime(Calendar.getInstance());
					boardingPassRequestList.addBoardingPassRequest(boardingPassRequest);
				} else {
					String errorMessage = Arrays.asList(checkInPaxResponse.getErrorList().getCheckInError()).get(0)
							.getErrorMessage();
					responseDto.setValidResponse(false);
					responseDto.setErrorMessage(errorMessage);
					break;

				}
			}

		}
		if (responseDto.isValidResponse()) {
			getBarCodedBoardingPassesMultipleRequestData.setBoardingPassRequestList(boardingPassRequestList);
			barCodedBoardingPassesMultipleRequest
					.setGetBarCodedBoardingPassesMultipleRequestData(getBarCodedBoardingPassesMultipleRequestData);
			Signature signature = new Signature();
			signature.setSignature(sign);
			ContractVersion cv = new ContractVersion();
			cv.setContractVersion(Constants.CONTRACT_VERSION);
			GetBarCodedBoardingPassesMultipleResponse barCodedBoardingPassesMultipleResponse = operationManagerStub
					.getBarCodedBoardingPassesMultiple(barCodedBoardingPassesMultipleRequest, cv, signature);
			return populateBoardingPassDetail(barCodedBoardingPassesMultipleResponse);
		}
		return responseDto;

	}

	private ResponseDto populateBoardingPassDetail(
			GetBarCodedBoardingPassesMultipleResponse barCodedBoardingPassesMultipleResponse) {

		String stringCorbaSupported = env.getProperty(Constants.CORBA_SUPPORTED);
		boolean isCORBASupported = stringCorbaSupported != null ? Boolean.valueOf(stringCorbaSupported) : true;

		List<BoardingPass> boardingPasses = new ArrayList<>();
		GetBarCodedBoardingPassesMultipleResponseData getBarCodedBoardingPassesMultipleResponseData = barCodedBoardingPassesMultipleResponse
				.getGetBarCodedBoardingPassesMultipleResponseData();
		ArrayOfBarCodedBoardingPass boardingPassRequestList = getBarCodedBoardingPassesMultipleResponseData
				.getBoardingPassRequestList();

		for (BarCodedBoardingPass barCodedBoardingPass : Arrays
				.asList(boardingPassRequestList.getBarCodedBoardingPass())) {

			for (BarCodedBoardingPassSegment barCodedBoardingPassSegment : Arrays
					.asList(barCodedBoardingPass.getSegments().getBarCodedBoardingPassSegment())) {
				OperationManagerStub.BarCodedBoardingPassLeg firstLeg = Arrays
						.asList(barCodedBoardingPassSegment.getLegs().getBarCodedBoardingPassLeg()).get(0);
				BoardingPass boardingPass = new BoardingPass();
				Name name = barCodedBoardingPass.getName();
				boardingPass.setFirstName(name.getFirstName());
				boardingPass.setLastName(name.getLastName());
				boardingPass.setTitle(name.getTitle());
				boardingPass.setArrivalStation(barCodedBoardingPassSegment.getArrivalStation());
				boardingPass.setDepartureStation(barCodedBoardingPassSegment.getDepartureStation());
				boardingPass.setGate(barCodedBoardingPassSegment.getDepartureGate());
				boardingPass.setRecordLocator(barCodedBoardingPass.getRecordLocator());
				boardingPass.setUnitDesignator(firstLeg.getSeatInfo());
				boardingPass.setBoardingDateTime(barCodedBoardingPassSegment.getBoardingTime());
				boardingPass.setDepartureDateTime(barCodedBoardingPassSegment.getDepartureTime());
				boardingPass.setArrivalDateTime(barCodedBoardingPassSegment.getArrivalTime());
				if (firstLeg.getSSRs() != null && firstLeg.getSSRs().getManifestLegSSR() != null) {
					boardingPass.setSsrs(populateSsrs(firstLeg.getSSRs().getManifestLegSSR()));
				}
				boardingPass.setSequenceNumber(String.valueOf(firstLeg.getBoardingSequence()));
				if (barCodedBoardingPassSegment.getInventoryLegKey() != null) {
					boardingPass.setCarrierCode(barCodedBoardingPassSegment.getInventoryLegKey().getCarrierCode());
					boardingPass.setFlightNumber(barCodedBoardingPassSegment.getInventoryLegKey().getFlightNumber());
					boardingPass.setArrivalStationAbbr(
							barCodedBoardingPassSegment.getInventoryLegKey().getArrivalStation());
					boardingPass.setDepartureStationAbbr(
							barCodedBoardingPassSegment.getInventoryLegKey().getDepartureStation());
				}

				if (barCodedBoardingPassSegment.getBarCodes() != null && !isCORBASupported) {
					boardingPass.setBarcodedString(
							barCodedBoardingPassSegment.getBarCodes().getBarCode()[0].getBarCodeData());
				}
				if (isCORBASupported) {
					IATABoardingPassBarcode iataBoardingPassBarcode = new IATABoardingPassBarcode(boardingPass);
					List<IATABoardingPassBarcode> barcodeList = new ArrayList<>();
					barcodeList.add(iataBoardingPassBarcode);
					boardingPass.setBarcodedString(
							contentDataBuilder.buildContentResponse(barcodeList, Template.BOARDING_PASS_BARCODE_STREAM)
									.getDatum().get(0));
				}
				boardingPasses.add(boardingPass);
			}
		}
		ResponseDto responseDto = new ResponseDto();
		responseDto.setValidResponse(true);
		responseDto.setBoardingPassList(boardingPasses);
		return responseDto;
	}
}
