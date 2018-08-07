package com.app.filedog.service;

import java.io.File;
import java.util.Set;

import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class MailService {
	@Autowired
	private JavaMailSender javaMailSender;

	@Autowired
	Environment env;
	
	@Autowired
	CommonService commonService;
	/**
	 * 
	 * @param email
	 * @param message
	 * @param subj
	 * @throws Exception
	 */
	public void sendEmail(String[] email, String message, String subj) throws Exception {
		MimeMessage mimeMesg = javaMailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(mimeMesg, true);
		helper.setTo(email);
		helper.setSubject(subj);
		helper.setText(message);
		helper.setFrom(env.getProperty("mail.from"), 
				commonService.ERROR_MAIL_HEADER_MSG);
		helper.setPriority(1);
		javaMailSender.send(mimeMesg);
	}

	/**
	 * 
	 * @param email
	 * @param message
	 * @param subj
	 * @param fileURL
	 * @throws Exception
	 */
	public void sendEmailWithAttachment(String[] email, String message, String subj, File fileURL) throws Exception {

		MimeMessage mimeMesg = javaMailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(mimeMesg, true);

		helper.setTo(email);
		helper.setSubject(subj);
		helper.setText(message);
		helper.setFrom(env.getProperty("mail.from"),
				commonService.ERROR_MAIL_HEADER_MSG);
		helper.setPriority(1);

		/*
		 * ClassPathResource file = new ClassPathResource(fileURL); FileSystemResource
		 * file = new FileSystemResource(fileURL);
		 * helper.addAttachment(fileURL.getName(), file);
		 */

		helper.addAttachment(fileURL.getName(), fileURL);
		javaMailSender.send(mimeMesg);
	}
	
	String cssStr="<style>\r\n" + 
			"table {\r\n" + 
			"	font-family: Helvetica, Arial, sans-serif; /* Nicer font */\r\n" + 
			"	border-collapse: collapse;\r\n" + 
			"	border-spacing: 0;\r\n" + 
			"}\r\n" + 
			"table.atb {\r\n" + 
			"    table-layout: auto;\r\n" + 
			"    width: 500px;    \r\n" + 
			"}\r\n" + 
			"\r\n" + 
			"span { \r\n" + 
			"    display:block;\r\n" + 
			"    width:400px;\r\n" + 
			"    word-wrap:break-word;\r\n" + 
			"}\r\n" + 
			"#customers {\r\n" + 
			"    font-family: \"Trebuchet MS\", Arial, Helvetica, sans-serif;\r\n" + 
			"    border-collapse: collapse;\r\n" + 
			"    width: 100%;\r\n" + 
			"}\r\n" + 
			"\r\n" + 
			"#customers td, #customers th {\r\n" + 
			"    border: 1px solid #ddd;\r\n" + 
			"    padding: 8px;\r\n" + 
			"	height: 30px;\r\n" + 
			"\r\n" + 
			"}\r\n" + 
			"\r\n" + 
			"#customers tr:nth-child(even){background-color: #f2f2f2;}\r\n" + 
			"\r\n" + 
			"#customers tr:hover {background-color: #ddd;}\r\n" + 
			"\r\n" + 
			"#customers th {\r\n" + 
			"    padding-top: 12px;\r\n" + 
			"    padding-bottom: 12px;\r\n" + 
			"    text-align: left;\r\n" + 
			"    background-color: #4CAF50;\r\n" + 
			"    color: white;\r\n" + 
			"}\r\n" + 
			"</style>";
	/***
	 * 
	 * @param email
	 * @param message
	 * @param subj
	 * @param fileURL
	 * @param type
	 * @param setFile
	 * @throws Exception
	 */
	public void sendEmailWithAttachments(String[] email, String message, String subj, File[] fileURL,String type, Set<File> setFile) throws Exception {

		MimeMessage mimeMesg = javaMailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(mimeMesg,true);

		helper.setTo(email);
		helper.setSubject(subj);
		helper.setText("<html><head>"+cssStr+"</head><body><div  id='customers'>"+message+"<div></body></html>",true);
		//System.out.println("<html><head>"+cssStr+"</head><body><div id='customers'>"+message+"<div></body></html>");
		if("3".equals(type)) {
		helper.setFrom(env.getProperty("mail.from"),
				commonService.ERROR_MAIL_HEADER_MSG);
		}else {
			helper.setFrom(env.getProperty("mail.from"),
					commonService.SUCCESS_MAIL_HEADER_MSG);
		}
		helper.setPriority(1);
		for (File file : setFile) {
			if(file!=null) {
				 helper.addAttachment(file.getName(), file);
			}
        }
		javaMailSender.send(mimeMesg);
	}

}
