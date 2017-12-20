package com.spicejet.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "ResponseDto")
public class ResponseDto {

	@XmlElement(name = "IsValidResponse")
	boolean isValidResponse;

	@XmlElement(name = "ErrorMessage")
	String errorMessage;
	
	
	public boolean isValidResponse() {
		return isValidResponse;
	}

	public void setValidResponse(boolean isValidResponse) {
		this.isValidResponse = isValidResponse;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	

}
