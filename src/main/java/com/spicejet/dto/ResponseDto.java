package com.spicejet.dto;

import java.util.List;


public class ResponseDto {

	boolean isValidResponse;

	String errorMessage;
	
	List<BoardingPass> boardingPassList;
	
	
	
	
	public List<BoardingPass> getBoardingPassList() {
		return boardingPassList;
	}

	public void setBoardingPassList(List<BoardingPass> boardingPassList) {
		this.boardingPassList = boardingPassList;
	}

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
