package com.spicejet.dto;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="twitter_pnr")
public class Status {
	
@Column	
String pnr;
@Column
String userid;
@Column
String status;
@Column(name="ceated_date")
String createdDate;
@Column(name="modified_date")
String modifiedDate;
public String getPnr() {
	return pnr;
}
public void setPnr(String pnr) {
	this.pnr = pnr;
}
public String getUserid() {
	return userid;
}
public void setUserid(String userid) {
	this.userid = userid;
}
public String getStatus() {
	return status;
}
public void setStatus(String status) {
	this.status = status;
}
public String getCreatedDate() {
	return createdDate;
}
public void setCreatedDate(String createdDate) {
	this.createdDate = createdDate;
}
public String getModifiedDate() {
	return modifiedDate;
}
public void setModifiedDate(String modifiedDate) {
	this.modifiedDate = modifiedDate;
}


}
