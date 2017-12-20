package com.spicejet.dto;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "JourneyListOfPnr")
public class JourneyListOfPnr {
	
	@XmlElement(name = "SingleJourney")
	List<String> singleJourney=new ArrayList<>();

	public List<String> getSingleJourney() {
		return singleJourney;
	}

	public void setSingleJourney(List<String> singleJourney) {
		this.singleJourney = singleJourney;
	}

	@Override
	public String toString() {
		return "JourneyListOfPnr [singleJourney=" + singleJourney + "]";
	}
	
	

}
