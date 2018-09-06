package gonParser;

//import gonLayout.MultiLevelLayout;
import growNetwork.EcWS;

import java.io.ByteArrayOutputStream;

import java.util.*;

import org.apache.commons.lang.StringEscapeUtils;
import org.restlet.util.XmlWriter;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

// CreateGonProcessXmlString------
// use XmlWriter to generate xml string
//V1: create a polymorphism of create(), so that xml of the process of ppi can be created. 
//V2: use CreateGonConnectorXmlString1
// CreateProcessXmlObject--------
// modified from CreateGonProcessXmlString2.java
// remove name space
// use CreateConnectorXmlObject.java
// mark out the second create() method
//CreateProcessXmlObjectWS---------
//v0:use EnzymeWS
//CreateEcProcessXmlObjectWS-------
//v0:the input parameter is EcWS instead of EnzymeWS
//v0 20110620: disable the usage of MultiLevelLayout


public class CreateEcProcessXmlObjectWS {

	
	public CreateEcProcessXmlObjectWS() {

	}

	public static String create(EcWS ec_, String processLabel,
			String processPriority, String processDelay,
			ArrayList connectorStore) {

		String processName = (String) ((ec_.getDisplayName())) + "|"
				+ ec_.getSpid();
		// double[] randomLocation=NodeLocator.create();
//		double[] enzymeLocation = MultiLevelLayout.createEnzymeCoordinate();
//		String coordinateX = enzymeLocation[0] + "";
//		String coordinateY = enzymeLocation[1] + "";
		String coordinateX="0";
		String coordinateY="0";
		
		ByteArrayOutputStream bos = null;

		try {
			bos = new ByteArrayOutputStream();
			XmlWriter w_process = new XmlWriter(bos);
			w_process.startDocument();
			w_process.setDataFormat(true); // this command can make each new tag
			// start in a different row
			w_process.setIndentStep(2);

			// w_process.setPrefix("http://www.csml.org/csml/version1", "csml");

			AttributesImpl attr1 = new AttributesImpl();
			attr1.addAttribute("", "edgeScore", "", "", "1.0");
			attr1.addAttribute("", "label", "", "", processLabel);
			attr1.addAttribute("", "name", "", "", processName);
			attr1.addAttribute("", "relationType", "", "", "Type 0");
			attr1.addAttribute("", "type", "", "", "continuous");
			w_process.startElement("", "process", "", attr1);

			w_process.startElement("simulationCondition");

			AttributesImpl attr2 = new AttributesImpl();
			attr2.addAttribute("", "value", "", "", processPriority);
			w_process.dataElement("", "priority", "", attr2, "");

			AttributesImpl attr3 = new AttributesImpl();
			attr3.addAttribute("", "firingStyle", "", "", "and");
			attr3.addAttribute("", "type", "", "", "Boolean");
			attr3.addAttribute("", "value", "", "", "true");
			w_process.dataElement("", "firing", "", attr3, "");

			AttributesImpl attr4 = new AttributesImpl();
			attr4.addAttribute("", "delayStyle", "", "", "delay");
			attr4.addAttribute("", "type", "", "", "Long");
			attr4.addAttribute("", "value", "", "", processDelay);
			w_process.dataElement("", "delay", "", attr4, "");

			AttributesImpl attr5 = new AttributesImpl();
			attr5.addAttribute("", "calcStyle", "", "", "add");
			w_process.dataElement("", "calc", "", attr5, "");

			AttributesImpl attr6 = new AttributesImpl();
			attr6.addAttribute("", "kineticStyle", "", "", "custom");
			attr6.addAttribute("", "type", "", "", "Double");
			attr6.addAttribute("", "value", "", "", "1");
			w_process.startElement("", "kinetic", "", attr6);

			AttributesImpl attr7 = new AttributesImpl();
			attr7.addAttribute("", "name", "", "", "custom");
			attr7.addAttribute("", "value", "", "", "1");
			w_process.dataElement("", "parameter", "", attr7, "");

			w_process.endElement("kinetic");

			w_process.endElement("simulationCondition");

			w_process.startElement("function");

			String allConnectorXmlString = "";
			for (Object connector_ : connectorStore) {
				Connector connector = (Connector) connector_;
				System.out.println("source object label:"
						+ connector.getSourceObjectLabel());
				System.out.println("connector label:"
						+ connector.getConnectorLabel());
				System.out.println("connector name:"
						+ connector.getConnectorName());
				System.out.println("sink object label:"
						+ connector.getSinkObjectLabel());
				System.out.println("threshold:" + connector.getThreshold());
				allConnectorXmlString = allConnectorXmlString
						+ "\n"
						+ CreateConnectorXmlObject
								.create(connector.getSourceObjectLabel(),
										connector.getConnectorLabel(),
										connector.getConnectorName(), connector
												.getSinkObjectLabel(),
										connector.getThreshold());

			}
			// System.out.println("allConnectorXmlString:\n"+allConnectorXmlString+"/CreateGonProcessXmlString");
			// w_process.endDocument();
			// System.out.println("before xmlwriter.characters():"+bos.toString()+"/CreateGonProcessXmlString");
			// w_process=new XmlWriter(bos);
			w_process.characters(allConnectorXmlString);
			// System.out.println("after xmlwriter.characters():"+bos.toString()+"/CreateGonProcessXmlString");
			// System.exit(0);

			w_process.endElement("function");

			w_process.startElement("biological");

			w_process.dataElement("effectList", "");

			w_process.endElement("biological");

			w_process.startElement("graphics");

			w_process.startElement("figure");

			AttributesImpl attr8 = new AttributesImpl();
			attr8.addAttribute("", "fillColor", "", "", "0 0 0 255");
			attr8.addAttribute("", "location", "", "", coordinateX + " "
					+ coordinateY);
			attr8.addAttribute("", "outlineColor", "", "", "0 0 0 255");
			w_process.dataElement("", "discreteProcess", "", attr8, "");

			w_process.endElement("figure");

			w_process.endElement("graphics");

			w_process.endElement("process");

			w_process.endDocument();

		} catch (SAXException saxex) {
			saxex.printStackTrace();
		}

		String returned = bos.toString();

		returned = StringEscapeUtils.unescapeXml(returned);
		return returned.replaceFirst(
				"<\\?xml version\\=\"1\\.0\" standalone=\\'yes\\'\\?>", "");

	}

	// public static String create(String processName_,String
	// processLabel,String processPriority,String processDelay,ArrayList
	// connectorStore){
	// String processName=processName_;
	// // String
	// processName=(String)((enzyme_.getDisplayName()))+"|"+enzyme_.getSpid();
	// //double[] randomLocation=NodeLocator.create();
	// double[] enzymeLocation=MultiLevelLayout.createEnzymeCoordinate();
	// String coordinateX=enzymeLocation[0]+"";
	// String coordinateY=enzymeLocation[1]+"";
	//		
	// ByteArrayOutputStream bos=null;
	//
	// try{
	// bos=new ByteArrayOutputStream();
	// XmlWriter w_process=new XmlWriter(bos);
	// w_process.startDocument();
	// w_process.setDataFormat(true); // this command can make each new tag
	// start in a different row
	// w_process.setIndentStep(2);
	//			
	// w_process.setPrefix("http://www.csml.org/csml/version1", "csml");
	//			
	// AttributesImpl attr1=new AttributesImpl();
	// attr1.addAttribute("", "edgeScore", "", "", "1.0");
	// attr1.addAttribute("","label","","",processLabel);
	// attr1.addAttribute("", "name", "", "", processName);
	// attr1.addAttribute("","relationType","","","Type 0");
	// attr1.addAttribute("", "type", "", "", "continuous");
	// w_process.startElement("http://www.csml.org/csml/version1", "process",
	// "", attr1);
	//			
	// w_process.startElement("http://www.csml.org/csml/version1","simulationCondition");
	//			
	// AttributesImpl attr2=new AttributesImpl();
	// attr2.addAttribute("", "value", "", "", processPriority);
	// w_process.dataElement("http://www.csml.org/csml/version1", "priority",
	// "", attr2, "");
	//			
	// AttributesImpl attr3=new AttributesImpl();
	// attr3.addAttribute("", "firingStyle", "", "", "and");
	// attr3.addAttribute("", "type", "", "", "Boolean");
	// attr3.addAttribute("" , "value", "", "", "true");
	// w_process.dataElement("http://www.csml.org/csml/version1","firing","",attr3,"");
	//			
	// AttributesImpl attr4=new AttributesImpl();
	// attr4.addAttribute("", "delayStyle", "", "", "delay");
	// attr4.addAttribute("","type","","","Long");
	// attr4.addAttribute("","value","","",processDelay);
	// w_process.dataElement("http://www.csml.org/csml/version1",
	// "delay","",attr4,"");
	//			
	// AttributesImpl attr5=new AttributesImpl();
	// attr5.addAttribute("", "calcStyle", "", "", "add");
	// w_process.dataElement("http://www.csml.org/csml/version1","calc","",attr5,"");
	//			
	// AttributesImpl attr6=new AttributesImpl();
	// attr6.addAttribute("", "kineticStyle", "", "", "custom");
	// attr6.addAttribute("", "type", "", "", "Double");
	// attr6.addAttribute("", "value", "", "", "1");
	// w_process.startElement("http://www.csml.org/csml/version1", "kinetic",
	// "", attr6);
	//			
	// AttributesImpl attr7=new AttributesImpl();
	// attr7.addAttribute("", "name", "", "", "custom");
	// attr7.addAttribute("","value", "", "", "1");
	// w_process.dataElement("http://www.csml.org/csml/version1", "parameter",
	// "", attr7, "");
	//			
	// w_process.endElement("http://www.csml.org/csml/version1","kinetic");
	//			
	// w_process.endElement("http://www.csml.org/csml/version1","simulationCondition");
	//			
	// w_process.startElement("http://www.csml.org/csml/version1","function");
	//			
	// String allConnectorXmlString="";
	// for(Object connector_:connectorStore){
	// Connector connector=(Connector)connector_;
	// allConnectorXmlString=allConnectorXmlString+"\n"+CreateGonConnectorXmlString1.create(connector.getSourceObjectLabel(),
	// connector.getConnectorLabel(), connector.getConnectorName(),
	// connector.getSinkObjectLabel(),connector.getThreshold());
	//
	// }
	// //
	// System.out.println("allConnectorXmlString:\n"+allConnectorXmlString+"/CreateGonProcessXmlString");
	// // w_process.endDocument();
	// //
	// System.out.println("before xmlwriter.characters():"+bos.toString()+"/CreateGonProcessXmlString");
	// // w_process=new XmlWriter(bos);
	// w_process.characters(allConnectorXmlString);
	// //
	// System.out.println("after xmlwriter.characters():"+bos.toString()+"/CreateGonProcessXmlString");
	// // System.exit(0);
	//			
	// w_process.endElement("http://www.csml.org/csml/version1","function");
	//			
	// w_process.startElement("http://www.csml.org/csml/version1",
	// "biological");
	//			
	// w_process.dataElement("http://www.csml.org/csml/version1",
	// "effectList","");
	//			
	// w_process.endElement("http://www.csml.org/csml/version1", "biological");
	//			
	// w_process.startElement("http://www.csml.org/csml/version1", "graphics");
	//			
	// w_process.startElement("http://www.csml.org/csml/version1","figure");
	//			
	// AttributesImpl attr8=new AttributesImpl();
	// attr8.addAttribute("", "fillColor", "", "", "0 0 0 255");
	// attr8.addAttribute("","location","","",coordinateX+" "+coordinateY);
	// attr8.addAttribute("", "outlineColor", "", "", "0 0 0 255");
	// w_process.dataElement("http://www.csml.org/csml/version1",
	// "discreteProcess", "", attr8, "");
	//			
	// w_process.endElement("http://www.csml.org/csml/version1","figure");
	//			
	// w_process.endElement("http://www.csml.org/csml/version1","graphics");
	//			
	// w_process.endElement("http://www.csml.org/csml/version1","process");
	//			
	// w_process.endDocument();
	//			
	// }catch(SAXException saxex){
	// saxex.printStackTrace();
	// }
	//
	// String returned=bos.toString();
	//
	// returned=StringEscapeUtils.unescapeXml(returned);
	// return
	// returned.replaceFirst("<\\?xml version\\=\"1\\.0\" standalone=\\'yes\\'\\?>",
	// "");
	//
	// }

}
