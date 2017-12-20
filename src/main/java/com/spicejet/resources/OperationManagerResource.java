package com.spicejet.resources;

import org.apache.axis2.AxisFault;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.spicejet.dto.BookingDetailDto;
import com.spicejet.dto.ResponseDto;
import com.spicejet.dto.SeatAvailabilityResponseDto;
import com.spicejet.service.inter.IOperationManagerService;


/**
 * Resource to expose all the endpoints required by a client for all the
 * operation related APIs of Navitaire.
 * <p>
 * To Contact : http://<hostname or ip>:<port>/session
 * <p>
 * This is Secured Resource , a valid session token will be needed to contact
 * this resource.
 */

@Service
public class OperationManagerResource {

	Logger logger = Logger.getLogger(OperationManagerResource.class);

	@Autowired
	@Qualifier("operationManagerServiceImpl")
	private IOperationManagerService operationManagerService;

	@Autowired
	Environment env;

	/**
	 * Endpoint to get seat details from Navitaire against passed parameters.
	 * <p>
	 * Consumes : "application/x-www-form-urlencoded" Produces :
	 * "application/xml" Method Type Supported: POST Path : "/getSeatDetails"
	 *
	 * @param sign
	 *            - Signature which was obtained at the time of Logon from
	 *            BookingManagerResource @BookingManagerResource.
	 * @param std
	 *            - Standard Time Of Departure of flight(It can be fetched from
	 *            getBookings in BookingManagerResource).
	 * @param dStation
	 *            - Departure Station of Flight.
	 * @param aStation
	 *            - Arrival Station of Flight.
	 * @param flightNumber
	 *            - Flight Number
	 * @param carrierCode
	 *            - Carrier Code - Code specific to Airlines Vendor.
	 * @return - SeatAvailabilityResponseDto @SeatAvailabilityResponseDto.
	 */
	
	public SeatAvailabilityResponseDto getSeatDetails(String sign,String std,String dStation,String aStation,String flightNumber,String carrierCode) {
		logger.info("................Fetching Seat Details(/getSeatDetails).....................");
		logger.info("Signature : " + sign);
		logger.info("Time : " + std);
		logger.info("Arival Station : " + aStation);
		logger.debug("Departure Station : " + dStation);
		logger.debug("Flight Number : " + flightNumber);
		logger.debug("Carrier Code : " + carrierCode);
		SeatAvailabilityResponseDto seatAvailabilityResponseDto = new SeatAvailabilityResponseDto();
		try {
			seatAvailabilityResponseDto = operationManagerService.fetchSeatDetails(sign, std, dStation, aStation,
					flightNumber, carrierCode);
			seatAvailabilityResponseDto.setValidSeatMap(true);

		} catch (AxisFault axisFault) {
			seatAvailabilityResponseDto.setValidSeatMap(false);
			seatAvailabilityResponseDto.setNotValidSeatMapReason(env.getProperty("app.axis.fault.message"));
			logger.error("Axis Fault", axisFault);
		} catch (Exception exception) {
			seatAvailabilityResponseDto.setValidSeatMap(false);
			seatAvailabilityResponseDto.setNotValidSeatMapReason(env.getProperty("error.not.found.message"));
			logger.error("Exception", exception);
		}
		return seatAvailabilityResponseDto;
	}

	/**
	 * Endpoint to Check in Pax(s) in Navitaire against passed parameters.
	 * <p>
	 * Consumes : "application/x-www-form-urlencoded" Produces :
	 * "application/xml" Method Type Supported: POST Path : "/checkIn"
	 *
	 * @param sign
	 *            - Signature which was obtained at the time of Logon from
	 *            BookingManagerResource @BookingManagerResource.
	 * @param pnr
	 *            - PNR for which we need to checkin pax(s).
	 * @param booking
	 *            - A valid XML string containing information of Booking against
	 *            passed PNR.
	 * @return - ResponseDto @ResponseDto.
	 */

	public ResponseDto checkIn(String sign,String pnr,BookingDetailDto booking) {

		ResponseDto responseDto = new ResponseDto();
		try {
			responseDto = operationManagerService.checkIn(sign, pnr, booking);
			// responseDto.setValidResponse(true);
		} catch (AxisFault axisFault) {		
			responseDto.setValidResponse(false);
			responseDto.setErrorMessage(env.getProperty("app.axis.fault.message"));
			logger.error("Axis Fault", axisFault);
		} catch (Exception exception) {
			
			responseDto.setValidResponse(false);
			responseDto.setErrorMessage(env.getProperty("error.not.found.message"));
			logger.error("Exception", exception);
		}
		return responseDto;

	}

	/**
	 * Endpoint to Check in Pax(s) in Navitaire against passed parameters.
	 * <p>
	 * Consumes : "application/x-www-form-urlencoded" Produces :
	 * "application/xml" Method Type Supported: POST Path : "/checkIn"
	 *
	 * @param sign
	 *            - Signature which was obtained at the time of Logon from
	 *            BookingManagerResource @BookingManagerResource.
	 * @param pnr
	 *            - PNR for which we need to checkin pax(s).
	 * @param booking
	 *            - A valid XML string containing information of Booking against
	 *            passed PNR.
	 * @return - ResponseDto @ResponseDto.
	 */

	public ResponseDto assignSeats(String sign,String pnr, BookingDetailDto booking) {

		ResponseDto responseDto = new ResponseDto();
		try {
			responseDto = operationManagerService.assignSeats(sign, pnr, booking);
			if(responseDto.isValidResponse()){
				logger.info("assignSeat Success");
			}
			responseDto.setValidResponse(true);
		} catch (AxisFault axisFault) {
			responseDto.setValidResponse(false);
			responseDto.setErrorMessage(env.getProperty("app.axis.fault.message"));
			logger.info("AssignSeat UnSuccessFull");
			logger.error("Axis Fault", axisFault);
		} catch (Exception exception) {
			responseDto.setValidResponse(false);
			responseDto.setErrorMessage(env.getProperty("error.not.found.message"));
			logger.info("AssignSeat UnSuccessFull");
			logger.error("Exception", exception);
		}
		return responseDto;

	}

	/**
	 * Endpoint to Un-Assign seat(s) in Navitaire against passed parameters.
	 * <p>
	 * Consumes : "application/x-www-form-urlencoded" Produces :
	 * "application/xml" Method Type Supported: POST Path : "/checkIn"
	 *
	 * @param sign
	 *            - Signature which was obtained at the time of Logon from
	 *            BookingManagerResource @BookingManagerResource.
	 * @param pnr
	 *            - PNR for which we need to checkin pax(s).
	 * @param booking
	 *            - A valid XML string containing information of Booking against
	 *            passed PNR.
	 * @return - ResponseDto @ResponseDto.
	 */

	public ResponseDto unAssign(String sign,String pnr,BookingDetailDto bookingDetailDto) {

		ResponseDto responseDto = new ResponseDto();
		try {
			responseDto = operationManagerService.unAssign(sign, pnr, bookingDetailDto);
			if(responseDto.isValidResponse()){
				logger.info("UnAssignSeat Success");
			}
			responseDto.setValidResponse(true);
		} catch (AxisFault axisFault) {
			responseDto.setValidResponse(false);
			responseDto.setErrorMessage(env.getProperty("app.axis.fault.message"));
			logger.info("UnAssignSeat UnSuccessFull");
			logger.error("Axis Fault", axisFault);
		} catch (Exception exception) {
			responseDto.setValidResponse(false);
			responseDto.setErrorMessage(env.getProperty("error.not.found.message"));
			logger.info("UnAssignSeat UnSuccessFull");
			logger.error("Exception", exception);
		}
		return responseDto;

	}

	/**
	 * Endpoint to baggage check-in .
	 * <p>
	 * Consumes : "application/x-www-form-urlencoded" Produces :
	 * "application/xml" Method Type Supported: POST Path : "/baggageCheckIn"
	 *
	 * @param baggageCheckIn
	 *            - A valid XML string containing information of BaggageCheck-in
	 * @param sign
	 *            - Signature which was obtained at the time of Logon from
	 *            BookingManagerResource @BookingManagerResource.
	 * @return
	 */
}
