package com.app.filedog.service;

import org.springframework.stereotype.Service;

@Service
public class AlertService {
	
	/**
	 *   

	 * @param msg
	 * @return
	 */
	public String sucess(String msg) {
		return "<div id='alertId' class='alert alert-success' data-dismiss='alert' aria-label='Close'"
				+ " role='alert'>" + msg + "<span style='float:right;cursor: pointer;'>&times;</span></div>";
	}

	/**
	 * 
	 * @param msg
	 * @return
	 */
	public String error(String msg) {
		return "<div id='alertId' class='alert alert-danger' data-dismiss='alert' aria-label='Close'"
				+ " role='alert'>" + msg + "<span style='float:right;cursor: pointer;'>&times;</span></div>";
		
	}

	/***
	 * 
	 * @param msg
	 * @return
	 */
	public String primary(String msg) {
		return "<div id='alertId' class='alert alert-primary' data-dismiss='alert' aria-label='Close'"
				+ " role='alert'>" + msg + "<span style='float:right;cursor: pointer;'>&times;</span></div>";
	}

}
