package com.spicejet.service.inter;

import java.rmi.RemoteException;

import com.spicejet.dto.BookingDetailDto;
import com.spicejet.dto.ResponseDto;
import com.spicejet.dto.SeatAvailabilityResponseDto;

/**
 * Service to facilitates all the Operation Manager Operations on Navitaire.
 *
 */
public interface IOperationManagerService {

    /**
     * Method will fetch seat details on the basis of passed parameters.
     *
     * @param sign - Signature which was obtained at the time of Logon from BookingManagerResource @BookingManagerResource.
     * @param std - Standard Time of Departure
     * @param dStation - Destination Station
     * @param aStation - Arrival Station
     * @param flightNumber - Flight Number
     * @param carrierCode - Carrier Code
     * @return - SeatAvailabilityResponseDto @SeatAvailabilityResponseDto
     * @throws RemoteException  - In case of any error occurs.
     */
	SeatAvailabilityResponseDto fetchSeatDetails(String sign, String std, String dStation, String aStation,
			String flightNumber, String carrierCode) throws RemoteException;

    /**
     * Method will do check-in on Navitaire against passed parameters.
     *
     * @param sign - Signature which was obtained at the time of Logon from BookingManagerResource @BookingManagerResource.
     * @param pnr - PNR for which we need to fetch bookings.
     * @param booking - Valid Boooking XML that will be un-unMarshall against BookingDetailDto.
     * @return - ResponseDto @ResponseDto
     * @throws RemoteException - In case of any error occurs.
     */
	ResponseDto checkIn(String sign, String pnr,BookingDetailDto booking) throws RemoteException;

    /**
     * Method to un-assign seat from Navitaire against passed parameters.
     *
     * @param sign - Signature which was obtained at the time of Logon from BookingManagerResource @BookingManagerResource.
     * @param pnr - PNR for which we need to fetch bookings.
     * @param bookingDetailDto - Valid Boooking XML that will be un-unMarshall against BookingDetailDto.
     * @return - ResponseDto @ResponseDto
     * @throws RemoteException - In case of any error ocuurs.
     */
	ResponseDto unAssign(String sign, String pnr, BookingDetailDto bookingDetailDto) throws RemoteException;

    /**
     * Method to assign seat to Navitaire against passed parameters.
     *
     * @param sign - Signature which was obtained at the time of Logon from BookingManagerResource @BookingManagerResource.
     * @param pnr - PNR for which we need to fetch bookings.
     * @param booking - Valid Boooking XML that will be un-unMarshall against BookingDetailDto.
     * @return - ResponseDto @ResponseDto
     * @throws RemoteException - In case of any error ocuurs.
     */
	ResponseDto assignSeats(String sign, String pnr, BookingDetailDto bookingDetailDto)throws RemoteException;

    /**
     * Method to add Baggage against passed Parameters.
     *
     * @param baggageCheckIn
     * @param sign
     * @return - ResponseDto @ResponseDto
     * @throws RemoteException - In case of any error occurs.
     */

}
