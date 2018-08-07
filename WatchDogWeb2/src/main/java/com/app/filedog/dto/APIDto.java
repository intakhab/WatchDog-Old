package com.app.filedog.dto;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class APIDto {
	private String apiName;
	private List<String[]> tagNameValue;
	private String apiStrProcess;
	private boolean status = false;
	private String currDate;
	private String fileName;
	private File file;
	private boolean allTrue=false;
	private String group;
	private File outputFile;
    private List<String> systemLoadID=new ArrayList<>(0);
   	private List<String> carrierCodes=new ArrayList<>(0);
    

	public List<String> getSystemLoadID() {
		return systemLoadID;
	}

	public void setSystemLoadID(List<String> systemLoadID) {
		this.systemLoadID = systemLoadID;
	}

	public List<String> getCarrierCodes() {
		return carrierCodes;
	}

	public void setCarrierCodes(List<String> carrierCodes) {
		this.carrierCodes = carrierCodes;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public boolean isAllTrue() {
		return allTrue;
	}

	public void setAllTrue(boolean allTrue) {
		this.allTrue = allTrue;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	public String getCurrDate() {
		return currDate;
	}

	public void setCurrDate(String currDate) {
		this.currDate = currDate;
	}

	public String getApiName() {
		return apiName;
	}

	public void setApiName(String apiName) {
		this.apiName = apiName;
	}

	public String getApiStrProcess() {
		return apiStrProcess;
	}

	public void setApiStrProcess(String apiStrProcess) {
		this.apiStrProcess = apiStrProcess;
	}

	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public List<String[]> getTagNameValue() {
		return tagNameValue;
	}

	public void setTagNameValue(List<String[]> tagNameValue) {
		this.tagNameValue = tagNameValue;
	}

	public File getOutputFile() {
		return outputFile;
	}

	public void setOutputFile(File outputFile) {
		this.outputFile = outputFile;
	}

}
