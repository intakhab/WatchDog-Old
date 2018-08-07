package com.app.filedog.dto;

public class ResponseDto {

	private boolean isCommandRun = false;
	private boolean responseError = false;
	private String  responseErrorStr="";
	private String responseFilePath="";

	public boolean isCommandRun() {
		return isCommandRun;
	}

	public void setCommandRun(boolean isCommandRun) {
		this.isCommandRun = isCommandRun;
	}

	public boolean isResponseError() {
		return responseError;
	}

	public void setResponseError(boolean responseError) {
		this.responseError = responseError;
	}

	public String getResponseErrorStr() {
		return responseErrorStr;
	}

	public void setResponseErrorStr(String responseErrorStr) {
		this.responseErrorStr = responseErrorStr;
	}

	public String getResponseFilePath() {
		return responseFilePath;
	}

	public void setResponseFilePath(String responseFilePath) {
		this.responseFilePath = responseFilePath;
	}

	

}
