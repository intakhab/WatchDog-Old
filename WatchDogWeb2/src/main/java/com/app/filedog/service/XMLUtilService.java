package com.app.filedog.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.app.filedog.common.FileException;
/***
 * 
 * @author intakhabalam.s@hcl.com
 * @version 1.1
 *
 */
@Service
public class XMLUtilService {
	private final Logger logger = LogManager.getLogger("Dog-X");
	@Autowired
	Environment env;

	/***
	 * 
	 * @param object
	 * @return
	 */
	public String convertObjectToXML(Object object) {
		try {
			StringWriter stringWriter = new StringWriter();
			JAXBContext context = JAXBContext.newInstance(object.getClass());
			Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			marshaller.marshal(object, stringWriter);
			return stringWriter.toString();
		} catch (JAXBException e) {
			logger.error(String.format("Exception while marshalling: %s", e.getMessage()));
		}
		return null;
	}

	/***
	 * 
	 * @param clazz
	 * @param file
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> T convertXMLToObject(Class<?> clazz, File file) throws JAXBException, FileNotFoundException {
		try {
			JAXBContext context = JAXBContext.newInstance(clazz);
			Unmarshaller um = context.createUnmarshaller();
			T unmarshal = (T) um.unmarshal(file);
			logger.info("{ " + clazz.getClass().getName() + " } data unmarshal sucessfully...");
			return unmarshal;
		} catch (JAXBException je) {
			logger.error("Error interpreting XML response {} ", je.getMessage());

		}
		return null;
	}

	/***
	 * 
	 * @param dogInfo
	 * @param fileName
	 * @return
	 * @throws JAXBException
	 * @throws FileNotFoundException
	 */
	public Object convertObjectToXML(Object classz, String fileName) throws JAXBException, FileNotFoundException {
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(classz.getClass());
			Marshaller marshaller = jaxbContext.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			marshaller.marshal(classz, new File(fileName));
			logger.info("{ " + classz.toString() + " } data marshall sucessfully...");
			return classz;
		} catch (JAXBException e) {
			logger.error("Error saving object to XML {} ", e.getMessage());
		}
		return null;
	}

	
	/**
	 * This method will give the values from XML
	 * 
	 * @param xml
	 * @return
	 * @throws FileException
	 */
	public String getValueFromXML(File xml, String elementName) throws FileException {
		String elementVal = null;
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			/*
			 * document = builder.parse(new InputSource(new StringReader(xml.toString())));
			 */ Document document = builder.parse(xml);
			Element rootElement = document.getDocumentElement();
			elementVal = getValueFromXMLNode(elementName, rootElement);
			logger.info("Parsing in XML found element name: [ " + elementName + " ] ==> element value: [ " + elementVal
					+ " ]");
		} catch (SAXException e) {
			throw new FileException("Error {1} : Parsing Exception " + e.getMessage());
		} catch (IOException e) {
			throw new FileException("Error {2} : I/O Exception " + e.getMessage());
		} catch (ParserConfigurationException e) {
			throw new FileException("Error {3} : Parsing Config Exception " + e.getMessage());
		}
		return elementVal;
	}

	/***
	 * 
	 * @param tagName
	 * @param element
	 * @return
	 */
	private String getValueFromXMLNode(String tagName, Element element) throws FileException {

		NodeList list = element.getElementsByTagName(tagName);
		if (list != null && list.getLength() > 0) {
			NodeList subList = list.item(0).getChildNodes();

			if (subList != null && subList.getLength() > 0) {
				return subList.item(0).getNodeValue();
			}
		}

		return null;
	}


	/***
	 * 
	 * @param xml
	 * @param elementName
	 * @param value
	 * @return
	 * @throws FileException
	 */
	public boolean modifyValuesInXML(File xml, String elementName, String value) throws FileException {
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(xml);
			doc.getDocumentElement().normalize();
			NodeList tagList = doc.getElementsByTagName(elementName);
			Node name = modifyNodeListValue(tagList);
			name.setTextContent(value);
			doc.getDocumentElement().normalize();
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(xml);
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.transform(source, result);
			logger.info("Parsing XML for modification {}  found element name: [ " + elementName
					+ " ] ==> element value: [ " + value + " ]");

			return true;
		} catch (Exception e) {
			logger.error("Error in modification XML {} ", e);
			throw new FileException("Error in modification XML {} " + e.getMessage());

		}
	}

	/***
	 * 
	 * @param filepath
	 * @param elementName
	 * @param value
	 * @return
	 * @throws FileException
	 */
	public boolean modifyValuesInXMLTags(File filepath, String elementName, List<String> value) throws FileException {
		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(filepath);
			NodeList list = doc.getElementsByTagName(elementName);
			int len = list.getLength();
			logger.info("Tag [ " + elementName + " ] total of elements  : " + len);
			for (int i = 0; i < len; i++) {
				Node node = modifyNodeValue(list.item(i));
				node.setTextContent(value.get(i));
				logger.info("Parsing XML for modification {}  found element name: [ " + elementName
						+ " ] ==> element value: [ " + value.get(i) + " ]");

			}
			doc.getDocumentElement().normalize();
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(filepath);
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.transform(source, result);

			return true;
		} catch (Exception e) {
			logger.error("Error in modifyValuesInXMLTags XML {} ", e);
			throw new FileException("Error in modifyValuesInXMLTags XML {} " + e.getMessage());

		}
	}

	/***
	 * 
	 * @param node
	 * @return
	 * @throws FileException
	 */
	private Node modifyNodeValue(Node node) throws FileException {
		NodeList subList = node.getChildNodes();
		if (subList != null && subList.getLength() > 0) {
			return subList.item(0);
		} else {
			return node;
		}
	}

	/***
	 * 
	 * @param list
	 * @return
	 * @throws FileException
	 */
	private Node modifyNodeListValue(NodeList list) throws FileException {

		if (list != null && list.getLength() > 0) {
			NodeList subList = list.item(0).getChildNodes();
			if (subList != null && subList.getLength() > 0) {
				return subList.item(0);
			} else {
				return list.item(0);
			}
		}
		return null;
	}

	/***
	 * This method will print the XML content
	 * 
	 * @param file
	 */
	public String printXML(File file) {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setValidating(false);
		DocumentBuilder db;
		String content = "";
		try {
			db = dbf.newDocumentBuilder();
			Document doc = db.parse(new FileInputStream(file));
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			Writer out = new StringWriter();
			transformer.transform(new DOMSource(doc), new StreamResult(out));
			content = out.toString();
			logger.info("\n" + content);
			out.close();

		} catch (ParserConfigurationException | SAXException | IOException | TransformerException e) {
			logger.error("Error in printing XML  {} : " + e.getMessage());
		}
		return content;

	}
  
	/***
	 * 
	 * @param XMLStr
	 * @param filename
	 * @return
	 */
	
	public boolean writeXMLString(String XMLStr, String filename) {

		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.parse(new InputSource(new StringReader(XMLStr)));
			TransformerFactory tranFactory = TransformerFactory.newInstance();
			Transformer aTransformer = tranFactory.newTransformer();
			Source src = new DOMSource(document);
			Result dest = new StreamResult(new File(filename));
			aTransformer.transform(src, dest);
		} catch (Exception e) {
			logger.error("Error in Writing XML  {} : " + e.getMessage());

			return false;
		}
		return true;
	}
	

}
