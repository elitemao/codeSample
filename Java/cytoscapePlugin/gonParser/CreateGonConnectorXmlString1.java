package gonParser;

import java.io.ByteArrayOutputStream;
import java.io.StringWriter;

import org.apache.commons.lang.StringEscapeUtils;
import org.restlet.util.XmlWriter;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;


/// CreateGonConnectorXmlString-------------
//V1: change the usage of emptyElement in some places to the usage of dataElement.

public class CreateGonConnectorXmlString1 {
//	public CreateGonConnectorXmlString1() {
//
//	}

	public static String create(String fromEntityOrProcessLabel,
			String connectorLabel, String connectorName,
			String toEntityOrProcessLabel, String threshold) {
		ByteArrayOutputStream bos = null;
		// StringWriter bos=null;
		try {
			bos = new ByteArrayOutputStream();
			// bos=new StringWriter();
			XmlWriter w_process = new XmlWriter(bos);
			w_process.startDocument();
			w_process.setDataFormat(true);
			w_process.setIndentStep(2);

			w_process.setPrefix("http://www.csml.org/csml/version1", "csml");

			AttributesImpl attr1 = new AttributesImpl();
			attr1.addAttribute("", "from", "", "", fromEntityOrProcessLabel);
			attr1.addAttribute("", "label", "", "", connectorLabel);
			attr1.addAttribute("", "linestyle", "", "", "straight");
			attr1.addAttribute("", "name", "", "", connectorName);
			attr1.addAttribute("", "supportpath", "", "", "true");
			attr1.addAttribute("", "to", "", "", toEntityOrProcessLabel);
			attr1.addAttribute("", "type", "", "", "process");
			w_process.startElement("http://www.csml.org/csml/version1",
					"connector", "", attr1);

			AttributesImpl attr2 = new AttributesImpl();
			attr2.addAttribute("", "connectorFiringStyle", "", "", "threshold");
			attr2.addAttribute("", "type", "", "", "Double");
			attr2.addAttribute("", "value", "", "", threshold);
			System.out.println("threshold/CreateGonConnectorXmlString1 "+new Exception().getStackTrace()[0].getLineNumber()+" "
					+  threshold);
			w_process.dataElement("http://www.csml.org/csml/version1",
					"firing", "", attr2, "");

			w_process.dataElement("http://www.csml.org/csml/version1",
					"kinetic", "");

			w_process.startElement("http://www.csml.org/csml/version1",
					"graphics");

			w_process.startElement("http://www.csml.org/csml/version1",
					"figure");

			AttributesImpl attr3 = new AttributesImpl();
			attr3.addAttribute("", "points", "", "",
					"91.0 622.0 139.9340976779419 562.1916583936265");
			w_process.dataElement("http://www.csml.org/csml/version1",
					"processConnector", "", attr3, "");
			w_process.endElement("http://www.csml.org/csml/version1", "figure");

			w_process.startElement("http://www.csml.org/csml/version1",
					"targetArrow");
			AttributesImpl attr4 = new AttributesImpl();
			attr4.addAttribute("", "points", "", "", "0.0 0.0 0.0 6.0 8.0 3.0");
			attr4
					.addAttribute(
							"",
							"pointsInAbsoluteCoordinate",
							"",
							"",
							"137.61222578033195 560.2919450228547 142.2559695755519 564.0913717643983 145.00000000000003 556.0");
			w_process.dataElement("http://www.csml.org/csml/version1",
					"processConnectorArrow", "", attr4, "");

			w_process.endElement("http://www.csml.org/csml/version1",
					"targetArrow");

			w_process.endElement("http://www.csml.org/csml/version1",
					"graphics");

			w_process.endElement("http://www.csml.org/csml/version1",
					"connector");

			w_process.endDocument();

		} catch (SAXException saxex) {
			saxex.printStackTrace();
		}
		// String connectorXmlString=
		// "	    <csml:connector from=\""+fromEntityOrProcessLabel+"\" label=\""+connectorLabel+"\" linestyle=\"straight\" name=\""+connectorName+"\" supportpath=\"true\" to=\""+toEntityOrProcessLabel+"\" type=\"process\">"+"\n"+
		// "			<csml:firing connectorFiringStyle=\"threshold\" type=\"Double\" value=\""+threshold+"\"></csml:firing>"+"\n"+
		// "   		<csml:kinetic></csml:kinetic>"+"\n"+
		// "			<csml:graphics>"+"\n"+
		// "				<csml:figure>"+"\n"+
		// "					<csml:processConnector points=\"91.0 622.0 139.9340976779419 562.1916583936265\"></csml:processConnector>"+"\n"+
		// "				</csml:figure>"+"\n"+
		// "				<csml:targetArrow>"+"\n"+
		// "					<csml:processConnectorArrow points=\"0.0 0.0 0.0 6.0 8.0 3.0\" pointsInAbsoluteCoordinate=\"137.61222578033195 560.2919450228547 142.2559695755519 564.0913717643983 145.00000000000003 556.0\"></csml:processConnectorArrow>"+"\n"+
		// "				</csml:targetArrow>"+"\n"+
		// "			</csml:graphics>"+"\n"+
		// "		</csml:connector>"+"\n";
		// return connectorXmlString;

		// System.out.println(bos.toString().replaceFirst("<\\?xml version\\=\"1\\.0\" standalone=\\'yes\\'\\?>",
		// "")+"/CreateGonConnectorXmlString");
		// System.exit(0);
		String returned = bos.toString();
		// returned.replace("\\&lt;", "\\<");
		// returned.replace("\\&gt;","\\>");
		// returned.replace("\\&amp;", "\\&");
		// returned.replace("\\&quot;", "\"");

		returned = StringEscapeUtils.unescapeXml(returned);
		return returned.replaceFirst(
				"<\\?xml version\\=\"1\\.0\" standalone=\\'yes\\'\\?>", "");
		// return
		// bos.toString().replaceFirst("<\\?xml version\\=\"1\\.0\" standalone=\\'yes\\'\\?>",
		// "");
	}

}
