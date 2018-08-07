package com.app.service;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
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
import com.app.filedog.service.CommonService;
import com.app.filedog.service.CounterService;
import com.app.filedog.service.MailService;
import com.app.filedog.service.XMLUtilService;

/***
 * 
 * @author intakhabalam.s@hcl.com
 *
 */
@Service
public class FinancialTestBatchService {

	private Logger logger = LogManager.getLogger("Dog-2");
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
    final String API_NAME_FOR_ARCHIVE="processGLTransactionRetrieve";

	@Autowired
	Environment env;
	/***
	 * 
	 * @param f
	 * @param count
	 * @throws FileException
	 */
	public boolean processFinRun(File inputFile, int count,APIDto api, String group, boolean archive) throws FileException {
		synchronized (this) {
			boolean success=false;
			String fileName = inputFile.getName();
			String apiName=null;
			String apiStr=null;
			try {
				if (checkFileConvention(fileName)) {
					logger.info(
							"------------------------------------------------------------------------------------------");
					logger.info("Fin Processing file name [ " + fileName + " ] - counter  [ " + count + " ]  -  [ "+group+" ]   ");
					logger.info("Fin found valid file, Start Processing...");
					apiName = api.getApiName();
					apiStr = api.getApiStrProcess();

					if (apiName != null && apiStr != null) {
						logger.info("Fin API type dyanmically search {} " + apiName + " = " + apiStr);
						// Means XML Modified successfully
						boolean isModifiedXML = modifyInputApiXML(inputFile, apiName, apiStr);
						if (isModifiedXML) {
							ResponseDto responseDto = commonService.runCommandLineBatch(
									dataLoader.configDto.getBatchFilePath(), prepareCommandArgs(fileName, apiName),
									fileName);
							if (responseDto.isCommandRun()) {
								logger.info("Fin Batch Run Successfully...");
								if (checkResponseCode(inputFile,apiName,archive)) {
									logger.info("Fin File completed sucessfully [ " + fileName + " ] ");
									success= true;
									commonService.addFilesForReports(fileName,"Passed","P");

								}
							}
						}
					}
				}else {
					if ("true".equalsIgnoreCase(dataLoader.configDto.getEnableArchiveOthersFile())) {
						 commonService.gotoArchiveFailureFolder(inputFile.getPath(), fileName,"P");
					}
				}

			 } catch (Exception e) {
				commonService.addFilesForReports(fileName,"Exception during processing","F");
				success=false;
				throw new FileException("Error :  {processOPTRun} :  " + e.getMessage());

			 }finally {
				 refreshXMLTagValues(inputFile, apiName, apiStr);
			 }
			
			return success;
		}
	}

	/***
	 * 
	 * @param inputFile
	 * @return
	 * @throws FileException
	 */
	private boolean checkResponseCode(File inputFile,String apiName,boolean archive) throws FileException {

		logger.info("OPT Scanning Response XML in directory  [ " + dataLoader.configDto.getOutputFolderPath() + " ]");
		String fileName = inputFile.getName();
		//String filePath = inputFile.getPath();
		String resFileNameStartWith = dataLoader.configDto.getResponseFilePrefix().concat(fileName);

		String responeFile = dataLoader.configDto.getOptOutputFolderPath().concat(File.separator)
				.concat(resFileNameStartWith);

		if (!checkResponseCodeFile(responeFile, inputFile)) {
			logger.error("Fin { :: Not found Response XML :: } ===>  for file [ " + fileName + " ]");
			commonService.sendMail(inputFile, "", "1");// Sending Mail
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
			final String contentXML= xmlUtilService.printXML(responseFile);
			if ("false".equalsIgnoreCase(responseTagVal)) {
				   commonService.sendMail(inputFile,contentXML,"3");
				   commonService.addFilesForReports(fileName,"Check Resoonse XML","F");

				return false;
			} else {
					if(API_NAME_FOR_ARCHIVE.equals(apiName) && archive) {
				        commonService.gotoArchiveFailureFolderFin(responeFile, resFileNameStartWith, "P");
					}
				return true;
			}

		} catch (Exception e) {
			commonService.addFilesForReports(fileName,"Exception during processing","F");
			throw new FileException("Error {000777} :  File Exeption " + e.getMessage());
		}

	}

	/**
	 * if sting will come like cam then changeArray=cam=changeEntity will get the
	 * values changeEntity
	 * 
	 * @param ct
	 * @return
	 * @throws FileException
	 */
	private boolean modifyInputApiXML(File inputFile, String apiName, String apiStiring) throws FileException {
	
		boolean b = false;
		if (apiStiring.contains("{") || apiStiring.contains("}")) {
			apiStiring = apiStiring.replaceAll("\\{", "").replaceAll("\\}", "");
		}
		if (apiStiring.isEmpty()) {
			logger.info("API Args are Empty {} ");
			return true;
		}
		String[] pipeSeparator = apiStiring.split("\\|");
		List<String> tagsList=new ArrayList<>();
		int count=0;
		for (String s : pipeSeparator) {
			String tagName = s.split("=")[0];
			String tagValue = s.split("=")[1];
			//First check the no of Tags 
			int noOfTags=commonService.countXMLTag(inputFile, tagName);
			if(noOfTags==2) {
				tagsList.add(tagValue);
				count++;
				if(count<2)
				continue;
			}
			if (tagValue.equals("?")) {
				tagValue = counterService.getCounter();
			}
			try {
				
				switch(noOfTags) {
				
				case 1:
					String xmlValuesFromTag = xmlUtilService.getValueFromXML(inputFile, tagName);
					logger.info("Current Tag value :: " + xmlValuesFromTag);
					// if updated then find the XML
					b = xmlUtilService.modifyValuesInXML(inputFile, tagName, tagValue);
				break;
				case 2:
					
				  b = xmlUtilService.modifyValuesInXMLTags(inputFile, tagName, tagsList);
                  break;
				case 0://means tag not found
				case -1:///means Exception found
				    b=false;
				    break;
					
				}

			} catch (FileException e) {
				b = false;
				throw new FileException("Error {00054} :  Modifying XML Exeption {} " + e.getMessage());

			}
		}
		return b;
	}
	
	/***
	 * 
	 * @param inputFile
	 * @param apiName
	 * @param apiStiring
	 * @return
	 * @throws FileException
	 */
	
	private boolean refreshXMLTagValues(File inputFile, String apiName, String apiStiring) throws FileException {
		
		boolean b = false;
		if (apiStiring.contains("{") || apiStiring.contains("}")) {
			apiStiring = apiStiring.replaceAll("\\{", "").replaceAll("\\}", "");
		}
		if (apiStiring.isEmpty()) {
			logger.info("API Args are Empty {} ");
			return true;
		}
		String[] pipeSeparator = apiStiring.split("\\|");
		List<String> tagsList=new ArrayList<>();
		int count=0;
		for (String s : pipeSeparator) {
			String tagName = s.split("=")[0];
			String tagValue = "";
			//First check the no of Tags 
			int noOfTags=commonService.countXMLTag(inputFile, tagValue);
			if(noOfTags==2) {
				tagsList.add(""); //Sending Blank Values as we need fresh XML
				count++;
				if(count<2)
				continue;
			}
			try {
				
				switch(noOfTags) {
				
				case 1:
					b = xmlUtilService.modifyValuesInXML(inputFile, tagName, "");
				break;
				case 2:
				  b = xmlUtilService.modifyValuesInXMLTags(inputFile, tagName, tagsList);
                  break;
				case 0://means tag not found
				case -1:///means Exception found
				    b=false;
				    break;
					
				}

			} catch (FileException e) {
				b = false;
				throw new FileException("Error {00055} :  Refreshing XML Exeption {} " + e.getMessage());

			}
		}
		return b;
	}
	
	
	/***
	 * 
	 * @param fileName
	 * @return
	 */

	private String prepareCommandArgs(String fileName, String apiName) {

		// String
		// apiName=fileName.split(dataLoader.configDto.getFileTypeSeparator())[0];
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
			String fileEndWith = dataLoader.configDto.getOptFileSupports();
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
	private boolean checkResponseCodeFile(String responeFile, File inputFile) {
		File resFile = Paths.get(responeFile).toFile();
		if (!resFile.exists()) {
			logger.info("Response XML file [ " + resFile.getName() + " ] not found in directory [ "
					+ dataLoader.configDto.getOptOutputFolderPath() + " ]");

			return false;

		}
		return true;
	}
}
