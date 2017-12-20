package com.spicejet.resources;

import java.rmi.RemoteException;

import org.apache.axis2.AxisFault;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.spicejet.dto.AuthenticationResponseDto;
import com.spicejet.service.inter.ISessionManagerService;

/**
 * Resource to expose all the endpoints required by a client for
 * all the session management related APIs of Navitaire.
 *
 * To Contact : http://<hostname or ip>:<port>/session
 *
 * This is Secured Resource , a valid session token will be needed to
 * contact this resource.
 */

@Service
public class SessionManagerResource {

	ISessionManagerService sessionManagerService;

	@Autowired
	public SessionManagerResource(ISessionManagerService sessionManagerService){
		this.sessionManagerService = sessionManagerService;
	}

    /**
     * Endpoint to logon on Navitaire..
     *
     * Produces : "application/xml"
     * Method Type Supported: GET
     * Path : "/logon"
     *
     * @return - AuthenticationResponseDto @AuthenticationResponseDto.
     */

	public AuthenticationResponseDto logon() throws AxisFault, RemoteException{
		return sessionManagerService.logon();
	}
}