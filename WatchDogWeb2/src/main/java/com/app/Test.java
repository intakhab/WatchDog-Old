package com.app;

import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import com.app.filedog.domain.GLTransaction;
import com.app.filedog.domain.GLTransactions;
import com.app.filedog.domain.ProcessGLTransactionCommit;

public class Test {

	public static void main(String[] args) {
		String fileName="c:\\dataload\\TMAPIBatch\\Input1\\Out_processGLTransactionRetrieve@csi.xml";
		
            try {
             /*   DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder db = dbf.newDocumentBuilder();
                Document doc = db.parse(fileName);
                doc.getDocumentElement().normalize();
                NodeList nodeList=doc.getElementsByTagName("CarrierCode");
                for (int i=0; i<nodeList.getLength(); i++){
                    // Get element
                    Element element = (Element)nodeList.item(i);
                    System.out.println(element.getNodeName() +"->"+element.getTextContent().trim());
                    
                }*/
            	
            	/*CISDocument duplicateDto=new CISDocument();
            	JAXBContext jaxbContext = JAXBContext.newInstance(CISDocument.class);
        		Marshaller marshaller = jaxbContext.createMarshaller();
        		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        		marshaller.marshal(duplicateDto, new File(fileName));
        		marshaller.marshal(duplicateDto, System.out);*/
        		
        		    File file = new File(fileName);
        	        JAXBContext jaxbContext = JAXBContext.newInstance(GLTransactions.class);
        	        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        	        GLTransactions  g = (GLTransactions) unmarshaller.unmarshal(file);
        	        
        	        GLTransaction[] glArrays=g.getGLTransaction();
        	        ProcessGLTransactionCommit prCommit=new ProcessGLTransactionCommit();
        	        
        	        String [] val=new String[10];
        	        int i=0;
        			for(GLTransaction gl:glArrays) {
        				//SystemGLTransactionID
        				val[i]=gl.getSystemGLTransactionID();
        				i++;
        				if(i==10){
        					break;
        				}
        				
        			}
        			prCommit.setSystemTransactionID(val);
        			
        			
        			try {
        				StringWriter stringWriter = new StringWriter();
        				JAXBContext context = JAXBContext.newInstance(ProcessGLTransactionCommit.class);
        				Marshaller marshaller = context.createMarshaller();
        				marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        				marshaller.marshal(prCommit, stringWriter);
        			    System.out.println(stringWriter.toString());
        			    
        			    
        			    
        			    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        			    
        		        DocumentBuilder builder;
        		        try
        		        {
        		            builder = factory.newDocumentBuilder();
        		 
        		            // Use String reader
        		            Document document = builder.parse( new InputSource(
        		                    new StringReader( stringWriter.toString() ) ) );
        		 
        		            TransformerFactory tranFactory = TransformerFactory.newInstance();
        		            Transformer aTransformer = tranFactory.newTransformer();
        		            Source src = new DOMSource( document );
        		            Date date = new Date() ;
        		            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss") ;
        		           // File file = new File(dateFormat.format(date) + ".tsv") ;
        		            //BufferedWriter out = new BufferedWriter(new FileWriter(file));
        		            //out.write("Writing to file");
        		            //out.close();

        		            Result dest = new StreamResult( new File( "C:\\dataload\\TMAPIBatch\\Input1\\xmlFileName_"+dateFormat.format(date)+".xml" ) );
        		            aTransformer.transform( src, dest );
        		        } catch (Exception e)
        		        {
        		            // TODO Auto-generated catch block
        		            e.printStackTrace();
        		        }

        			} catch (JAXBException e) {
        				e.printStackTrace(System.err);
        			}
        	        
            	
            	/* File file = new File(fileName);
     	        JAXBContext jaxbContext = JAXBContext.newInstance(LoadsEntity.class);
     	        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
     	       LoadsEntity  product = (LoadsEntity) unmarshaller.unmarshal(file);
     	        System.out.println(product);*/
     	        
     	         
     	       /*LoadsEntity load=convertXMLToObject(LoadsEntity.class,file);*/
     	        
     	        
     	      // System.out.println(load);

            }catch (Exception e) {
            	
            	e.fillInStackTrace();
				// TODO: handle exception
			}
	}
	
	public static  <T> T convertXMLToObject(Class clazz, File file) {
        try {
            JAXBContext context = JAXBContext.newInstance(clazz);
            Unmarshaller um = context.createUnmarshaller();
            return (T) um.unmarshal(file);
        } catch (JAXBException je) {
            throw new RuntimeException("Error interpreting XML response", je);
        }
    }


}
