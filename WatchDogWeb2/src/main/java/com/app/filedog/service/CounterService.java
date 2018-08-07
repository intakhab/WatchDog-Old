package com.app.filedog.service;

import java.io.File;
import java.io.FileNotFoundException;

import javax.xml.bind.JAXBException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.app.filedog.domain.Counter;

@Service
public class CounterService {

	private final Logger logger = LogManager.getLogger("Dog-C");

	@Autowired
	Environment env;
	
	@Autowired
	XMLUtilService xmlUtilService;
	
	/***
	 * 
	 * @param counter
	 * @param fileName
	 * @return
	 * @throws JAXBException
	 * @throws FileNotFoundException
	 */
	public Counter counterObjectToXml(Counter counter) throws JAXBException, FileNotFoundException {
		String fileName=env.getProperty("db.counter");
		
		/*JAXBContext jaxbContext = JAXBContext.newInstance(Counter.class);
		Marshaller marshaller = jaxbContext.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		marshaller.marshal(counter, new File(fileName));*/
		

		return (Counter) xmlUtilService.convertObjectToXML(counter,fileName);

	}

	/****
	 * 
	 * @param fileName
	 * @return
	 * @throws JAXBException
	 * @throws FileNotFoundException
	 */
	public Counter xmlToCounterObject() throws JAXBException, FileNotFoundException {
		File file = new File(env.getProperty("db.counter"));
		return xmlUtilService.convertXMLToObject(Counter.class, file);
		
	}

	/***
	 * 
	 * @param fileName
	 * @return
	 */
	public String getCounter() {
		String sb="99999";
		try {
			Counter count=xmlToCounterObject();
			int num=Integer.parseInt(count.getAutonumber())+1;
			sb = String.format(env.getProperty("auto.limit"), num);
			counterObjectToXml(new Counter(sb));
			
		} catch (FileNotFoundException | JAXBException e) {
			logger.error("Finding auto number problem {} ",e);
		}
		logger.info("Returning auto number [  "+sb+ "]  ");
		return sb;
		
	}
	
}
