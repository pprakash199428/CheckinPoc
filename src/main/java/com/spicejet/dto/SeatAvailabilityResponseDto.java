package com.spicejet.dto;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "SeatAvailabilityResponseDto")
public class SeatAvailabilityResponseDto {

	@XmlElement(name = "AvailableUnits")
	int availableUnits;

	@XmlElement(name = "ArrivalStation")
	String arrivalStation;

	@XmlElement(name = "DepartureStation")
	String departureStation;

	@XmlElement(name = "PlaneType")
	String planeType;

	@XmlElement(name = "FlightNumber")
	String flightNumber;

	@XmlElement(name = "CarrierCode")
	String carrierCode;

	@XmlElement(name = "IsValidSeatMap")
	boolean isValidSeatMap;

	@XmlElement(name = "NotValidSeatMapReason")
	String notValidSeatMapReason;

	@XmlElement(name = "SeatRows")
	List<SeatRow> seatRows;

	public String getCarrierCode() {
		return carrierCode;
	}

	public void setCarrierCode(String carrierCode) {
		this.carrierCode = carrierCode;
	}

	public String getFlightNumber() {
		return flightNumber;
	}

	public void setFlightNumber(String flightNumber) {
		this.flightNumber = flightNumber;
	}

	public int getAvailableUnits() {
		return availableUnits;
	}

	public void setAvailableUnits(int availableUnits) {
		this.availableUnits = availableUnits;
	}

	public String getArrivalStation() {
		return arrivalStation;
	}

	public void setArrivalStation(String arrivalStation) {
		this.arrivalStation = arrivalStation;
	}

	public String getDepartureStation() {
		return departureStation;
	}

	public void setDepartureStation(String departureStation) {
		this.departureStation = departureStation;
	}

	public String getPlaneType() {
		return planeType;
	}

	public void setPlaneType(String planeType) {
		this.planeType = planeType;
	}

	public List<SeatRow> getSeatRows() {
		if(this.seatRows==null){
			seatRows= new ArrayList<>();
		}
		return seatRows;
	}

	public void setSeatRows(List<SeatRow> seatRows) {
		this.seatRows = seatRows;
	}

	public String getNotValidSeatMapReason() {
		return notValidSeatMapReason;
	}

	public void setNotValidSeatMapReason(String notValidSeatMapReason) {
		this.notValidSeatMapReason = notValidSeatMapReason;
	}

	public boolean isValidSeatMap() {
		return isValidSeatMap;
	}

	public void setValidSeatMap(boolean isValidSeatMap) {
		this.isValidSeatMap = isValidSeatMap;
	}

	public SeatAvailabilityResponseDto() {
		this.isValidSeatMap=true;
	}
	

}
