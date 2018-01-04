package com.spicejet.dto;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.spicejet.util.Constants;
import com.spicejet.util.SpiceJetUtil;

public class IATABoardingPassBarcode {

    String firstName;

    String lastName;

    String departureStation;

    String arrivalStation;

    String departureStationAbbr;

    String arrivalStationAbbr;

    String unitDesignator;

    String flightNumber;

    String carrierCode;

    String sequenceNumber;

    String recordLocator;

    int noOfLegsEncoded = 1;

    Calendar departureDateTime;

    public IATABoardingPassBarcode(BoardingPass boardingPass) {
        this.firstName = boardingPass.getFirstName();
        this.lastName = boardingPass.getLastName();
        this.departureStation = boardingPass.getDepartureStation();
        this.arrivalStation = boardingPass.getArrivalStation();
        this.departureStationAbbr = boardingPass.getDepartureStationAbbr();
        this.arrivalStationAbbr = boardingPass.getArrivalStationAbbr();
        this.unitDesignator = boardingPass.getUnitDesignator();
        this.flightNumber = boardingPass.getFlightNumber();
        this.carrierCode = boardingPass.getCarrierCode();
        this.sequenceNumber = boardingPass.getSequenceNumber();
        this.recordLocator = boardingPass.getRecordLocator();
        this.departureDateTime = boardingPass.getDepartureDateTime();
    }

    public String getDepartureStation() {
        return departureStation.toUpperCase();
    }

    public String getArrivalStation() {
        return arrivalStation.toUpperCase();
    }

    public String getDepartureStationAbbr() {
        return departureStationAbbr.toUpperCase();
    }

    public String getArrivalStationAbbr() {
        return arrivalStationAbbr.toUpperCase();
    }

    public String getUnitDesignator() {
        return SpiceJetUtil.leftPadSpacesIfRequired(unitDesignator.toUpperCase(), 4);
    }

    public String getFlightNumber() {
        return SpiceJetUtil.leftPadSpacesIfRequired(flightNumber.toUpperCase(), 5);
    }

    public String getCarrierCode() {
        return SpiceJetUtil.rightPadSpacesIfRequired(carrierCode.toUpperCase(), 3);
    }

    public String getSequenceNumber() {
        return SpiceJetUtil.rightPadSpacesIfRequired(sequenceNumber.toUpperCase(), 5);
    }

    public String getRecordLocator() {
        return SpiceJetUtil.rightPadSpacesIfRequired(recordLocator.toUpperCase(), 7);
    }

    public String getFullName() {
        return SpiceJetUtil.rightPadSpacesIfRequired((this.lastName + Constants.BACK_SLASH + this.firstName).toUpperCase(), 20);
    }

    public String getDisplayFlightNumber() {
        return this.carrierCode + this.getFlightNumber();
    }

    public int getNoOfLegsEncoded() {
        return noOfLegsEncoded;
    }

    public String getDateOfFlight() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("ddMMyyyy");
        return SpiceJetUtil.convertToJulian(simpleDateFormat.format(this.departureDateTime.getTime()));
    }
}
