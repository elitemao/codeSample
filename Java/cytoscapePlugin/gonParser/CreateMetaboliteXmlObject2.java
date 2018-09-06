package gonParser;

//import growNetwork.Metabolite;
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
// v1: add one more create() method for generating entity for interactor
//CreateMetaboliteEntityXmlObject------
//v0: modified from CreatGonEntityXmlWriter. For saving the network object from GrowNetwork.
//v1: remove name space
//v1: use MetaboliteWS

//CreateMetaboliteXmlObject----------
//v0: Modified from CreateMetaboliteEntityXmlObject1. The tag name for the metabolite is no longer "entity". Change the tag name for metabolite to "metabolite"
//v1 20110119: change the tag "annotation" into "annotation_m" and "cellularLocationStore" to "subcellularLocationStore", and "cellularLocation" to "subcellularLocation"
//v2 20110208: change the entity name of metabolite to commonName, not commonName|keggCompId
//v2 20110620: disable the usage of MultiLevelLayout

public class CreateMetaboliteXmlObject2 {

	public CreateMetaboliteXmlObject2() {

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

			// w_metabolite.setPrefix("http://www.csml.org/csml/version1",
			// "csml");

			String metaboliteName = (String) ((ArrayList) (metabolite_
					.getName())).get(0);
			String keggCompoundId = metabolite_.getKeggCompoundId();
			String entityName = metaboliteName;
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
			w_metabolite.startElement("", "metabolite", "", attr1);

			AttributesImpl attr2 = new AttributesImpl();
			attr2.addAttribute("", "label", "", "", parameterLabel);
			attr2.addAttribute("", "type", "", "", "Double");
			attr2.addAttribute("", "initialValue", "", "",
					((Double) initialValue).toString());
			attr2.addAttribute("", "minimumValue", "", "", "0");
			attr2.addAttribute("", "maximumValue", "", "", "infinite");
			attr2.addAttribute("", "unit", "", "", "unit");
			w_metabolite.emptyElement("", "parameter", "", attr2);

			w_metabolite.startElement("graphics");
			w_metabolite.startElement("figure");

			AttributesImpl attr3 = new AttributesImpl();
			attr3.addAttribute("", "continuousEntity", "", "location",
					coordinateX + " " + coordinateY);
			w_metabolite.emptyElement("", "continuousEntity", "", attr3);

			w_metabolite.endElement("figure");
			w_metabolite.endElement("graphics");

			w_metabolite.startElement("annotation_m");

			w_metabolite.dataElement("moleType", "metabolite");

			w_metabolite.startElement("commonNameStore");
			for (Object i : metabolite_.getName()) {
				w_metabolite.dataElement("commonName", (String) i);
			}
			w_metabolite.endElement("commonNameStore");

			w_metabolite.startElement("subcellularLocationStore");
			for (Object i : metabolite_.getCellularLocation()) {
				w_metabolite.dataElement("subcellularLocation", (String) i);
			}
			w_metabolite.endElement("subcellularLocationStore");

			w_metabolite.startElement("tissueStore");
			for (Object i : metabolite_.getTissue()) {
				w_metabolite.dataElement("tissue", (String) i);
			}
			w_metabolite.endElement("tissueStore");

			w_metabolite.dataElement("keggCompoundId", metabolite_
					.getKeggCompoundId());

			w_metabolite.dataElement("hmdbIdSingle", metabolite_.getHmdbId());
			w_metabolite.dataElement("chebiId", metabolite_.getChebiId());
			w_metabolite.dataElement("casNumber", metabolite_.getCasNumber());
			w_metabolite.dataElement("chemicalFormula", metabolite_
					.getChemicalFormula());

			w_metabolite.endElement("annotation_m");

			w_metabolite.endElement("metabolite");
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

	// this method is designed for ppi interactor entity
	// public String create(String entityName_,String entityLabel,String
	// parameterLabel,double initialValue) throws UnsupportedEncodingException{
	// ByteArrayOutputStream bos=null;
	//
	// try{
	//
	// bos=new ByteArrayOutputStream();
	// XmlWriter w_interactor=new XmlWriter(bos);
	// w_interactor.startDocument();
	// w_interactor.setDataFormat(true); // this command can make each new tag
	// start in a different row
	// w_interactor.setIndentStep(2);
	//			
	// w_interactor.setPrefix("http://www.csml.org/csml/version1", "csml");
	//			
	// String entityName=entityName_;
	// // double[] randomLocation=NodeLocator.create();
	// double[]
	// metaboliteLocation=MultiLevelLayout.createMetaboliteCoordinate();
	// String coordinateX=metaboliteLocation[0]+"";
	// String coordinateY=metaboliteLocation[1]+"";
	//			
	// AttributesImpl attr1=new AttributesImpl();
	// attr1.addAttribute("", "label", "", "", entityLabel);
	// attr1.addAttribute("","name" , "", "", entityName);
	// attr1.addAttribute("","type","","","continuous");
	// attr1.addAttribute("", "codelinkProbeId", "", "", "");
	// attr1.addAttribute("","extension" , "", "", "");
	// attr1.addAttribute("","accession" , "", "", "");
	// w_interactor.startElement("http://www.csml.org/csml/version1",
	// "entity","",attr1);
	//			
	// AttributesImpl attr2=new AttributesImpl();
	// attr2.addAttribute("", "label", "","", parameterLabel);
	// attr2.addAttribute("", "type", "", "", "Double");
	// attr2.addAttribute("", "initialValue", "", "",
	// ((Double)initialValue).toString());
	// attr2.addAttribute("", "minimumValue", "", "", "0");
	// attr2.addAttribute("", "maximumValue", "", "", "infinite");
	// attr2.addAttribute("", "unit", "", "", "unit");
	// w_interactor.emptyElement("http://www.csml.org/csml/version1",
	// "parameter", "", attr2);
	//			
	// w_interactor.startElement("http://www.csml.org/csml/version1","graphics");
	// w_interactor.startElement("http://www.csml.org/csml/version1","figure");
	//			
	// AttributesImpl attr3=new AttributesImpl();
	// attr3.addAttribute("", "continuousEntity", "", "location",
	// coordinateX+" "+coordinateY);
	// w_interactor.emptyElement("http://www.csml.org/csml/version1",
	// "continuousEntity", "", attr3);
	//			
	// w_interactor.endElement("http://www.csml.org/csml/version1","figure");
	// w_interactor.endElement("http://www.csml.org/csml/version1","graphics");
	// w_interactor.endElement("http://www.csml.org/csml/version1","entity");
	// w_interactor.endDocument();
	//
	//			
	// }catch(SAXException saxex){
	// saxex.printStackTrace();
	// }
	//
	//		
	// String returned=bos.toString();
	//
	//		
	// returned=StringEscapeUtils.unescapeXml(returned);
	// return
	// returned.replaceFirst("<\\?xml version\\=\"1\\.0\" standalone=\\'yes\\'\\?>",
	// "");
	//		
	//		
	//		
	// // since the use of starDocument() and endDocument() will generate
	// "<?xml version="1.0" standalone='yes'?>" every time when is is called,
	// // remove it and add it back to the front of the document later.
	// // return
	// bos.toString().replaceFirst("<\\?xml version\\=\"1\\.0\" standalone=\\'yes\\'\\?>",
	// "");
	// }

}
