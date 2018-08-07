package com.app.filedog.domain;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType (XmlAccessType.FIELD)
public class Entity {
	@XmlElement
	private Load Load;

	public Load getLoad() {
		return Load;
	}

	public void setLoad(Load Load) {
		this.Load = Load;
	}

	@Override
	public String toString() {
		return "ClassPojo [Load = " + Load + "]";
	}
}
