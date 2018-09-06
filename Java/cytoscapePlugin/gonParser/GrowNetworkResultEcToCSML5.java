package gonParser;

//import gonLayout.TwoLevelLayout;
import growNetwork.EcWS;
import growNetwork.MetaboliteWS;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.StringWriter;
import java.util.*;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.restlet.util.XmlWriter;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;
import org.apache.commons.lang.*;


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
//v18: use connectorLabelGenerator, processLabelGenerator, entityLabelGenerator to comply with cytoscape default nomenclature
// v19: use CreateGonProcessXmlString3.java
//v20: create the ppi entity and process when the ppi has not been processed.
//v21: there are duplicated process being produced in v20. Create "displayNameToProcessLabelStore" hashmap to check the duplication.
//v21: since the MetabolicConnectorLabelGenerator has the same function as ConnectorLabelGenerator, mark out MetabolicConnectorLabelGenerator.
//v21: use EnzymeWS,MetaboliteWS,CreateGonProcessXmlStringWS3
//v22: change the petri net representation of ppi. Create a entity for the complex of two interacting proteins
//v23: add connector for regenerating the consumed enzyme from the metabolic process.
//GrowNetworkResultEcToCSML---------------------
//v0:modified from GrowNetworkResultToCSML23. Use EcWS instead of EnzymeWS
//v0: add a conditional statement to check if substrateEnzymePairStore and enzymeSubstratePairStore are empty
//v1: Disable the creating of enzyme entity. There is no need to create an entity for enzyme. The enzyme entity is meaningful when a specific enzyme is used.
//v2: use CreateGonEntityXmlString2
//v3: use CreateGonProcessXmlStringEcWS1
//v4 20110617: use CreateGonEntityXmlString3, CreateGonProcessXmlStringEcWS2. WrapperSimple has been changed to use agbi.
//v5 20110624: use CreateGonProcessXmlStringEcWS3

/**
 * @uml.dependency supplier="growNetwork.MetaboliteWS"
 */
public class GrowNetworkResultEcToCSML5 {

	ArrayList networkToTransform = null;
	// networkToTransform is an array of array from GrowNetwork.
	String totalEntityXmlString = "";
	String pathToSave;
	ParameterLabelGenerator parameterLabelGenerator;
	EntityLabelGenerator entityLabelGenerator;
	ProcessLabelGenerator processLabelGenerator;
	ConnectorLabelGenerator connectorLabelGenerator;

	// MetabolicConnectorLabelGenerator metabolicConnectorLabelGenerator=new
	// MetabolicConnectorLabelGenerator();
	// MetaboliteEntityLabelGenerator metaboliteEntityLabelGenerator=new
	// MetaboliteEntityLabelGenerator();
	// EntityLabelGenerator entityLabelGenerator=new EntityLabelGenerator();
	// MetabolicProcessLabelGenerator metabolicProcessLabelGenerator=new
	// MetabolicProcessLabelGenerator();
	// InteractionProcessLabelGenerator interactionProcessLabelGenerator=new
	// InteractionProcessLabelGenerator();
	// InteractionConnectorLabelGenerator interactionConnectorLabelGenerator=new
	// InteractionConnectorLabelGenerator();
	// create virtual entity node to represent ppi.
	// EnzymeEntityLabelGenerator enzymeEntityLabelGenerator=new
	// EnzymeEntityLabelGenerator();

	public static void main(String[] args){
		String serPath=args[0];
		String pathToSaveCsml=args[1];
		
		try{
			ObjectInputStream objstream = new ObjectInputStream(new FileInputStream(serPath));
	        Object object = objstream.readObject();
	       new GrowNetworkResultEcToCSML5((ArrayList)object,pathToSaveCsml).transform();
	        }catch(IOException ioex){
				ioex.printStackTrace();
			}catch(ClassNotFoundException classex){
				classex.printStackTrace();
			}
	}
	public GrowNetworkResultEcToCSML5(ArrayList growNetworkResult_,
			String pathToSave_) {
//		JOptionPane.showMessageDialog(new JFrame(), "in the constructor of "+this.getClass().getName());
		/**
		 * @uml.property name="parameterLabelGenerator"
		 * @uml.associationEnd
		 */
		parameterLabelGenerator = new ParameterLabelGenerator();
		/**
		 * @uml.property name="entityLabelGenerator"
		 * @uml.associationEnd
		 */
		entityLabelGenerator = new EntityLabelGenerator();
		/**
		 * @uml.property name="processLabelGenerator"
		 * @uml.associationEnd
		 */
		processLabelGenerator = new ProcessLabelGenerator();
		/**
		 * @uml.property name="connectorLabelGenerator"
		 * @uml.associationEnd
		 */
		connectorLabelGenerator = new ConnectorLabelGenerator();

		networkToTransform = growNetworkResult_;
		pathToSave = pathToSave_;
		System.out.println("in the constructor/" + this.getClass().getName());
	}

	public void transform() { // this method use XmlWriter class
		String metabolicProcessXmlCombinedString = "";
		String interactionProcessXmlCombinedString = "";
//		System.out.println("in the transform/" + this.getClass().getName());
		ArrayList keggCompoundIdStore = new ArrayList();
		// ArrayList enzymeAsEntityStore=new ArrayList();
		ArrayList metabolicProcessLabelStore = new ArrayList();
		// HashMap keggCompoundIdToMetaboliteObj=new HashMap();
		HashMap keggCompoundIdToMetaboliteEntityLabelStore = new HashMap();
		// HashMap processLabelToEnzymeObj=new HashMap();
		// HashMap processLabelToSpid=new HashMap();
		HashMap spidToEnzymeEntityLabelStore = new HashMap();
		HashMap displayNameToProcessLabelStore = new HashMap();
		// HashMap keggCompIdToMetaboliteEntityLabelStore=new HashMap();
//		TwoLevelLayout layoutGenerator = new TwoLevelLayout();
		ArrayList drawnInteraction = new ArrayList();

		try {
			StringWriter sw = new StringWriter(); // the use of StringWriter
			// will eliminate the
			// problem of escaped
			// character

			XmlWriter w_global = new XmlWriter(sw);
			w_global.setPrefix("http://www.csml.org/csml/version1", "csml");
			w_global.setDataFormat(true);
			w_global.setIndentStep(2);
			w_global.startDocument();
			AttributesImpl attr1 = new AttributesImpl();
			attr1.addAttribute("", "majorVersion", "", "", "1");
			attr1.addAttribute("", "minorVersion", "", "", "9");
			w_global.startElement("http://www.csml.org/csml/version1", "model",
					"", attr1);
			w_global.emptyElement("http://www.csml.org/csml/version1",
					"unitdefs");
			w_global.startElement("http://www.csml.org/csml/version1", "net");

			for (Object growFrOneMetabolite : networkToTransform) {
				for (Object oneRelatedEnzyForOneMetabolite : (ArrayList) growFrOneMetabolite) {
					ArrayList connectorStore = new ArrayList();
					Object substrateEnzymePairStore = ((ArrayList) oneRelatedEnzyForOneMetabolite)
							.get(0);
					Object enzymeSubstratePairStore = ((ArrayList) oneRelatedEnzyForOneMetabolite)
							.get(1);
					if(((ArrayList)substrateEnzymePairStore).isEmpty() || ((ArrayList)enzymeSubstratePairStore).isEmpty()){
						continue;
					}
					String processLabelForEnzyme1AndEnzyme2 = processLabelGenerator
							.generate();// generate a process label as the
					// process identifier in CI
					EcWS e = null;
					
					for (Object oneSubstrateEnzymePair : (ArrayList) substrateEnzymePairStore) {

						// String xmlStringReactant1="";

						// substrate object here has nothing in the entity label
						// field
						MetaboliteWS substrate = (MetaboliteWS) ((ArrayList) oneSubstrateEnzymePair)
								.get(0);
						String keggCompoundIdSubstrate = substrate
								.getKeggCompoundId().trim();
						if(keggCompoundIdSubstrate.equals("")||keggCompoundIdSubstrate==""){
							System.out.println(this.getClass().getName()+" line "+new Exception().getStackTrace()[0].getLineNumber());
							System.out.println("keggId is empty");
							System.exit(2);
						}
						// enzyme1 should be the same as enzyme2
						EcWS ec1 = (EcWS) ((ArrayList) oneSubstrateEnzymePair)
								.get(1);
//						System.out.println(this.getClass().getName()+" line "+ new Exception().getStackTrace()[0].getLineNumber());
//System.out.println("ec:"+ec1.getEc()+"/"+this.getClass().getName()+" line "+ new Exception().getStackTrace()[0].getLineNumber());
						String metaboliteEntityLabel;
						e = ec1;
						// Check if the metabolite entity has been processed or
						// not.
						// If it has been processed, get the entity label from
						// the processed metabolite object.
						// This is to avoid multiple replicate of same molecule
						// in the Petri Nets.
						if (keggCompoundIdStore
								.contains(keggCompoundIdSubstrate)) {
							// substrate=(Metabolite)(keggCompoundIdToMetaboliteObj.get(keggCompoundIdSubstrate));
							metaboliteEntityLabel = (String) keggCompoundIdToMetaboliteEntityLabelStore
									.get(keggCompoundIdSubstrate);
							// the substrate obtained from
							// keggCompoundIdToMetaboliteObj has been provided a
							// entity label
						} else { // if the kegg compound has not been processed
							// before, create the metabolic entity xml
							// string
							// substrate.setEntityLabel(metaboliteEntityLabelGenerator.generate());
							metaboliteEntityLabel = entityLabelGenerator
									.generate();
														
							keggCompoundIdStore.add(keggCompoundIdSubstrate);
							// keggCompoundIdToMetaboliteObj.put(keggCompoundIdSubstrate,substrate);
							keggCompoundIdToMetaboliteEntityLabelStore.put(
									keggCompoundIdSubstrate,
									metaboliteEntityLabel);
							String metaboliteEntityParameterLabel = metaboliteEntityLabel;
//							System.out.println(this.getClass().getName()+" line "+new Exception().getStackTrace()[0].getLineNumber());
							String xmlStringReactant1 = (new CreateGonEntityXmlString3())
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
							totalEntityXmlString = totalEntityXmlString + "\n"
									+ xmlStringReactant1;
						}
						// each edge is a connector object
						String connectorLabel1 = connectorLabelGenerator
								.generate();
						// System.out.println("connector from:"+metaboliteEntityLabel);
						// System.out.println("connector label:"+connectorLabel1);
						// System.out.println("connector to:"+processLabelForEnzyme1AndEnzyme2);
						// System.out.println("connector threshold:"+substrate.getStoichiometry());
						Connector con1 = new Connector(metaboliteEntityLabel,
								connectorLabel1, connectorLabel1,
								processLabelForEnzyme1AndEnzyme2, substrate
										.getStoichiometry());
						connectorStore.add(con1);

					}

					for (Object oneEnzymeSubstratePair : (ArrayList) enzymeSubstratePairStore) {

						// String xmlStringReactant2="";
						// enzyme2 should be the same as enzyme1
						// enzyme2 object here has no enzyme entity label
						EcWS ec2 = (EcWS) ((ArrayList) oneEnzymeSubstratePair)
								.get(0);
						String spidEnzyme2 = ec2.getSpid();
						System.out.println("spid of enzyme2:"
								+ spidEnzyme2
								+ "/"
								+ this.getClass().getName()
								+ " line "
								+ new Exception().getStackTrace()[0]
										.getLineNumber());
						String metaboliteEntityLabel;

						// check if enzyme1 and enzyme2 are the same
						if (!(e.getSpid()).equals(ec2.getSpid())) {
							System.out.println("enzyme is not equal");
							System.exit(1);
						}
						if (!(e.getDisplayName()).equals(ec2.getDisplayName())) {
							System.out
									.println("enzyme and reaction id is not equal");
							System.exit(1);
						}
						
						

						// the product object here has no metabolic entity label
						MetaboliteWS product = (MetaboliteWS) ((ArrayList) oneEnzymeSubstratePair)
								.get(1);
						String keggCompoundIdProduct = product
								.getKeggCompoundId();
						System.out.println("keggCompoundIdProduct:"
								+ keggCompoundIdProduct
								+ "/"
								+ this.getClass().getName()
								+ "line "
								+ new Exception().getStackTrace()[0]
										.getLineNumber());
						if(keggCompoundIdProduct.equalsIgnoreCase("")||keggCompoundIdProduct==""){
							System.out.println(this.getClass().getName()+" line "+new Exception().getStackTrace()[0].getLineNumber());
							System.out.println("keggId is empty");
							System.exit(4);
						}
						if (keggCompoundIdStore.contains(keggCompoundIdProduct)) {
							// product=(Metabolite)(keggCompoundIdToMetaboliteObj.get(keggCompoundIdProduct));
							metaboliteEntityLabel = (String) keggCompoundIdToMetaboliteEntityLabelStore
									.get(keggCompoundIdProduct);
						} else {
							metaboliteEntityLabel = entityLabelGenerator
									.generate();
							
							// product.setEntityLabel(metaboliteEntityLabelGenerator.generate());
							keggCompoundIdStore.add(keggCompoundIdProduct);
							// keggCompoundIdToMetaboliteObj.put(keggCompoundIdProduct,product);
							// //the product object here has metabolite entity
							// label
							keggCompoundIdToMetaboliteEntityLabelStore.put(
									keggCompoundIdProduct,
									metaboliteEntityLabel);
//							System.out.println(this.getClass().getName()+" line "+new Exception().getStackTrace()[0].getLineNumber());
							String xmlStringReactant2 = (new CreateGonEntityXmlString3())
									.create(product, metaboliteEntityLabel,
											metaboliteEntityLabel, product
													.getInitialValue());
							totalEntityXmlString = totalEntityXmlString + "\n"
									+ xmlStringReactant2;
						}

						ec2.setProcessLabel(processLabelForEnzyme1AndEnzyme2);
						// processLabelToSpid.put(processLabelForEnzyme1AndEnzyme2,
						// spidEnzyme2);

						// each edge is a connector object
						String connectorLabel2 = connectorLabelGenerator
								.generate();
//						System.out.println("connector from:"
//								+ processLabelForEnzyme1AndEnzyme2);
//						System.out
//								.println("connector label:" + connectorLabel2);
						System.out.println("connector to:"
								+ metaboliteEntityLabel
								+ "/"
								+ this.getClass().getName()
								+ " line "
								+ new Exception().getStackTrace()[0]
										.getLineNumber());
						System.out.println("connector threshold:"
								+ product.getStoichiometry()+" "+this.getClass().getName()+" line "+new Exception().getStackTrace()[0].getLineNumber());
						Connector con2 = new Connector(
								processLabelForEnzyme1AndEnzyme2,
								connectorLabel2, connectorLabel2,
								metaboliteEntityLabel, product
										.getStoichiometry());
						connectorStore.add(con2);
//						System.out.println(this.getClass().getName()+" line "+new Exception().getStackTrace()[0].getLineNumber());
					}

					// here is to check if the process with the same
					// spid+reactionId has been processed before so the
					// replication of process can be avoided.
					Set processedDisplayNameStore = displayNameToProcessLabelStore
							.keySet();
//					System.out.println(this.getClass().getName()+" line "+new Exception().getStackTrace()[0].getLineNumber());
					String ssssss = e.getEc();
					System.out.println("ec of EcWS: "
							+ ssssss
							+ "/"
							+ this.getClass().getName()
							+ " line "
							+ new Exception().getStackTrace()[0]
									.getLineNumber());
					if (processedDisplayNameStore.contains(e.getDisplayName())) {
						continue; // if the displayName has been seen before,
						// skip rest of the code, which means no new
						// process will be created
					} else {
						displayNameToProcessLabelStore.put(e.getDisplayName(),
								processLabelForEnzyme1AndEnzyme2);
					}

			// The code below is deactivated if EC is used instead of swissprot id,because "enzyme entity" has meaning only if spid is used 		
					// / to create enzyme "entity" xml string. This makes each
					// enzyme has a corresponding place in the petri net.
					// / a new connector from the enzyme entity to metabolic
					// process also must be created.
//					String enzymeEntityLabel = ""; // to save the enzyme entity
//					// label for the metabolic
//					// enzyme place
//					if (!(spidToEnzymeEntityLabelStore.keySet().contains(e
//							.getSpid()))) {
//						enzymeEntityLabel = entityLabelGenerator.generate();
//						System.out.println(this.getClass().getName()+" line "+new Exception().getStackTrace()[0].getLineNumber());
//						System.out.println(e.getSpid());
//						System.out.println(enzymeEntityLabel);
//						String enzymeEntityParameterLabel = enzymeEntityLabel;
//						System.out.println(this.getClass().getName()+" line "+new Exception().getStackTrace()[0].getLineNumber());
//						String xmlStringEnzymeEntity = (new CreateGonEntityXmlString2())
//								.create(e.getSpid(), enzymeEntityLabel,
//										enzymeEntityParameterLabel, 1000);// the
//						// initial
//						// value
//						// of
//						// enzyme
//						// quantity
//						// can
//						// be
//						// changed
//						// here
//						totalEntityXmlString = totalEntityXmlString + "\n"
//								+ xmlStringEnzymeEntity;
//						spidToEnzymeEntityLabelStore.put(e.getSpid(),
//								enzymeEntityLabel);
//					} else if (spidToEnzymeEntityLabelStore.keySet().contains(
//							e.getSpid())) {
//
//						enzymeEntityLabel = (String) spidToEnzymeEntityLabelStore
//								.get(e.getSpid());
//					}
//
//					String connectorLabelEnzymeEntityToMetabolicProcess = connectorLabelGenerator
//							.generate();
//					String connectorLabelMetabolicProcessToEnzymeEntity = connectorLabelGenerator
//							.generate();
//					Connector conEnzymeEntityToMetabolicProcess = new Connector(
//							enzymeEntityLabel,
//							connectorLabelEnzymeEntityToMetabolicProcess,
//							connectorLabelEnzymeEntityToMetabolicProcess,
//							processLabelForEnzyme1AndEnzyme2, "1");// enzyme
//					// trigger
//					// threshold
//					// is set
//					// here
//					Connector conMetabolicProcessToEnzymeEntity = new Connector(
//							processLabelForEnzyme1AndEnzyme2,
//							connectorLabelMetabolicProcessToEnzymeEntity,
//							connectorLabelMetabolicProcessToEnzymeEntity,
//							enzymeEntityLabel, "1");// the consumed enzyme is
//					// regenerated here
//					connectorStore.add(conEnzymeEntityToMetabolicProcess);
//					connectorStore.add(conMetabolicProcessToEnzymeEntity);
			// The code above is deactivated if EC is used instead of swissprot id,because "enzyme entity" has meaning only if spid is used 			
					
					// create a new metabolic process
					metabolicProcessXmlCombinedString = metabolicProcessXmlCombinedString
							+ "\n"
							+ CreateGonProcessXmlStringEcWS3.create(e,
									processLabelForEnzyme1AndEnzyme2, e
											.getProcessPriority(), e
											.getProcessDelay(), connectorStore,
									"metabolic");

					// now is to create the protein-protein interaction network
					// xml

					// {// this block is disabled because of no ppi data for
					// EcWS object
					// ArrayList interactingPartner=e.getInteractingPartner();
					//					
					// for(Object onePartner:interactingPartner){
					// if(((String)onePartner)!=""){
					// ArrayList connectorStoreForOnePpiPair=new ArrayList();
					// String partnerSpid=(String)onePartner;
					// String
					// interactionProcessLabel=processLabelGenerator.generate();
					// String
					// interactionConnectorLabel1=connectorLabelGenerator.generate();
					// //for the enzyme to interaction process
					// String
					// interactionConnectorLabel2=connectorLabelGenerator.generate();
					// //for the partner to interaction process
					// String
					// interactionConnectorLabel3=connectorLabelGenerator.generate();
					// //for the interaction process to complex
					// // create entity xml string for partner
					// String partnerEnzymeEntityLabel="";
					// if(!(spidToEnzymeEntityLabelStore.keySet().contains(partnerSpid))){
					// partnerEnzymeEntityLabel=entityLabelGenerator.generate();
					// String xmlStringEnzymeEntity=(new
					// CreateGonEntityXmlString2()).create(partnerSpid,partnerEnzymeEntityLabel,partnerEnzymeEntityLabel,1000);//
					// the initial value of enzyme quantity can be changed here
					// spidToEnzymeEntityLabelStore.put(partnerSpid,
					// partnerEnzymeEntityLabel);
					// totalEntityXmlString=totalEntityXmlString+"\n"+xmlStringEnzymeEntity;
					// }else
					// if(spidToEnzymeEntityLabelStore.keySet().contains(partnerSpid)){
					// partnerEnzymeEntityLabel=(String)spidToEnzymeEntityLabelStore.get(partnerSpid);
					// }
					//						
					// // test if the ppi pair has been processed already. If
					// not, create the ppi entity and process
					// String
					// onePpiPair=enzymeEntityLabel+"_"+partnerEnzymeEntityLabel;
					// if(!drawnInteraction.contains(onePpiPair)){
					// // create process between the metabolic enzyme and its
					// interactor
					// String
					// complexEntityLabel=entityLabelGenerator.generate();
					// String xmlStringComplexEntity=(new
					// CreateGonEntityXmlString1()).create(e.getSpid()+"|"+partnerSpid,complexEntityLabel,complexEntityLabel,0);
					// totalEntityXmlString=totalEntityXmlString+"\n"+xmlStringComplexEntity;
					// Connector conPre1=new
					// Connector(enzymeEntityLabel,interactionConnectorLabel1,interactionConnectorLabel1,interactionProcessLabel,"1");
					// Connector conPre2=new
					// Connector(partnerEnzymeEntityLabel,interactionConnectorLabel2,interactionConnectorLabel2,interactionProcessLabel,"1");
					// Connector conPost=new
					// Connector(interactionProcessLabel,interactionConnectorLabel3,interactionConnectorLabel3,complexEntityLabel,"1");
					// connectorStoreForOnePpiPair.add(conPre1);
					// connectorStoreForOnePpiPair.add(conPre2);
					// connectorStoreForOnePpiPair.add(conPost);
					// interactionProcessXmlCombinedString=interactionProcessXmlCombinedString+"\n"+CreateGonProcessXmlStringWS3.create("inter_"+e.getSpid()+"|"+(String)onePartner,
					// interactionProcessLabel, "1", "0",
					// connectorStoreForOnePpiPair,"interaction");//process
					// priority is set here.//firing delay is set here.
					// drawnInteraction.add(onePpiPair);
					// }
					// }
					// }
					// }

				}

			}

			String processXmlCombinedString = metabolicProcessXmlCombinedString
					+ "\n" + interactionProcessXmlCombinedString;
			String x = StringEscapeUtils.unescapeXml(totalEntityXmlString);
			w_global.characters(x);
			// System.out.println(x);
			String y = StringEscapeUtils.unescapeXml(processXmlCombinedString);
			w_global.characters(y);
			// System.out.println(y);

			w_global.endElement("http://www.csml.org/csml/version1", "net");

			AttributesImpl attr9 = new AttributesImpl();
			attr9.addAttribute("", "enhancedFiring", "", "", "true");
			attr9.addAttribute("", "samplingInterval", "", "", "0.1");
			attr9.addAttribute("", "simulationTime", "", "", "1000.0");
			attr9.addAttribute("", "logUpdataInterval", "", "", "1.0");
			attr9.addAttribute("", "plotUpdataInterval", "", "", "1.0");
			attr9.addAttribute("", "useContinuousWeakFiring", "", "", "false");
			attr9.addAttribute("", "useDiscreteWeakFiring", "", "", "false");
			attr9.addAttribute("", "firingAccuracy", "", "", "1.0E-10");
			w_global.dataElement("http://www.csml.org/csml/version1",
					"simulation", "", attr9, "");
			w_global.endElement("http://www.csml.org/csml/version1", "model");
			w_global.endDocument();

			sw.flush();
			String stringToWrite = StringEscapeUtils.unescapeXml(sw.toString());
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
