package com.app.filedog.controller;

import java.awt.Desktop;
import java.io.File;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ShutdownController implements ApplicationContextAware {

	private ApplicationContext context;
	@Autowired
	Environment env;

	@GetMapping("/shutdownContext")
	public void shutdownContext() {

		closeBanner();
		((ConfigurableApplicationContext) context).close();
	}

	/***
	 * 
	 */
	private void closeBanner() {
		try {

			File htmlFile = new File(env.getProperty("error.info"));
			if(htmlFile.exists()) {
			   Desktop.getDesktop().browse(htmlFile.toURI());
			}
			final String filePath= env.getProperty("remote.start");
			
			File filepath = new File(filePath);
			if(filepath.exists()) {
				FileUtils.deleteQuietly(filepath);
			}
		} catch (Exception e) {
		}
	}

	@Override
	public void setApplicationContext(ApplicationContext ctx) throws BeansException {
		this.context = ctx;

	}

}