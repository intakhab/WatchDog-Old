package com.app.filedog.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
/***
 * 
 * @author intakhabalam.s@hcl.com
 *
 */
@Configuration
/*@PropertySource("classpath:application.properties")
*/
@PropertySources({
	@PropertySource("classpath:application.properties"),
	@PropertySource("classpath:mail.properties")
})
public class PropertiesConfig {
	
	
	
	@Value("${polling.time}")
	public String pollingTime;
	
	@Value("${initial.polling.delay}")
	public String initialPollingDelay;
	
	
	
}
