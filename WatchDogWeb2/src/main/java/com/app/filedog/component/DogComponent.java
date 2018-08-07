package com.app.filedog.component;

import java.io.File;
import java.io.FileFilter;
import java.nio.file.Paths;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.app.filedog.common.FileException;
import com.app.filedog.config.PropertiesConfig;
import com.app.filedog.dto.APIDto;
import com.app.filedog.service.CommonService;
import com.app.filedog.service.DogService;
import com.app.filedog.service.FinancialBatchService;
import com.app.filedog.service.NonEdiCaMService;
/***
 * 
 * @author intakhabalam.s@hcl.com
 *
 */
@DisallowConcurrentExecution
@Component
public class DogComponent {
	private Logger logger = LogManager.getLogger("Dog-0");

	@Autowired
	DogService dogService;
	
	@Autowired
	FinancialBatchService financialBatchService;
	
	@Autowired
	NonEdiCaMService nonEdiCaMService;
	
	@Autowired
	PropertiesConfig propertiesConfig;
	
	@Autowired
	Scheduler scheduler;
	
	@Autowired
	CommonService commonService;
	
    @Autowired
    DataLoader dataLoader;
    
    
/*    @Bean
    public String getNonEdiValue(){
        return env.getProperty("${polling.nonedi.time}");
    }*/

    
    

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
		logger.info("Fin Input Directory [ " + dataLoader.configDto.getOptInputFolderPath() +" ]");
		logger.info("Fin Output Directory [ " + dataLoader.configDto.getOptOutputFolderPath() +" ]");
		logger.info("Fin Archive Directory [  " + dataLoader.configDto.getOptArchiveFolderPath() +" ]");
		logger.info("Fin Failure Directory [  " + dataLoader.configDto.getOptFailureFolderPath() +" ]");
		logger.info("Fin File Supports with [ " + dataLoader.configDto.getOptFileSupports()  +" ]");
		
		logger.info("File Run [ " + dataLoader.configDto.isStopFileRun()  +" ]");
		logger.info("Batch Run [ " + dataLoader.configDto.isStopBatchRun()  +" ]");
		logger.info("NonEdi Run [ " + dataLoader.configDto.isStopNonEdiBatchRun()  +" ]");

		
		logger.info("======================================================================================");
	}
	
	
	
	/***
	 * This scheduler will invoke the file processing
	 */
    
	@Scheduled(fixedRateString = "${polling.time}", initialDelayString = "${initial.polling.delay}")
	public void invokeDog() {
		     // validate before execution of program.
		if(dataLoader.configDto!=null && !dataLoader.configDto.getFlag() ) {
			logger.info("Please configure setting, Watch Dog will start once configuration setting done.");
			return;
		}
		if(dataLoader.configDto!=null  && dataLoader.configDto.isStopFileRun()) {
			logger.info("File Dog is stop. for start reconfigure setting");
			return;
		}
		 if(dataLoader.configDto.getInputFolderPath()==null) {
				logger.info("Input Folder is not configure..");

	         	return;
	     }
		final long startTime = System.currentTimeMillis();
		logger.info("=======================================================================");
		logger.info("Starting Time [ " + LocalTime.now() + " ]");
		
		try {

			final File inputDirFiles= Paths.get(dataLoader.configDto.getInputFolderPath()).toFile();
			logger.info("Scanning Input directory [ " + dataLoader.configDto.getInputFolderPath() + " ]");
			//create a FileFilter and override its accept-method
			   FileFilter logFilefilter = new FileFilter() {
                  //Override accept method
                  public boolean accept(File file) {
                     //if the file extension is .log return true, else false
                	  String[] supportsFile=dataLoader.configDto.getFileSupports().split(",");
                	  for (String s : supportsFile) {
  						if (getEndWith(file.getName().toLowerCase()).startsWith(s.toLowerCase())) {
  							return true;

  						}
  					}
                     return false;
                  }
               };

			File[] files = inputDirFiles.listFiles(logFilefilter);
			if (files.length == 0) {
				logger.info("Input directory is Empty.");
			} else {
				int count =1;
				logger.info("Input directory size [ " + files.length + " ] ");
				for (File f : files) {
					dogService.processFileRun(f,count);
					//sleep time being 
			        try {
						TimeUnit.SECONDS.sleep(2);
					} catch (InterruptedException e1) {
						logger.error("InterruptedException {} "+e1.getMessage());

					}
					count++;
				}
				

			}

		} catch (FileException ex) {
			logger.error("Run into an error {File Exception}", ex);

		}
		final long endTime = System.currentTimeMillis();
		final double totalTimeTaken = (endTime - startTime)/(double)1000;
		logger.info("Finishing Time [ " + LocalTime.now() + " ] => Total time taken to be completed  [ " + totalTimeTaken + "s ]");
		double min=(Double.parseDouble(propertiesConfig.pollingTime)/(double)(60*1000));
		logger.info("will wake up in [ "  +commonService.getDecimalNumber(min) +" ] m)");
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
			logger.info("Fin Dog is stop. for start reconfigure setting");
			return false;
		}
		 if(dataLoader.configDto.getOptInputFolderPath()==null) {
			logger.info("Fin Input Folder is not configure..");
         	return false;
         }
		return true;
	}
	
	/***
	 * G1
	 */
	@Scheduled(cron = "${cron.time.g1}")
	public void invokeFinOPTRunGrop1() {
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

			// create a FileFilter and override its accept-method
			FileFilter logFilefilter = new FileFilter() {
				// Override accept method
				public boolean accept(File file) {
					// if the file extension is .log return true, else false
					String[] supportsFile = dataLoader.configDto.getOptFileSupports().split(",");
					for (String s : supportsFile) {
						if (getEndWith(file.getName().toLowerCase()).startsWith(s.toLowerCase())) {
							return true;

						}
					}
					return false;
				}
				
			};

			File[] filesInDir = inputDirFiles.listFiles(logFilefilter);
			if (filesInDir.length == 0) {
				logger.info("FinG1 Input directory is Empty.");
			} else {
				int count = 1;
				boolean isRunNext = false;
				logger.info("FinG1 Input directory size [ " + filesInDir.length + " ] ");
				
                List<APIDto> apiDtoList=getAPIOptDto("G1");
                Set<File> succesFailList=new LinkedHashSet<File>();
                boolean isStop=false;
				for (APIDto api : apiDtoList) {
					for (File fileToProcess : filesInDir) {
						filePath=fileToProcess.getPath();
						fileName=fileToProcess.getName();
						if (fileToProcess.getName().startsWith(api.getApiName())) {
							succesFailList.add(fileToProcess);
							isRunNext = financialBatchService.processFinRun(fileToProcess, count, api,"FinG1",false);
							api.setFileName(fileName);
							api.setFile(fileToProcess);
							api.setStatus(isRunNext);
							if (!isRunNext) {
								isStop=true;
								break;
							}
							// sleep time being
							try {
								TimeUnit.SECONDS.sleep(2);
							} catch (InterruptedException e1) {
								logger.error("FinG1 InterruptedException {} " + e1.getMessage());
							}
							count++;
						}
						
					}
					if (isStop) {
						break;
					}
				}
				commonService.sendReports(apiDtoList,succesFailList,"G1");

			}

		} catch (FileException ex) {
			if(!filePath.isEmpty() && !fileName.isEmpty()) {
				commonService.addFilesForReports(fileName,"Exception during processing(FinG1)","F");
			    commonService.gotoArchiveFailureFolderFin(filePath, fileName, "F");
			}
			logger.error("FinG1 Run into an error {File Exception}", ex);
		}
		final long endTime = System.currentTimeMillis();
		final double totalTimeTaken = (endTime - startTime) / (double) 1000;
		logger.info("FinG1 Finishing Time [ " + LocalTime.now() + " ] => Total time taken to be completed  [ "
				+ totalTimeTaken + "s ]");

	}
	
	/*****
	 * G4
	 */
	@Scheduled(cron = "${cron.time.g2}")
	public void invokeFinOPTRunGrop2() {
		// validate before execution of program.
		 if(!validateFinRun()) {
			 return;
		 }
		 
		final long startTime = System.currentTimeMillis();
		logger.info("=======================================================================");
		logger.info("FinG2 Starting Time [ " + LocalTime.now() + " ]");
		String filePath="";
		String fileName="";
		try {
           
			final File inputDirFiles = Paths.get(dataLoader.configDto.getOptInputFolderPath()).toFile();
			logger.info("FinG2 Scanning Input directory [ " + dataLoader.configDto.getOptInputFolderPath() + " ]");

			// create a FileFilter and override its accept-method
			FileFilter logFilefilter = new FileFilter() {
				// Override accept method
				public boolean accept(File file) {
					// if the file extension is .log return true, else false
					String[] supportsFile = dataLoader.configDto.getOptFileSupports().split(",");
					for (String s : supportsFile) {
						if (getEndWith(file.getName().toLowerCase()).startsWith(s.toLowerCase())) {
							return true;
						}
					}
					return false;
				}
				
			};

			File[] filesInDir = inputDirFiles.listFiles(logFilefilter);
			if (filesInDir.length == 0) {
				logger.info("FinG2 Input directory is Empty.");
			} else {
				int count = 1;
				boolean isRunNext = false;
				logger.info("FinG2 Input directory size [ " + filesInDir.length + " ] ");
				
                List<APIDto> apiDtoList=getAPIOptDto("G2");
                Set<File> succesFailList=new LinkedHashSet<File>();
                boolean isStop=false;
				for (APIDto api : apiDtoList) {
					for (File fileToProcess : filesInDir) {
						filePath=fileToProcess.getPath();
						fileName=fileToProcess.getName();
						if (fileToProcess.getName().startsWith(api.getApiName())) {
							succesFailList.add(fileToProcess);
							isRunNext = financialBatchService.processFinRun(fileToProcess, count, api,"FinG2",true);
							api.setFileName(fileName);
							api.setFile(fileToProcess);
							api.setStatus(isRunNext);
							if (!isRunNext) {
								isStop=true;
								break;
							}
							// sleep time being
							try {
								TimeUnit.SECONDS.sleep(2);
							} catch (InterruptedException e1) {
								logger.error("FinG2 InterruptedException {} " + e1.getMessage());
							}
							count++;
						}
					}
					if (isStop) {
						break;
					}
				}
				commonService.sendReports(apiDtoList,succesFailList,"G2");
			}

		} catch (FileException ex) {
			if(!filePath.isEmpty() && !fileName.isEmpty()) {
				commonService.addFilesForReports(fileName,"Exception during processing(FinG2)","F");
			    commonService.gotoArchiveFailureFolderFin(filePath, fileName, "F");
			}
			logger.error("FinG2 Run into an error {File Exception}", ex);
		}
		final long endTime = System.currentTimeMillis();
		final double totalTimeTaken = (endTime - startTime) / (double) 1000;
		logger.info("FinG2 Finishing Time [ " + LocalTime.now() + " ] => Total time taken to be completed  [ "
				+ totalTimeTaken + "s ]");
	}
	/***
	 * G3
	 */
	@Scheduled(cron = "${cron.time.g3}")
	public void invokeFinOPTRunGrop3() {
		// validate before execution of program.
		 if(!validateFinRun()) {
			 return;
		 }
		 
		final long startTime = System.currentTimeMillis();
		logger.info("=======================================================================");
		logger.info("FinG3 Starting Time [ " + LocalTime.now() + " ]");
		String filePath="";
		String fileName="";
		try {
           
			final File inputDirFiles = Paths.get(dataLoader.configDto.getOptInputFolderPath()).toFile();
			logger.info("FinG3 Scanning Input directory [ " + dataLoader.configDto.getOptInputFolderPath() + " ]");

			// create a FileFilter and override its accept-method
			FileFilter logFilefilter = new FileFilter() {
				// Override accept method
				public boolean accept(File file) {
					// if the file extension is .log return true, else false
					String[] supportsFile = dataLoader.configDto.getOptFileSupports().split(",");
					for (String s : supportsFile) {
						if (getEndWith(file.getName().toLowerCase()).startsWith(s.toLowerCase())) {
							return true;
						}
					}
					return false;
				}
				
			};

			File[] filesInDir = inputDirFiles.listFiles(logFilefilter);
			if (filesInDir.length == 0) {
				logger.info("FinG3 Input directory is Empty.");
			} else {
				int count = 1;
				boolean isRunNext = false;
				logger.info("FinG3 Input directory size [ " + filesInDir.length + " ] ");
				
                List<APIDto> apiDtoList=getAPIOptDto("G3");
                Set<File> succesFailList=new LinkedHashSet<File>();
                boolean isStop=false;
				for (APIDto api : apiDtoList) {
					for (File fileToProcess : filesInDir) {
						filePath=fileToProcess.getPath();
						fileName=fileToProcess.getName();
						if (fileToProcess.getName().startsWith(api.getApiName())) {
							succesFailList.add(fileToProcess);
							isRunNext = financialBatchService.processFinRun(fileToProcess, count, api,"FinG3",true);
							api.setFileName(fileName);
							api.setFile(fileToProcess);
							api.setStatus(isRunNext);
							if (!isRunNext) {
								isStop=true;
								break;
							}
								
							// sleep time being
							try {
								TimeUnit.SECONDS.sleep(2);
							} catch (InterruptedException e1) {
								logger.error("FinG3 InterruptedException {} " + e1.getMessage());
							}
							count++;
						}
					}
					if (isStop) {
						break;
					}
				}
				commonService.sendReports(apiDtoList,succesFailList,"G3");
			}

		} catch (FileException ex) {
			if(!filePath.isEmpty() && !fileName.isEmpty()) {
				commonService.addFilesForReports(fileName,"Exception during processing(FinG3)","F");
			    commonService.gotoArchiveFailureFolderFin(filePath, fileName, "F");
			}
			logger.error("FinG3 Run into an error {File Exception}", ex);
		}
		final long endTime = System.currentTimeMillis();
		final double totalTimeTaken = (endTime - startTime) / (double) 1000;
		logger.info("FinG3 Finishing Time [ " + LocalTime.now() + " ] => Total time taken to be completed  [ "
				+ totalTimeTaken + "s ]");
	}
	
	
	@Scheduled(fixedRateString = "${polling.nonedi.time}", initialDelayString = "1000")
	public void invokeNonEdiRun() {
		// validate before execution of program.
		if(dataLoader.configDto!=null && !dataLoader.configDto.getFlag() ) {
			logger.info("Please configure setting, Watch Dog will start once configuration setting done.");
			return;
		}
		if(dataLoader.configDto!=null  && dataLoader.configDto.isStopNonEdiBatchRun()) {
			logger.info("NonEdiCaM Dog is stop. for start reconfigure setting");
			return ;
		}
		 if(dataLoader.configDto.getInputFolderPath()==null) {
				logger.info("Input Folder is not configure..");
	         	return;
	     }
		 
		final long startTime = System.currentTimeMillis();
		logger.info("=======================================================================");
		logger.info("NonEdiCaM Starting Time [ " + LocalTime.now() + " ]");
		String filePath="";
		String fileName="";
		try {
			final File inputDirFiles = Paths.get(dataLoader.configDto.getInputFolderPath()).toFile();
			logger.info("NonEdiCaM Scanning Input directory [ " + dataLoader.configDto.getInputFolderPath() + " ]");
			// create a FileFilter and override its accept-method
			FileFilter logFilefilter = new FileFilter() {
				public boolean accept(File file) {
					String[] supportsFile = dataLoader.configDto.getNonEdiCamFileSupports().split(",");
					for (String s : supportsFile) {
						if (getEndWith(file.getName().toLowerCase()).startsWith(s.toLowerCase())) {
							return true;
						}
					}
					return false;
				}
				
			};

			File[] filesInDir = inputDirFiles.listFiles(logFilefilter);
			if (filesInDir.length == 0) {
				logger.info("NonEdiCaM Input directory is Empty.");
			} else {
				
				int count = 1;
				boolean isRunNext = false;
				logger.info("NonEdiCaM Input directory size [ " + filesInDir.length + " ] ");
				
                List<APIDto> apiDtoList=getAPINonEdiCaMDto();
                Set<File> succesFailList=new LinkedHashSet<File>();
                boolean isStop=false;
				for (APIDto api : apiDtoList) {
					for (File fileToProcess : filesInDir) {
						filePath=fileToProcess.getPath();
						fileName=fileToProcess.getName();
						if (fileToProcess.getName().startsWith(api.getApiName())) {
							succesFailList.add(fileToProcess);
							api.setFileName(fileName);
							api.setFile(fileToProcess);
							api.setStatus(isRunNext);
							isRunNext = nonEdiCaMService.processNonEdiRun(api,count);
							if (!isRunNext) {
								isStop=true;
								break;
							}
							// sleep time being
							try {
								TimeUnit.SECONDS.sleep(2);
							} catch (InterruptedException e1) {
								logger.error("NonEdiCaM InterruptedException {} " + e1.getMessage());
							}
							count++;
						}
					}
					if (isStop) {
						break;
					}
				}
				commonService.sendReports(apiDtoList,succesFailList,"NULL");
			}
		} catch (Exception ex) {
			if(!filePath.isEmpty() && !fileName.isEmpty()) {
				commonService.addFilesForReports(fileName,"Exception during processing(NonEdiCaM)","F");
			    commonService.gotoArchiveFailureFolderFin(filePath, fileName, "F");
			}
			logger.error("NonEdiCaM Run into an error {File Exception}", ex);
		}
		final long endTime = System.currentTimeMillis();
		final double totalTimeTaken = (endTime - startTime) / (double) 1000;
		logger.info("NonEdiCaM Finishing Time [ " + LocalTime.now() + " ] => Total time taken to be completed  [ "
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
	 * 
	 * @param succesFailList
	 * @param status
	 */

	@SuppressWarnings("unused")
	private void moveFile(Set<File> succesFailList,String status) {
		
	    	for(File fdo:succesFailList) {
	    		if(fdo!=null)
		          commonService.gotoArchiveFailureFolder(fdo.getPath(), fdo.getName(), status);
			}
	}
	
	/***
	 * This method will give Object of API
	 * @return
	 */
	private List<APIDto> getAPIOptDto(String group) {
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

/***
 * 	
 * @param group
 * @return
 */
	private List<APIDto> getAPINonEdiCaMDto() {
		String[] fileOrder = dataLoader.configDto.getNonEdiCamSupportsAPI();
		List<APIDto> apiDtoList = new ArrayList<>();
		for (String s : fileOrder) {
			APIDto apiDto = new APIDto();
            String apiName=s.split(dataLoader.configDto.getFileTypeSeparator())[0];
            String apiStrArgs=s.split(dataLoader.configDto.getFileTypeSeparator())[1];
				apiDto.setApiName(apiName.trim());
				apiDto.setApiStrProcess(apiStrArgs.trim());
				apiDtoList.add(apiDto);
		}
		return apiDtoList;
	}

	
	

	/**
	 * 
	 */
	@PreDestroy
	private void onClose() {
		logger.info("Before closing called System GC.");
		 System.gc();
		 try {
			scheduler.clear();
			scheduler.shutdown();
		} catch (SchedulerException e) {
			 logger.error("Scheduler Exception {} "+e);

		}
		 logger.info("Happy to safe closing with ( CTR + C ).");
		 logger.info(commonService.CLOSE_BANNER);
		
	}
	
	/**
	 * 	
	 * @throws FileException
	 */
	public void validation() throws FileException {
		
		commonService.scanDirectory();
		
	}
	
}
