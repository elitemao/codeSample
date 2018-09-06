package gonParser;

//import gonLayout.MultiLevelLayout;
import growNetwork.EcWS;
import growNetwork.EnzymeWS;

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
// CreateEnzymeEntityXmlObject------
//v0: modified from CreatGonEntityXmlWriter. For saving the network object from GrowNetwork.
//v1: remove name space
//CreateEnzymeEntityXmlObjectWS--------
//v1:modified from CreateEnzymeEntityXmlObject1
//v1: use EnzymeWS
//v1: write the omim info out to HML
//CreateEcEntityXmlObjectWS-----------
//v0:To create the xml for a ec object
//v0:to make entityName the same as displayName

//CreateProteinXmlObjectWS---------
//v0:modified from CreateEcEntityXmlObjectWS. Change the tag name "entity" into "protein"
//v1 20110119: change the tag "annotation" into "annotation_p" so as to distinguish from the annotation tag under metabolite
//v1 20110620: disable the usage of MultiLevelLayout

//CreateEcEntityHmlObject--------------------------
//v0 20110624: renamed from CreateProteinXmlObject1, since this class is to generate the xml for "EC".
//v0 20110624: change the xml tag for enzyme to "enzyme"(before it was "protein")

public class CreateEcEntityHmlObject {

	public CreateEcEntityHmlObject() {

	}

	public String create(EcWS ec_, String entityLabel, String parameterLabel,
			double initialValue) throws UnsupportedEncodingException {
		ByteArrayOutputStream bos = null;

		try {

			bos = new ByteArrayOutputStream();
			XmlWriter w_ec = new XmlWriter(bos);
			w_ec.startDocument();
			w_ec.setDataFormat(true); // this command can make each new tag
			// start in a different row
			w_ec.setIndentStep(2);

			// w_ec.setPrefix("http://www.csml.org/csml/version1", "csml");

			String displayName = ec_.getDisplayName();
			String spid = ec_.getSpid();
			ArrayList spaccStore = ec_.getSpacc();
			String species = ec_.getSpecies();
			ArrayList hprdIdStore = ec_.getHprdId();
			ArrayList omimStore = ec_.getOmim();
			ArrayList tissueStore = ec_.getTissue();
			ArrayList subcellularLocationStore = ec_.getSubcellularLocation();
			String ec = ec_.getEc();
			ArrayList inhibitorStore = ec_.getInhibitor();
			ArrayList cofactorStore = ec_.getCofactor();
			ArrayList interactPartnerStore = ec_.getInteractingPartner();
			String cas = ec_.getCas();
			// String keggCompoundId=metabolite_.getKeggCompoundId();
			// String entityName=metaboliteName+"|"+keggCompoundId;
			// String entityName=spid;
			String entityName = displayName;
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
			w_ec.startElement("", "enzyme", "", attr1);

			AttributesImpl attr2 = new AttributesImpl();
			attr2.addAttribute("", "label", "", "", parameterLabel);
			attr2.addAttribute("", "type", "", "", "Double");
			attr2.addAttribute("", "initialValue", "", "",
					((Double) initialValue).toString());
			attr2.addAttribute("", "minimumValue", "", "", "0");
			attr2.addAttribute("", "maximumValue", "", "", "infinite");
			attr2.addAttribute("", "unit", "", "", "unit");
			w_ec.emptyElement("", "parameter", "", attr2);

			w_ec.startElement("graphics");
			w_ec.startElement("figure");

			AttributesImpl attr3 = new AttributesImpl();
			attr3.addAttribute("", "continuousEntity", "", "location",
					coordinateX + " " + coordinateY);
			w_ec.emptyElement("", "continuousEntity", "", attr3);

			w_ec.endElement("figure");
			w_ec.endElement("graphics");
			w_ec.startElement("annotation_p");

			w_ec.dataElement("moleType", "enzyme");

			w_ec.dataElement("casNumber", cas);

//			w_ec.dataElement("species", species);

			w_ec.startElement("spaccStore");
			for (Object i : spaccStore) {
				w_ec.dataElement("spacc", (String) i);

			}
			w_ec.endElement("spaccStore");
			w_ec.startElement("ecStore");

			w_ec.dataElement("ec", ec_.getEc());

			w_ec.endElement("ecStore");
			w_ec.startElement("subcellularLocationStore");
			for (Object i : subcellularLocationStore) {
				w_ec.dataElement("subcellularLocation", (String) i);

			}
			w_ec.endElement("subcellularLocationStore");
			w_ec.startElement("hprdIdStore");
			for (Object i : hprdIdStore) {
				w_ec.dataElement("hprdId", (String) i);

			}
			w_ec.endElement("hprdIdStore");

			w_ec.startElement("tissueStore");
			for (Object i : tissueStore) {
				w_ec.dataElement("tissue", (String) i);

			}
			w_ec.endElement("tissueStore");

			w_ec.startElement("interactorStore");
			for (Object i : interactPartnerStore) {
				w_ec.dataElement("partnerSpid", (String) i);

			}
			w_ec.endElement("interactorStore");

			w_ec.startElement("cofactorStore");
			for (Object i : cofactorStore) {
				w_ec.dataElement("cofactor", (String) i);

			}
			w_ec.endElement("cofactorStore");

			w_ec.startElement("inhibitorStore");
			for (Object i : inhibitorStore) {
				w_ec.dataElement("inhibitor", (String) i);

			}
			w_ec.endElement("inhibitorStore");

			w_ec.startElement("omimStore");
			for (Object i : omimStore) {
				w_ec.dataElement("omim", (String) i);
			}
			w_ec.endElement("omimStore");

			w_ec.endElement("annotation_p");
			w_ec.endElement("enzyme");
			w_ec.endDocument();

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
