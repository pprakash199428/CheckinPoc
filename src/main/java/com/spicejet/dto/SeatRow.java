package com.spicejet.dto;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "SeatRow")
public class SeatRow implements Comparable<SeatRow> {

	@XmlElement(name = "GroupNumber")
	Integer groupNumber;

	@XmlElement(name = "AvailableSeat")
	List<SeatColumn> availableSeat;

	public Integer getGroupNumber() {
		return groupNumber;
	}

	public void setGroupNumber(Integer groupNumber) {
		this.groupNumber = groupNumber;
	}

	public List<SeatColumn> getAvailableSeat() {
		return availableSeat;
	}

	public void setAvailableSeat(List<SeatColumn> availableSeat) {
		this.availableSeat = availableSeat;
	}

	@Override
	public int compareTo(SeatRow o) {
		return this.groupNumber.compareTo(o.groupNumber);
	}
}
