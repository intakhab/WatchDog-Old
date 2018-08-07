package com.app.filedog.component;

import java.io.File;
import java.io.FileNotFoundException;

import javax.annotation.PostConstruct;
import javax.xml.bind.JAXBException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.app.filedog.common.FileException;
import com.app.filedog.dto.FileDogInfoDto;
import com.app.filedog.service.DogConfigService;

@Component
public class DataLoader {
	private Logger logger = LogManager.getLogger("Dog-L");

	@Autowired
	private DogConfigService watchDogConfigService;
	public FileDogInfoDto configDto;
	
	@PostConstruct
	public void loadData() {
		try {
			this.configDto = watchDogConfigService.getDtoFromObj("1");
			logger.info("Configuration data loading from DB");
		} catch (FileNotFoundException | JAXBException e) {
			logger.error("Data Loading fail, Loading data from another source {}  " + e.getMessage());
			try {
				this.configDto = watchDogConfigService.getDtoFromObj("2");
				logger.info("Configuration data loading from Temp DB");
			} catch (FileNotFoundException | JAXBException e1) {
				logger.error("Data Loading fail  {} " + e1.getMessage());

			}

		}
	}
	/***
	 * This method will refresh the data once you save the data from UI
	 * @param fileDogInfoDto
	 */
    public void refreshData(FileDogInfoDto fileDogInfoDto) {
    	logger.info("Refreshing data, change DB Mode");
    	this.configDto=fileDogInfoDto;
    	buildConfiguration();
     }
    
      private void buildConfiguration()  {
    	 validateDirectories();
		logger.info("============ Read the Configuaration Information ======================");
		
		logger.info("Batch File Path [ " + configDto.getBatchFilePath() +" ]");
		logger.info("Input Directory [ " + configDto.getInputFolderPath() +" ]");
		logger.info("Output Directory [ " + configDto.getOutputFolderPath() +" ]");
		logger.info("Archive Directory [  " + configDto.getArchiveFolderPath() +" ]");
		logger.info("Failure Directory [  " + configDto.getFailureFolderPath() +" ]");
		logger.info("File Extension [ " + configDto.getFileExtension() +" ]");
		logger.info("File Type Separator [ " + configDto.getFileTypeSeparator() +" ]");
		logger.info("Response XML file start with [ " + configDto.getResponseFilePrefix() +" ]");
		logger.info("File Supports with [ " + configDto.getFileSupports()  +" ]");
		//
		logger.info("OPT Input Directory [ " + configDto.getOptInputFolderPath() +" ]");
		logger.info("OPT Output Directory [ " + configDto.getOptOutputFolderPath() +" ]");
		logger.info("OPT Archive Directory [  " + configDto.getOptArchiveFolderPath() +" ]");
		logger.info("OPT Failure Directory [  " + configDto.getOptFailureFolderPath() +" ]");
		logger.info("OPT File Supports with [ " + configDto.getOptFileSupports()  +" ]");
		
		logger.info("File Run [ " + configDto.isStopFileRun()  +" ]");
		logger.info("Batch Run [ " + configDto.isStopBatchRun()  +" ]");

		logger.info("=======================================================================");
	}
      
      /**
  	 * 	
  	 * @throws FileException
  	 */
  	private void validateDirectories()  {
  		createFolder(configDto.getInputFolderPath(),"Input");
  		createFolder(configDto.getOutputFolderPath(),"Output");
  		createFolder(configDto.getArchiveFolderPath(),"Archive");
  		createFolder(configDto.getFailureFolderPath(),"Failure");
  		//
  		createFolder(configDto.getOptInputFolderPath(),"OPT Input");
		createFolder(configDto.getOptOutputFolderPath(),"OPT Output");
		createFolder(configDto.getOptArchiveFolderPath(),"OPT Archive");
		createFolder(configDto.getOptFailureFolderPath(),"OPT Failure");
		//
  		logger.info("Validated directories pass {} ");
  	}
  	
  	private void createFolder(String path, String type) {
  		File file = new File(path);
          if (!file.exists()) {
              if (file.mkdir()) {
      			 logger.info(type +" directory is created!");

              } else {
      			 logger.error("Failed to create directory!");

              }
          }
  	}
}
