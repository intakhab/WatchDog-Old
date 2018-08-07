package com.app.filedog.dto;

public class ReportDto {
	private String id;
	private String filename;
	private String filedat;
	private String description;
	private String status;
    //
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public String getFiledat() {
		return filedat;
	}
	public void setFiledat(String filedat) {
		this.filedat = filedat;
	}
}
