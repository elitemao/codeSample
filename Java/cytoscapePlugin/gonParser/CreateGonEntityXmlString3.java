package gonParser;

import growNetwork.MetaboliteWS;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.*;

import org.apache.commons.lang.StringEscapeUtils;
import org.restlet.util.XmlWriter;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

// CreateGonEntityXmlWriter-----------
// use XmlWriter to generate xml
// v1: add one more create() method for generating entity for interacting proteins
//v1: use MetaboliteWS
//v2: add "comment" tag to entity. The content in the comment tag will be ignored by CI.
//v2: the moleType tag in comment has value metabolite for metabolite entity, protein for interacting protein entity
//v2: add more sub-tags into comment tag
//v3 20110617: in both create methods, wrap the data content in the comment tag with CDATA
//v3 20110620: disable the usage of MultiLevelLayout

public class CreateGonEntityXmlString3 {

	public CreateGonEntityXmlString3() {

	}

	public String create(MetaboliteWS metabolite_, String entityLabel,
			String parameterLabel, double initialValue)
			throws UnsupportedEncodingException {
		ByteArrayOutputStream bos = null;

		try {

			bos = new ByteArrayOutputStream();
			XmlWriter w_metabolite = new XmlWriter(bos);
			w_metabolite.startDocument();
			w_metabolite.setDataFormat(true); // this command can make each new
			// tag start in a different row
			w_metabolite.setIndentStep(2);

			w_metabolite.setPrefix("http://www.csml.org/csml/version1", "csml");

			String metaboliteName = (String) ((ArrayList) (metabolite_
					.getName())).get(0);
			String keggCompoundId = metabolite_.getKeggCompoundId();
			String entityName = metaboliteName + "|" + keggCompoundId;
			// double[] randomLocation=NodeLocator.create();
//			double[] metaboliteLocation = MultiLevelLayout.createMetaboliteCoordinate();
//			String coordinateX = metaboliteLocation[0] + "";
//			String coordinateY = metaboliteLocation[1] + "";
			String coordinateX="0";
			String coordinateY="0";
			AttributesImpl attr1 = new AttributesImpl();
			attr1.addAttribute("", "label", "", "", entityLabel);
			attr1.addAttribute("", "name", "", "", entityName);
			attr1.addAttribute("", "type", "", "", "continuous");
			attr1.addAttribute("", "codelinkProbeId", "", "", "");
			attr1.addAttribute("", "extension", "", "", "");
			attr1.addAttribute("", "accession", "", "", "");
			w_metabolite.startElement("http://www.csml.org/csml/version1",
					"entity", "", attr1);

			AttributesImpl attr2 = new AttributesImpl();
			attr2.addAttribute("", "label", "", "", parameterLabel);
			attr2.addAttribute("", "type", "", "", "Double");
			attr2.addAttribute("", "initialValue", "", "",
					((Double) initialValue).toString());
			attr2.addAttribute("", "minimumValue", "", "", "0");
			attr2.addAttribute("", "maximumValue", "", "", "infinite");
			attr2.addAttribute("", "unit", "", "", "unit");
			w_metabolite.emptyElement("http://www.csml.org/csml/version1",
					"parameter", "", attr2);

			w_metabolite.startElement("http://www.csml.org/csml/version1",
					"graphics");
			w_metabolite.startElement("http://www.csml.org/csml/version1",
					"figure");

			AttributesImpl attr3 = new AttributesImpl();
			attr3.addAttribute("", "continuousEntity", "", "location",
					coordinateX + " " + coordinateY);
			w_metabolite.emptyElement("http://www.csml.org/csml/version1",
					"continuousEntity", "", attr3);

			w_metabolite.endElement("http://www.csml.org/csml/version1",
					"figure");
			w_metabolite.endElement("http://www.csml.org/csml/version1",
					"graphics");

			
			// wrap the content in the comment tag with CDATA tag
			ByteArrayOutputStream bosCommentContent = new ByteArrayOutputStream();
			XmlWriter w_commentContent = new XmlWriter(bosCommentContent);
			w_commentContent.startDocument();
			w_commentContent.setDataFormat(true); // this command can make each new tag start in a different row
			w_commentContent.setIndentStep(2);
//			w_commentContent.setPrefix("http://www.csml.org/csml/version1", "csml");
			
			w_commentContent.dataElement("", "moleType","metabolite");
			w_commentContent.dataElement("","keggCompId",keggCompoundId);
			
			w_commentContent.dataElement("", "name", metaboliteName);
			w_commentContent.endDocument();
			
			String commentContentString=bosCommentContent.toString();
			String replacedString=commentContentString.replaceFirst("<\\?xml version\\=\"1\\.0\" standalone=\\'yes\\'\\?>", "");
			System.out.println("comment string:"+replacedString+"***********************");
			String cdataWrappedCommentContentString="<![CDATA["+replacedString.trim()+"]]>";
			
			// add comment tag and the CDATA wrapped content
			w_metabolite.dataElement("http://www.csml.org/csml/version1", "comment", cdataWrappedCommentContentString);
			

			w_metabolite.endElement("http://www.csml.org/csml/version1",
					"entity");
			w_metabolite.endDocument();

		} catch (SAXException saxex) {
			saxex.printStackTrace();
		}

		String returned = bos.toString();

		returned = StringEscapeUtils.unescapeXml(returned);
		return returned.replaceFirst(
				"<\\?xml version\\=\"1\\.0\" standalone=\\'yes\\'\\?>", "");

		// since the use of starDocument() and endDocument() will generate
		// "<?xml version="1.0" standalone='yes'?>" every time when is is
		// called,
		// remove it and add it back to the front of the document later.
		// return
		// bos.toString().replaceFirst("<\\?xml version\\=\"1\\.0\" standalone=\\'yes\\'\\?>",
		// "");
	}

	// this method is designed for metabolic enzyme entity and ppi interacting protein entity
	public String create(String entityName_, String entityLabel,
			String parameterLabel, double initialValue)
			throws UnsupportedEncodingException {
		ByteArrayOutputStream bos = null;

		try {

			bos = new ByteArrayOutputStream();
			XmlWriter w_interactor = new XmlWriter(bos);
			w_interactor.startDocument();
			w_interactor.setDataFormat(true); // this command can make each new
			// tag start in a different row
			w_interactor.setIndentStep(2);

			w_interactor.setPrefix("http://www.csml.org/csml/version1", "csml");

			String entityName = entityName_;
			// double[] randomLocation=NodeLocator.create();
//			double[] metaboliteLocation = MultiLevelLayout
//					.createMetaboliteCoordinate();
//			String coordinateX = metaboliteLocation[0] + "";
//			String coordinateY = metaboliteLocation[1] + "";
			String coordinateX="0";
			String coordinateY="0";
			AttributesImpl attr1 = new AttributesImpl();
			attr1.addAttribute("", "label", "", "", entityLabel);
			attr1.addAttribute("", "name", "", "", entityName);
			attr1.addAttribute("", "type", "", "", "continuous");
			attr1.addAttribute("", "codelinkProbeId", "", "", "");
			attr1.addAttribute("", "extension", "", "", "");
			attr1.addAttribute("", "accession", "", "", "");
			w_interactor.startElement("http://www.csml.org/csml/version1",
					"entity", "", attr1);

			AttributesImpl attr2 = new AttributesImpl();
			attr2.addAttribute("", "label", "", "", parameterLabel);
			attr2.addAttribute("", "type", "", "", "Double");
			attr2.addAttribute("", "initialValue", "", "",
					((Double) initialValue).toString());
			attr2.addAttribute("", "minimumValue", "", "", "0");
			attr2.addAttribute("", "maximumValue", "", "", "infinite");
			attr2.addAttribute("", "unit", "", "", "unit");
			w_interactor.emptyElement("http://www.csml.org/csml/version1",
					"parameter", "", attr2);

			w_interactor.startElement("http://www.csml.org/csml/version1",
					"graphics");
			w_interactor.startElement("http://www.csml.org/csml/version1",
					"figure");

			AttributesImpl attr3 = new AttributesImpl();
			attr3.addAttribute("", "continuousEntity", "", "location",
					coordinateX + " " + coordinateY);
			w_interactor.emptyElement("http://www.csml.org/csml/version1",
					"continuousEntity", "", attr3);

			w_interactor.endElement("http://www.csml.org/csml/version1",
					"figure");
			w_interactor.endElement("http://www.csml.org/csml/version1",
					"graphics");
			
			// wrap the content in the comment tag with CDATA tag
			ByteArrayOutputStream bosCommentContent = new ByteArrayOutputStream();
			XmlWriter w_commentContent = new XmlWriter(bosCommentContent);
			w_commentContent.startDocument();
			w_commentContent.setDataFormat(true); // this command can make each new tag start in a different row
			w_commentContent.setIndentStep(2);
//			w_commentContent.setPrefix("http://www.csml.org/csml/version1", "csml");
//			w_commentContent.forceNSDecl("http://www.csml.org/csml/version1");
			w_commentContent.startElement("",
			"moleType");
			w_commentContent.characters("protein");  //be careful! Here says "protein" instead of "enzyme"
			w_commentContent.endElement("",
			"moleType");
			
			String commentContentString=bosCommentContent.toString();
			String replacedString=commentContentString.replaceFirst("<\\?xml version\\=\"1\\.0\" standalone=\\'yes\\'\\?>", "");
			System.out.println("comment string:"+replacedString+"***********************");
			String cdataWrappedCommentContentString="<![CDATA["+replacedString.trim()+"]]>";
			
			// add comment tag and the CDATA wrapped content
			w_interactor.dataElement("http://www.csml.org/csml/version1", "comment", cdataWrappedCommentContentString);
		

			w_interactor.endElement("http://www.csml.org/csml/version1",
					"entity");
			w_interactor.endDocument();

		} catch (SAXException saxex) {
			saxex.printStackTrace();
		}

		String returned = bos.toString();

		returned = StringEscapeUtils.unescapeXml(returned);
		return returned.replaceFirst(
				"<\\?xml version\\=\"1\\.0\" standalone=\\'yes\\'\\?>", "");

		// since the use of starDocument() and endDocument() will generate
		// "<?xml version="1.0" standalone='yes'?>" every time when is is
		// called,
		// remove it and add it back to the front of the document later.
		// return
		// bos.toString().replaceFirst("<\\?xml version\\=\"1\\.0\" standalone=\\'yes\\'\\?>",
		// "");
	}

}
