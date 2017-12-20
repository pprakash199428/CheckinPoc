package com.spicejet.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "SeatColumn")
public class SeatColumn implements Comparable<SeatColumn> {

	@XmlElement(name = "Assignable")
	boolean assignable;

	@XmlElement(name = "SeatAvailabilityType")
	String seatAvailabilityType;

	@XmlElement(name = "IsSeatAvailable")
	boolean isSeatAvailable;

	@XmlElement(name = "SeatName")
	String seatName;

	@XmlElement(name = "seatGroup")
	short seatGroup;

	@XmlElement(name = "seatType")
	String seatType;

	public boolean isAssignable() {
		return assignable;
	}

	public void setAssignable(boolean assignable) {
		this.assignable = assignable;
	}

	public String getSeatAvailabilityType() {
		return seatAvailabilityType;
	}

	public void setSeatAvailabilityType(String seatAvailabilityType) {
		this.seatAvailabilityType = seatAvailabilityType;
	}

	public boolean isSeatAvailable() {
		return isSeatAvailable;
	}

	public void setSeatAvailable(boolean isSeatAvailable) {
		this.isSeatAvailable = isSeatAvailable;
	}

	public String getSeatName() {
		return seatName;
	}

	public void setSeatName(String seatName) {
		this.seatName = seatName;
	}

	public short getSeatGroup() {
		return seatGroup;
	}

	public void setSeatGroup(short seatGroup) {
		this.seatGroup = seatGroup;
	}

	public String getSeatType() {
		return seatType;
	}

	public void setSeatType(String seatType) {
		this.seatType = seatType;
	}

	@Override
	public int compareTo(SeatColumn o) {
		return this.seatName.compareTo(o.seatName);
	}

}
