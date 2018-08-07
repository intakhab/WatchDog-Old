package com.app.filedog.domain;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

 @XmlRootElement
@XmlAccessorType (XmlAccessType.FIELD)
 public class Load {
	@XmlElement
	private String CarrierCode;
	@XmlElement
	private String SystemLoadID;

	public String getCarrierCode() {
		return CarrierCode;
	}

	public void setCarrierCode(String CarrierCode) {
		this.CarrierCode = CarrierCode;
	}

	public String getSystemLoadID() {
		return SystemLoadID;
	}

	public void setSystemLoadID(String SystemLoadID) {
		this.SystemLoadID = SystemLoadID;
	}

	@Override
	public String toString() {
		return "ClassPojo [CarrierCode = " + CarrierCode + ", SystemLoadID = " + SystemLoadID + "]";
	}
}
