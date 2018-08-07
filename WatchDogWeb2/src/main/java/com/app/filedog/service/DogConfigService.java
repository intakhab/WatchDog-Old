package com.app.filedog.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Set;
import java.util.TreeSet;

import javax.xml.bind.JAXBException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.app.filedog.domain.DogInfoConfiguration;
import com.app.filedog.dto.FileDogInfoDto;
/***
 * 
 * @author intakhabalam.s@hcl.com
 *
 */
@Service
public class DogConfigService {

	@Autowired
	private XMLUtilService xmlUtilService;
	@Autowired
	private CommonService commonService;
	@Autowired
	Environment env;
	/***
	 * 
	 * @param fdogDto
	 * @return
	 * @throws JAXBException 
	 * @throws FileNotFoundException 
	 */
	public DogInfoConfiguration saveOrUpdate(FileDogInfoDto fdogDto) throws
	FileNotFoundException, JAXBException {

		DogInfoConfiguration config = getObjFromDto(fdogDto);
		String fileName = env.getProperty("db.location");
		commonService.backupConfigFile(fileName);

		return (DogInfoConfiguration) xmlUtilService.convertObjectToXML(config, env.getProperty("db.location"));
	}
		
	/***
	 * 
	 * @param fdogDto
	 * @return
	 */
	public DogInfoConfiguration getObjFromDto(FileDogInfoDto fdogDto) {
		DogInfoConfiguration wdInfo = new DogInfoConfiguration();
		wdInfo.setDogId("1");
		wdInfo.setBatchFilePath(fdogDto.getBatchFilePath());
		wdInfo.setDogType("FD");
		wdInfo.setArchiveFolderPath(fdogDto.getArchiveFolderPath());
		wdInfo.setEnableArchiveOthersFile(Boolean.valueOf(fdogDto.getEnableArchiveOthersFile()));
		wdInfo.setEnableMail(fdogDto.getEnableMail());
		wdInfo.setEnableResponseCodeLog(Boolean.valueOf(fdogDto.getEnableResponseCodeLog()));
		wdInfo.setFileExtension(fdogDto.getFileExtension());
		wdInfo.setFileSupports(fdogDto.getFileSupports());
		wdInfo.setFileTypeSeparator(fdogDto.getFileTypeSeparator());
		wdInfo.setInputFolderPath(fdogDto.getInputFolderPath());
		wdInfo.setOutputFolderPath(fdogDto.getOutputFolderPath());
		wdInfo.setFailureFolderPath(fdogDto.getFailureFolderPath());
		wdInfo.setResponeCodeTag(fdogDto.getResponeCodeTag());
		wdInfo.setResponseFilePrefix(fdogDto.getResponseFilePrefix());
		wdInfo.setSupportsAPI(arrayModification(fdogDto.getSupportsAPI()));
		wdInfo.setToWhomEmail(fdogDto.getToWhomEmail());
		wdInfo.setFlag(true);
		wdInfo.setOptInputFolderPath(fdogDto.getOptInputFolderPath());
		wdInfo.setOptOutputFolderPath(fdogDto.getOptOutputFolderPath());
		wdInfo.setOptArchiveFolderPath(fdogDto.getOptArchiveFolderPath());
		wdInfo.setOptFailureFolderPath(fdogDto.getOptFailureFolderPath());
		wdInfo.setOptFileSupports(fdogDto.getOptFileSupports());
		wdInfo.setOptSupportsAPI(arrayModification(fdogDto.getOptSupportsAPI()));
		wdInfo.setNonEdiCamFileSupports(fdogDto.getNonEdiCamFileSupports());
		wdInfo.setNonEdiCamSupportsAPI(arrayModification(fdogDto.getNonEdiCamSupportsAPI()));
		wdInfo.setStopFileRun(fdogDto.isStopFileRun());
		wdInfo.setStopBatchRun(fdogDto.isStopBatchRun());
		wdInfo.setStopNonEdiBatchRun(fdogDto.isStopNonEdiBatchRun());
		wdInfo.setLimitFilesFolder(fdogDto.getLimitFilesFolder());
		wdInfo.setSystemPlanIdText(fdogDto.getSystemPlanIdText());
		wdInfo.setSystemPlanId(arrayModification(fdogDto.getSystemPlanId(),fdogDto.getSystemPlanIdText()));

		return wdInfo;
	}
	
	
	/****
	 * 
	 * @return
	 * @throws JAXBException 
	 * @throws FileNotFoundException 
	 */
	public FileDogInfoDto getDtoFromObj(String location) throws FileNotFoundException, JAXBException {
		String dbLocation;
		if("1".equals(location)) {
			dbLocation=env.getProperty("db.location");
		}else if("3".equals(location)) {//backup
			dbLocation=env.getProperty("backup.dir")+"/"+"config.db";
		}
		else {
			dbLocation=env.getProperty("db.template");
		}
		DogInfoConfiguration  wdInfo=xmlUtilService.convertXMLToObject(DogInfoConfiguration.class, new File(dbLocation));
		
		
		FileDogInfoDto fdogDto=new FileDogInfoDto();
			fdogDto.setDogId(String.valueOf(wdInfo.getDogId()));
			fdogDto.setBatchFilePath(wdInfo.getBatchFilePath());
			fdogDto.setDogType(wdInfo.getDogType());
			fdogDto.setArchiveFolderPath(wdInfo.getArchiveFolderPath());
			fdogDto.setEnableArchiveOthersFile(String.valueOf(wdInfo.isEnableArchiveOthersFile()));
			fdogDto.setEnableMail(wdInfo.isEnableMail());
			fdogDto.setEnableResponseCodeLog(String.valueOf(wdInfo.isEnableResponseCodeLog()));
			fdogDto.setFileExtension(wdInfo.getFileExtension());
			fdogDto.setFileSupports(wdInfo.getFileSupports());
			fdogDto.setFileTypeSeparator(wdInfo.getFileTypeSeparator());
			fdogDto.setInputFolderPath(wdInfo.getInputFolderPath());
			fdogDto.setOutputFolderPath(wdInfo.getOutputFolderPath());
			fdogDto.setResponeCodeTag(wdInfo.getResponeCodeTag());
			fdogDto.setResponseFilePrefix(wdInfo.getResponseFilePrefix());
			fdogDto.setSupportsAPI(wdInfo.getSupportsAPI().split(","));
			fdogDto.setToWhomEmail(wdInfo.getToWhomEmail());
			fdogDto.setFailureFolderPath(wdInfo.getFailureFolderPath());
			fdogDto.setFlag(wdInfo.isFlag());
			fdogDto.setOptInputFolderPath(wdInfo.getOptInputFolderPath());
			fdogDto.setOptOutputFolderPath(wdInfo.getOptOutputFolderPath());
			fdogDto.setOptArchiveFolderPath(wdInfo.getOptArchiveFolderPath());
			fdogDto.setOptFailureFolderPath(wdInfo.getOptFailureFolderPath());
			fdogDto.setOptFileSupports(wdInfo.getOptFileSupports());
			fdogDto.setOptSupportsAPI(wdInfo.getOptSupportsAPI().split(","));
			fdogDto.setNonEdiCamFileSupports(wdInfo.getNonEdiCamFileSupports());
			fdogDto.setNonEdiCamSupportsAPI(wdInfo.getNonEdiCamSupportsAPI().split(","));
			fdogDto.setStopFileRun(wdInfo.isStopFileRun());
			fdogDto.setStopBatchRun(wdInfo.isStopBatchRun());
			fdogDto.setStopNonEdiBatchRun(wdInfo.isStopNonEdiBatchRun());

			fdogDto.setLimitFilesFolder(wdInfo.getLimitFilesFolder());
			fdogDto.setSystemPlanId(wdInfo.getSystemPlanId().split(","));
			fdogDto.setSystemPlanIdText(wdInfo.getSystemPlanIdText());
			
		return fdogDto;
	}


	/****
	 * This method will modify system plan id and system pan text
	 * @param systemPlanId
	 * @param systemPlanIdText
	 * @return
	 */
	private String arrayModification(String[] systemPlanId, String systemPlanIdText) {
		Set<String> set = new TreeSet<String>();
		if (systemPlanId.length != 0) {
			for (String s : systemPlanId) {
				if (!s.isEmpty()) {
					set.add(s);
				}
			}
		}
		set.add(systemPlanIdText);
		StringBuilder sb=new StringBuilder("");
		for(String s:set) {
				sb.append(s).append(",");
		}
		if (sb.length() > 0) {
			sb.setLength(sb.length() - 1);
		}
		return sb.toString();
		
	}


	/***
	 * 
	 * @param apiArrays
	 * @return
	 */
	private String arrayModification(String[] apiArrays) {
		StringBuilder sb = new StringBuilder("");
		if (apiArrays.length != 0) {
			for (String s : apiArrays) {
				if (s != null && !s.isEmpty()) {
					sb.append(s).append(",");
				}
			}
			if (sb.length() > 0) {
				sb.setLength(sb.length() - 1);
			}
		}
		return sb.toString();
	}
}
