package com.spicejet.dto;

import java.util.Calendar;
import java.util.List;

import com.spicejet.util.Constants;
import com.spicejet.util.DateUtil;

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

    Calendar departureDateTime;

    Calendar arrivalDateTime;

    Calendar boardingDateTime;

    String sequenceNumber;

    String recordLocator;

    String barcodedString;

    List<String> ssrs;

    public String getFirstName() {
        return firstName.toUpperCase();
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName.toUpperCase();
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getDepartureStation() {
        return departureStation.toUpperCase();
    }

    public void setDepartureStation(String departureStation) {
        this.departureStation = departureStation;
    }

    public String getArrivalStation() {
        return arrivalStation.toUpperCase();
    }

    public void setArrivalStation(String arrivalStation) {
        this.arrivalStation = arrivalStation;
    }

    public String getDepartureStationAbbr() {
        return departureStationAbbr.toUpperCase();
    }

    public void setDepartureStationAbbr(String departureStationAbbr) {
        this.departureStationAbbr = departureStationAbbr;
    }

    public String getArrivalStationAbbr() {
        return arrivalStationAbbr.toUpperCase();
    }

    public void setArrivalStationAbbr(String arrivalStationAbbr) {
        this.arrivalStationAbbr = arrivalStationAbbr;
    }

    public String getUnitDesignator() {
        return unitDesignator.toUpperCase();
    }

    public void setUnitDesignator(String unitDesignator) {
        this.unitDesignator = unitDesignator;
    }

    public String getFlightNumber() {
        return flightNumber.toUpperCase();
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

    public Calendar getDepartureDateTime() {
        return departureDateTime;
    }

    public String getDepartureDisplayDateTime() {
        return DateUtil.formatTimeForBoardingPass(this.getDepartureDateTime());
    }

    public String getDepartureDisplayDate() {
        return DateUtil.formatDateForBoardingPass(this.getDepartureDateTime());
    }

    public void setDepartureDateTime(Calendar departureDateTime) {
        this.departureDateTime = departureDateTime;
    }

    public Calendar getArrivalDateTime() {
        return arrivalDateTime;
    }

    public String getArrivalDisplayDateTime() {
        return DateUtil.formatTimeForBoardingPass(this.getArrivalDateTime());
    }

    public void setArrivalDateTime(Calendar arrivalDateTime) {
        this.arrivalDateTime = arrivalDateTime;
    }

    public Calendar getBoardingDateTime() {
        return boardingDateTime;
    }

    public String getBoardingDisplayDateTime() {
        return DateUtil.formatTimeForBoardingPass(this.getBoardingDateTime());
    }

    public void setBoardingDateTime(Calendar boardingDateTime) {
        this.boardingDateTime = boardingDateTime;
    }

    public String getSequenceNumber() {
        return sequenceNumber.toUpperCase();
    }

    public void setSequenceNumber(String sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public String getRecordLocator() {
        return recordLocator.toUpperCase();
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

    public List<String> getSsrs() {
        return ssrs;
    }

    public String getSSRs(){
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
