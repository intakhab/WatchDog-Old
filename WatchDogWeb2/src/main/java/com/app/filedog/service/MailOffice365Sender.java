package com.app.filedog.service;

import java.util.Date;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MailOffice365Sender {
	private static final Logger LOGGER = LogManager.getLogger(MailOffice365Sender.class);

	private static final String HOST = "smtp.office365.com";
	private static final int PORT = 587;
	private static final String USER_NAME = "intakhabalam.s@hcl.com";
	private static final String PASSWORD = "XYZ";

	private final String from = "intakhabalam.s@hcl.com";
	private final String to = "intakhabalam.s@hcl.com";

	private final String subject = "Test";
	private final String messageContent = "Test with testy sweet";

	/**
	 * 
	 * @return
	 */
	public Properties getEmailProperties() {
		final Properties config = new Properties();
		config.put("mail.smtp.auth", "true");
		config.put("mail.smtp.starttls.enable", "true");
		config.put("mail.smtp.host", HOST);
		config.put("mail.smtp.port", PORT);
		return config;
	}

	/**
	 * 
	 */
	public void simpleEmailSend() {
		final Session session = Session.getInstance(this.getEmailProperties(), new Authenticator() {

			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(USER_NAME, PASSWORD);
			}

		});

		try {
			final Message message = new MimeMessage(session);
			//message.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
			
			// Set To: header field of the header.
	         message.setRecipients(Message.RecipientType.TO,
	            InternetAddress.parse(to));


			message.setFrom(new InternetAddress(from));
			message.setSubject(subject);
			message.setText(messageContent);
			message.setSentDate(new Date());
			Transport.send(message);
		} catch (MessagingException ex) {
			LOGGER.error("Error sending mail {}  " + ex.getMessage(), ex);
		}
	}

}
