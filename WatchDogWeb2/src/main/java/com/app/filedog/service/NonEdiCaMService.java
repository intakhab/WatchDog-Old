package com.app.filedog.service;

import java.io.File;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.app.filedog.common.FileException;
import com.app.filedog.component.DataLoader;
import com.app.filedog.dto.APIDto;
import com.app.filedog.dto.ResponseDto;

@Service
public class NonEdiCaMService {
	


	private Logger logger = LogManager.getLogger("Dog-5");
	@Autowired
	MailService mailService;
	@Autowired
	CommonService commonService;
	
	@Autowired 
	XMLUtilService xmlUtilService;
	
	@Autowired
	DataLoader dataLoader;
	private String fileType;
	@Autowired
	CounterService counterService;
	final String PROCESS_STOP_CONFIRM ="processStopConfirm";
    final String PROCESS_STOP_DELIVER="processSetStopToDelivered";
    final String FIND_ENTITY="findEntity";

	@Autowired
	Environment env;
	/***
	 * 
	 * @param f
	 * @param count
	 * @throws FileException
	 */
	public boolean processNonEdiRun(APIDto apiDto,int count) throws FileException {

		synchronized (this) {
			boolean success=false;
			String fileName = apiDto.getFileName();
			try {
				if (checkFileConvention(fileName)) {//ok
					String[] apis=apiDto.getApiName().split("_");

					List<String>  pipeSeparator = getTagsAndValueApi(apiDto.getApiStrProcess());
					if(pipeSeparator.size()==0) {
						throw new FileException("Missing APIs Arguments {  } " ) ; 
					}

					String[] readput=pipeSeparator.get(0).split("=")[1].split("_");
					for(String rp:readput) {
						if("READ".equalsIgnoreCase(rp)) {
							logger.info("NonEdiCaM read: APIs  { "+Arrays.toString(apis)+ "} Calling  [ "+apis[0] +"]");
							String command=prepareCommandArgs(apiDto.getFileName(),apis[0].trim());//findEntity=apis[0]
							commonService.runCommandLineBatch(dataLoader.configDto.getBatchFilePath(),
									command,apiDto.getFileName());
							logger.info("NonEdiCaM Batch Run Successfully...");

							if(checkResponseCode(apiDto,apiDto.getFile())) {//Bogous as need that 

								switch(pipeSeparator.size()) {

								case 1: 
									logger.info("Case 1 Invoking for READ");
									String systemLoadIdTag=pipeSeparator.get(0).split("=")[0];
									List<String> systemLoadList=commonService.getValuesFromXML(apiDto.getOutputFile(), systemLoadIdTag);
									int size=systemLoadList.size();
									logger.info("Total Loads Founds [ "+size+" ]");
									apiDto.setSystemLoadID(systemLoadList);
									if(size>0) {
										success=true;
										apiDto.setAllTrue(true);
										logger.info("Loaded all xml valus in case 1");
									}
									break;
								case 2:
									logger.info("Case 2 Invoking for READ");

									String systemLoadIdTags=pipeSeparator.get(0).split("=")[0];
									List<String> sysLoadList1=commonService.getValuesFromXML(apiDto.getOutputFile(), systemLoadIdTags);
									String carrierCodeTag=pipeSeparator.get(1).split("=")[0];
									List<String> carrierCodeList=commonService.getValuesFromXML(apiDto.getOutputFile(), carrierCodeTag);
									//
									int carrierSize=carrierCodeList.size();
									int loadSize=sysLoadList1.size();
									//
									apiDto.setSystemLoadID(sysLoadList1);
									apiDto.setCarrierCodes(carrierCodeList);
									//
									logger.info("Total Loads Founds [ "+loadSize+" ]");
									logger.info("Total CarrierCode Founds [ "+carrierSize+" ]");
									if((loadSize>0 && carrierSize>0) 
											&& loadSize==carrierSize) {
										apiDto.setAllTrue(true);
										success=true;
										logger.info("Loaded all xml valus in case 2");
									}
									break;
								default:
									success=false;
									break;
								}
							}else {
								success=false;
							}

						}if("PUT".equalsIgnoreCase(rp)) {

							if(success) {
								logger.info("PUT: APIs  { "+Arrays.toString(apis)+ "} Calling  [ "+apis[1] +"]");
								String fname=apis[1]+dataLoader.configDto.getFileTypeSeparator()+
										dataLoader.configDto.getNonEdiCamFileSupports()+"."+dataLoader.configDto.getFileExtension();
								
								logger.info("File for XML Tag modification : "+fname);
								
								File sysCamFile=new File(dataLoader.configDto.getInputFolderPath()+File.separator+fname);

								switch(pipeSeparator.size()) {

								case 1: 
									logger.info("Case 1 Invoking for PUT");

									List<String> sysLoadList1=apiDto.getSystemLoadID();
									//Invoking batch one by one//
									success=processApis(sysLoadList1,null,sysCamFile,apis[1],apiDto,"sys");
									apiDto.setAllTrue(true);

									break;
								case 2:
									logger.info("Case 2 Invoking for PUT");

									List<String> sysLoadList2 = apiDto.getSystemLoadID();
									List<String> carrierLoadList = apiDto.getCarrierCodes();
									int loadSize = sysLoadList2.size();
									int camSize = carrierLoadList.size();
									if ((loadSize > 0 && camSize > 0) && loadSize == camSize) {

										success = processApis(sysLoadList2, carrierLoadList, sysCamFile, apis[1],
												apiDto, "cam");
										apiDto.setAllTrue(true);

									}

									break;
								default:
									success = false;
									apiDto.setAllTrue(false);

									break;
								}

							}

						}
					}

				}

			} catch (Exception e) {
				success = false;
				apiDto.setAllTrue(false);
				commonService.addFilesForReports(fileName, "Exception during processing", "F");
				throw new FileException("Error :  {processOPTRun} :  " + e.getMessage());

			}

			return success;
		}
	}

	/***
	 * 
	 * @param systemLoadList
	 * @param carrierLoadList
	 * @param sysCamFile
	 * @param apis
	 * @param apiDto
	 * @param type
	 * @return
	 * @throws FileException
	 */
	private boolean processApis(List<String> systemLoadList, List<String> carrierLoadList, File sysCamFile,
			String apis, APIDto apiDto, String type) throws FileException {
		boolean isRunning = false;
		if ("sys".equals(type)) {
			
			for (String s : systemLoadList) {
				// modify SystemLoadId
				if (xmlUtilService.modifyValuesInXML(sysCamFile, "SystemLoadID", s)) {
					/*
					 * String command=prepareCommandArgs(sysCamFile.getName(),apis); ResponseDto
					 * respD=commonService.runCommandLineBatch(dataLoader.configDto.getBatchFilePath
					 * (), command,sysCamFile.getName()); if(respD.isCommandRun()) {
					 * checkResponseCode(apiDto, sysCamFile); }
					 */ 
					isRunning=invokeBatchRuns(sysCamFile, apis, apiDto);
					try {
						Thread.sleep(1000);
					} catch (Exception e) {
						// TODO: handle exception
					}
				}

			}
		} else {
             
			for (int i = 0; i < systemLoadList.size(); i++) {
				String systemLoadIDvalue = systemLoadList.get(i);
				String carrierCodes = carrierLoadList.get(i);
				isRunning = xmlUtilService.modifyValuesInXML(sysCamFile, "SystemLoadID", systemLoadIDvalue);
				if (isRunning)
					isRunning = xmlUtilService.modifyValuesInXML(sysCamFile, "CarrierCode", carrierCodes);

				if (isRunning) {

					isRunning=invokeBatchRuns(sysCamFile, apis, apiDto);

					try {
						Thread.sleep(1000); // One seconds would be fine
					} catch (Exception e) {
						// TODO: handle exception
					}
				}

			}

		}
		return isRunning;
	}


	/***
	 * This method will invoke the Batch File with parameter
	 * @param sysCamFile
	 * @param apis
	 * @param apiDto
	 * @return
	 * @throws FileException
	 */

	private boolean invokeBatchRuns(File sysCamFile, String apis, APIDto apiDto) throws FileException {
		String command=prepareCommandArgs(sysCamFile.getName(),apis);
		ResponseDto respD=commonService.runCommandLineBatch(dataLoader.configDto.getBatchFilePath(),
				command,sysCamFile.getName());
		if(respD.isCommandRun()) {
			return checkResponseCode(apiDto, sysCamFile);
		}
		return false;		
	}


	/***
	 * 
	 * @param str
	 * @return
	 * @throws FileException
	 */
	private List<String> getTagsAndValueApi(String str) throws FileException {
		String apiString = str;
		if (apiString.contains("{") || apiString.contains("}")) {
			apiString = apiString.replaceAll("\\{", "").replaceAll("\\}", "");
		}
		if (apiString.isEmpty()) {
			logger.info("NonEdiCaM API args are Empty {} ");
			return Collections.emptyList();
		}
		return commonService.split(apiString, "\\|");
	}
	

	/***
	 * 
	 * @param inputFile
	 * @return
	 * @throws FileException
	 */
	private boolean checkResponseCode(APIDto apiDto,File f) throws FileException {
         File inputFile=f;
		logger.info("NonEdiCaM Scanning Response XML in directory  [ " + dataLoader.configDto.getOutputFolderPath() + " ]");
		String fileName = inputFile.getName();
		//String filePath = inputFile.getPath();
		String resFileNameStartWith = dataLoader.configDto.getResponseFilePrefix().concat(fileName);

		String responeFile = dataLoader.configDto.getOutputFolderPath().concat(File.separator)
				.concat(resFileNameStartWith);

		if (!checkFile(responeFile, inputFile)) {
			logger.error("NonEdiCaM { :: Not found Response XML :: } ===>  for file [ " + fileName + " ]");
			commonService.addFilesForReports(fileName,"Not found Response XML","F");
			return false;
		}

		logger.info("Fin { :: Found Response XML :: } ===>  for file [ " + resFileNameStartWith + " ]");
		logger.info("Response XML file location [ " + responeFile + " ]");

		try {
			File responseFile = Paths.get(responeFile).toFile();
			String responseTagVal = xmlUtilService.getValueFromXML(responseFile,
					dataLoader.configDto.getResponeCodeTag());
			// Print XML
            apiDto.setOutputFile(responseFile);
			final String contentXML= xmlUtilService.printXML(responseFile);
			if ("false".equalsIgnoreCase(responseTagVal)) {
				   commonService.sendMail(inputFile,contentXML,"3");
				   commonService.addFilesForReports(fileName,"Check Resoonse XML","F");

				return false;
			}

		} catch (Exception e) {
			commonService.addFilesForReports(fileName,"Exception during processing","F");
			throw new FileException("Error {89898} :  File Exeption " + e.getMessage());
		}
		return true;

	}

	
	
	/***
	 * 
	 * @param fileName
	 * @return
	 */

	private String prepareCommandArgs(String fileName, String apiName) {
		// String
		String inputFolderFileName = dataLoader.configDto.getOptInputFolderPath() + File.separator + fileName;
		String outPutFolder = dataLoader.configDto.getOptOutputFolderPath() + File.separator + ""
				+ dataLoader.configDto.getResponseFilePrefix() + fileName;

		StringBuilder sb = new StringBuilder(apiName).append("&").append(inputFolderFileName).append("&")
				.append(outPutFolder);
		return sb.toString();
	}

	/***
	 * This method will check the file convention for further process
	 * 
	 * @param fileName
	 * @return
	 */
	private boolean checkFileConvention(String fileName) {

		if (!StringUtils.getFilenameExtension(fileName).equals(dataLoader.configDto.getFileExtension())) {

			return false;
		}
		fileType = "NOT VALID";
		final String fname = fileName;
		try {
			String fileEndWith = dataLoader.configDto.getNonEdiCamFileSupports();
			if (fileEndWith.isEmpty() && fileEndWith.length() == 0) {
				throw new FileException(
						"OPT File naming convention {empty}. Check properties file for your reference.");
			}
			String fileNameAfterFileSepartor = fname.split(dataLoader.configDto.getFileTypeSeparator())[1]
					.split("\\.")[0];// .xml

			List<String> fileDcOrCam = commonService.split(fileEndWith.trim(), ",");
			for (String fn : fileDcOrCam) {
				String noSpaceStr = fn.replaceAll("\\s", "");
				if (fileNameAfterFileSepartor.equalsIgnoreCase(noSpaceStr)
						|| StringUtils.startsWithIgnoreCase(fileNameAfterFileSepartor, noSpaceStr)) {
					fileType = fn;
					logger.info("[ :: " + fileType + " ::] type file found.");
					return true;
				}
			}

		} catch (Exception e) {
			logger.error("Error {00099} : File Convention : " + (fileName) + "  " + e.getMessage());
			return false;
		}
		return false;
	}

	

	/**
	 * 
	 * @param responeFile
	 * @param inputFile
	 * @return
	 */
	private boolean checkFile(String responeFile, File inputFile) {
		File resFile = Paths.get(responeFile).toFile();
		if (!resFile.exists()) {
			logger.info("Response XML file [ " + resFile.getName() + " ] not found in directory [ "
					+ dataLoader.configDto.getOptOutputFolderPath() + " ]");

			return false;

		}
		return true;
	}

}
