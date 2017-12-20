package com.spicejet.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "AuthenticationResponseDto")
public class AuthenticationResponseDto {

	public AuthenticationResponseDto() {
	}

	public AuthenticationResponseDto(String token, String signature) {
		super();
		this.token = token;
		this.signature = signature;
	}

	@XmlElement(name = "token")
	String token;

	@XmlElement(name = "signature")
	String signature;

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

	@Override
	public String toString() {
		return "Token : " + this.token + ", Signature : " + this.signature;
	}

}