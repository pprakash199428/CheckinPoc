package com.spicejet.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

public class PassengerDetail {

	short passengerNumber;

	long passengerId;

	String title;

	String firstName;

	String middleName;

	String lastName;

	String suffix;

	boolean primaryLeader;

	List<String> assignedSeats;

	List<Boolean> liftStatus;

	List<String> paxSSRList;

	String sequenceNumber;

	String paxType;

	Map<String, String> passengerJourney;

	public short getPassengerNumber() {
		return passengerNumber;
	}

	public void setPassengerNumber(short passengerNumber) {
		this.passengerNumber = passengerNumber;
	}

	public long getPassengerId() {
		return passengerId;
	}

	public void setPassengerId(long passengerId) {
		this.passengerId = passengerId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getMiddleName() {
		return middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public boolean isPrimaryLeader() {
		return primaryLeader;
	}

	public void setPrimaryLeader(boolean primaryLeader) {
		this.primaryLeader = primaryLeader;
	}

	public List<String> getAssignedSeats() {
		if (assignedSeats == null) {
			assignedSeats = new ArrayList<String>();
		}
		return assignedSeats;
	}

	public void setAssignedSeats(List<String> assignedSeats) {
		this.assignedSeats = assignedSeats;
	}

	public void setPaxType(String paxType) {
		this.paxType = paxType;
	}

	public String getPaxType() {
		return paxType;
	}

	public String getSequenceNumber() {
		return sequenceNumber;
	}

	public void setSequenceNumber(String sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}

	public String getSuffix() {
		return suffix;
	}

	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

	public List<String> getPaxSSRList() {
		if (paxSSRList == null) {
			paxSSRList = new ArrayList<String>();
		}
		return paxSSRList;
	}

	public void setPaxSSRList(List<String> paxSSRList) {
		this.paxSSRList = paxSSRList;
	}

	public List<Boolean> getLiftStatus() {
		if (liftStatus == null) {
			liftStatus = new ArrayList<Boolean>();
		}
		return liftStatus;
	}

	public void setLiftStatus(List<Boolean> liftStatus) {
		this.liftStatus = liftStatus;
	}

	public Map<String, String> getPassengerJourney() {
		if (passengerJourney == null) {
			passengerJourney = new HashMap<String, String>();
		}
		return passengerJourney;
	}

	public void setPassengerJourney(Map<String, String> passengerJourney) {
		this.passengerJourney = passengerJourney;
	}

}
