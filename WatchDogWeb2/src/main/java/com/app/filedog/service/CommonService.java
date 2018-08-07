package com.app.filedog.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileTime;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import com.app.filedog.common.FileException;
import com.app.filedog.component.DataLoader;
import com.app.filedog.domain.DogInfoConfiguration;
import com.app.filedog.domain.Reports;
import com.app.filedog.dto.APIDto;
import com.app.filedog.dto.FileDto;
import com.app.filedog.dto.ReportDto;
import com.app.filedog.dto.ResponseDto;

/***
 * 
 * @author intakhabalam.s
 *
 */
@Service
public class CommonService {

	private final Logger logger = LogManager.getLogger("Dog-S");
	@Autowired
	MailService mailService;

	@Autowired
	DataLoader dataLoader;

	final String exceptionPattern = "(.*)(Exception+)(.*)";
	final String exceptionPattern1 = "Exception";
	final String exceptionPattern2 = "exception";
	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss") ;
	 
	@Autowired
	Environment env;
	
	
	public String currentTime() {
		
        return  dateFormat.format(new Date());
	}

	/**
	 * This method will find the Exception from String
	 * 
	 * @param str
	 * @return
	 */
	public boolean findExceptionFromString(String str) {
		try {
			if (searchRegexText(str, exceptionPattern)) {
				return true;
			}
			if (searchRegexText(str, exceptionPattern1)) {
				return true;
			}

			final List<String> foundList = Arrays.asList(str.split(" "));

			if (foundList.contains(exceptionPattern1)) {
				return true;
			}
			if (foundList.contains(exceptionPattern2)) {
				return true;
			}
			if (str.indexOf(exceptionPattern1) != -1) {
				return true;
			}
			if (str.indexOf(exceptionPattern2) != -1) {
				return true;
			}

		} catch (Exception e) {
			return false;
		}
		return false;
	}

	/**
	 * 
	 * @return
	 * @throws IOException
	 */
	public List<FileDto> loadFiles(String folderPath, int limit) {
		if (folderPath == null) {
			logger.error("Folders Paths are not defined. Please check configuartion setting");
			return null;
		}

		Path dir = FileSystems.getDefault().getPath(folderPath);
		List<FileDto> fileDtoList = new ArrayList<>();
		AtomicInteger counter = new AtomicInteger(0);

		try (DirectoryStream<Path> files = Files.newDirectoryStream(dir)) {
			StreamSupport.stream(files.spliterator(), false).sorted((o1, o2) -> {
				try {
					return Files.getLastModifiedTime(o2).compareTo(Files.getLastModifiedTime(o1));
				} catch (IOException ex) {
				}
				return 0;
			}).filter(file -> Files.isRegularFile(file)).limit(limit).forEach(file -> {
				// if(f.isFile()) {
				try {
					FileDto fdto = new FileDto();
					fdto.setId(String.valueOf(counter.incrementAndGet()));
					fdto.setFileName(file.getFileName().toString());
					fdto.setLastModifiedDate(printFileTime(Files.getLastModifiedTime(file)));
					fdto.setFileFullPath(FileSystems.getDefault().getPath(file.toString()).toString());
					fdto.setFileStatus(checkFileConvention(file.getFileName().toString()));
					fdto.setFileEndWith(fileEndWith());
					fileDtoList.add(fdto);

				} catch (Exception e) {
					// TODO: handle exception
				}
			});
		} catch (Exception e1) {
			logger.error("Error {loadFiles}", e1);

		}

		return fileDtoList;

	}

	/***
	 * 
	 * @return
	 */
	public List<String> loadReportsDir() {
		final String reportsPath=env.getProperty("reports.path").split("/")[0];
		Path dir = FileSystems.getDefault().getPath(reportsPath);
		List<String> dirList = new ArrayList<>();
		try (DirectoryStream<Path> files = Files.newDirectoryStream(dir)) {
			StreamSupport.stream(files.spliterator(), false).sorted((o2, o1) -> {
				try {
					return Files.getLastModifiedTime(o1).compareTo(Files.getLastModifiedTime(o2));
				} catch (IOException ex) {
				}
				return 0;
			}).filter(file -> Files.isRegularFile(file)).
			limit(10).
			forEach(file -> {
				dirList.add(file.getFileName().toString());
			});
		} catch (Exception e1) {
			logger.error("Error {loadReportsDir}", e1);

		}
		return dirList;
	}

	/***
	 * 
	 * @param fileTime
	 * @return
	 */
	private String printFileTime(FileTime fileTime) {
		try {
			DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy - hh:mm:ss");
			return dateFormat.format(fileTime.toMillis());
		} catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}

	/***
	 * 
	 * @param fileName
	 * @return
	 */
	public String checkFileConvention(String fileName) {
		String fileType = "INVALID FILE";
		final String fname = fileName;
		try {
			String fileEndWith = dataLoader.configDto.getFileSupports().concat(",")
					.concat(dataLoader.configDto.getOptFileSupports()).concat(",")
					.concat(dataLoader.configDto.getNonEdiCamFileSupports());

			String fileNameAfterFileSepartor = fname.split(dataLoader.configDto.getFileTypeSeparator())[1]
					.split("\\.")[0];
			List<String> fileDcOrCam = split(fileEndWith.trim(), ",");
			for (String fn : fileDcOrCam) {
				String noSpaceStr = fn.replaceAll("\\s", "");
				if (fileNameAfterFileSepartor.equalsIgnoreCase(noSpaceStr)
						|| StringUtils.startsWithIgnoreCase(fileNameAfterFileSepartor, noSpaceStr)) {
					fileType = fn;
				}
			}

		} catch (Exception e) {
			return fileType;
		}
		return fileType;

	}

	/***
	 * 
	 * @param apiDtoList
	 * @param setFile
	 */
	public void sendReports(List<APIDto> apiDtoList, Set<File> setFile, String group) {
		StringBuilder sb = new StringBuilder("");
		int apiSize = apiDtoList.size();
		File[] s1 = new File[apiSize];
		int count = 0;
		int trueCount = 0;
		sb.append(
				"<table id='customers' class='atb'><tr><td> API NAME</td><td>  API DATA TO BE PROCESSED   </td><td>  FILE NAME </td> <td> STATUS </td></tr>");
		for (APIDto dto : apiDtoList) {
			String s = dto.getFileName() != null ? dto.getFileName() : " Not Process ";
			boolean status = dto.isStatus();
			String statusStr = "Failed";
			if (status) {
				trueCount++;
				statusStr = "Passed";
			}
			String toMailContent = "<tr><td>" + dto.getApiName() + "</td><td><span> " + dto.getApiStrProcess()
			+ "</span></td><td>" + s + "</td><td>" + statusStr + "</td></tr>";
			// logger.info(toMailContent);
			sb.append(toMailContent);
			s1[count] = dto.getFile();
			count++;
		}
		sb.append("</table>");
		if (trueCount == apiSize) {
			sendMailsWithAttachment(s1, sb.toString(), "4", setFile);
		} else {
			sendMailsWithAttachment(s1, sb.toString(), "3", setFile);

		}

	}

	/***
	 * 
	 * @return
	 */
	public String fileEndWith() {
		String fileEndWith = dataLoader.configDto.getFileSupports().concat(",")
				.concat(dataLoader.configDto.getOptFileSupports()).concat(",")
				.concat(dataLoader.configDto.getNonEdiCamFileSupports());
		List<String> fileDcOrCam = split(fileEndWith.trim(), ",");
		StringBuilder sb = new StringBuilder("<select id='fileEndWithId'>");
		sb.append("<option value=''>").append("Select File Type").append("</option>");
		for (String s : fileDcOrCam) {
			sb.append("<option value=" + s + ">").append(s).append("</option>");
		}
		sb.append("</select>");
		return sb.toString();
	}

	/**
	 * public boolean findDuplicateFromString(String str) { try { if
	 * (searchRegexText(str, duplicatePatter)) { return true; } if
	 * (searchRegexText(str, duplicatePatter1)) { return true; } } catch (Exception
	 * e) { return false; } return false; }
	 */

	/***
	 * Regaex
	 * 
	 * @param str
	 * @param regex
	 * @return
	 */

	public boolean searchRegexText(String str, String regex) {
		// Create a Pattern object
		Pattern r = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
		// Now create matcher object.
		Matcher m = r.matcher(str);
		if (m.find()) {
			logger.info("During XML parsing found(s) error ==> " + m.group());
			return true;
		} else {
			logger.info("NO MATCH ===> Pattern { " + regex + " }");
			return false;
		}
	}

	/***
	 * Creating dir
	 */
	public void scanDirectory() {
		logger.info("Scanning all directory...");

		createFolder(dataLoader.configDto.getInputFolderPath(), "Input");
		createFolder(dataLoader.configDto.getOutputFolderPath(), "Output");
		createFolder(dataLoader.configDto.getArchiveFolderPath(), "Archive");
		createFolder(dataLoader.configDto.getFailureFolderPath(), "Failure");
		//
		createFolder(dataLoader.configDto.getOptInputFolderPath(), "OPT Input");
		createFolder(dataLoader.configDto.getOptOutputFolderPath(), "OPT Output");
		createFolder(dataLoader.configDto.getOptArchiveFolderPath(), "OPT Archive");
		createFolder(dataLoader.configDto.getOptFailureFolderPath(), "OPT Failure");
		createFolder(env.getProperty("backup.dir"), "Backup Folder");
		createFolder("reports", "Reports");

	}

	/***
	 * 
	 * @param path
	 * @param type
	 */
	public void createFolder(String path, String type) {
		File file = new File(path);
		if (!file.exists()) {
			if (file.mkdir()) {
				logger.info(type + " directory is created");

			} else {
				logger.error("Error : {Failed to create directory "+type+"}");

			}
		}
	}

	/**
	 * This method will move file to destination dir, if exits then replace it
	 * 
	 * @param src
	 * @param destSrc
	 * @throws FileException
	 */
	public void moveReplaceFile(String src, String destSrc) {
		Path sourcePath = Paths.get(src);
		Path destinationPath = Paths.get(destSrc);
		try {
			Files.move(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);
		} catch (Exception e) {
			logger.error("Error:  {File Moving Problem} - " + e);
		}
	}

	/***
	 * This method will send the Email
	 * 
	 * @param inputFile
	 * @param contentXML
	 * @param type
	 */
	public void sendMail(File inputFile, String contentXML, String type) {

		if (!dataLoader.configDto.getEnableMail()) {
			return;
		}
		logger.info("Sending email {}...");
		String msgBody = "";
		if ("1".equals(type)) {
			msgBody = env.getProperty("mail.response.body.error1");
		}
		if ("2".equals(type)) {
			MessageFormat msgFormat = new MessageFormat(env.getProperty("mail.response.body.error2"));
			msgBody = msgFormat.format(new Object[] { contentXML });
		}
		if ("3".equals(type)) {
			MessageFormat msgFormat = new MessageFormat(env.getProperty("mail.response.body.error3"));
			msgBody = msgFormat.format(new Object[] { contentXML });
		}

		final String[] toMail = dataLoader.configDto.getToWhomEmail().split(",");
		final String singatureWithMsg = msgBody + WATCH_DOG_GREET_MSG;
		final String sub = env.getProperty("mail.response.body.sub").concat("[ " + inputFile.getName() + " ]");
		try {

			mailService.sendEmailWithAttachment(toMail, singatureWithMsg, sub, inputFile);
			logger.info("Email sent to  " + Arrays.toString(toMail));

		} catch (Exception e) {
			logger.error("Problem in sending email {FD} " + e.getMessage());

		}
	}

	/****
	 * 
	 * @param inputFile
	 * @param contentXML
	 * @param type
	 * @param setFile
	 */
	public void sendMailsWithAttachment(File[] inputFile, String contentXML, String type, Set<File> setFile) {

		if (!dataLoader.configDto.getEnableMail()) {
			return;
		}
		logger.info("Sending email {}...");
		String msgBody = "";
		String sub = "";

		if ("3".equals(type)) {
			MessageFormat msgFormat = new MessageFormat(env.getProperty("mail.response.body.error2"));
			msgBody = msgFormat.format(new Object[] { contentXML });
			sub = env.getProperty("mail.response.body.sub");
		}
		if ("4".equals(type)) {
			MessageFormat msgFormat = new MessageFormat(env.getProperty("mail.response.body.info"));
			msgBody = msgFormat.format(new Object[] { contentXML });
			sub = env.getProperty("mail.response.body.info.sub");
		}

		final String[] toMail = dataLoader.configDto.getToWhomEmail().split(",");
		final String singatureWithMsg = msgBody + WATCH_DOG_GREET_MSG;
		try {

			mailService.sendEmailWithAttachments(toMail, singatureWithMsg, sub, inputFile, type, setFile);
			// mailService.sendEmailWithAttachments(toMail, singatureWithMsg, sub,
			// inputFile);
			logger.info("Email sent to  " + Arrays.toString(toMail));

		} catch (Exception e) {
			logger.error("Problem in sending email {FD} " + e.getMessage());

		}
	}

	/**
	 * 
	 * @param src
	 * @param destSrc
	 * @throws FileException
	 */
	public void copyReplaceFile(String src, String destSrc) {
		Path sourcePath = Paths.get(src);
		Path destinationPath = Paths.get(destSrc);
		try {
			Files.copy(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);
		} catch (FileAlreadyExistsException e) {
			// throw new FileException();
			logger.error("Error : {Ddestination file already exists} - " + e);
			// destination file already exists
		} catch (Exception e) {
			// something else went wrong
			logger.error("Error :  {Something went wrong} - " + e);
			// throw new FileException("Error : {IOException} - " + e);
		}

	}


	/***
	 * 
	 * @param src
	 * @param what
	 * @return
	 */
	public boolean containsIgnoreCaseRegexp(String src, String what) {
		return Pattern.compile(Pattern.quote(what), Pattern.CASE_INSENSITIVE).matcher(src).find();
	}

	/***
	 * 
	 * @param d
	 * @return
	 */
	public String getDecimalNumber(double d) {
		DecimalFormat df2 = new DecimalFormat("#.##");
		return df2.format(d);
	}

	/***
	 * 
	 * @param dogInfo
	 * @param fileName
	 * @return
	 * @throws JAXBException
	 * @throws FileNotFoundException
	 */
	// Moved to XMLUtilService
	@Deprecated
	public DogInfoConfiguration objectToXml(DogInfoConfiguration dogInfo, String fileName)
			throws JAXBException, FileNotFoundException {
		backupConfigFile(fileName);
		JAXBContext jaxbContext = JAXBContext.newInstance(DogInfoConfiguration.class);
		Marshaller marshaller = jaxbContext.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		marshaller.marshal(dogInfo, new File(fileName));
		// marshaller.marshal(dogInfo, System.out);
		logger.info("Configuration data marshaller sucessfully...");

		return dogInfo;
	}

	/***
	 * Backup of DB
	 * @param fileName
	 */
	public void backupConfigFile(String fileName) {
		String newFile=env.getProperty("backup.dir")+"/"+"config_"+currentTime()+".db";
		File file=new File(fileName);
		if(file.exists()) {
			copyReplaceFile(fileName, newFile);

		}
		logger.info("DB backup done sucessfully...");

	}

	/****
	 * @param fileName
	 * @return
	 * @throws JAXBException
	 * @throws FileNotFoundException
	 */
	//Moved to XML UtilService
	@Deprecated
	public DogInfoConfiguration xmlToObject(String fileName) throws JAXBException, FileNotFoundException {
		File file = new File(fileName);
		if(!file.exists()) {
			return new DogInfoConfiguration();
		}
		JAXBContext jaxbContext = JAXBContext.newInstance(DogInfoConfiguration.class);
		Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
		DogInfoConfiguration info = (DogInfoConfiguration) unmarshaller.unmarshal(file);
		logger.info("Configuration data unmarshaller sucessfully...");
		return info;
	}


	/***
	 * This method will give the List of values
	 * @param fileName
	 * @param tagName
	 * @return
	 */
	public List<String> getValuesFromXML(File fileName, String tagName) {
		List<String> nonEdiList = new ArrayList<>(0);
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(fileName);
			doc.getDocumentElement().normalize();
			NodeList nodeList = doc.getElementsByTagName(tagName);
			for (int i = 0; i < nodeList.getLength(); i++) {
				Element element = (Element) nodeList.item(i);
				nonEdiList.add(element.getTextContent().trim());

			}
			logger.debug("Load values against  tagname { "+tagName+" } : size [ "+nonEdiList.size() +" ]");
		} catch (Exception e) {
			logger.error("Error : {getValuesFromXML} : " + e.getMessage());
		}

		return nonEdiList;
	}

	/**
	 * This method will reload Config
	 * Directory once user will click from UI
	 */
	public void reloaConfigBackup() {
		String dbLocation=env.getProperty("backup.dir")+"/"+"config.db";
		String dbPath=env.getProperty("reports.path");
		copyReplaceFile(dbLocation, dbPath);
	}

	/**
	 * @param currentFileName
	 * @param archiveFolder
	 */
	public void gotoArchiveFailureFolder(String currentFileName, String fileName, String type) {
		try {
			String archiveFailureName = dataLoader.configDto.getArchiveFolderPath() + File.separator + fileName;
			if ("F".equals(type)) {
				archiveFailureName = dataLoader.configDto.getFailureFolderPath() + File.separator + fileName;
			}
			moveReplaceFile(currentFileName, archiveFailureName);
			logger.info("Moved file here : [ " + archiveFailureName + " ]");

		} catch (Exception e) {
			logger.error("Error  {gotoArchiveFailureFolder} : " + e.getMessage());
			// FileUtils.deleteQuietly(FileUtils.getFile(currentFileName));

		}
	}

	/***
	 * 
	 * @param currentFileName
	 * @param fileName
	 * @param type
	 */
	public void gotoArchiveFailureFolderFin(String currentFileName, String fileName, String type) {
		try {
			String archiveOrFailureName = getFullFileNameForArchive(fileName);

			if ("F".equals(type)) {
				archiveOrFailureName = dataLoader.configDto.getOptFailureFolderPath() + File.separator + fileName;
			}
			copyReplaceFile(currentFileName, archiveOrFailureName);

			logger.info("Fin file here : [ " + archiveOrFailureName + " ]");
		} catch (Exception e) {
			logger.error("Error  {gotoArchiveFailureFolderFin} : " + e.getMessage());

		}
	}
	
	/****
	 * 
	 * @param fileName
	 * @return
	 */
	public String getFullFileNameForArchive(String fileName) {
		String baseName=FilenameUtils.getBaseName(fileName);
		String extension=FilenameUtils.getExtension(fileName);
		String archiveFailureName = dataLoader.configDto.getOptArchiveFolderPath() +
				File.separator + baseName+"_"+currentTime()+"."+extension;
		return archiveFailureName;
	}


	/***
	 * 
	 * @param currentFileName
	 * @param type
	 */
	public void addFilesForReports(String currentFileName,String desc,String type) {

		long currtime=System.currentTimeMillis();
		ReportDto reports=new ReportDto();
		reports.setId(""+currtime);
		reports.setFilename(currentFileName);
		reports.setFiledat(printFileTime(FileTime.fromMillis(currtime)));
		reports.setDescription(desc);
		reports.setStatus(type);
		createReports(reports);
	}
	/****
	 * @param repDto
	 */
	public void createReports(ReportDto repDto) {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(false);
			factory.setValidating(false);
			DocumentBuilder builder = factory.newDocumentBuilder();
			final String reportsPath=env.getProperty("reports.path");
			File file = new File(reportsPath); // XML file to read
			if(!file.exists()) {
				createDummyXML(reportsPath);
			}
			Document document = builder.parse(file);
			Element reports = document.getDocumentElement();
			Element fileReport = document.createElement("report");
			fileReport.setAttribute("id", repDto.getId());

			Element filename = document.createElement("filename");
			Text filenameText = document.createTextNode(Paths.get(repDto.getFilename()).toFile().getName());
			filename.appendChild(filenameText);
			fileReport.appendChild(filename);
			//
			Element filedat = document.createElement("filedat");
			Text filedatText = document.createTextNode(repDto.getFiledat());
			filedat.appendChild(filedatText);
			fileReport.appendChild(filedat);
			//
			Element description = document.createElement("description");
			Text descriptionText = document.createTextNode(repDto.getDescription());
			description.appendChild(descriptionText);
			fileReport.appendChild(description);

			Element status = document.createElement("status");
			Text statusText = document.createTextNode(repDto.getStatus().equals("F")?"FAIL":"PASS");
			status.appendChild(statusText);
			fileReport.appendChild(status);


			reports.appendChild(fileReport);
			TransformerFactory tfact = TransformerFactory.newInstance();
			Transformer tform = tfact.newTransformer();
			tform.setOutputProperty(OutputKeys.INDENT, "yes");
			tform.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "3");
			//tform.transform(new DOMSource(document), new StreamResult(System.out));
			tform.transform(new DOMSource(document), new StreamResult(file));
			logger.info("Reports saved successfully...");

			//
		} catch (Exception e) {
			logger.error("Errror in {createReports} ",e);

		}
	}



	/****
	 * This will create a dummy XML
	 * @param filePath
	 */
	private  void createDummyXML(String filePath) {

		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			Document doc = docBuilder.newDocument();
			Element rootElement = doc.createElement("reports");
			doc.appendChild(rootElement);
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File(filePath));
			transformer.transform(source, result);
			logger.info("Dummy Reports created");

		} catch (Exception e) {
			logger.error("Errror in {createDummyXML} ",e);
		}
	}


	/***
	 * THis method will load All Reports..
	 */
	public Reports loadRerorts(String path) {
		Reports reports=null;
		try {
			File newFile = new File(path);
			JAXBContext jaxbContext = JAXBContext.newInstance(Reports.class);
			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
			if(!newFile.exists()) {
				createDummyXML(newFile.getPath());
			}else {
				int countTag=countXMLTag(newFile, "report");
				if (countTag > 1000) {
					backUpReports(countTag,newFile);
				}
			}
			reports = (Reports) unmarshaller.unmarshal(newFile);
			logger.info("Reports data loaded sucessfully...");

		} catch (Exception e) {
			logger.error("Errror in {getRerorts} ",e);
		}
		return reports;
	}

	/***
	 * Find the no of Tag in XML
	 * 
	 * @param filepath
	 * @param elementName
	 * @return
	 */
	public int countXMLTag(File filepath, String elementName) {
		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(filepath);
			NodeList list = doc.getElementsByTagName(elementName);
			return list.getLength();
		} catch (Exception e) {
			logger.error("Error in countXMLTag XML {} ", e);
			return -1;
		}
	}
	public Reports loadRerortsAjax(String reportsName) {
		Reports reports=null;
		try {
			String folder=env.getProperty("reports.path").split("/")[0];
			File newFile = new File(folder+"/"+reportsName);
			JAXBContext jaxbContext = JAXBContext.newInstance(Reports.class);
			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
			reports = (Reports) unmarshaller.unmarshal(newFile);
			logger.info("Reports data loaded sucessfully...");

		} catch (Exception e) {
			logger.error("Errror in {getRerorts} ",e);
		}
		return reports;
	}
	/***
	 * 
	 * @param countTag
	 * @param oldFile
	 */
	private void backUpReports(int countTag, File oldFile) {
		try {
			String folder = env.getProperty("reports.path").split("/")[0];
			final String strNewFile = folder + "/reports_" + currentTime() + ".db";
			File nowFile = new File(strNewFile);
			if (oldFile.renameTo(nowFile)) {
				logger.info("Reports DB backup successfully");
			} else {
				logger.error("Sorry! the Reports DB can't be backup");
			}

		} catch (Exception e) {
			logger.error("Sorry! the Reports DB can't be backup {} ",e);
		}
	}

	/**
	 * 
	 * @param str
	 * @param splitType
	 * @return
	 */
	public List<String> split(String str, String splitType) {
		return Stream.of(str.split(splitType)).map(elem -> new String(elem)).collect(Collectors.toList());
	}

	/**
	 * This code using Java ProcessBuilder class to run a batch file Java
	 * ProcessBuilder and BufferedReader classes are used to implement this.
	 * 
	 * @param batchFileName
	 */
	public synchronized ResponseDto runCommandLineBatch(String batchFilePath, String commandArgs, String filename) {
		// final String batchFilePath = propertiesConfig.batchFilePath;
		ResponseDto resDto = new ResponseDto();

		logger.info("Invoking Command Line Batch  [ " + batchFilePath + " ]");

		boolean processComplete = false;
		ProcessBuilder processBuilder = null;
		if (commandArgs == null) {
			processBuilder = new ProcessBuilder(batchFilePath);
			logger.info("Batch Invoking with no Args {EMPTY Args}");
		} else {
			logger.info("Batch Invoking with Following args  ");
			String[] splitArgs = commandArgs.split("\\&");
			logger.info("API : " + splitArgs[0]);
			logger.info("Input Folder : " + splitArgs[1]);
			logger.info("Output Folder : " + splitArgs[2]);
			resDto.setResponseFilePath(splitArgs[2]);
			//Process builder will take these arguments and will fire to batch file.
			processBuilder = new ProcessBuilder(batchFilePath, splitArgs[0], splitArgs[1], splitArgs[2]);
		}
		processBuilder.redirectErrorStream(true);
		try {
			final Process process = processBuilder.start();
			final InputStream is = process.getInputStream();
			// the background thread watches the output from the process
			new Thread(new Runnable() {
				public void run() {
					try (InputStreamReader input = new InputStreamReader(is);
							BufferedReader reader = new BufferedReader(input)) {
						String line;
						StringBuilder sb = new StringBuilder();
						while ((line = reader.readLine()) != null) {
							sb.append(line).append("\n");
						}
						if ("true".equalsIgnoreCase(dataLoader.configDto.getEnableResponseCodeLog())) {
							logger.info("Respone code - " + sb.toString());
						}
						if (findExceptionFromString(sb.toString())) {
							writeFile(sb.toString(), filename);
							resDto.setResponseError(true);
							resDto.setResponseErrorStr(sb.toString());
						}
						logger.info("Read all input data as response.");
					} catch (IOException e) {
						resDto.setResponseError(true);
						logger.error(
								"Error {0003} : ProcessBuilder: Exception occured during : When Invoking Batch File "
										+ e.getMessage());
					} finally {
						try {
							is.close();
						} catch (IOException e) {
							logger.error("Error {0004} : Input stream closing problem : When Invoking Batch File "
									+ e.getMessage());

						}
					}
				}
			}).start();
			// Wait to get exit value
			// the outer thread waits for the process to finish

			try {
				processComplete = process.waitFor(1, TimeUnit.MINUTES);
			} catch (Exception e) {
				logger.error("Forcely Interrupted {} ", e);
				resDto.setCommandRun(false);
				process.destroyForcibly();
			}

			logger.info("Process Builder result code : [ " + processComplete + " ]");
			if (!processComplete) {
				logger.error("Error: Something bad Happened !!! ");
				logger.error("Error: Host server is not responding !!! ");
				logger.error("Error: Server Response code taking too much time. Timeout occured!!!");
				process.destroyForcibly();
			} else {
				logger.info("Process Done!!! ");
				resDto.setCommandRun(true);
			}
		} catch (Exception e) {
			logger.error("Error {0005} : Process Builder result code: [" + processComplete + " ] " + e.getMessage());
			resDto.setCommandRun(false);
		}
		return resDto;
	}

	/***
	 * 
	 * @param data
	 * @param path
	 */
	public void writeFile(String data, String filename) {

		final String filePath = env.getProperty("logs.dir") + File.separator + "Error_"
				+ FilenameUtils.getBaseName(filename) + ".log";

		logger.info("Writing file in error folder with name [ " + filePath + "]");

		Path filepath = Paths.get(filePath);
		byte[] bytes = data.getBytes();
		try (OutputStream out = Files.newOutputStream(filepath)) {
			out.write(bytes);

		} catch (Exception e) {
			logger.error("Error during writing file {}  ", e);

		}
	}

	/***********************************************/

	public final String CLOSE_BANNER = "\n\n __        __          _            _         ____                       ____   _                 _                 \r\n"
			+ " \\ \\      / /   __ _  | |_    ___  | |__     |  _ \\    ___     __ _     / ___| | |   ___    ___  (_)  _ __     __ _ \r\n"
			+ "  \\ \\ /\\ / /   / _` | | __|  / __| | '_ \\    | | | |  / _ \\   / _` |   | |     | |  / _ \\  / __| | | | '_ \\   / _` |\r\n"
			+ "   \\ V  V /   | (_| | | |_  | (__  | | | |   | |_| | | (_) | | (_| |   | |___  | | | (_) | \\__ \\ | | | | | | | (_| |\r\n"
			+ "    \\_/\\_/     \\__,_|  \\__|  \\___| |_| |_|   |____/   \\___/   \\__, |    \\____| |_|  \\___/  |___/ |_| |_| |_|  \\__, |\r\n"
			+ "                                                              |___/                                           |___/ \r\n"
			+ "";

	public final String OPEN_BANNER = "\n\n __        __          _            _         ____                      ____    _                    _     _                 \r\n"
			+ " \\ \\      / /   __ _  | |_    ___  | |__     |  _ \\    ___     __ _    / ___|  | |_    __ _   _ __  | |_  (_)  _ __     __ _ \r\n"
			+ "  \\ \\ /\\ / /   / _` | | __|  / __| | '_ \\    | | | |  / _ \\   / _` |   \\___ \\  | __|  / _` | | '__| | __| | | | '_ \\   / _` |\r\n"
			+ "   \\ V  V /   | (_| | | |_  | (__  | | | |   | |_| | | (_) | | (_| |    ___) | | |_  | (_| | | |    | |_  | | | | | | | (_| |\r\n"
			+ "    \\_/\\_/     \\__,_|  \\__|  \\___| |_| |_|   |____/   \\___/   \\__, |   |____/   \\__|  \\__,_| |_|     \\__| |_| |_| |_|  \\__, |\r\n"
			+ "                                                              |___/                                                    |___/ \r\n"
			+ "";

	public final String DUPLICATE_STR = "duplicate";
	public final String ERROR_CODE = "Code";
	public final String FILE_PROCESS_MSG = "######### File process completed ###########";

	public final String ERROR_MAIL_HEADER_MSG = "[|-rror //\\/\\essage";
	public final String SUCCESS_MAIL_HEADER_MSG = "((uccess //\\/\\essage";

	public final String WATCH_DOG_GREET_MSG = "\\\\/\\/atch  [|)og";



}
