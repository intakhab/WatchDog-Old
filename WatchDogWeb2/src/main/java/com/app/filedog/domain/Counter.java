package com.app.filedog.domain;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "counter")
public class Counter {
	private String autonumber="0000";

	public Counter() {

	}

	public Counter(String autoNumber) {
		this.setAutonumber(autoNumber);
	}

	public String getAutonumber() {
		return autonumber;
	}

	public void setAutonumber(String autonumber) {
		this.autonumber = autonumber;
	}

}
