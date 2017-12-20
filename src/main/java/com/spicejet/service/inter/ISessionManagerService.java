package com.spicejet.service.inter;

import java.rmi.RemoteException;

import org.apache.axis2.AxisFault;

import com.spicejet.dto.AuthenticationResponseDto;

/**
 * Service to facilitates Session specific operations.
 */
public interface ISessionManagerService {

    /**
     * Method to logon on Navitaire.
     *
     * @return AuthenticationResponseDto @AuthenticationResponseDto
     * @throws AxisFault - In case of any Axis Fault occurs. i.e. in calling Navitaire API.
     * @throws RemoteException - In case of any other network error occurs.
     */
	public AuthenticationResponseDto logon() throws AxisFault, RemoteException;

    /**
     * Method ends session with Navitaire. Obtained Signature will no longer be valid.
     *
     * @param sign - Signature which was obtained at the time of Logon from BookingManagerResource @BookingManagerResource.
     * @throws AxisFault - In case of any Axis Fault occurs. i.e. in calling Navitaire API.
     * @throws RemoteException - In case of any other network error occurs.
     */
	public void logout(String sign) throws AxisFault, RemoteException;
}
