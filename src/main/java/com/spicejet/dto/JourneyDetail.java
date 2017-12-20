package com.spicejet.dto;

import java.util.Calendar;

import javax.xml.bind.annotation.XmlElement;

public class JourneyDetail {

	String pnr;

	String flightNumber;

	String carrierCode;

	boolean onTime;

	String departureStation;

	Calendar departureDateTime;

	String departureTerminal;

	String departureGate;

	String arrivalStation;

	Calendar arrivalDateTime;

	String arrivalTerminal;

	@XmlElement(name = "ArrivalGate")
	String arrivalGate;

	@XmlElement(name = "IsVia")
	boolean isVia;

	@XmlElement(name = "ViaStation")
	String viaStation;

	public boolean isVia() {
		return isVia;
	}

	public void setVia(boolean isVia) {
		this.isVia = isVia;
	}

	public String getViaStation() {
		return viaStation;
	}

	public void setViaStation(String viaStation) {
		this.viaStation = viaStation;
	}

	public String getPnr() {
		return pnr;
	}

	public void setPnr(String pnr) {
		this.pnr = pnr;
	}

	public String getFlightNumber() {
		return flightNumber;
	}

	public void setFlightNumber(String flightNumber) {
		this.flightNumber = flightNumber;
	}

	public String getCarrierCode() {
		return carrierCode;
	}

	public void setCarrierCode(String carrierCode) {
		this.carrierCode = carrierCode;
	}

	public boolean isOnTime() {
		return onTime;
	}

	public void setOnTime(boolean onTime) {
		this.onTime = onTime;
	}

	public String getDepartureStation() {
		return departureStation;
	}

	public void setDepartureStation(String departureStation) {
		this.departureStation = departureStation;
	}

	public String getDepartureTerminal() {
		return departureTerminal;
	}

	public void setDepartureTerminal(String departureTerminal) {
		this.departureTerminal = departureTerminal;
	}

	public String getDepartureGate() {
		return departureGate;
	}

	public void setDepartureGate(String departureGate) {
		this.departureGate = departureGate;
	}

	public String getArrivalStation() {
		return arrivalStation;
	}

	public void setArrivalStation(String arrivalStation) {
		this.arrivalStation = arrivalStation;
	}

	public String getArrivalTerminal() {
		return arrivalTerminal;
	}

	public void setArrivalTerminal(String arrivalTerminal) {
		this.arrivalTerminal = arrivalTerminal;
	}

	public String getArrivalGate() {
		return arrivalGate;
	}

	public void setArrivalGate(String arrivalGate) {
		this.arrivalGate = arrivalGate;
	}

	public Calendar getDepartureDateTime() {
		return departureDateTime;
	}

	public void setDepartureDateTime(Calendar departureDateTime) {
		this.departureDateTime = departureDateTime;
	}

	public Calendar getArrivalDateTime() {
		return arrivalDateTime;
	}

	public void setArrivalDateTime(Calendar arrivalDateTime) {
		this.arrivalDateTime = arrivalDateTime;
	}

}
