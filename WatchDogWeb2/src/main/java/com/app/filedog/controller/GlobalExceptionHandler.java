package com.app.filedog.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalExceptionHandler {
	/**
	 * 
	 * @param request
	 * @param ex
	 * @return
	 */
	@ExceptionHandler(Exception.class)
	public String handleSQLException(HttpServletRequest request, Exception ex){
		request.setAttribute("param1", ex.getLocalizedMessage() +"{ }  "+" "+ex.getStackTrace()[0]);
		return "/errorpage";
	}
	/**
	 * 
	 * @param request
	 * @param ex
	 * @return
	 */
    @ResponseStatus(value=HttpStatus.NOT_FOUND, reason="IOException occured")
	@ExceptionHandler(IOException.class)
	public String handleIOException(HttpServletRequest request, Exception ex){
    	request.setAttribute("param1", ex.getLocalizedMessage() +"{ }  "+" "+ex.getStackTrace()[0]);
		return "/errorpage";
	}
}
