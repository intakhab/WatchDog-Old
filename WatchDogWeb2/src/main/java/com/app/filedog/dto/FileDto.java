package com.app.filedog.dto;

import java.io.File;

public class FileDto {
	private String id;
	private String fileName;
	private String lastModifiedDate;
	private String fileFullPath;
	private String fileStatus;
	private String fileEndWith;
	private File file;
	//
	
	
	//
	
	

	public String getFileFullPath() {
		return fileFullPath;
	}

	public void setFileFullPath(String fileFullPath) {
		this.fileFullPath = fileFullPath;
	}

	//
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getLastModifiedDate() {
		return lastModifiedDate;
	}

	public void setLastModifiedDate(String lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}

	public String getFileStatus() {
		return fileStatus;
	}

	public void setFileStatus(String fileStatus) {
		this.fileStatus = fileStatus;
	}

	public String getFileEndWith() {
		return fileEndWith;
	}

	public void setFileEndWith(String fileEndWith) {
		this.fileEndWith = fileEndWith;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

}
