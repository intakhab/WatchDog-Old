package com.app.filedog.domain;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "CISDocument")
@XmlAccessorType (XmlAccessType.FIELD)
public class ProcessGLTransactionCommit {
	
	@XmlElement
	private ApiHeader ApiHeader=new ApiHeader();
	
	@XmlElement
	private String[] SystemTransactionID;


	public String[] getSystemTransactionID() {
		return SystemTransactionID;
	}

	public void setSystemTransactionID(String[] SystemTransactionID) {
		this.SystemTransactionID = SystemTransactionID;
	}

	public ApiHeader getApiHeader() {
		return ApiHeader;
	}

	public void setApiHeader(ApiHeader ApiHeader) {
		this.ApiHeader = ApiHeader;
	}
}
@XmlRootElement(name = "ApiHeader")
@XmlAccessorType (XmlAccessType.FIELD)
class ApiHeader {
	@XmlElement
	private String ClientID="";
	
	@XmlElement
	private String OperationName="processGLTransactionCommit";

	public String getOperationName() {
		return OperationName;
	}

	public void setOperationName(String OperationName) {
		this.OperationName = OperationName;
	}

	public String getClientID() {
		return ClientID;
	}

	public void setClientID(String ClientID) {
		this.ClientID = ClientID;
	}

}
