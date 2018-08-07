package com.app.service;

import java.io.File;
import java.nio.file.Paths;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.app.filedog.common.FileException;
import com.app.filedog.component.DataLoader;
import com.app.filedog.dto.ResponseDto;
import com.app.filedog.service.CommonService;
import com.app.filedog.service.XMLUtilService;
/***
 * 
 * @author intakhabalam.s@hcl.com
 *
 */
@Service
public class DogTestService {
	private Logger logger = LogManager.getLogger("Dog-1");
	
	private String fileType;

	@Autowired
	CommonService commonService;
	@Autowired
	XMLUtilService xmlUtilService;
	
	@Autowired
	DataLoader dataLoader;
	
	@Autowired
	public Environment env;
	
	
	
	/**
	 * 
	 * @throws FileException
	 */
	public void processFileRun(File inputFile, int count,String inputFolder,String 
			
			outputFolder) throws FileException {
		
		synchronized (this) {
			
			try {
				boolean isValidFile = false;
				String fileName = inputFile.getName();
				//String filePath=inputFile.getPath();
				if (StringUtils.getFilenameExtension(fileName).
						equals(dataLoader.configDto.getFileExtension())) {

					// String inputFileName = inputFile.getName();
					logger.info("------------------------------------------------------------------------------------------");
					logger.info("Processing file name [ " + fileName + " ] - counter  [ " + count+ " ]");

					if (checkFileConvention(fileName)) {//
						isValidFile = true;
						logger.info("Found valid file, Start Processing...");
					}
					// IF valid file name found then calling command line batch
					if (isValidFile) {
							ResponseDto responseDto=commonService.runCommandLineBatch(dataLoader.configDto.getBatchFilePath(),
									createCommandArgs(fileName),fileName);
							
							if (responseDto.isCommandRun()) {
								logger.info("File Batch Run Successfully...");
								checkResponseCode(inputFile,inputFolder,outputFolder);
							}
					}

				}

			} catch (FileException e) {
				throw new FileException("Error {0001} :  " + e.getMessage());
			} catch (Exception e) {
				throw new FileException("Error {0002} :  " + e.getMessage());

			}
		}
	}
	
	
	/***
	 * 
	 * @param fileName
	 * @return
	 */
	private String createCommandArgs(String fileName) {
		
		String apiName=fileName.split(dataLoader.configDto.getFileTypeSeparator())[0];
		String inputFolderFileName=dataLoader.configDto.getInputFolderPath()+File.separator+fileName;
		String outPutFolder=dataLoader.configDto.getOutputFolderPath()+File.separator+""+dataLoader.configDto.getResponseFilePrefix()+fileName;
		
		StringBuilder sb=new StringBuilder(apiName).append("&").append(inputFolderFileName).append("&").append(outPutFolder);
		return sb.toString();
	}
		    
	/**
	 * 
	 * @param resFileName
	 * @param dcode
	 * @param inputFile
	 * @param whichFile
	 * @throws FileException
	 */
	private boolean checkResponseCode(File inputFile,String inputFolder,String outputFolder) throws FileException {

		logger.info("Scanning Response XML in directory  [ " + dataLoader.configDto.getOutputFolderPath() + " ]");
		String fileName = inputFile.getName();
		String resFileNameStartWith = dataLoader.configDto.getResponseFilePrefix().concat(fileName);
		
		String responeFile = outputFolder.concat(File.separator).concat(resFileNameStartWith);

		if (!checkOutFile(responeFile, inputFile)) {
			logger.error("{ :: Not found Response XML :: } ===> for file [ " + fileName + " ]");
			logger.info(commonService.FILE_PROCESS_MSG);
			return false;
		}

		logger.info("{ :: Found Response XML :: } ===>  for file name [ " + resFileNameStartWith + " ]");
		logger.info("Response XML file location [ " + responeFile + " ]");

		try {
			File responseFile = Paths.get(responeFile).toFile();
			
			String responseTagVal = xmlUtilService.getValueFromXML(responseFile, dataLoader.configDto.getResponeCodeTag());
			// Print XML //final String contentXML=
			xmlUtilService.printXML(responseFile);
			
			if ("false".equalsIgnoreCase(responseTagVal)) {
				
				String changeEntityType = getEntityfromFileType(fileType.toLowerCase());
				
				// In case of EDI it will throw error. so put the check here..
				logger.info("File Type :: [ " + fileType.toLowerCase() + " ] ===> Entity Type :: [  " + changeEntityType
						+ " ]");

				if (null != changeEntityType) {
					String baseLastName = StringUtils.split(fileName, dataLoader.configDto.getFileTypeSeparator())[1];
					String changeEntityFileName = changeEntityType.concat(dataLoader.configDto.getFileTypeSeparator())
							.concat(baseLastName);
					String currentFileName =inputFolder.
							          concat(File.separator).concat(changeEntityFileName);

					if (inputFile.renameTo(Paths.get(currentFileName).toFile())) {

						logger.info("Current Input file name [ " +fileName+  "] ===> Rename To  [ " + changeEntityFileName + " ]");
						logger.info("New Modified file location [ " + currentFileName + " ]");
						ResponseDto responseDto=commonService.runCommandLineBatch(dataLoader.configDto.getBatchFilePath(),
								createCommandArgs(fileName),fileName);
						
						if (responseDto.isCommandRun()) {
							  return  checkResponseCodeForUpdate(Paths.get(currentFileName).toFile(),inputFolder,outputFolder);

						}

					}
				}
			}
			logger.info(commonService.FILE_PROCESS_MSG);

		} catch (Exception e) {
			throw new FileException("Error {0007} :  File Exeption " + e.getMessage());
		}
		return false;

	}
	
	/**
	 * if sting will come like cam then changeArray=cam=changeEntity will
	 * get the values changeEntity
	 * @param ct
	 * @return
	 */
	private String getEntityfromFileType(String ct) {
       try {
		String[] changeArray = 	dataLoader.configDto.getSupportsAPI();
		
		for(String s:changeArray) {
			if(s.isEmpty()) {
				logger.warn("Change Entity does not found for [ "+ct+" ] ");
				continue;
			}
			//
			String etype="";
			try {
				etype=StringUtils.trimWhitespace(s).split(ct.concat("="))[1]; //cam=changeEntity =>cam=
			}catch (Exception e) {
				// TODO: handle exception
			}
			//
			if(etype!=null && !etype.isEmpty()) {
				logger.info("Change Entity file type dyanmically search {} "+etype);
				return etype.trim();
			}
		}
       }catch (Exception e) {
			logger.error("Change Entity does not found, check in Confiugration setting { "+ct+" }",e.getMessage());
	    }
		return null;
	}

	
	
	/****
	 * 
	 * @param inputFile
	 * @throws FileException
	 */
	private boolean checkResponseCodeForUpdate(File inputFile,String inputFolder,String outputFolder) throws FileException {
		
		logger.info("Reprocessing for Change Entity.");
		
		String fileName = inputFile.getName();
		try {
		   logger.info("Scanning Response XML in directory  [ " + dataLoader.configDto.getOutputFolderPath() + " ]");
		   String resFileNameStartWith = dataLoader.configDto.getResponseFilePrefix().concat(fileName);
		   String responeFile = dataLoader.configDto.getOutputFolderPath().concat(File.separator).concat(resFileNameStartWith);

		if (!checkOutFile(responeFile, inputFile)) {
			logger.error("{ :: Not found Response XML :: } ===> for file [ " + fileName + " ]");
			return false;
		}

		logger.info("{ :: Found Response XML :: } ===>  for file [ " + resFileNameStartWith + " ]");
			File responseFile = Paths.get(responeFile).toFile();
			String responseTagVal = xmlUtilService.getValueFromXML(responseFile, dataLoader.configDto.getResponeCodeTag());
			// Print XML
			xmlUtilService.printXML(responseFile);
			if ("false".equalsIgnoreCase(responseTagVal)) {
				  return false;

			}else{
				  logger.info("Reprocessing finished.");
                  return true;

			}

		} catch (Exception e) {
	    	
			throw new FileException("Error {00077} :  File Exeption " + e.getMessage());
		}

	}
	
	
    /**
     * 
     * @param responeFile
     * @param inputFile
     * @return
     */
	private boolean checkOutFile(String responeFile, File inputFile) {
		File resFile=Paths.get(responeFile).toFile();
		if (!resFile.exists()) {
			logger.info("Scanning directory for response XML [ "  + dataLoader.configDto.getOutputFolderPath() + " ]");
			return false;

		}
		return true;
	}


	
	/**
	 * @- File Separtor
	 * @param fileName
	 * @return
	 */

	public boolean checkFileConvention(String fileName) {
		fileType = "NOT VALID";
		final String fname = fileName;
		try {
			String fileEndWith = dataLoader.configDto.getFileSupports().concat(",")
					.concat(dataLoader.configDto.getOptFileSupports());
			
			if (fileEndWith.isEmpty() && fileEndWith.length() == 0) {
				throw new FileException("File naming convention {empty}") ;
			}
			String fileNameAfterFileSepartor = fname.split(dataLoader.configDto.getFileTypeSeparator())[1].split("\\.")[0];

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
			logger.error("Error {0009} : File Convention : " + (fileName) + "  " + e.getMessage());
			return false;
		}
		return false;

	}

  
}
