package com.spicejet.dto;

public class PassSetup {
	
	String passengerName;
	String flight;
	String flight1;
	String depart;
	String arrival;
	String gate;
	String gate1;
	String pnr;
	String seat;
	String seq;
	public String getPnr() {
		return pnr;
	}
	public void setPnr(String pnr) {
		this.pnr = pnr;
	}
	public String getPassengerName() {
		return passengerName.toUpperCase();
	}
	public void setPassengerName(String passengerName) {
		this.passengerName = passengerName;
	}
	public String getFlight() {
		return flight.toUpperCase();
	}
	public void setFlight(String flight) {
		this.flight = flight;
	}
	public String getFlight1() {
		return flight1.toUpperCase();
	}
	public void setFlight1(String flight1) {
		this.flight1 = flight1;
	}
	public String getDepart() {
		return depart;
	}
	public void setDepart(String depart) {
		this.depart = depart;
	}
	public String getArrival() {
		return arrival;
	}
	public void setArrival(String arrival) {
		this.arrival = arrival;
	}
	public String getGate() {
		return gate.toUpperCase();
	}
	public void setGate(String gate) {
		this.gate = gate;
	}
	public String getGate1() {
		return gate1.toUpperCase();
	}
	public void setGate1(String gate1) {
		this.gate1 = gate1;
	}
	public String getSeat() {
		return seat.toUpperCase();
	}
	public void setSeat(String seat) {
		this.seat = seat;
	}
	public String getSeq() {
		return seq.toUpperCase();
	}
	public void setSeq(String seq) {
		this.seq = seq;
	}
	

}
