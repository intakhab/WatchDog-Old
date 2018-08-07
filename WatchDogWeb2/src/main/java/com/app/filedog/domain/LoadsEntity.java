package com.app.filedog.domain;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "CISDocument")
@XmlAccessorType (XmlAccessType.FIELD)
public class LoadsEntity {
	
	@XmlElement
	private Entity[] Entity;


	public Entity[] getEntity() {
		return Entity;
	}

	public void setEntity(Entity[] Entity) {
		this.Entity = Entity;
	}
}

