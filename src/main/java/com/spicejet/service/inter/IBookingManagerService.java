package com.spicejet.service.inter;

import java.rmi.RemoteException;

import com.spicejet.dto.BookingDetailDto;
import com.spicejet.kiosk.webservices.bookingManager.BookingManagerStub;
import com.spicejet.kiosk.webservices.bookingManager.BookingManagerStub.BookingHistory;

/**
 * Service to facilitates all the Booking Operations.
 */
public interface IBookingManagerService {

    /**
     * Method which fetches Booking against passed parameters.
     *
     * @param sign - Signature which was obtained at the time of Logon from BookingManagerResource @BookingManagerResource.
     * @param pnr - PNR for which we need to fetch bookings.
     *
     * @return - BookingManagerStub.Booking
     * @throws RemoteException - In case any error occured , it will throw that in RemoteException.
     */
    public BookingManagerStub.Booking getBooking(String sign, String pnr) throws RemoteException;
    
    public BookingHistory[] bookingHistory(String sign, long bookingId) throws RemoteException;

    public void sellSSR(BookingDetailDto bookingDetailDto, String sign, String pnr) throws RemoteException;
    
    void sellSSRWEBC(BookingDetailDto bookingDetailDto, String sign, String pnr) throws RemoteException;
}
