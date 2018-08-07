package com.app;

import java.awt.Desktop;
import java.io.File;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@ComponentScan(basePackages = "com.app")
//@EnableAutoConfiguration(exclude = {ErrorMvcAutoConfiguration.class})
public class WatchDogWebApp {

	public static void main(String[] args) {
		try {
			File htmlFile = new File("start.html");
			if (htmlFile.exists()) {
				Desktop.getDesktop().browse(htmlFile.toURI());
			}
		} catch (Exception e) {
		}
		SpringApplication.run(WatchDogWebApp.class, args);
	}
	
}
