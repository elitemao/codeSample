package gonParser;

//import gonLayout.TwoLevelLayout;
import growNetwork.EcWS;

import growNetwork.MetaboliteWS;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.*;
import org.restlet.util.XmlWriter;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;
import org.apache.commons.lang.*;
import gonParser.Connector;


// GrowNetworkResultToCSML---
//v7: add one new method- transform1() which use XmlWriter to genertate xml.
//v8: use CreateGonEntityXmlString.java, CreateGonProcessXmlString.java
//v8: change the signature of the constructor
//v9: put a BufferedWriter object as the argument for "new XmlWriter()".
//v9: use StringEscapeUtils to change escaped tags to unescaped tags
//v9: use StringWriter as the argument to XmlWriter, and then get the string from StringWriter, then write out to file.
//v10: removed the comment out line
//v11: STRONG RENOVATION!! Put ppi info into petri net
//v12: make several changes from v11.
//v12: use MetaboliteEntityLabelGenerator to replace EntityLabelGenerator
//v12: add variables- enzymeAsEntityStore, metaboliteProcessLabelStore, keggCompoundIdToMetaboliteEntityLabelStore, keggCompIdToMetaboliteEntityLabelStore
//v13: remove some unused variables from version 12.
//v14: use CreateGonEntityXmlString1. Create a connector between enzyme entity and metabolic process.
//v15: change String xmlStringReactant1=(new CreateGonEntityXmlString1()).create(substrate,metaboliteEntityLabel,parameterLabelGenerator.generate(),substrate.getInitialValue()); to String xmlStringReactant1=(new CreateGonEntityXmlString1()).create(substrate,metaboliteEntityLabel,metaboliteEntityLabel,substrate.getInitialValue());
//v16: change "String xmlStringEnzymeEntity=(new CreateGonEntityXmlString1()).create(e.getSpid(),partnerEnzymeEntityLabel,parameterLabelGenerator.generate(),0);" to "String xmlStringEnzymeEntity=(new CreateGonEntityXmlString1()).create(partnerSpid,partnerEnzymeEntityLabel,partnerEnzymeEntityLabel,1000);" around line 222
//v17: use CreateGonProcessXmlString2
// GrowNetworkResultToHml--------
// v0:modified from GrowNetworkResultToCSML17
// v0:remove name space
// v0:use CreateEnzymeEntityXmlObject1, CreateMetaboliteEntityXmlObject1, CreateProcessXmlObject
// v1: change "else{}" to "else if() {}" at line 136,150,196,211
//v1:remove the usage of usedSpidStore, usedKeggCompoundIdStore at line 132,136,146,150,192,196,207,211
//v1: mark out variable usedSpidStore, usedKeggCompoundIdStore
//v1: use EnzymeWS,MetaboliteWS,CreateEnzymeEntityXmlObjectWS1,CreateProcessXmlObjectWS
//GrowNetworkResultEcToHml---------------
//v0:use EcWS instead of EnzymeWS. THis code is generated due to the usage of EC number to replace the Spid in the GrowNetResult
//v1:use displayName as the key in ecToEnzymeEntityLabelStore
//v1:use ec1 to replace ec.
//v2:check the number of element in substrateEnzymePairStore.If it contains 0 element, go to the next loop.Otherwise it will cause problem when ec1,substrate are called in the following code 
//v3: use CreateMetaboliteXmlObject, CreateProteinXmlObject to comply with Hml2v
//v4 20110119: use CreateMetaboliteXmlObject1, CreateProteinXmlObject1 to comply with Hml2v1
//v5 20110208: use CreateMetaboliteXmlObject2
//v6 20110624: use CreateEcEntityHmlObject
//v6 20110626: disable the initiation of InteractionProcessLabelGenerator,InteractionConnectorLabelGenerator
/**
 * @author hang-mao
 */
public class GrowNetworkResultEcToHml6 {

	ArrayList networkToTransform = null;
	// networkToTransform is an array of array from GrowNetwork.
	String totalEntityXmlString = "";
	String totalMetaboliteXmlString = "";
	String totalEcXmlString = "";
	String pathToSave;
	/**
	 * @uml.property name="metabolicConnectorLabelGenerator"
	 * @uml.associationEnd
	 */
	MetabolicConnectorLabelGenerator metabolicConnectorLabelGenerator = new MetabolicConnectorLabelGenerator();
	/**
	 * @uml.property name="metaboliteEntityLabelGenerator"
	 * @uml.associationEnd
	 */
	MetaboliteEntityLabelGenerator metaboliteEntityLabelGenerator = new MetaboliteEntityLabelGenerator();
	// EntityLabelGenerator entityLabelGenerator=new EntityLabelGenerator();
	/**
	 * @uml.property name="metabolicProcessLabelGenerator"
	 * @uml.associationEnd
	 */
	MetabolicProcessLabelGenerator metabolicProcessLabelGenerator = new MetabolicProcessLabelGenerator();
	/**
	 * @uml.property name="interactionProcessLabelGenerator"
	 * @uml.associationEnd
	 */
//	InteractionProcessLabelGenerator interactionProcessLabelGenerator = new InteractionProcessLabelGenerator();
	/**
	 * @uml.property name="interactionConnectorLabelGenerator"
	 * @uml.associationEnd
	 */
//	InteractionConnectorLabelGenerator interactionConnectorLabelGenerator = new InteractionConnectorLabelGenerator();
	// create virtual entity node to represent ppi.
	/**
	 * @uml.property name="enzymeEntityLabelGenerator"
	 * @uml.associationEnd
	 */
	EnzymeEntityLabelGenerator enzymeEntityLabelGenerator = new EnzymeEntityLabelGenerator();
	/**
	 * @uml.property name="parameterLabelGenerator"
	 * @uml.associationEnd
	 */
	ParameterLabelGenerator parameterLabelGenerator = new ParameterLabelGenerator();

	public GrowNetworkResultEcToHml6(ArrayList growNetworkResult_,
			String pathToSave_) {
		networkToTransform = growNetworkResult_;
		pathToSave = pathToSave_;
		System.out.println("in the constructor/"+this.getClass().getName());
	}

	public void transform() { // this method use XmlWriter class
		String metabolicProcessXmlCombinedString = "";
		// String interactionProcessXmlCombinedString="";
		System.out.println("in the transform/"+this.getClass().getName()+" line "+new Exception().getStackTrace()[0].getLineNumber());
		// ArrayList usedKeggCompoundIdStore=new ArrayList();
		// ArrayList usedSpidStore=new ArrayList();
		// ArrayList enzymeAsEntityStore=new ArrayList();
		ArrayList metabolicProcessLabelStore = new ArrayList();
		// HashMap keggCompoundIdToMetaboliteObj=new HashMap();
		HashMap keggCompoundIdToMetaboliteEntityLabelStore = new HashMap();
		HashMap ecToEnzymeEntityLabelStore = new HashMap();
		// HashMap processLabelToEnzymeObj=new HashMap();
		// HashMap processLabelToSpid=new HashMap();

		// HashMap keggCompIdToMetaboliteEntityLabelStore=new HashMap();
//		TwoLevelLayout layoutGenerator = new TwoLevelLayout();

		try {
			StringWriter sw = new StringWriter(); // the use of StringWriter
			// will eliminate the
			// problem of escaped
			// character

			XmlWriter w_global = new XmlWriter(sw);
			// w_global.setPrefix("http://www.csml.org/csml/version1", "csml");
			w_global.setDataFormat(true);
			w_global.setIndentStep(2);
			w_global.startDocument();
			// AttributesImpl attr1=new AttributesImpl();
			// attr1.addAttribute("", "majorVersion", "", "", "1");
			// attr1.addAttribute("", "minorVersion", "", "", "9");
			// w_global.startElement("http://www.csml.org/csml/version1","model","",attr1);
			w_global.startElement("model");
			// w_global.emptyElement("http://www.csml.org/csml/version1","unitdefs");
			// w_global.startElement("http://www.csml.org/csml/version1","net");
			w_global.emptyElement("unitdefs");
			w_global.startElement("net");

			for (Object growFrOneMetabolite : networkToTransform) {
				for (Object oneRelatedEnzyForOneMetabolite : (ArrayList) growFrOneMetabolite) {
					ArrayList connectorStore = new ArrayList();
					System.out
							.println("no of element in oneRealtedEnzyForOneMetabolite:"
									+ ((ArrayList) oneRelatedEnzyForOneMetabolite)
											.size() + "(should be 2)");
					Object substrateEnzymePairStore = ((ArrayList) oneRelatedEnzyForOneMetabolite)
							.get(0);
					Object enzymeSubstratePairStore = ((ArrayList) oneRelatedEnzyForOneMetabolite)
							.get(1);
					String processLabelForEnzyme1AndEnzyme2 = metabolicProcessLabelGenerator
							.generate();// generate a process label as the
					// process identifier in CI

					metabolicProcessLabelStore
							.add(processLabelForEnzyme1AndEnzyme2);
					EcWS ec1 = null;
					System.out
							.println("no of element in substratEnzymePaireStore/"+this.getClass().getName()+" line "+new Exception().getStackTrace()[0].getLineNumber()+" "
									+ ((ArrayList) substrateEnzymePairStore)
											.size());
					if (((ArrayList) substrateEnzymePairStore).size() == 0) {
						continue; // the reason why substrateEnzymePairStore is
						// empty is not clear now.
					}
					for (Object oneSubstrateEnzymePair : (ArrayList) substrateEnzymePairStore) {

						// String xmlStringReactant1="";

						// substrate object here has nothing in the entity label
						// field
						MetaboliteWS substrate = (MetaboliteWS) ((ArrayList) oneSubstrateEnzymePair)
								.get(0);
						String keggCompoundIdSubstrate = substrate
								.getKeggCompoundId();
						// enzyme1 should be the same as enzyme2
						ec1 = (EcWS) ((ArrayList) oneSubstrateEnzymePair)
								.get(1);
						System.out.println("ec1.getDisplayName():"
								+ ec1.getDisplayName());
						System.out.println("ec1.getProcessPriority():"
								+ ec1.getProcessPriority());
						System.out.println("ec1.getProcessDelay():"
								+ ec1.getProcessDelay());
						String metaboliteEntityLabel = "";
						String ecEntityLabel = "";
						// ec=ec1;
						// Check if the metabolite entity has been processed or
						// not.
						// If it has been processed, get the entity label from
						// the processed metabolite object.
						// This is to avoid multiple replicate of same molecule
						// in the Petri Nets.
						if (keggCompoundIdToMetaboliteEntityLabelStore.keySet()
								.contains(keggCompoundIdSubstrate)) {
							// substrate=(Metabolite)(keggCompoundIdToMetaboliteObj.get(keggCompoundIdSubstrate));
							metaboliteEntityLabel = (String) keggCompoundIdToMetaboliteEntityLabelStore
									.get(keggCompoundIdSubstrate);
							// the substrate obtained from
							// keggCompoundIdToMetaboliteObj has been provided a
							// entity label
						} else if (!keggCompoundIdToMetaboliteEntityLabelStore
								.keySet().contains(keggCompoundIdSubstrate)) { // if
							// the
							// kegg
							// compound
							// has
							// not
							// been
							// processed
							// before,
							// create
							// the
							// metabolic
							// entity
							// xml
							// string
							// substrate.setEntityLabel(metaboliteEntityLabelGenerator.generate());
							metaboliteEntityLabel = metaboliteEntityLabelGenerator
									.generate();
							// usedKeggCompoundIdStore.add(keggCompoundIdSubstrate);
							// keggCompoundIdToMetaboliteObj.put(keggCompoundIdSubstrate,substrate);
							keggCompoundIdToMetaboliteEntityLabelStore.put(
									keggCompoundIdSubstrate,
									metaboliteEntityLabel);
							String metaboliteEntityParameterLabel = metaboliteEntityLabel;
							String xmlStringReactant1 = (new CreateMetaboliteXmlObject2())
									.create(substrate, metaboliteEntityLabel,
											metaboliteEntityParameterLabel,
											substrate.getInitialValue());// the
							// label
							// attribute
							// in
							// the
							// parameter
							// tag
							// is
							// used
							// by
							// CI
							totalMetaboliteXmlString = totalMetaboliteXmlString
									+ "\n" + xmlStringReactant1;
							// System.out.println(xmlStringReactant1);
							// System.out.println(totalMetaboliteXmlString);
						}
						if (ecToEnzymeEntityLabelStore.keySet().contains(
								ec1.getDisplayName())) {
							// substrate=(Metabolite)(keggCompoundIdToMetaboliteObj.get(keggCompoundIdSubstrate));
							ecEntityLabel = (String) ecToEnzymeEntityLabelStore
									.get(ec1.getDisplayName());
							// the substrate obtained from
							// keggCompoundIdToMetaboliteObj has been provided a
							// entity label
						} else if (!ecToEnzymeEntityLabelStore.keySet()
								.contains(ec1.getDisplayName())) { // if the
							// kegg
							// compound
							// has not
							// been
							// processed
							// before,
							// create
							// the
							// metabolic
							// entity
							// xml
							// string
							// substrate.setEntityLabel(metaboliteEntityLabelGenerator.generate());
							ecEntityLabel = enzymeEntityLabelGenerator
									.generate();
							// usedSpidStore.add(enzyme1.getSpid());
							// keggCompoundIdToMetaboliteObj.put(keggCompoundIdSubstrate,substrate);
							ecToEnzymeEntityLabelStore.put(
									ec1.getDisplayName(), ecEntityLabel);
							String enzymeEntityParameterLabel = ecEntityLabel;
							String xmlStringEc1 = (new CreateEcEntityHmlObject())
									.create(ec1, ecEntityLabel,
											enzymeEntityParameterLabel, 0);// the
							// label
							// attribute
							// in
							// the
							// parameter
							// tag
							// is
							// used
							// by
							// CI
							totalEcXmlString = totalEcXmlString + "\n"
									+ xmlStringEc1;
						}
						// each edge is a connector object
						String connectorLabel1 = metabolicConnectorLabelGenerator
								.generate();
						System.out.println(substrate.getKeggCompoundId()
								+ " to " + ec1.getEc());
						System.out
								.println(keggCompoundIdToMetaboliteEntityLabelStore
										.get(substrate.getKeggCompoundId())
										+ " to "
										+ ecToEnzymeEntityLabelStore.get(ec1
												.getEc()));
						// System.out.println("connector from:"+metaboliteEntityLabel);
						// System.out.println("connector label:"+connectorLabel1);
						// System.out.println("connector to:"+processLabelForEnzyme1AndEnzyme2);
						// System.out.println("connector threshold:"+substrate.getStoichiometry());
						Connector con1 = new Connector(metaboliteEntityLabel,
								connectorLabel1, connectorLabel1,
								ecEntityLabel, substrate.getStoichiometry());
						connectorStore.add(con1);

					}
					System.out
							.println("ec1.getDisplayName():"
									+ ec1.getDisplayName()+"/"+this.getClass().getName()+" line "+new Exception().getStackTrace()[0].getLineNumber());

					for (Object oneEnzymeSubstratePair : (ArrayList) enzymeSubstratePairStore) {
						// String xmlStringReactant2="";
						// enzyme2 should be the same as enzyme1
						// enzyme2 object here has no enzyme entity label
						EcWS ec2 = (EcWS) ((ArrayList) oneEnzymeSubstratePair)
								.get(0);
						String spidEc2 = ec2.getSpid();
						System.out
								.println("ec for Ec2:"
										+ ec2.getEc()+"/"+this.getClass().getName()+" line "+new Exception().getStackTrace()[0].getLineNumber());
						String metaboliteEntityLabel = "";
						String ecEntityLabel = "";
						// check if enzyme1 and enzyme2 are the same
						if (!(ec1.getEc()).equals(ec2.getEc())) {
							System.out.println("enzyme is not equal");
							System.exit(1);
						}

						// the product object here has no metabolic entity label
						MetaboliteWS product = (MetaboliteWS) ((ArrayList) oneEnzymeSubstratePair)
								.get(1);
						String keggCompoundIdProduct = product
								.getKeggCompoundId();
						System.out.println("keggCompoundIdProduct:"
								+ keggCompoundIdProduct
								+ "/"+this.getClass().getName()+" line "+new Exception().getStackTrace()[0].getLineNumber());
						if (keggCompoundIdToMetaboliteEntityLabelStore.keySet()
								.contains(keggCompoundIdProduct)) {
							// product=(Metabolite)(keggCompoundIdToMetaboliteObj.get(keggCompoundIdProduct));
							metaboliteEntityLabel = (String) keggCompoundIdToMetaboliteEntityLabelStore
									.get(keggCompoundIdProduct);
							System.out.println(this.getClass().getName()+" line "+new Exception().getStackTrace()[0].getLineNumber());
						} else if (!keggCompoundIdToMetaboliteEntityLabelStore
								.keySet().contains(keggCompoundIdProduct)) {
							metaboliteEntityLabel = metaboliteEntityLabelGenerator
									.generate();
							// product.setEntityLabel(metaboliteEntityLabelGenerator.generate());
							// usedKeggCompoundIdStore.add(keggCompoundIdProduct);
							// keggCompoundIdToMetaboliteObj.put(keggCompoundIdProduct,product);
							// //the product object here has metabolite entity
							// label
							keggCompoundIdToMetaboliteEntityLabelStore.put(
									keggCompoundIdProduct,
									metaboliteEntityLabel);
							String xmlStringReactant2 = (new CreateMetaboliteXmlObject2())
									.create(product, metaboliteEntityLabel,
											metaboliteEntityLabel, product
													.getInitialValue());
							totalMetaboliteXmlString = totalMetaboliteXmlString
									+ "\n" + xmlStringReactant2;
							// System.out.println(xmlStringReactant2);
							// System.out.println(totalMetaboliteXmlString);
							System.out
									.println(this.getClass().getName()+" line "+new Exception().getStackTrace()[0].getLineNumber());
						}

						if (ecToEnzymeEntityLabelStore.keySet().contains(
								ec2.getDisplayName())) {
							// substrate=(Metabolite)(keggCompoundIdToMetaboliteObj.get(keggCompoundIdSubstrate));
							ecEntityLabel = (String) ecToEnzymeEntityLabelStore
									.get(ec2.getDisplayName());
							// the substrate obtained from
							// keggCompoundIdToMetaboliteObj has been provided a
							// entity label
						} else if (!ecToEnzymeEntityLabelStore.keySet()
								.contains(ec2.getDisplayName())) { // if the
							// kegg
							// compound
							// has not
							// been
							// processed
							// before,
							// create
							// the
							// metabolic
							// entity
							// xml
							// string
							// substrate.setEntityLabel(metaboliteEntityLabelGenerator.generate());
							ecEntityLabel = enzymeEntityLabelGenerator
									.generate();
							// usedSpidStore.add(enzyme2.getSpid());
							// keggCompoundIdToMetaboliteObj.put(keggCompoundIdSubstrate,substrate);
							ecToEnzymeEntityLabelStore.put(
									ec2.getDisplayName(), ecEntityLabel);
							String ecEntityParameterLabel = ecEntityLabel;
							String xmlStringEc2 = (new CreateEcEntityHmlObject())
									.create(ec2, ecEntityLabel,
											ecEntityParameterLabel, 0);// the
							// label
							// attribute
							// in
							// the
							// parameter
							// tag
							// is
							// used
							// by CI
							totalEcXmlString = totalEcXmlString + "\n"
									+ xmlStringEc2;

							System.out
									.println(this.getClass().getName()+" line "+new Exception().getStackTrace()[0].getLineNumber());
						}
						// enzyme2.setProcessLabel(processLabelForEnzyme1AndEnzyme2);
						// processLabelToSpid.put(processLabelForEnzyme1AndEnzyme2,
						// spidEnzyme2);
						// modified to here 20100629
						// each edge is a connector object
						String connectorLabel2 = metabolicConnectorLabelGenerator
								.generate();
						System.out.println(ec2.getDisplayName() + " to "
								+ product.getKeggCompoundId());
						System.out.println(ecToEnzymeEntityLabelStore.get(ec2
								.getDisplayName())
								+ " to "
								+ keggCompoundIdToMetaboliteEntityLabelStore
										.get(product.getKeggCompoundId()));

						// System.out.println("connector from:"+processLabelForEnzyme1AndEnzyme2);
						// System.out.println("connector label:"+connectorLabel2);
						// System.out.println("connector to:"+metaboliteEntityLabel+"/GrwoNetworkResultToCSML13 line 177");
						// System.out.println("connector threshold:"+product.getStoichiometry());
						Connector con2 = new Connector(ecEntityLabel,
								connectorLabel2, connectorLabel2,
								metaboliteEntityLabel, product
										.getStoichiometry());
						connectorStore.add(con2);

					}

					// / to create enzyme "entity" xml string. This makes each
					// enzyme has a corresponding place in the petri net.
					// / a new connector from the enzyme entity to metabolic
					// process also must be created.
					// String enzymeEntityLabel=""; //to save the enzyme entity
					// label for the metabolic enzyme place
					// if(!(spidToEnzymeEntityLabelStore.keySet().contains(e.getSpid()))){
					//						
					// enzymeEntityLabel=enzymeEntityLabelGenerator.generate();
					// System.out.println(e.getSpid());
					// System.out.println(enzymeEntityLabel);
					// String enzymeEntityParameterLabel=enzymeEntityLabel;
					// String xmlStringEnzymeEntity=(new
					// CreateGonEntityXmlString1()).create(e.getSpid(),enzymeEntityLabel,enzymeEntityParameterLabel,1000);//
					// the initial value of enzyme quantity can be changed here
					// totalEntityXmlString=totalEntityXmlString+"\n"+xmlStringEnzymeEntity;
					// spidToEnzymeEntityLabelStore.put(e.getSpid(),
					// enzymeEntityLabel);
					// }else
					// if(spidToEnzymeEntityLabelStore.keySet().contains(e.getSpid())){
					//						
					// enzymeEntityLabel=(String)spidToEnzymeEntityLabelStore.get(e.getSpid());
					// }

					// String
					// connectorLabelEnzymeEntityToMetabolicProcess=metabolicConnectorLabelGenerator.generate();
					// Connector conEnzymeEntityToMetabolicProcess=new
					// Connector(enzymeEntityLabel,connectorLabelEnzymeEntityToMetabolicProcess,connectorLabelEnzymeEntityToMetabolicProcess,processLabelForEnzyme1AndEnzyme2,"1");//enzyme
					// trigger threshold is set here
					// connectorStore.add(conEnzymeEntityToMetabolicProcess);
					System.out.println("processLabelForEnzyme1AndEnzyme2:"
							+ processLabelForEnzyme1AndEnzyme2);
					// ec.setProcessPriority("4");
					System.out.println("ec1.getProcessPriority():"
							+ ec1.getProcessPriority());
					System.out.println("ec1.getProcessDelay():"
							+ ec1.getProcessDelay());

					metabolicProcessXmlCombinedString = metabolicProcessXmlCombinedString
							+ "\n"
							+ CreateEcProcessXmlObjectWS.create(ec1,
									processLabelForEnzyme1AndEnzyme2, ec1
											.getProcessPriority(), ec1
											.getProcessDelay(), connectorStore);

					// now is to create the protein-protein interaction network
					// xml
					// ArrayList interactingPartner=e.getInteractingPartner();

					// for(Object onePartner:interactingPartner){
					// ArrayList connectorStoreForOnePpiPair=new ArrayList();
					// String partnerSpid=(String)onePartner;
					// String
					// interactionProcessLabel=interactionProcessLabelGenerator.generate();
					// String
					// interactionConnectorLabel1=interactionConnectorLabelGenerator.generate();
					// String
					// interactionConnectorLabel2=interactionConnectorLabelGenerator.generate();
					// // create entity xml string for partner
					// String partnerEnzymeEntityLabel="";
					// if(!(spidToEnzymeEntityLabelStore.keySet().contains(partnerSpid))){
					// partnerEnzymeEntityLabel=enzymeEntityLabelGenerator.generate();
					// String xmlStringEnzymeEntity=(new
					// CreateGonEntityXmlString1()).create(partnerSpid,partnerEnzymeEntityLabel,partnerEnzymeEntityLabel,1000);//
					// the initial value of enzyme quantity can be changed here
					// spidToEnzymeEntityLabelStore.put(partnerSpid,
					// partnerEnzymeEntityLabel);
					// totalEntityXmlString=totalEntityXmlString+"\n"+xmlStringEnzymeEntity;
					// }else
					// if(spidToEnzymeEntityLabelStore.keySet().contains(partnerSpid)){
					// partnerEnzymeEntityLabel=(String)spidToEnzymeEntityLabelStore.get(partnerSpid);
					// }
					//						
					//						
					// // create process between the metabolic enzyme and its
					// interactor
					// Connector conPre=new
					// Connector(enzymeEntityLabel,interactionConnectorLabel1,interactionConnectorLabel1,interactionProcessLabel,"1");
					// Connector conPost=new
					// Connector(interactionProcessLabel,interactionConnectorLabel2,interactionConnectorLabel2,partnerEnzymeEntityLabel,"1");
					// connectorStoreForOnePpiPair.add(conPre);
					// connectorStoreForOnePpiPair.add(conPost);
					// interactionProcessXmlCombinedString=interactionProcessXmlCombinedString+"\n"+CreateGonProcessXmlString2.create("inter_"+e.getSpid()+"|"+(String)onePartner,
					// interactionProcessLabel, "1", "0",
					// connectorStoreForOnePpiPair);//process priority is set
					// here.//firing delay is set here.
					//						
					// }

				}

			}

			// String
			// processXmlCombinedString=metabolicProcessXmlCombinedString+"\n"+interactionProcessXmlCombinedString;
			totalEntityXmlString = totalMetaboliteXmlString + "\n"
					+ totalEcXmlString + "\n";
			String x = StringEscapeUtils.unescapeXml(totalEntityXmlString);

			w_global.characters(x);
			// System.out.println(x);
			// String y=StringEscapeUtils.unescapeXml(processXmlCombinedString);
			String y = StringEscapeUtils
					.unescapeXml(metabolicProcessXmlCombinedString);
			w_global.characters(y);
			// System.out.println(y);

			w_global.endElement("net");

			AttributesImpl attr9 = new AttributesImpl();
			attr9.addAttribute("", "enhancedFiring", "", "", "true");
			attr9.addAttribute("", "samplingInterval", "", "", "0.1");
			attr9.addAttribute("", "simulationTime", "", "", "1000.0");
			attr9.addAttribute("", "logUpdataInterval", "", "", "1.0");
			attr9.addAttribute("", "plotUpdataInterval", "", "", "1.0");
			attr9.addAttribute("", "useContinuousWeakFiring", "", "", "false");
			attr9.addAttribute("", "useDiscreteWeakFiring", "", "", "false");
			attr9.addAttribute("", "firingAccuracy", "", "", "1.0E-10");
			w_global.dataElement("", "simulation", "", attr9, "");
			w_global.endElement("model");
			w_global.endDocument();

			sw.flush();
			String stringToWrite = StringEscapeUtils.unescapeXml(sw.toString());
//			System.out.println(stringToWrite+"/"+this.getClass().getName()+"/line "+new Exception().getStackTrace()[0].getLineNumber());
			sw.close();

			BufferedWriter bwFile = new BufferedWriter(new FileWriter(
					pathToSave));
			bwFile.write(stringToWrite);
			bwFile.flush();
			bwFile.close();

		} catch (IOException ioex) {
			ioex.printStackTrace();

		} catch (SAXException saxex) {
			saxex.printStackTrace();
		}

	}

}
