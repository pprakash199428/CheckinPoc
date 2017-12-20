package com.spicejet.service.impl;

import java.rmi.RemoteException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.spicejet.dto.AuthenticationResponseDto;
import com.spicejet.kiosk.webservices.sessionManager.SessionManagerStub;
import com.spicejet.kiosk.webservices.sessionManager.SessionManagerStub.ContractVersion;
import com.spicejet.kiosk.webservices.sessionManager.SessionManagerStub.LogonRequest;
import com.spicejet.kiosk.webservices.sessionManager.SessionManagerStub.LogonRequestData;
import com.spicejet.kiosk.webservices.sessionManager.SessionManagerStub.LogonResponse;
import com.spicejet.kiosk.webservices.sessionManager.SessionManagerStub.LogoutRequest;
import com.spicejet.kiosk.webservices.sessionManager.SessionManagerStub.Signature;
import com.spicejet.service.inter.ISessionManagerService;
import com.spicejet.util.Constants;

@Service
@Qualifier("sessionService")
public class SessionManagerServiceImpl implements ISessionManagerService {

	@Autowired
	private Environment env;

	@Override
	public AuthenticationResponseDto logon() throws RemoteException {
		AuthenticationResponseDto resEntity = new AuthenticationResponseDto();
			SessionManagerStub sessionManager = new SessionManagerStub(env.getProperty(Constants.SESSION_MANAGER));
			LogonRequest request = new LogonRequest();
			LogonRequestData data = new LogonRequestData();

			data.setDomainCode(env.getProperty(Constants.DOMAIN_CODE));
			data.setAgentName(env.getProperty(Constants.AGENT_NAME));
			data.setPassword(env.getProperty(Constants.PASSWORD));
			ContractVersion cv = new ContractVersion();
			cv.setContractVersion(Constants.CONTRACT_VERSION);
			request.setLogonRequestData(data);

			LogonResponse response = sessionManager.logon(request, cv);
			resEntity = new AuthenticationResponseDto();
			resEntity.setSignature(response.getSignature());
		return resEntity;
	}

	@Override
	public void logout(String sign) throws RemoteException {

		Signature signature = new Signature();
		signature.setSignature(sign);

		ContractVersion cv = new ContractVersion();
		cv.setContractVersion(Constants.CONTRACT_VERSION);

		SessionManagerStub sessionManager = new SessionManagerStub(env.getProperty(Constants.SESSION_MANAGER));
		LogoutRequest logoutRequest = new LogoutRequest();
		sessionManager.logout(logoutRequest, cv, signature);

	}
}
