package com.app.filedog.domain;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
/***
 * 
 * @author intakhabalam.s@hcl.com
 *
 */
@XmlRootElement(name = "CISDocument")
@XmlAccessorType(XmlAccessType.FIELD)
public class GLTransactions {

	@XmlElement
	private GLTransaction[] GLTransaction;

	public GLTransaction[] getGLTransaction() {
		return GLTransaction;
	}

	public void setGLTransaction(GLTransaction[] GLTransaction) {
		this.GLTransaction = GLTransaction;
	}

	@Override
	public String toString() {
		return "ClassPojo [GLTransaction = " + GLTransaction + "]";
	}
}
