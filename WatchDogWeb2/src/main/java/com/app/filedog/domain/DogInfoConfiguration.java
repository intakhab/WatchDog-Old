package com.app.filedog.domain;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "DogInfoConfiguration")
public class DogInfoConfiguration {

	private String dogId="";
	private String batchFilePath="";
	private String inputFolderPath="";
	private String outputFolderPath="";
	private String failureFolderPath="";
	private String archiveFolderPath="";
	private String carrierCodeTag = "CarrierCode";
	private String responeCodeTag = "CompletedSuccessfully";
	private String fileExtension = "xml";
	private String fileTypeSeparator = "@";
	private String responseFilePrefix = "Out_";
	private String fileSupports="";
	private String supportsAPI="";
	private String dogType = "FD";
	private String optInputFolderPath="";
	private String optOutputFolderPath="";
	private String optArchiveFolderPath="";
	private String optFailureFolderPath="";
	private String optFileSupports="";
	private String optSupportsAPI="";
	private String nonEdiCamFileSupports="";
	private String nonEdiCamSupportsAPI="";
	private String toWhomEmail="";
	private boolean enableArchiveOthersFile = false;
	private boolean enableResponseCodeLog = false;
	private boolean enableMail = false;
	private boolean flag = false;
	private boolean stopFileRun=false;
	private boolean stopBatchRun=false;
	private boolean stopNonEdiBatchRun=false;
	private String limitFilesFolder="100";
	private String systemPlanId="";
	private String systemPlanIdText="00000000";

	
	
	/***************************/
	
	

	public String getDogType() {
		return dogType;
	}

	public void setDogType(String dogType) {
		this.dogType = dogType;
	}

	public boolean isEnableArchiveOthersFile() {
		return enableArchiveOthersFile;
	}

	public void setEnableArchiveOthersFile(boolean enableArchiveOthersFile) {
		this.enableArchiveOthersFile = enableArchiveOthersFile;
	}

	public boolean isEnableResponseCodeLog() {
		return enableResponseCodeLog;
	}

	public void setEnableResponseCodeLog(boolean enableResponseCodeLog) {
		this.enableResponseCodeLog = enableResponseCodeLog;
	}

	public boolean isEnableMail() {

		return enableMail;
	}

	public void setEnableMail(boolean enableMail) {
		this.enableMail = enableMail;
	}

	public String getBatchFilePath() {
		return batchFilePath;
	}

	public void setBatchFilePath(String batchFilePath) {
		this.batchFilePath = batchFilePath;
	}

	public String getInputFolderPath() {
		return inputFolderPath;
	}

	public void setInputFolderPath(String inputFolderPath) {
		this.inputFolderPath = inputFolderPath;
	}

	public String getOutputFolderPath() {
		return outputFolderPath;
	}

	public void setOutputFolderPath(String outputFolderPath) {
		this.outputFolderPath = outputFolderPath;
	}

	public String getArchiveFolderPath() {
		return archiveFolderPath;
	}

	public void setArchiveFolderPath(String archiveFolderPath) {
		this.archiveFolderPath = archiveFolderPath;
	}

	public String getResponeCodeTag() {
		return responeCodeTag;
	}

	public void setResponeCodeTag(String responeCodeTag) {
		this.responeCodeTag = responeCodeTag;
	}

	public String getFileExtension() {
		return fileExtension;
	}

	public void setFileExtension(String fileExtension) {
		this.fileExtension = fileExtension;
	}

	public String getFileTypeSeparator() {
		return fileTypeSeparator;
	}

	public void setFileTypeSeparator(String fileTypeSeparator) {
		this.fileTypeSeparator = fileTypeSeparator;
	}

	public String getResponseFilePrefix() {
		return responseFilePrefix;
	}

	public void setResponseFilePrefix(String responseFilePrefix) {
		this.responseFilePrefix = responseFilePrefix;
	}

	public String getFileSupports() {
		return fileSupports;
	}

	public void setFileSupports(String fileSupports) {
		this.fileSupports = fileSupports;
	}

	public String getDogId() {
		return dogId;
	}

	public void setDogId(String dogId) {
		this.dogId = dogId;
	}

	public String getToWhomEmail() {
		return toWhomEmail;
	}

	public void setToWhomEmail(String toWhomEmail) {
		this.toWhomEmail = toWhomEmail;
	}

	public String getSupportsAPI() {
		return supportsAPI;
	}

	public void setSupportsAPI(String supportsAPI) {
		this.supportsAPI = supportsAPI;
	}

	public String getFailureFolderPath() {
		return failureFolderPath;
	}

	public void setFailureFolderPath(String failureFolderPath) {
		this.failureFolderPath = failureFolderPath;
	}

	public boolean isFlag() {
		return flag;
	}

	public void setFlag(boolean flag) {
		this.flag = flag;
	}

	public String getCarrierCodeTag() {
		return carrierCodeTag;
	}

	public void setCarrierCodeTag(String carrierCodeTag) {
		this.carrierCodeTag = carrierCodeTag;
	}

	public String getOptInputFolderPath() {
		return optInputFolderPath;
	}

	public void setOptInputFolderPath(String optInputFolderPath) {
		this.optInputFolderPath = optInputFolderPath;
	}

	public String getOptOutputFolderPath() {
		return optOutputFolderPath;
	}

	public void setOptOutputFolderPath(String optOutputFolderPath) {
		this.optOutputFolderPath = optOutputFolderPath;
	}

	public String getOptFailureFolderPath() {
		return optFailureFolderPath;
	}

	public void setOptFailureFolderPath(String optFailureFolderPath) {
		this.optFailureFolderPath = optFailureFolderPath;
	}

	public String getOptFileSupports() {
		return optFileSupports;
	}

	public void setOptFileSupports(String optFileSupports) {
		this.optFileSupports = optFileSupports;
	}

	public String getOptSupportsAPI() {
		return optSupportsAPI;
	}

	public void setOptSupportsAPI(String optSupportsAPI) {
		this.optSupportsAPI = optSupportsAPI;
	}

	public String getOptArchiveFolderPath() {
		return optArchiveFolderPath;
	}

	public void setOptArchiveFolderPath(String optArchiveFolderPath) {
		this.optArchiveFolderPath = optArchiveFolderPath;
	}

	public boolean isStopFileRun() {
		return stopFileRun;
	}

	public void setStopFileRun(boolean stopFileRun) {
		this.stopFileRun = stopFileRun;
	}

	public boolean isStopBatchRun() {
		return stopBatchRun;
	}

	public void setStopBatchRun(boolean stopBatchRun) {
		this.stopBatchRun = stopBatchRun;
	}

	public String getLimitFilesFolder() {
		return limitFilesFolder;
	}

	public void setLimitFilesFolder(String limitFilesFolder) {
		this.limitFilesFolder = limitFilesFolder;
	}

	public String getSystemPlanId() {
		return systemPlanId;
	}

	public void setSystemPlanId(String systemPlanId) {
		this.systemPlanId = systemPlanId;
	}


	public String getSystemPlanIdText() {
		return systemPlanIdText;
	}

	public void setSystemPlanIdText(String systemPlanIdText) {
		this.systemPlanIdText = systemPlanIdText;
	}

	public String getNonEdiCamSupportsAPI() {
		return nonEdiCamSupportsAPI;
	}

	public void setNonEdiCamSupportsAPI(String nonEdiCamSupportsAPI) {
		this.nonEdiCamSupportsAPI = nonEdiCamSupportsAPI;
	}

	public String getNonEdiCamFileSupports() {
		return nonEdiCamFileSupports;
	}

	public void setNonEdiCamFileSupports(String nonEdiCamFileSupports) {
		this.nonEdiCamFileSupports = nonEdiCamFileSupports;
	}

	public boolean isStopNonEdiBatchRun() {
		return stopNonEdiBatchRun;
	}

	public void setStopNonEdiBatchRun(boolean stopNonEdiBatchRun) {
		this.stopNonEdiBatchRun = stopNonEdiBatchRun;
	}
}
