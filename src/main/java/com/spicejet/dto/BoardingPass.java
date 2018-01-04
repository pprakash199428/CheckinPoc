package com.spicejet.dto;

import com.spicejet.util.Constants; 
import com.spicejet.util.DateUtil;

import java.util.Calendar;
import java.util.List;

public class BoardingPass {

    String firstName;

    String lastName;

    String departureStation;

    String arrivalStation;

    String departureStationAbbr;

    String arrivalStationAbbr;

    String unitDesignator;

    String flightNumber;

    String carrierCode;

    String gate;

    String departureDateTime;

    String arrivalDateTime;	

    String boardingDateTime;

    String sequenceNumber;

    String recordLocator;

    String barcodedString;

    List<String> ssrs;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getDepartureStation() {
        return departureStation;
    }

    public void setDepartureStation(String departureStation) {
        this.departureStation = departureStation;
    }

    public String getArrivalStation() {
        return arrivalStation;
    }

    public void setArrivalStation(String arrivalStation) {
        this.arrivalStation = arrivalStation;
    }

    public String getDepartureStationAbbr() {
        return departureStationAbbr;
    }

    public void setDepartureStationAbbr(String departureStationAbbr) {
        this.departureStationAbbr = departureStationAbbr;
    }

    public String getArrivalStationAbbr() {
        return arrivalStationAbbr;
    }

    public void setArrivalStationAbbr(String arrivalStationAbbr) {
        this.arrivalStationAbbr = arrivalStationAbbr;
    }

    public String getUnitDesignator() {
        return unitDesignator;
    }

    public void setUnitDesignator(String unitDesignator) {
        this.unitDesignator = unitDesignator;
    }

    public String getFlightNumber() {
        return flightNumber;
    }

    public void setFlightNumber(String flightNumber) {
        this.flightNumber = flightNumber;
    }

    public String getCarrierCode() {
        return carrierCode.toUpperCase();
    }

    public void setCarrierCode(String carrierCode) {
        this.carrierCode = carrierCode;
    }

    public String getGate() {
        return gate.toUpperCase();
    }

    public void setGate(String gate) {
        this.gate = gate;
    }

    public String getDepartureDateTime() {
        return departureDateTime;
    }

    public String getDepartureDisplayDateTime() {
        return departureDateTime;
    }

    public String getDepartureDisplayDate() {
        return departureDateTime;
    }

    public void setDepartureDateTime(String departureDateTime) {
        this.departureDateTime = departureDateTime;
    }

    public String getArrivalDateTime() {
        return arrivalDateTime;
    }

    public String getArrivalDisplayDateTime() {
        return arrivalDateTime;
    }

    public void setArrivalDateTime(String arrivalDateTime) {
        this.arrivalDateTime = arrivalDateTime;
    }

    public String getBoardingDateTime() {
        return boardingDateTime;
    }

    public String getBoardingDisplayDateTime() {
        return boardingDateTime;
    }

    public void setBoardingDateTime(String boardingDateTime) {
        this.boardingDateTime = boardingDateTime;
    }

    public String getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(String sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public String getRecordLocator() {
        return recordLocator;
    }

    public void setRecordLocator(String recordLocator) {
        this.recordLocator = recordLocator;
    }

    public String getFullName() {
        return (this.lastName + Constants.BACK_SLASH + this.firstName).toUpperCase();
    }

    public String getFromTo() {
        return (this.departureStationAbbr + Constants.BACK_SLASH + this.arrivalStationAbbr).toUpperCase();
    }

    public String getBarcodedString() {
        return barcodedString;
    }

    public void setBarcodedString(String barcodedString) {
        this.barcodedString = barcodedString;
    }

    public String getSsrs(){
        StringBuffer ssrsBuffer = new StringBuffer();
        for(String ssr : this.ssrs){
            ssrsBuffer.append("(").append(ssr).append(")").append(" ");
        }
        return ssrsBuffer.toString();
    }
    public void setSsrs(List<String> ssrs) {
        this.ssrs = ssrs;
    }
    
    public String getDisplayFlightNumber(){
    	 return this.carrierCode + this.getFlightNumber();
    }
}
