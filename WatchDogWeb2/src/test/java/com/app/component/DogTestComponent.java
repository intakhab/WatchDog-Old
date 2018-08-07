package com.app.component;

import java.io.File;
import java.io.FilenameFilter;
import java.nio.file.Paths;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.app.filedog.common.FileException;
import com.app.filedog.component.DataLoader;
import com.app.filedog.config.PropertiesConfig;
import com.app.filedog.dto.APIDto;
import com.app.filedog.service.CommonService;
import com.app.service.DogTestService;
import com.app.service.FinancialTestBatchService;
/***
 * 
 * @author intakhabalam.s@hcl.com
 *
 */
@Component
public class DogTestComponent {
	private Logger logger = LogManager.getLogger("Dog-0");

	@Autowired
	DogTestService dogTestService;
	
	@Autowired
	FinancialTestBatchService financialTestBatchService;
	
	@Autowired
	PropertiesConfig propertiesConfig;
	
	@Autowired
	CommonService commonService;
	
    @Autowired
    DataLoader dataLoader;
    
	/**
	 * 
	 * @throws FileException
	 */
	@PostConstruct
	private void buildConfiguration() throws FileException  {
		
	    logger.info(commonService.OPEN_BANNER);
        if(dataLoader.configDto!=null && !dataLoader.configDto.getFlag()) {
    	   return;
        }
		validation();
		logger.info("===================== Read the Configuaration Information ===========================");
		logger.info("Polling Time [ " + propertiesConfig.pollingTime +" ]");
		logger.info("Intial Polling Time [ " + propertiesConfig.initialPollingDelay +" ]");
		logger.info("Batch File Path [ " + dataLoader.configDto.getBatchFilePath() +" ]");
		logger.info("Input Directory [ " + dataLoader.configDto.getInputFolderPath() +" ]");
		logger.info("Output Directory [ " + dataLoader.configDto.getOutputFolderPath() +" ]");
		logger.info("Archive Directory [  " + dataLoader.configDto.getArchiveFolderPath() +" ]");
		logger.info("Filure Directory [  " + dataLoader.configDto.getFailureFolderPath() +" ]");
		logger.info("File Extension [ " + dataLoader.configDto.getFileExtension() +" ]");
		logger.info("File Type Separator [ " + dataLoader.configDto.getFileTypeSeparator() +" ]");
		logger.info("Response XML file start with [ " + dataLoader.configDto.getResponseFilePrefix() +" ]");
		logger.info("File Supports with [ " + dataLoader.configDto.getFileSupports()  +" ]");
		//
		logger.info("OPT Input Directory [ " + dataLoader.configDto.getOptInputFolderPath() +" ]");
		logger.info("OPT Output Directory [ " + dataLoader.configDto.getOptOutputFolderPath() +" ]");
		logger.info("OPT Archive Directory [  " + dataLoader.configDto.getOptArchiveFolderPath() +" ]");
		logger.info("OPT Failure Directory [  " + dataLoader.configDto.getOptFailureFolderPath() +" ]");
		logger.info("OPT File Supports with [ " + dataLoader.configDto.getOptFileSupports()  +" ]");
		
		logger.info("File Run [ " + dataLoader.configDto.isStopFileRun()  +" ]");
		logger.info("Batch Run [ " + dataLoader.configDto.isStopBatchRun()  +" ]");

		logger.info("======================================================================================");
	}
	
	
	
	/***
	 * This scheduler will invoke the file processing
	 */
    
	public void invokeDog(String filename) {
		     // validate before execution of program.
		logger.info("=======================================================================");
		logger.info("Starting Time [ " + LocalTime.now() + " ]");
		
		try {

			final File inputDirFiles= Paths.get(filename).toFile();
			
				AtomicInteger count=new AtomicInteger(0);
					dogTestService.processFileRun(inputDirFiles,count.incrementAndGet(),"","");


		} catch (Exception ex) {
			logger.error("Run into an error {File Exception}", ex);

		}
		logger.info("Finishing Time [ " + LocalTime.now() + " ] ");
		
	}
	
	
	/***
	 * 
	 * @author intakhabalam.s
	 *
	 */
	
	class FileExtensionFilter implements FilenameFilter {
		String dir;
		String name;
		public FileExtensionFilter(String dir,String name) {
			this.dir=dir;
			this.name=name;
		}

		public boolean accept(File dir, String name) {
			// if the file extension is .log return true, else false
			String[] supportsFile = dataLoader.configDto.getFileSupports().split(",");
			for (String s : supportsFile) {
				if (getEndWith(name.toLowerCase()).startsWith(s.toLowerCase())) {
					return true;

				}
			}
			return false;
		}
	}
	
	/****
	 *  This is method is for Batch Optimization API. 
	 */
	private boolean validateFinRun() {
		// validate before execution of program.
		if (dataLoader.configDto!=null && !dataLoader.configDto.getFlag()) {
			logger.info("Please configure setting, Watch Dog will start once configuration setting done.");
			return false;
		}
		if(dataLoader.configDto!=null  && dataLoader.configDto.isStopBatchRun()) {
			logger.info("FinG1 Dog is stop. for start reconfigure setting");
			return false;
		}
		 if(dataLoader.configDto.getOptInputFolderPath()==null) {
			logger.info("FinG1 Input Folder is not configure..");
         	return false;
         }
		return true;
	}
	
	/***
	 * G1
	 */
	public void invokeFinOPTRunGrop1(String dir,String group,boolean isRun) {
		 if(!validateFinRun()) {
			 return;
		 }
		 
		final long startTime = System.currentTimeMillis();
		logger.info("=======================================================================");
		logger.info("FinG1 Starting Time [ " + LocalTime.now() + " ]");
		String filePath="";
		String fileName="";
		try {
           
			final File inputDirFiles = Paths.get(dataLoader.configDto.getOptInputFolderPath()).toFile();
			logger.info("FinG1 Scanning Input directory [ " + dataLoader.configDto.getOptInputFolderPath() + " ]");
			File[] filesInDir = inputDirFiles.listFiles(new FileExtensionFilter(dir,inputDirFiles.getName()));
			if (filesInDir.length == 0) {
				logger.info("FinG1 Input directory is Empty.");
			} else {
				int count = 1;
				boolean isRunNext = false;
				logger.info("FinG1 Input directory size [ " + filesInDir.length + " ] ");
				
                List<APIDto> apiDtoList=getAPIDto(group);
                Set<File> succesFailList=new LinkedHashSet<File>();
                boolean isStop=false;
				for (APIDto api : apiDtoList) {
					for (File fileToProcess : filesInDir) {
						filePath=fileToProcess.getPath();
						fileName=fileToProcess.getName();
						if (fileToProcess.getName().startsWith(api.getApiName())) {
							succesFailList.add(fileToProcess);
							isRunNext = financialTestBatchService.processFinRun(fileToProcess, count, api,group,isRun);
							api.setFileName(fileName);
							api.setFile(fileToProcess);
							api.setStatus(isRunNext);
							if (!isRunNext) {
								isStop=true;
								break;
							}
							count++;
						}
						
					}
					if (isStop) {
						break;
					}
				}
			}

		} catch (FileException ex) {
			logger.error("FinG1 Run into an error {File Exception}", ex);
		}
		final long endTime = System.currentTimeMillis();
		final double totalTimeTaken = (endTime - startTime) / (double) 1000;
		logger.info("FinG1 Finishing Time [ " + LocalTime.now() + " ] => Total time taken to be completed  [ "
				+ totalTimeTaken + "s ]");

	}
	
   	
	
	/***
	 * This method will manipulate the name
	 * @param name
	 * @return
	 */
	private String getEndWith(String name) {
		try{
		if(name.contains(dataLoader.configDto.getFileTypeSeparator())) 	
		    return name.split(dataLoader.configDto.getFileTypeSeparator())[1].split("\\.")[0];//xyz@abc.xml
		else
			return name;
		
		}catch (Exception e) {
			logger.error("Valid file not found in input directory");
			return "NOTVALID";
		}
	}
	


	
	/***
	 * This method will give Object of API
	 * @return
	 */
	private List<APIDto> getAPIDto(String group) {
		String[] fileOrder = dataLoader.configDto.getOptSupportsAPI();
		List<APIDto> apiDtoList = new ArrayList<>();
		for (String s : fileOrder) {
			APIDto apiDto = new APIDto();
            String apiName=s.split(dataLoader.configDto.getFileTypeSeparator())[0];
            String apiStrArgs=s.split(dataLoader.configDto.getFileTypeSeparator())[1];
            if(apiName.startsWith(group)) {
            	apiName=apiName.replaceAll(group.concat("-"), "");
				apiDto.setApiName(apiName);
				apiDto.setApiStrProcess(apiStrArgs);
				apiDtoList.add(apiDto);
            }
		}
		return apiDtoList;
	}



	
	/**
	 * 	
	 * @throws FileException
	 */
	public void validation() throws FileException {
		
		commonService.scanDirectory();
		
	}
	
}
