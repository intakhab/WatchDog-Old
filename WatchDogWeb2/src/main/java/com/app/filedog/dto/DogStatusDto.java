package com.app.filedog.dto;

import javax.xml.bind.annotation.XmlRootElement;

/***
 * 
 * @author intakhabalam.s
 *
 */
@XmlRootElement

public class DogStatusDto {
	private String serverStatus="";
	private String hostAddress="";
	private String hostName="";
	private String cononicalHostName="";
	private String userName="";

	////////////////////////////////////////////
	public String getServerStatus() {
		return serverStatus;
	}

	public void setServerStatus(String serverStatus) {
		this.serverStatus = serverStatus;
	}

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public String getCononicalHostName() {
		return cononicalHostName;
	}

	public void setCononicalHostName(String cononicalHostName) {
		this.cononicalHostName = cononicalHostName;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getHostAddress() {
		return hostAddress;
	}

	public void setHostAddress(String hostAddress) {
		this.hostAddress = hostAddress;
	}

}
