package com.app.filedog.domain;

import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "reports")
@XmlAccessorType(XmlAccessType.FIELD)
public class Reports {

	@XmlElement(name = "report")
	private List<Report> reports;

	public List<Report> getReports() {
		if(reports!=null) {
		   Collections.reverse(reports);
		}
		return reports;
	}

	public void setReports(List<Report> report) {
		this.reports = report;
	}

}
