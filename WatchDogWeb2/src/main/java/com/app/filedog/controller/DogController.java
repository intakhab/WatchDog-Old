package com.app.filedog.controller;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.util.FileCopyUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.app.filedog.component.DataLoader;
import com.app.filedog.config.PropertiesConfig;
import com.app.filedog.domain.Report;
import com.app.filedog.domain.Reports;
import com.app.filedog.dto.DogStatusDto;
import com.app.filedog.dto.FileDogInfoDto;
import com.app.filedog.dto.FileDto;
import com.app.filedog.service.AlertService;
import com.app.filedog.service.CommonService;
import com.app.filedog.service.DogConfigService;
/***
 * 
 * @author intakhabalam.s@hcl.com
 *
 */
@Controller
public class DogController {
	//private final Logger logger = LogManager.getLogger("DoC-1");

	@Autowired
	PropertiesConfig propertiesConfig;

	@Autowired
	CommonService commonService;

	@Autowired
	DogConfigService dogConfigService;

	@Autowired
	AlertService alertService;
	@Autowired
	DataLoader dataLoader;
	@Autowired
	Environment env;
	
	
	/***
	 * This is landing pages
	 * @param model
	 * @return
	 */
	
	@RequestMapping("/")
	public String landingPage(Map<String, Object> model) {
		model.put("statusList", getServerStatus());
		return "status";
	}

	/***
	 * This will load all configuration data from DB
	 * @param model
	 * @return
	 */
	@RequestMapping("/configfile")
	public String fileConfig(ModelMap model) {
		FileDogInfoDto infoObj;
			try {
				infoObj = dogConfigService.getDtoFromObj("1");
				model.addAttribute("infoObj", infoObj);
				
			} catch (FileNotFoundException | JAXBException e) {
				try {
					
					infoObj = dogConfigService.getDtoFromObj("2");
					 model.addAttribute("infoObj", infoObj);

				} catch (FileNotFoundException | JAXBException e1) {
					model.addAttribute("msg", e.getMessage());
					return "redirect:/errorpage?msg=Configuration file loading error " + e1.getMessage();		
					
				}
			} 
		
		
		return "configfile";
	}
	
	
	@RequestMapping("/restoreconfig")
	public String restoreConfig(ModelMap model) {
		FileDogInfoDto infoObj;
		try {
			commonService.reloaConfigBackup();
			infoObj = dogConfigService.getDtoFromObj("1");
			model.addAttribute("infoObj", infoObj);
			model.addAttribute("msg", alertService.sucess("Configuration load successfully"));

		} catch (Exception e) {
			return "redirect:/errorpage?msg=Configuration file loading error " + e.getMessage();

		}
		return "configfile";
	}
	

	/***
	 * This method will save config information
	 * @param watchDogObj
	 * @param result
	 * @param model
	 * @return
	 */
	@RequestMapping("/saveconfiginfo")
	public String saveConfigInfo(@Valid @ModelAttribute("infoObj") FileDogInfoDto infoObj, BindingResult result,
			ModelMap model) {
		try {
			if (result.hasErrors()) {
				return "redirect:/errorpage?msg=Data having some issues ";
			}
			dogConfigService.saveOrUpdate(infoObj);
			model.addAttribute("msg", alertService.sucess("Information saved successfully"));
			infoObj = dogConfigService.getDtoFromObj("1");
			model.addAttribute("infoObj", infoObj);
			dataLoader.refreshData(infoObj);
			
		} catch (Exception e) {
			model.addAttribute("msg", e.getMessage());
			return "redirect:/errorpage?msg=Saving confiuaration error " + e.getMessage();
		}
		return "configfile";
	}
	
	
	
	/***
	 * Logs page
	 * @param model
	 * @return
	 */
		
	@RequestMapping("/logspage")
	public String logsPage(ModelMap model) {
		List<FileDto> fileDtoList = commonService.loadFiles(env.getProperty("logs.dir"),
				Integer.valueOf(dataLoader.configDto.getLimitFilesFolder()));
		model.put("fileList", fileDtoList);
		return "logspage";
	}
	
	/***
	 * Report Error
	 * @param model
	 * @return
	 */
	@RequestMapping("/reportspage")
	public String reportsPage(ModelMap model,HttpServletRequest request) {
		try {
			
			String dbNmae=request.getParameter("db");
			List<String> dirList = commonService.loadReportsDir();
			model.addAttribute("dirList", dirList);
			Reports reports=null;
			if(dbNmae==null) {
				reports = commonService.loadRerorts(env.getProperty("reports.path"));
			}else {
				reports = commonService.loadRerortsAjax(dbNmae);
			}
			if (reports != null) {
				model.addAttribute("infoObj", reports.getReports());
			} else {
				model.addAttribute("infoObj", Collections.emptyList());
			}
      
		} catch (Exception e) {
			return "redirect:/errorpage?msg=Reports DB loading error {} " + e.getMessage();

		}
		return "reportspage";
	}

	/***
	 * 
	 * @param reportsDb
	 * @return
	 */
	@RequestMapping(value = "/reportslist/{reportsdb}", method = RequestMethod.POST)
	public @ResponseBody List<Report> getReportsList(@PathVariable("reportsdb") String reportsDb) {
		
		Reports reports = commonService.loadRerortsAjax(reportsDb);
		if (reports != null) {
			return reports.getReports();
		} else {
			return Collections.emptyList();
		}
	}

	
	
	/****
	 * @return
	 */
	@RequestMapping("/responseout")
	public ModelAndView responseOut() {
		ModelAndView mav = new ModelAndView();
		List<FileDto> fileDtoList = commonService.loadFiles(dataLoader.configDto.getOutputFolderPath(),
				Integer.valueOf(dataLoader.configDto.getLimitFilesFolder()));
		mav.addObject("fileList", fileDtoList);
		mav.setViewName("responseout");

		return mav;
	}
   /***
    * 
    * @return
    */
	@RequestMapping("/archiveout")
	public ModelAndView archiveOut() {
		ModelAndView mav = new ModelAndView();
		List<FileDto> fileDtoList = commonService.loadFiles(dataLoader.configDto.getArchiveFolderPath(),
				Integer.valueOf(dataLoader.configDto.getLimitFilesFolder()));
		mav.addObject("fileList", fileDtoList);
		mav.setViewName("archiveout");
		return mav;
	}
    /***
     * 
     * @return
     */
	@RequestMapping("/inputdir")
	public ModelAndView inputDir() {
		ModelAndView mav = new ModelAndView();
		List<FileDto> fileDtoList = commonService.loadFiles(dataLoader.configDto.getInputFolderPath(),
				Integer.valueOf(dataLoader.configDto.getLimitFilesFolder()));
		mav.addObject("fileList", fileDtoList);
		mav.setViewName("inputdir");
		return mav;
	}
    /***
     * 
     * @return
     */
	@RequestMapping("/failuredir")
	public ModelAndView failureDir(HttpServletRequest httpRequest) {
		ModelAndView mav = new ModelAndView();
		List<FileDto> fileDtoList = commonService.loadFiles(dataLoader.configDto.getFailureFolderPath(),
				Integer.valueOf(dataLoader.configDto.getLimitFilesFolder()));
	    String errorMsg = httpRequest.getParameter("msg");
		mav.addObject("msg", errorMsg);
		mav.addObject("fileList", fileDtoList);
		mav.setViewName("failuredir");
		return mav;
	}
	
    /***
     * downloadFile All files
     * @param fileName
     * @param fileType
     * @param response
     * @return
     */
	@RequestMapping("/downloadfile/{fileName}/{fileType}")
	public String downloadFile(@PathVariable("fileName") String fileName, @PathVariable("fileType") String fileType,
			HttpServletResponse response) {
		String filetoDownload = "";
		try {
			switch (fileType) {

			case "in":
				filetoDownload = dataLoader.configDto.getInputFolderPath();
				break;

			case "out":
				filetoDownload = dataLoader.configDto.getOutputFolderPath();
				break;
			case "ar":
				filetoDownload = dataLoader.configDto.getArchiveFolderPath();
				break;
			case "fail":
				filetoDownload = dataLoader.configDto.getFailureFolderPath();
				break;
			case "logs":
				filetoDownload = env.getProperty("logs.dir");
				break;
			default:

				break;

			}
			if (!filetoDownload.isEmpty()) {
				final String filePath = filetoDownload.concat(File.separator).concat(fileName);
				return fileDonwload(filePath, response);
			} else {
				return "redirect:/errorpage?msg=Error in downloading file";

			}
		} catch (Exception e) {
			return "redirect:/errorpage?msg=Error in downloading file";

		}
		// return null;
	}

	@RequestMapping("/renameinvalidfile/{fileName}/{fileType}")
	public String renameInvalidFile(@PathVariable("fileName") String fileName,
			@PathVariable("fileType") String fileType, Model model) {

		try {
			final String filePath = dataLoader.configDto.getInputFolderPath().concat(File.separator).concat(fileName);
			File file = Paths.get(filePath).toFile();
			if (file.exists()) {
				String currFileBase = fileName.split(dataLoader.configDto.getFileTypeSeparator())[0];
				// String currFileExtension=FilenameUtils.getExtension(fileName);
				String renameFileName = currFileBase.concat(dataLoader.configDto.getFileTypeSeparator())
						.concat(fileType).concat("_" + System.currentTimeMillis()).concat(".").concat("xml");
				boolean isRename = file.renameTo(new File(
						dataLoader.configDto.getInputFolderPath().concat(File.separator).concat(renameFileName)));
				if (isRename) {
					model.addAttribute("msg", alertService.sucess("File Rename Successfully to " + renameFileName + ""));
					return "redirect:/inputdir?msg=File Rename Successfully to " + renameFileName ;

				}
			}

		} catch (Exception e) {
			return "redirect:/errorpage?msg=Error occured during renaming file";
		}
		return "redirect:/errorpage?msg=Error occured during renaming file";

	}

	@RequestMapping("/fileupload")
	public String fileUpload(Map<String, Object> model) {
		return "fileupload";
	}

	/**
	 * Upload single file using Spring Controller
	 */
	@RequestMapping(value = "/uploadfile", method = RequestMethod.POST)
	public ModelAndView uploadFile(@RequestParam("file") MultipartFile file) {
		if (file != null && !file.isEmpty()) {
			try {

				MultipartFile multipartFile = file;
				String uploadPath = dataLoader.configDto.getInputFolderPath() + File.separator;
				// Now do something with file...
				FileCopyUtils.copy(file.getBytes(), new File(uploadPath + file.getOriginalFilename()));
				String fileName = multipartFile.getOriginalFilename();
				return new ModelAndView("fileupload", "msg",
						alertService.sucess("File saved successfully " + fileName + ""));

			} catch (Exception e) {
				return new ModelAndView("fileupload", "msg",
						alertService.error("Problem in saving file, Check file size before upload"));

			}
		} else {
			return new ModelAndView("fileupload", "msg", alertService.error("Select file to upload"));

		}
	}

	/***
	 * 
	 * @param fileName
	 * @param response
	 * @return
	 */
	@RequestMapping("/movetoinputdir/{fileName}/{type}")
	public String moveToInputDir(@PathVariable("fileName") String fileName, @PathVariable("type") String type,HttpServletResponse response) {
        String mesg="File moved successfully";
		try {
			String action="redirect:/archiveout?msg="+mesg;
			 String filePath = dataLoader.configDto.getArchiveFolderPath().concat(File.separator).concat(fileName);
			if("fail".equalsIgnoreCase(type)) {
				filePath=dataLoader.configDto.getFailureFolderPath().concat(File.separator).concat(fileName);
				action="redirect:/failuredir?msg="+mesg;
			}
			if("in".equalsIgnoreCase(type)) {
				filePath=dataLoader.configDto.getInputFolderPath().concat(File.separator).concat(fileName);
				action="redirect:/inputdir?msg="+mesg;
			}
			File file = new File(filePath);
			if (!file.exists()) {
				String errorMessage = "Sorry. The file you are looking for does not exist";
				return "redirect:/errorpage?msg=" + errorMessage;

			}
			doMoveFile(filePath, fileName,type);
			return action;
		} catch (Exception e) {
			return "redirect:/errorpage?msg=Error occured during moving to input folder";
		}

	}

	/**
	 * 
	 * @param fileName
	 * @param response
	 * @return
	 */

	@RequestMapping("/deletefilefrominput/{fileName}")
	public String deleteFileFromInputDir(@PathVariable("fileName") String fileName, HttpServletResponse response) {

		try {
			final String filePath = dataLoader.configDto.getInputFolderPath().concat(File.separator).concat(fileName);
			File file = new File(filePath);

			if (file.exists()) {
				FileUtils.deleteQuietly(FileUtils.getFile(filePath));
			}

		} catch (Exception e) {
			return "redirect:/errorpage?msg=Error occured during deleting file from input folder";

		}
		return "redirect:/inputdir";

	}

	/**
	 * 
	 * @param arachiveFilePath
	 * @param fileName
	 */
	public void doMoveFile(String arachiveFilePath, String fileName,String type) {
		try {

			String inputFolderFile = dataLoader.configDto.getInputFolderPath() + File.separator + fileName;
			if("in".equals(type)) {
				inputFolderFile=dataLoader.configDto.getArchiveFolderPath() + File.separator + fileName;
			}
			commonService.moveReplaceFile(arachiveFilePath, inputFolderFile);

		} catch (Exception e) {
			FileUtils.deleteQuietly(FileUtils.getFile(arachiveFilePath));

		}
	}

	/***
	 * 
	 * @return
	 */
	public List<DogStatusDto> getServerStatus() {
		List<DogStatusDto> dogList = new ArrayList<>();

		try {
			InetAddress ipAddr = InetAddress.getLocalHost();
			DogStatusDto dog = new DogStatusDto();
			dog.setServerStatus("Server Status");
			dog.setHostAddress("Host Address");
			dog.setHostName("Host Name");
			dog.setCononicalHostName("Canonical Host Name");
			dog.setUserName("User Name");
			dogList.add(dog);

			DogStatusDto dog1 = new DogStatusDto();
			dog1.setServerStatus("Running");
			dog1.setHostAddress(ipAddr.getHostAddress());
			dog1.setHostName(ipAddr.getHostName());
			dog1.setCononicalHostName(ipAddr.getCanonicalHostName());
			String username = System.getProperty("user.name");
			if (username != null) {
				dog1.setUserName(username);
			} else {
				dog1.setUserName("");

			}
			dogList.add(dog1);
            statusToXml(dog1);
            writeFile(dog1.getHostAddress());
		} catch (Exception e) {
			return null;
		}

		return dogList;
	}
	
	/***
	 * 
	 * @param data
	 */
	public void writeFile(String data) {
		
		String newData="<!DOCTYPE html>\n" + 
				"<html lang=\"en\">\n" + 
				"<head>\n" + 
				"<meta http-equiv=\"refresh\" content=\"5;url=http://"+data+":"+env.getProperty("server.port")+"/\" />\n" + 
				"<title>Watch Dog</title>\n" + 
				"</head>\n" + 
				"<body>\n" + 
				"\n" + 
				"	<div class=\"container\">\n" + 
				"			\n" + 
				"			<div style=\"height:50px; color: white; background-color: green ; \n" + 
				"				font-family:Sans-Serif;text-align:center;padding-top:40px;\">\n" + 
				"				 <strong>Starting...\n" + 
				"				 </strong>\n" + 
				"			       <img  src=\"data:image/gif;base64,R0lGODlhEAAQAPIAAP///wAAAMLCwkJCQgAAAGJiYoKCgpKSkiH/C05FVFNDQVBFMi4wAwEAAAAh/hpDcmVhdGVkIHdpdGggYWpheGxvYWQuaW5mbwAh+QQJCgAAACwAAAAAEAAQAAADMwi63P4wyklrE2MIOggZnAdOmGYJRbExwroUmcG2LmDEwnHQLVsYOd2mBzkYDAdKa+dIAAAh+QQJCgAAACwAAAAAEAAQAAADNAi63P5OjCEgG4QMu7DmikRxQlFUYDEZIGBMRVsaqHwctXXf7WEYB4Ag1xjihkMZsiUkKhIAIfkECQoAAAAsAAAAABAAEAAAAzYIujIjK8pByJDMlFYvBoVjHA70GU7xSUJhmKtwHPAKzLO9HMaoKwJZ7Rf8AYPDDzKpZBqfvwQAIfkECQoAAAAsAAAAABAAEAAAAzMIumIlK8oyhpHsnFZfhYumCYUhDAQxRIdhHBGqRoKw0R8DYlJd8z0fMDgsGo/IpHI5TAAAIfkECQoAAAAsAAAAABAAEAAAAzIIunInK0rnZBTwGPNMgQwmdsNgXGJUlIWEuR5oWUIpz8pAEAMe6TwfwyYsGo/IpFKSAAAh+QQJCgAAACwAAAAAEAAQAAADMwi6IMKQORfjdOe82p4wGccc4CEuQradylesojEMBgsUc2G7sDX3lQGBMLAJibufbSlKAAAh+QQJCgAAACwAAAAAEAAQAAADMgi63P7wCRHZnFVdmgHu2nFwlWCI3WGc3TSWhUFGxTAUkGCbtgENBMJAEJsxgMLWzpEAACH5BAkKAAAALAAAAAAQABAAAAMyCLrc/jDKSatlQtScKdceCAjDII7HcQ4EMTCpyrCuUBjCYRgHVtqlAiB1YhiCnlsRkAAAOwAAAAAAAAAAAA==\" />\n" + 
				"\n" + 
				"			</div>\n" + 
				"			<h4 class=\"alert-heading\">\n" + 
				"					<img height=\"400px\" style=\"padding-left: 250px;\"  src=\"data:image/png;base64,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D+PzAZ4b8HxF7eETGAAPiD944Lk37+jcrm1pEJryhDCf8oQWrVF/pnnPPjvwgeWTuoR9MiN05DezQv+5I0o19lCS7LWSROlTR5MUfSuTu/lbM2Fof+C5ME/c6T2BAYD4gwceeOCBB15z4dkr/Khs8MADDzzwwGsaPFQOeOCBBx544EH8UTnggQceeOCBB/FHZYMHHnjggQcexB+VDR544IEHHngQf/DAAw888MADD+IPHnjggQceeOC5oviLPv2HygYPPPDAAw+8JsHjQ/+LDhLkg8oGDzzwwAMPPLcXfw9RBkCQT9gXlQ0eeOCBBx54bi3+fL4fywaAe7E3N/r3RWWDBx544IEHntuKvyeX7beVxdD/3Iu9uNG/jyC3MCobPPDAAw888NyL58UVnQGw5hTaCAyADyobPPDAAw888NyO583pOW8APKytEXgJDEBbVDZ44IEHHnjguR2P13DeALSyNPXvwTkE3gB4o7LBAw888MADz+14/Ow9bwA8LYl/S84dtBasF6CywQMPPPDAA8/9eL4CA+BlbdOf0AB4io4ShMoGDzzwwAMPPFfj8QbA26Kec29qKTgjCPEHDzzwwAMPPPfl+YrawycwAB4Qf/DAAw888MBze56403sCAwDxBw888MADD7zmwrNX+FHZ4IEHHnjggdc0eKgc8MADDzzwwIP4o3LAAw888MADD+KPygYPPPDAAw88iD8qGzzwwAMPPPAg/uCBBx544IEHHsQfPPDAAw888MBzRfEXffoPlQ0eeOCBBx54TYLHh/4XHSTIB5UNHnjggQceeG4v/h6iDIAgn7AvKhs88MADDzzw3Fr8+Xw/lg0A92JvbvTvi8oGDzzwwAMPPLcVf08u228ri6H/uRd7caN/H0FuYVQ2eOCBBx544LkXz4srOgNgzSm0ERgAH1Q2eOCBBx544Lkdz5vTc94AeFhbI/ASGIC2qGzwwAMPPPDAczser+G8AWhlaerfg3MIvAHwRmWDBx544IEHntvx+Nl73gB4WhL/lpw7aC1YL0BlgwceeOCBB5778XwFBsDL2qY/oQHwFB0lCJUNHnjggQceeK7G4w2At0U9597UUnBGEOIPHnjggQceeO7L8xW1h09gADwg/uCBBx544IHn9jxxp/cEBgDiDx544IEHHnjNhWev8KOywQMPPPDAA69p8FA54IEHHnjggQfxR+WABx544IEHHsQflQ0eeOCBBx54EH9UNnjggQceeOBB/MEDDzzwwAMPPIg/eOCBBx544IHniuIv+vQfKhs88MADDzzwmgSPD/0vOkiQDyobPPDAAw888Nxe/D1EGQBBPmFfVDZ44IEHHnjgubX48/l+LBsA7sXe3OjfF5UNHnjggQceeG4r/p5ctt9WFkP/cy/24kb/PoLcwqhs8MADDzzwwHMvnhdXdAbAmlNoIzAAPqhs8MADDzzwwHM7njen57wB8LC2RuAlMABtUdnggQceeOCB53Y8XsN5A9DK0tS/B+cQeAPgjcoGDzzwwAMPPLfj8bP3vAHwtCT+LTl30FqwXoDKBg888MADDzz34/kKDICXtU1/QgPgKTpKECobPPDAAw888FyNxxsAb4t6zr2ppeCMIMQfPPDAAw888NyX5ytqD5/AAHhA/MEDDzzwwAPP7XniTu8JDADEHzzwwAMPPPCaC89e4UdlgwceeOCBB17T4KFywAMPPPDAAw/ij8oBDzzwwAMPPIg/Khs88MADDzzwIP6obPDAAw888MCD+IMHHnjggQceeBB/8MADDzzwwAPPFcVf9Ok/VDZ44IEHHnjgNQkeH/pfdJAgH1Q2eOCBBx544Lm9+HuIMgCCfMK+qGzwwAMPPPDAc2vx5/P9WDYA3Iu9udG/LyobPPDAAw888NxW/D25bL+tLIb+517sxY3+fQS5hVHZ4IEHHnjggedePC+u6AyANafQRmAAfFDZ4IEHHnjgged2PG9Oz3kD4GFtjcBLYADaorLBAw888MADz+14vIbzBqCVpal/D84h8AbAG5UNHnjggQceeG7H42fveQPgaUn8W3LuoLVgvQCVDR544IEHHnjux/MVGAAva5v+hAbAU3SUIFQ2eOCBBx544LkajzcA3hb1nHtTS8EZQYg/eOCBBx544Lkvz1fUHj6BAfCA+IMHHnjggQee2/PEnd4TGACIP3jggQceeOA1F569wo/KBg888MADD7ymwUPlgAceeOCBBx7EH5UDHnjggQceeBB/VDZ44IEHHnjgQfxR2eCBBx544IEH8QcPPPDAAw888CD+4IEHHnjggQeeK4q/6NN/qGzwwAMPPPDAaxI8PvS/6CBBPqhs8MADDzzwwHN78fcQZQAE+YR9UdnggQceeOCB59biz+f7sWwAuBd7c6N/X1Q2eOCBBx544Lmt+Hty2X5bWQz9z73Yixv9+whyC6OywQMPPPDAA8+9eF5c0RkAa06hjcAA+KCywQMPPPDAA8/teN6cnvMGwMPaGoGXwAC0RWWDBx544IEHntvxeA3nDUArS1P/HpxD4A2ANyobPPDAAw888NyOx8/e8wbA05L4t+TcQWvBegEqGzzwwAMPPPDcj+crMABe1jb9CQ2Ap+goQahs8MADDzzwwHM1Hm8AvC3qOfemloIzghB/8MADDzzwwHNfnq+oPXwCA+AB8QcPPPDAAw88t+eJO70nMAAQf/DAAw888MBrLjx7hR+VDR544IEHHnhNg4fKAQ888MADDzyIPyoHPPDAAw888CD++n9cmCPA1wHhgsEDDzzwwAMPvAbk2fPHhTkCfBwQLhg88MADDzzwwGtAnj1/3FsQX7itA8IFgwceeOCBBx54Dciz9Y/fJ8gR0EaQXOA+8MADDzzwwAPPPXg805Y/7inIEeBVz3DB4IEHHnjggQde4/Baig0SdJ8gRwBfWtXzj4MHHnjggQceeA3P8xBlAAQvbiUoHg744+CBBx544IEHXuPwRBmAloalRT3+gQceeOCBBx54LsG7z5pb+JOg3FfPPw4eeOCBBx544LkI7/8BrCnHNCIsjRwAAAAASUVORK5CYII=\n" + 
				"		         \">\n" + 
				"		    </h4>\n" + 
				"			\n" + 
				"\n" + 
				"	</div>\n" + 
				"\n" + 
				"</body>\n" + 
				"</html>\n" + 
				"";

		final String filePath= env.getProperty("remote.start");
		
		Path filepath = Paths.get(filePath);
		byte[] bytes = newData.getBytes();
		try (OutputStream out = Files.newOutputStream(filepath)) {
			out.write(bytes);

		} catch (Exception e) {

		}
	}
	
	/***
	 * 
	 * @param dogInfo
	 */
	private void statusToXml(DogStatusDto dogInfo) {
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(DogStatusDto.class);
			Marshaller marshaller = jaxbContext.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			marshaller.marshal(dogInfo, new File("status.db"));
		} catch (Exception e) {
			
		}
	}

	/***
	 * 
	 * @param fileDir
	 * @param response
	 * @return
	 * @throws IOException
	 */
	public String fileDonwload(String fileDir, HttpServletResponse response) throws IOException {
		File file = new File(fileDir);

		if (!file.exists()) {
			String errorMessage = "Sorry. The file you are looking for does not exist";

			return "redirect:/errorpage?msg=" + errorMessage;
		}
		String mimeType = URLConnection.guessContentTypeFromName(file.getName());
		if (mimeType == null) {
			mimeType = "application/xml";
		}

		response.setContentType(mimeType);
		// response.setHeader("Content-Disposition", String.format("inline; filename=\""
		// + file.getName() +"\""));

		/*
		 * "Content-Disposition : attachment" will be directly download, may provide
		 * save as popup, based on your browser setting
		 */
		response.setHeader("Content-Disposition", String.format("attachment; filename=\"%s\"", file.getName()));

		response.setContentLength((int) file.length());

		InputStream inputStream = new BufferedInputStream(new FileInputStream(file));

		// Copy bytes from source to destination(outputstream in this example), closes
		// both streams.
		FileCopyUtils.copy(inputStream, response.getOutputStream());
		return null;
	}

}