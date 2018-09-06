package plugin;

//ShowNetworkB----------------
//V0: use CyNetFrObject.java
//V1: use CyNetFrSerPath.java so that NetDeserialization.java is not needed.
//V1: use CyNetFrSerPath1.java
//V2: add "show ppi" button and its handler
//V3: use CyNetWithProteinInteraction1.java
//V3:use CyNetWithProteinInteraction2.java
//V4: in the statement of method-JButtonShowPpiHandler:
// 1.use VizMapperT.java
// 2.mark out- 'CyNetworkView cyView=Cytoscape.createNetworkView(cyNetWPpi,"Net with PPI");'
//V5: add omim button
//V6: add a static variable- omimNodeGroup
//V6: use RevealOmim1.java
//V6: add Class JButtonHideOmimHandler
//V6: add border to jp3


//ShowNetworkC---------------
//modified from ShowNetworkB6.java
//read the in-house xml network file and display in Cytoscape
//The content of Class JButtonLoadNetworkHandler deals with the layout too
//v1: replace the usage of XmlToCyNet to HmlToCyNet in inner class "JButtonLoadNetworkHandler"
//v1: use CyNetWithProteinInteractionA in inner class "JButtonShowPpiHandler"

//ShowNetworkD----
//v0:modified from ShowNetworkC1. Add growNetwork function
//v0:use GrowNetEcWSUseStartBufferedWriter8 to grow the network

//ShowNetworkE-----
//v0: add task bar 
//v1: use GrowNetEcWSUseStartBufferedWriter9

//GrowNetwork----
//v0 20110111:renamed from ShowNetworkE1
//v0 20110111: use GrowNetEcWSUseStartBufferedWriter10
//v2 20110112:use GrowNetEcWSUseStartBufferedWriter11
//v3 20110113: show the spacc for the selected node
//v4 20110113: use HmlToCyNet1 for assigning each node a attribute
//v5 20110115: use Hml2vToCyNet
//v6 20110118: use GrowNetEcWSUseStartBufferedWriter12
//v6 20110119: use Hml2vToCyNet1
//v7 20110119: use GrowNetEcWSUseStartBufferedWriter13, Hml2v1ToCyNet
//v7 20110120: use Hml2v1ToCyNet1
//v8 20110120: add spacc nodes connecting to ec node
//v9 20110120: deal with the layout after new spacc nodes are added by using VizMapperEMP
//v10 20110121: add hml parser button
//v11 20110127: add filebrowser button
//v11 20110127: add pubmed document search function for enzyme node
//v11 20110128: make VisualStyleBuilder a global variable
//v11 20110128: mark out the use of GlobalAppearanceCalculator.Try to deal with the node shape.Not achieved
//v11 20110204: "adding the doc count" is functional. Add the doc count from BioText and eUtility
//v11 20110204: make WrapperSimple as a static variable. Add pubmed document search function for metabolite node 
//v12 20110207: add UIds(an ArrayList) attribute to the docCount node
//v13 20110208: use GrowNetEcWSUseStartBufferedWriter14
//v13 20110208: add "CN:" to the identifier of common name node. Doing this can distinguish the commonName node from metabolite node
//v13 20110208: remove the code of adding uidString to the node attribute
//v13 20110208: add myPanel to the control panel
//v14 20110209: move GrowNetwork frame to the control panel. Show UID in myPanel.
//v15 20110209: Put UID in a JTable,and put the JTable to myPanel
//v16 20110301: Refine the UID list using PMC full text and Whatizit.Use RefineBioTextResult2.
//v17 20110302: use RefineBioTextResult3 for correctly parsing pmc article-1995220
//v18 20110302: use RefineBioTextResult4 for correctly parsing pmc article-538269
//v19 20110302: use RefineBioTextResult6 for correctly parsing review article-pmc1852094
//v20 20110303: use RefineBioTextResult7 for correctly parsing pmid18670615. Besides, making the biotext refinement into another seperate step(Add RefineBioTxHandler)
//v21 20110307: do examination before creating the ref count node to eliminate the node duplication, such as 4.1.1.47 will map to P0AEP8 and P0AEP7.
//              and both of them have the common names "Tartronate-semialdehyde synthase" and "Glyoxylate carboligase"
//v21 20110308: change the node shape and color for some type of nodes.
//v22 20110308: To make the "refine ref" function applicable to both refCountNode from biotext and eutil.
//v23 20110308: use RefineBioTextResult8
//v23 20110308: set the x and y position of spacc node and refcountNode of spacc
//v24 20110308: set x and y coordinate for common name, biotextRefCount,eUtilRefCount nodes
//v24 20110308: set x and y coordinate for the refinedRef node
//v24 20110308: change the line style for the reference count related net
//v25 20110309: make the keyword for text-mining user definable. Add one text field in myPanel for keyword input. Add one keyword node between commonName node and refCount node
//v26 20110310: add Organism filter function
//v27 20110311: make the organism filter applicable to both enzyme and protein nodes
//v27 20110311: use OrganismFilter1.OrganismFilter generated too many queries to the server.
//v28 20110406: use IntactAgent to add interacting protein.
//v29 20110408: change the way by which the visual style is redrawn after adding ppi(not using VizMapperEMP)
//v30 20110408: add button to remove related spacc for the selected ec node.
//v30 20110409: add button to remove ppi for selected protein node.
//v30 20110409: explicitly set the node_label parameter in visualStyle for the nodes generated from text-mining and the nodes generated from ppi
//v31 20110410: add showing and removing protein-chemical interaction button
//v32 20110411: disable the use of VizMapperS in class "Hml2v1ParserHandler". Use VizMapperEMP instead.
//v32x 20110411: use Hml2v1ToCyNet2, VizMapperEMP1 in "Hml2v1ParserHandler"
//v32x 20110411: add "displayName" attribute to cyNode and use VizMapperEMP1 in class "AddRelatedSpaccHandler".
//v33 20110411: add "displayName" attribute to cyNode and set NODE_LABEL of visualStyle in class "GetRefCountHandler"
//v34 20110412: remove isolated nodes after organismFilter. Add class "CleanIsolatedNode"
//v34 20110413: set the displayName property for node "refinedUidNode" and set the visual property NODE_LABEL
//v35 20110413: use RefineBioTextResult9
//v36 20110413: use RefineBioTextResult10
//v37 20110414: make uid displayed in a new jframe
//v37 20110414: set moleType and nodeType attributes for the nodes generated by text-mining.
//v38 20110414: use VizMapperEMP2 in class "Hml2v1ParserHandler"
//v38 20110414: add "docCount" and "parentNodeId" to all the nodes representing the search result from BioTx and Eutil
//v39 20110415: add SumDocCountToBioEntityHandler and a new button
//v39 20110416: use BioEntityScoreCalculator1
//v40 20110416: add class "ScoreConvergerHandler"
//v41 20110417: add checkbox "biotext" and "eutility"
//v42 20110417: add one more try.. catch in class "RefineUidListHandler" to cope with the contact failure to Whatizit
//v43 20110418: add year input column for text-mining
//v44 20110420: add "pubmed link" attribute to biotext and eutil unrefined ref_count node
//v45 20110420: check if the refCount node from biotext and eUtil has existed already for the metabolite node.
//v45 20110420: check if the user has provided all the essential info before network creation.
//v45 20110420: use Hml2v1ToCyNet2, VizMapperEMP2 in class "MyTask"
//v46 20110420: modify the code in class "CheckBoxHandler".Add conditional expression "isSelected()" for JCheckBox
//v47 20110421: will read parameter from gProp.txt file
//v48 20110421: make noOfRelatedReactionForEc and noOfRelatedReactionForComp user definable
//v48 20110422: add JScrollPane to MyPanel
// NcDocReSy----------------------------------------------
//v0: renamed from GrowNetwork48
//v0: use GrowNetEcWSUseStartBufferedWriter15
//v1 20110424: add the publication year filter in class "GetRefCountHandler"
//v2 20110424: don't use refCountNodeList to keep track of the created refCountNodes.When biotext or eutil is going to be use, get the current node list and check if the same tool(biotext or etuil) has been used.
//v3 20110425: try to get the nodes which are "displayed" in the current network panel before going to biotext and eutility
//v4 20110425: use arraylist to save the node id of all selected nodes for checking the biotext has been used on a common name or not.
//    Check if the refCount node for Biotext or Eutility has been created immediately after the commonName and keyword node are generated.
//v5 20110425: if no associated reaction is found for the input enzyme or metabolite, a node is still added to the network panel
//v6 20110502: use BioEntityScoreCalculator2. Change the bioentity node size according to ref count
//v6 20110502: use Iterator in method "GetRefCountHandler" around line 1502 and 1902
//v6 20110502: Iterator and Set will have "ConcurrentModificationException". Use an arraylist to save the ids of selected nodes(around line 1511);
//v6 20110503: clean the content in processedKeggCompId, processedReationId in StartStaticVariable.
//v7 20110503: the organism restriction is necessary for the network generation. Use GrowNetEcWSUseStartBufferedWriter16
//v8 20110525: add the starting node to the constructed metabolic network(only for starting with enzyme).
//v9 20110528: change the way to check if the combination of commonName and keyword has been used on Biotext or eUtil before using Biotext and eutil
//v10 20110529: use BioTextNodeProducer,EutilNodeProducer
//v10 20110529: add few codes for organism checking for the related spacc.
//v11 20110530: use CommonNameRootedTreeProducer
//v12 20110530: use BioEntityScoreCalculator3. Use a correct method to get the currently displayed network in class "SumDocCountToBioEntityHandler"
//v13 20110608: use GrowNetEcWSUseStartBufferedWriter17, RefineBioTextResult11
//v14 20110608: use CommonNameRootedTreeProducer1 to allow multiple keyword for biotext and eutil.
//v15 20110609: modify codes in RefineUidListHandler due to the allowance of multiple free text keywords
//v16 20110609: use RefineBioTextResult12
//v16 20110616: add one more variable to the argument of CommonNameRootedTreeProducer1
//v17 20110617: use GrowNetEcWSUseStartBufferedWriter18 and web service on agbi
//v18 20110618: add organism textfield for metabolic network growing
//v18 20110621: after loading the created metabolic network in Cytoscape, export the net to a .cys file
//v19 20110621: since the "db" is set to "pmc" when using the eutility, the id returned by eutility is pmcid already. There is no need to do pmcid conversion.
//v20 20110621: use CommonNameRootedTreeProducer2. 
//v21 20110621: change codes in RefineUidListHandler
//v21 20110621: use RefineBioTextResult14
//v22 20110622: disable the hml parser and add csml parser in CsmlParserHandler
//v22 20110624: use Csml19ToCyNet1 in CsmlParserHandler
//v23 20110624: use GrowNetEcWSUseStartBufferedWriter19,Hml2v1ToCyNet3 

// NcDocReSy102-------------
// 20110624: modified from NcDocReSy23. Hide some GUI components of NcDocReSy23
//v103 20110624: modify the code in RefineUidListHandler
//v104 20110625: use RefineBioTextResult15 in RefineUidListHandler
//v104 20110626: change some variable name in RefineUidListHandler
//v105 20110626: use RefineBioTextResult16
//v106 20110626: change the name of the used class "RefineBioTextResult16" to "RefineArticle"
//v106 20110626: add key "protein" to the "userQuery" HashMap in RefineUidListHandler.And in the previous version, the way to get the common name of bioentity node is incorrect.
//v107 20110626: set the "UID/PMCID" and "PubMed link" of the refinedRef node according to biotext or eutil in RefineUidListHandler
//v108 20110627: modify the way of setting the visualStyle of starting metabolite node. Use Hml2v1ToCyNet4
//v109 20110630: use GrowNetEcWSUseStartBufferedWriter21, Csml19ToCyNet2
//v109_ 20110702: after the "grow net" button is pressed, show the related pathway id in the network view panel(KeggPathwayQueryHandler).
//v109_ 20110702: add ShowPathwayNetworkHandler,MyNodeContextMenuListener which will show the network of pathway of selected pathway node
//v109_ 20110702: use Csml19ToCYNet3
//v109_ 20110703: modify the gui so that the network reconstruction is disabled and shows the established kegg pathway topology instead.
//v109_ 20110703: disable the usage of OrganismFilter1 and use OrganismFilter3
//v109_ 20110703: allow the enzyme node(ec node) could be selected for literature search
//v109_ 20110704: after the generation of a network by whatever methods, a VisualStyleBuilder object should be created. Create a VisualStyleBuilder object after generating the network form the "pathway" CyNode
//v110 20110704: use RefineArticle1
//v111 20110707: use Csml19ToCyNet4. Allow pathwayId node to be used for document retrieval
//v111 20110708: an visualStyleBuilder has to be create every time after a network is creaetd
//v112 20110708: add edge between the starting node(represent either a spacc or metabolite)and the pathway node. Also add edge between the spacc node and the corresponding ec node
//v112 20110711: implement the code for showing the related pathway when starting with metabolite in KeggPathwayQueryHandler
//v113 20110711: use Csm19ToCyNet4
//v113 20110711: use Csm19ToCyNet5
//v113 20110711: modify the code for adding edge between the starting spacc and the enzyme node of the generated pathway in ShowPathwayNetworkHandler
//v113 20110711: use VizMapperEMP3 in ShowPathwayNetworkHandler
//v114 20220712: add JButton "jbSumDocToEntity" to literature search component(jpPart3)
//v114 20110712: use VizMapperEMP3
//v115 20110715: use only db=pmbmed in the e-utility literature search.So...if eutility is used, it retrieves pmid and the abstract of each pmid is sent to whatizit.
//				If biotext is used, biotext also returns pmid. Then the corresponding pmcid is searched, and the full text is retrieved for the pmcid. The full text is sent to whatizit
//v115_1 20110717: since tunicata is down at this day, change the way to get the pathway id from spid(avoid using redoxnw).The way to do it is: spid->ec>pathwayId.Before: spid->spacc->kegg geneId->pathwayId
//v115_1 20110718: use TerminologyAgent1
//v115_1 20110718: the pubmed link for the biotext and eutil nodes and their docRefine nodes are always "http://www.ncbi.nlm.nih.gov/pubmed/"
//v115_2 20110718: put the required path parameters in the gProp.txt file.This file should contain two variables: recordFolder, tempCsmlFullPath
//v115_2 20110719: mark out the codes dealing with omim
//v116 20110721: use CommonNameRootedTreeProducer3 so that user can choose to use pmc, pubmed, biotext
//v117 20110722: use SummarizeCitation
//v117 20110723: rename SumDocCountToBioEntityHandler to SummarizeLiteSearchResultHandler
//v117 20110723: use kegg to get the common name for ec node
//v118 20110724: use CommonNameRootedTreeProducer4
//v119 20110725: use RefCountScoreCalculator to calculate the ref count score for the bioentity node
//v119 20110725: add jComponents for score calculation
//v120 20110725: use NodeFilledColorCalculator in SummarizeLiteSearchResultHandler
//v121 20110726: In GetRefCountHandler, if the common name of enzyme or protein has length<6, skip this common name it.
//v122 20110727: Even user doesn't specify the organism, the protein member of selected EC still can be fetched.
//v123 20110727: use RefCountScoreCalculator1, SummarizeCitation1
//v124 20110728: use SummarizeCitation2. Use StartStaticVariable.hiddenNodeId,StartStaticVariable.originalCyEdgeList in ShowHiddenNodeHandler
//v125 20110728: use VizMapperBioentityLiteSearchNet and hide unimportant metabolite in ShowHiddenNodeHandler. 
//v126 20110729: change all the usage of VizMapperEMP3 to VizMapperBioentityLiteSearchNet.Use VizMapperBioentityLiteSearchNet in GetRefCountHandler
//v126 20110807: use VizMapperBioentityLiteSearchNet1 due to the addition of attribute "usePhraseSearch" of the refCountEutil node
//v128 20110807: modified from v126. Change the nodeType attribute of refinedRef node for PMC,PM,BioTx around line 2353. Use VizMapperBioentityLiteSearchNet1 in RefineUidListHandler
//v129 20110807: use SummarizeCitation2 in ScoreCalculateHandler in case user forgets to do summarization before score calculation
//v130 20110808: use ButtonGroup on moleType button and scoreCaculator button.
//v131 20110809: change the jcomponent arrangement in control panel
//v132 20110809: use SummarizeCitation3 in SummarizeLiteSearchResultHandler and ScoreCalculateHandler
//v133 20110809: use CommonNameRootedTreeProducer7
//v134 20110810: make the score calculation mode browse and discover not mutual exclusive
//v134 20110810: mark out the code relating to the old network construction method(GrowNetHandler, MyTask)


import java.util.ArrayList;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JTextField;

import javax.swing.border.Border;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;



import textMining.EutilityPubMed5;
import textMining.PmidToPmcid;

import textMining.RefCountScoreCalculator1;
import textMining.RefineArticle1;
import textMining.TerminologyAgent1;
import textMining.ValidateProteinCommonName;

import whatizitws.client.WhatizitException_Exception;

import java.io.*;

import giny.view.NodeView;
import gonParser.Csml19ToCyNet5;

import growNetwork.EcToSpaccWS;
import growNetwork.CsmlStringFromPathwayId;
import growNetwork.EcTransformerWS;

import growNetwork.KeggCompIdToPathwayId;
import growNetwork.KeggCompoundIdTransformer;

import growNetwork.OrganismFilter3;
import growNetwork.RejectMetabolite;
import growNetwork.SpaccTransformerWS;
import growNetwork.SpidToEcWS1;
import growNetwork.SpidToSpaccWS;
import growNetwork.StartStaticVariable;

import cytoscape.groups.CyGroup;
import cytoscape.groups.CyGroupManager;

import cytoscape.plugin.CytoscapePlugin;
import cytoscape.util.CytoscapeAction;
import cytoscape.CyEdge;
import cytoscape.Cytoscape;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.view.CyNetworkView;
import cytoscape.view.cytopanels.CytoPanelImp;
import cytoscape.visual.GlobalAppearanceCalculator;
import cytoscape.visual.LineStyle;
import cytoscape.visual.NodeShape;
import cytoscape.visual.VisualPropertyType;

import cytoscape.data.CyAttributes;
import cytoscape.data.CyAttributesUtils;
import cytoscape.data.Semantics;
import cytoscape.data.readers.VisualStyleBuilder;

import ding.view.NodeContextMenuListener;

import eUtilAbstract2135422Jaxb.AbstractText;
import eUtilAbstract2135422Jaxb.PubmedArticleSet;

import edu.emory.mathcs.backport.java.util.Collections;

public class NcDocReSy134 extends CytoscapePlugin {
	HashMap<String,String> filePathStore=new HashMap<String,String>();
	JButton jbGrow;
	JButton jbGetSpacc;
//	JButton jbParseHml2v1;
	
	JButton jbGetRefCount;
	JButton jbFileBrowser;
	JButton jbShowUid;
	JButton jbRefineUidList;
	JButton jbShowPpi;
	JButton jbOrganismFilter;
	JButton jbRelatedSpaccCleaner;
	JButton jbRemovePpi;
	JButton jbShowProteinChemicalInteraction;
	JButton jbRemoveProteinChemicalInteraction;
	JButton jbSummarizeLiteSearchResult;
	JButton jbScoreConverger;
	JButton jbCsmlToCyNet;
	JButton jbShowHiddenNode;
	
	JTextField jTextFieldId;
	JTextField jTextFieldReactionNoForEnzyme;
	JTextField jTextFieldReactionNoForComp;
	JTextField jTextFieldOrganism;
	JComboBox comboBoxLevel;
	JTextField jTextFieldCsmlPath;
//	JTextField jTextFieldHml2v1ToParse;
	JTextField jTextFieldCsmlToCyNet;
	JTextField jTextFieldKeyword;
	JTextField jTextFieldYear;
	JTextField jTextFieldTaxo;
	JTextField jTextFieldKeywordForConverge;
	JComboBox comboBoxLayer;
	
	MyPanel myPanel;
//	JScrollPane jscrollPane;
	String diskSymbol16g="";
	String recordFolderPath="";
	
	String selectedSerPath;
	Object netObject;
	String serPath;
	String xmlPath;
	
	int levelToGrow;
	String tissue;
	String organelle;
	String pathForCsml;
	String startId;
	String checkBoxMoleType;
	
//	static int docCountTailing=0;
	
//	public static cytoscape.task.TaskMonitor taskMonitor;
	
	JCheckBox jcMetabolite;
	JCheckBox jcEnzyme;
	JCheckBox jcBiotext;
	JCheckBox jcPubmed;
	JCheckBox jcPmc;
	JCheckBox jcBrowse;
	JCheckBox jcDiscover;
	
	boolean useBiotext;
	boolean usePubmed;
	boolean usePmc;
	boolean calculateBrowseScore;
	boolean calculateDiscoverScore;
	
	static CyGroup omimNodeGroup=CyGroupManager.createGroup("omim","test");
	
	VisualStyleBuilder vs;
	
//	ArrayList refCountNodeList=new ArrayList();  //keep track of the created refCount node
	
	public BufferedWriter bw_messageRecorder;
	
	
	/**
	 * This constructor creates an action and adds it to the Plugins menu.
	 */
	public NcDocReSy134() {
//		String recordFolderPath="";
//		if(!new File("C:\\Program Files\\Cytoscape_v2.7.0\\plugins\\gProp.txt").exists()){
//			JOptionPane.showMessageDialog(new JFrame(), "please set the parameter in property file-C:\\Program Files\\Cytoscape_v2.7.0\\plugins\\gProp.txt");	
//			System.exit(0);
//		}
//		try{
//			BufferedReader br_propertyFile=new BufferedReader(new FileReader("C:\\Program Files\\Cytoscape_v2.7.0\\plugins\\gProp.txt"));
//			String ln_prop;
//			while((ln_prop=br_propertyFile.readLine())!=null){
//				String[] lnPropArray=ln_prop.split("=");
//				if(lnPropArray[0].equalsIgnoreCase("recordFolder")){
//					recordFolderPath=lnPropArray[1];
//				}
//			}
//			bw_messageRecorder=new BufferedWriter(new FileWriter(recordFolderPath+"messageRecorder.txt"));
//			StartStaticVariable.bw_messageRecorder=bw_messageRecorder;
////			String computername=InetAddress.getLocalHost().getHostName();
////			System.out.println(computername);
////			if(computername.equalsIgnoreCase("trevally")){
////				diskSymbol16g="J";
////			}else if(computername.equalsIgnoreCase("uni-afe0b6f3dd0")){
////				diskSymbol16g="d";
////			}else if(computername.equalsIgnoreCase("hangmaolee-38b5")){
////				diskSymbol16g="d";
////			}
//		}catch(IOException ex){
//			ex.printStackTrace();
//		}
		
		StartStaticVariable xx=new StartStaticVariable();  //this line can't be marked-out. Why??
		
		String[] det={};
		Vector vx=StartStaticVariable.wrapperSimple.getResults(1, "show tables", det);
		Vector vx1=StartStaticVariable.wrapperSimple.getResults(2, "show tables", det);
		//create a new action to respond to menu activation
		MaoCytoscapePluginAction action = new MaoCytoscapePluginAction();
		//set the preferred menu
		action.setPreferredMenu("Plugins");
		//and add it to the menus
		Cytoscape.getDesktop().getCyMenus().addAction(action);
		System.out.println("disksymbol:"+diskSymbol16g);
		
//		CytoPanelImp ctrlPanel=(CytoPanelImp)Cytoscape.getDesktop().getCytoPanel(SwingConstants.WEST);
//		myPanel=new MyPanel();
//		
//		ctrlPanel.add("myPanel",myPanel);
//		int indexInCytoPanel=ctrlPanel.indexOfComponent("myPanel");
//		ctrlPanel.setSelectedIndex(indexInCytoPanel);
	}

	
	/**
	 * This class gets attached to the menu item.
	 */
	public class MaoCytoscapePluginAction extends CytoscapeAction {


		/**
		 * The constructor sets the text that should appear on the menu item.
		 */
		public MaoCytoscapePluginAction() {super("ncDocReSy134");}     ///!!!!!!!!!!!!!! modifiy here!!!!!!!!

		/**
		 * This method is called when the user selects the menu item.
		 */
		public void actionPerformed(ActionEvent ae) {

			 String currentDir = new File("").getAbsolutePath();
			 System.out.println("current directory:"+currentDir);
			 String testedGPropPath=currentDir+File.separator+"gProp.txt";
			 System.out.println("gprop fiel path:"+testedGPropPath);
			if(!new File(testedGPropPath).exists()){
				JOptionPane.showMessageDialog(new JFrame(), "please set the parameter in property file- gProp.txt- in "+currentDir);	
				
			}
			try{
				BufferedReader br_propertyFile=new BufferedReader(new FileReader(testedGPropPath));
				String ln_prop;
				while((ln_prop=br_propertyFile.readLine())!=null ){
					if( !ln_prop.startsWith("#")){
						System.out.println(ln_prop);
						String[] lnPropArray=ln_prop.split("=");
						filePathStore.put(lnPropArray[0], lnPropArray[1]);
						System.out.println(lnPropArray[0]+"=>"+lnPropArray[1]);
						//					if(lnPropArray[0].equalsIgnoreCase("recordFolder")){
						//						recordFolderPath=lnPropArray[1];
						//					}
					}
				}
//				bw_messageRecorder=new BufferedWriter(new FileWriter(recordFolderPath+"messageRecorder.txt"));
				bw_messageRecorder=new BufferedWriter(new FileWriter(filePathStore.get("recordFolder")+"messageRecorder.txt"));
				StartStaticVariable.bw_messageRecorder=bw_messageRecorder;
//				String computername=InetAddress.getLocalHost().getHostName();
//				System.out.println(computername);
//				if(computername.equalsIgnoreCase("trevally")){
//					diskSymbol16g="J";
//				}else if(computername.equalsIgnoreCase("uni-afe0b6f3dd0")){
//					diskSymbol16g="d";
//				}else if(computername.equalsIgnoreCase("hangmaolee-38b5")){
//					diskSymbol16g="d";
//				}
			}catch(IOException ex){
				ex.printStackTrace();
			}
			CytoPanelImp ctrlPanel=(CytoPanelImp)Cytoscape.getDesktop().getCytoPanel(SwingConstants.WEST);
			myPanel=new MyPanel();
			
			ctrlPanel.add("ncDocReSy",myPanel);
			int indexInCytoPanel=ctrlPanel.indexOfComponent("myPanel");
			ctrlPanel.setSelectedIndex(indexInCytoPanel);

			
		}

		/**
		 * Gets the canonical name of the given node from the network object
		 * and returns a String holding just the last letter of that name.
		 *
		 * Returns null if a valid name cannot be obtained.
		 */
		//        private String getLastLetter(CyNetwork network, CyNode node) {
		//            String canonicalName = (String)network.getNodeAttributeValue(node, Semantics.CANONICAL_NAME);
		//            //return nothing if we can't get a valid name
		//            if (canonicalName == null || canonicalName.length() == 0) {return null;}
		//            //extract the last letter
		//            int length = canonicalName.length();
		//            String lastLetter = canonicalName.substring(length-1);
		//            return lastLetter;
		//        }
	}
	
	
	
	/**
	 * Gives a description of this plugin.
	 */
	public String describe() {
		StringBuffer sb = new StringBuffer();
		sb.append("Network centric document retrieval system");
		
		
		return sb.toString();
	}

	class MyPanel extends JPanel{
		public MyPanel(){
			super();
//			this.removeAll();
			
//			setLayout(new GridLayout(2,1));
//			setPreferredSize(new Dimension(310,110));
			
			
//			JPanel jpTest=new JPanel();
//			JScrollPane jsTest=new JScrollPane(jpTest);
//			jsTest.setViewportView(jpTest);
//			jsTest.add(jpTest);
//			jsTest.setVerticalScrollBar(new JScrollBar());
//			jsTest.setHorizontalScrollBar(new JScrollBar());
//			JTextArea jta=new JTextArea(500,500);
//			jpTest.add(jta);
//			jpTest.setVisible(true);
//			jsTest.setVisible(true);
//			jsTest.validate();
			String message0="input starting metabolite or ec number";
//			JPanel jpSuperSuper=new JPanel();
			
			
			
//			JPanel jpSuper=new JPanel(new GridLayout(2,1));
//			jpSuper.setPreferredSize(new Dimension(280,900));
//			JScrollPane jscrollPaneSuper=new JScrollPane(jpSuper,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
//			jscrollPaneSuper.setPreferredSize(new Dimension(300,900));
//			jscrollPaneSuper.setViewportView(jpSuper);
//			jpSuperSuper.add(jscrollPaneSuper);
//			this.add(jpSuperSuper);			
//			JScrollPane jscrollPane=new JScrollPane(jpSuper,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
//			JScrollPane jscrollPane=new JScrollPane();
//			jscrollPane.setPreferredSize(new Dimension(300,1000));
//			jscrollPane.add(jpSuper);
			
			//********************** initiate all JPanel
			JPanel jpNetGrowComp=new JPanel(new GridLayout(2,1));
			jpNetGrowComp.setPreferredSize(new Dimension(300,400));
			JPanel jpLiterSearchComp=new JPanel(new GridLayout(2,1));
			jpLiterSearchComp.setBorder(BorderFactory.createTitledBorder("Literature search"));
			
			this.add(jpNetGrowComp);
			this.add(jpLiterSearchComp);		
//			JPanel jpPart1=new JPanel(new GridLayout(7,1));
			JPanel jpPart1=new JPanel(new GridLayout(4,1));
			jpPart1.setPreferredSize(new Dimension(280,460));
			jpNetGrowComp.add(jpPart1);
//			jpPart1.setBorder(BorderFactory.createTitledBorder("Network construction"));
//			JScrollPane jscrollPane1=new JScrollPane(jpPart1,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
//			jscrollPane1.setPreferredSize(new Dimension(320,850));
			//			JScrollPane jscrollPane1=new JScrollPane();
//			jscrollPane1.setHorizontalScrollBar(new JScrollBar());
//			jscrollPane1.setVerticalScrollBar(new JScrollBar());
//			jscrollPane1.setViewportView(jpPart1);
			//			jpPart1.setPreferredSize(new Dimension(300,100));  //for displaying in ibm
			JPanel jpPart2=new JPanel(new GridLayout(4,1));
			jpPart2.setPreferredSize(new Dimension(280,50));
			jpNetGrowComp.add(jpPart2);
			jpNetGrowComp.setBorder(BorderFactory.createTitledBorder("Network construction"));
//			JScrollPane jscrollPane2=new JScrollPane(jpPart2,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
//			jscrollPane2.setPreferredSize(new Dimension(300,200));
			//			JScrollPane jscrollPane2=new JScrollPane();
//			jscrollPane2.setViewportView(jpPart2);
			//			jpPart2.setPreferredSize(new Dimension(300,150)); //for displaying in ibm
			JPanel jpPart3=new JPanel(new GridLayout(3,1));
			jpPart3.setPreferredSize(new Dimension(280,180));
			JPanel jpPart4=new JPanel(new GridLayout(2,1));
			jpPart4.setPreferredSize(new Dimension(280,180));
			jpLiterSearchComp.add(jpPart3);
			jpLiterSearchComp.add(jpPart4);
			
//			JScrollPane jscrollPane3=new JScrollPane(jpPart3,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
//			jscrollPane3.setPreferredSize(new Dimension(300,400));
			//			JScrollPane jscrollPane3=new JScrollPane();
//			jscrollPane3.setViewportView(jpPart3);
			//			jpPart3.setPreferredSize(new Dimension(300,200));  //for displaying in ibm
			JPanel jpEnzymeOrMetabolite=new JPanel(new FlowLayout());
			JPanel jpStartId=new JPanel(new GridLayout(2,1));
			JPanel jpLevel=new JPanel(new FlowLayout());
			JPanel jpRelevantReactionNo=new JPanel(new FlowLayout());
			JPanel jpNetForTaxo=new JPanel(new FlowLayout());
			JPanel jpTissue=new JPanel(new FlowLayout());
			JPanel jpOrganelle=new JPanel(new FlowLayout());
			JPanel jpSavedAsCsml=new JPanel(new FlowLayout());
			JPanel jpImportCsml=new JPanel(new GridLayout(3,1));
			JPanel jpGrowButton=new JPanel(new FlowLayout());
			
			
			JPanel jpGetSpacc=new JPanel(new GridLayout(1,2));
			JPanel jpPpi=new JPanel(new GridLayout(1,2)); //show and remove ppi
			
			JPanel jpPci=new JPanel(new GridLayout(1,2));// for protein-chemical interaction
			
			
			
			JPanel jpHmlParser=new JPanel(new GridLayout(4,1));
//			jp9=new JPanel(new FlowLayout());
//			jp10=new JPanel(new GridLayout(7,1));
			JPanel jpDocReParameter=new JPanel(new GridLayout(3,1)); //for text mining limitation
			JPanel jpQueryTerm=new JPanel(); //for keywork
			jpQueryTerm.setPreferredSize(new Dimension(280,10));
			JPanel jpEuOrBx=new JPanel(new GridLayout(1,5)); //for biotext or eutil
			jpEuOrBx.setPreferredSize(new Dimension(280,10));
			JPanel jpYear=new JPanel(); //for year
			jpYear.setPreferredSize(new Dimension(280,10));
			JPanel jpDocReButtons=new JPanel(new FlowLayout()); //this panel is for text mining buttons
			jpDocReButtons.setPreferredSize(new Dimension(280,30));
			JPanel jpOrganismFilterForProtein=new JPanel(new GridLayout(3,1));  //organism filter
			jpOrganismFilterForProtein.setPreferredSize(new Dimension(5, 3)); 
			JPanel jpRefScoreCalculator=new JPanel();
			jpRefScoreCalculator.setPreferredSize(new Dimension(280,50));
			
//			jf1.setPreferredSize(new Dimension(4000,400));
			
//			jp1.setPreferredSize(new Dimension(60,50));
//			jp2.setPreferredSize(new Dimension(60,50));
			this.setLayout(new FlowLayout());
			
			//************create each elementary jcomponent:jbutton,jlabel,jtextfield
			JLabel jlchooseEnzymeMetabolite=new JLabel("Seed molecular type");
			CheckBoxHandler checkBoxHandler=new CheckBoxHandler();
			jcMetabolite=new JCheckBox("metabolite");
			jcMetabolite.addItemListener(checkBoxHandler);
			jcEnzyme=new JCheckBox("enzyme");
			jcEnzyme.addItemListener(checkBoxHandler);
			ButtonGroup bgMoleType=new ButtonGroup();
			bgMoleType.add(jcMetabolite);
			bgMoleType.add(jcEnzyme);
			
			JLabel jlId=new JLabel("ID(kegg compId or spid)(either case is fine)");
			jlId.setSize(60, 10);
			jTextFieldId=new JTextField();
			jTextFieldId.setColumns(15);
			
			JLabel jlLevel=new JLabel("level to grow");
			String[] listLevel={"1","2","3","4"};
			comboBoxLevel=new JComboBox(listLevel);
			
			jTextFieldReactionNoForEnzyme=new JTextField();
			jTextFieldReactionNoForEnzyme.setColumns(4);
			
			jTextFieldReactionNoForComp=new JTextField();
			jTextFieldReactionNoForComp.setColumns(4);
			
			JLabel jLabelOrganism=new JLabel("Focus on NCBI taxonomy number");
			jTextFieldOrganism=new JTextField();
			jTextFieldOrganism.setColumns(7);
			
			JLabel jLabelTissue=new JLabel("tissue");
			JTextField jTextFieldTissue=new JTextField();
			jTextFieldTissue.setColumns(15);
			
			JLabel jLabelOrganelle=new JLabel("organelle");
			JTextField jTextFieldOrganelle=new JTextField();
			jTextFieldOrganelle.setColumns(15);
			
			JLabel jLabelCsmlPath=new JLabel("csml path");
			jTextFieldCsmlPath=new JTextField();
			jTextFieldCsmlPath.setColumns(15);
			
					
			jbGrow=new JButton("Get related kegg pathway");
			jbGrow.addActionListener(new KeggPathwayQueryHandler());
			
			JLabel jLabelCsmlToCyNet=new JLabel("import CSML file");
			jTextFieldCsmlToCyNet=new JTextField();
			jTextFieldCsmlToCyNet.setColumns(25);
			jbCsmlToCyNet=new JButton("Parse CSML19 to CyNet");
			jbCsmlToCyNet.addActionListener(new CsmlParserHandler());
			
			
			jbGetSpacc=new JButton("Get protein members");
			jbGetSpacc.setPreferredSize(new Dimension(5, 3));
			jbGetSpacc.addActionListener(new AddRelatedSpaccHandler());
			
			jbRelatedSpaccCleaner=new JButton("Remove protein members for selected EC");
			jbRelatedSpaccCleaner.setPreferredSize(new Dimension(5, 3));
			jbRelatedSpaccCleaner.addActionListener(new RemoveRelatedSpaccHandler());
			
			jbShowPpi=new JButton("Show interacting proteins");
			jbShowPpi.setPreferredSize(new Dimension(5, 3));
			jbShowPpi.addActionListener(new ShowPpiHandler());
			
			jbRemovePpi=new JButton("Remove ppi");
			jbRemovePpi.setPreferredSize(new Dimension(5, 3));
			jbRemovePpi.addActionListener(new RemovePpiHandler());
			
			jbShowProteinChemicalInteraction=new JButton("Show protein-chemical interaction");
			jbShowProteinChemicalInteraction.setPreferredSize(new Dimension(5, 3));
			jbShowProteinChemicalInteraction.addActionListener(new ShowProChemInterHandler());
	
			jbRemoveProteinChemicalInteraction=new JButton("Remove protein-chemical interaction");
			jbRemoveProteinChemicalInteraction.setPreferredSize(new Dimension(5, 3));
			jbRemoveProteinChemicalInteraction.addActionListener(new RemoveProChemInterHandler());
			
			JLabel jLabelParseCsml=new JLabel("Parse HML2v1 to CyNet");
//			jTextFieldHml2v1ToParse=new JTextField();
//			jTextFieldHml2v1ToParse.setColumns(25);
			
						
			jbFileBrowser=new JButton("Browse File");
			jbFileBrowser.addActionListener(new FileBrowserHandler());
//			jbParseHml2v1=new JButton("Parse HML2v1 to CyNet");
//			jbParseHml2v1.addActionListener(new Hml2v1ParserHandler());
			
			JLabel jLabelGetRelevantRef=new JLabel("query terms seperated by space");
			jTextFieldKeyword=new JTextField("oxidative stress");
			jTextFieldKeyword.setColumns(20);
			jcBiotext=new JCheckBox("Biotext");
			jcBiotext.addItemListener(checkBoxHandler);
			jcPubmed=new JCheckBox("PubMed");
			jcPubmed.addItemListener(checkBoxHandler);
			jcPmc=new JCheckBox("PMC");
			jcPmc.addItemListener(checkBoxHandler);
			JLabel jLabelYear=new JLabel("Limit to the publication years until:");
			jTextFieldYear=new JTextField();
			jTextFieldYear.setColumns(6);
			
			jbGetRefCount=new JButton("get ref");
//			jbGetRefCount.setPreferredSize(new Dimension(70, 20));
			jbGetRefCount.addActionListener(new GetRefCountHandler());
			jbShowUid=new JButton("Show UIDs");
//			jbShowUid.setPreferredSize(new Dimension(70, 20));
			jbShowUid.addActionListener(new ShowUidInMyPanel());
			jbRefineUidList=new JButton("Refine UID list");
			jbRefineUidList.addActionListener(new RefineUidListHandler());
			
			JLabel jLabelOrganismFilter=new JLabel("Filter proteins by NCBI taxonomy number");
			jTextFieldTaxo=new JTextField();
			jTextFieldTaxo.setColumns(7);
			jbOrganismFilter=new JButton("filter");
			jbOrganismFilter.setPreferredSize(new Dimension(5, 3));
			jbOrganismFilter.addActionListener(new OrganismFilterHandler());
	
//			JLabel jLabelBioEntityDocSum=new JLabel("Sum ref count for selected bioentity");
			
			jbSummarizeLiteSearchResult=new JButton("Summarize literature search result");
			jbSummarizeLiteSearchResult.addActionListener(new SummarizeLiteSearchResultHandler());
			
			jbShowHiddenNode=new JButton("show hidden nodes");
			jbShowHiddenNode.addActionListener(new ShowHiddenNodeHandler());
			
			JLabel jLabelKeywordToConverge=new JLabel("keyword to converge");
			jTextFieldKeywordForConverge=new JTextField("oxidative stress");
			jTextFieldKeywordForConverge.setColumns(10);
			
			jcBrowse=new JCheckBox("Browse");
			jcBrowse.addItemListener(checkBoxHandler);
			jcDiscover=new JCheckBox("Discover");
			jcDiscover.addItemListener(checkBoxHandler);
//			ButtonGroup bgScoreCalculator=new ButtonGroup();
//			bgScoreCalculator.add(jcBrowse);
//			bgScoreCalculator.add(jcDiscover);
			
			JLabel jLabelLayer=new JLabel("layers to cover");
			String[] listLayer={"0","1","2","3","4"};
			comboBoxLayer=new JComboBox(listLayer);
			jbScoreConverger=new JButton("Calculate citation score for all bioentity node");
			jbScoreConverger.addActionListener(new ScoreCalculateHandler());
			
			//ADD ELEMENTARY COMPONENT TO DIFFERENT PANELS
			//*****************************************************************
			jpEnzymeOrMetabolite.add(jlchooseEnzymeMetabolite,new FlowLayout());
			jpEnzymeOrMetabolite.add(jcMetabolite,new FlowLayout());
			jpEnzymeOrMetabolite.add(jcEnzyme,new FlowLayout());
			jpStartId.add(jlId);
			jpStartId.add(jTextFieldId);
			jpLevel.add(jlLevel,new FlowLayout());
			jpLevel.add(comboBoxLevel,new FlowLayout());
			jpRelevantReactionNo.add(new JLabel("No. path for enzyme"));
			jpRelevantReactionNo.add(jTextFieldReactionNoForEnzyme);
			jpRelevantReactionNo.add(new JLabel("No. path for Comp"));
			jpRelevantReactionNo.add(jTextFieldReactionNoForComp);
			jpNetForTaxo.add(jLabelOrganism,new FlowLayout());
			jpNetForTaxo.add(jTextFieldOrganism, new FlowLayout());
			
			jpTissue.add(jLabelTissue,new FlowLayout());
			jpTissue.add(jTextFieldTissue,new FlowLayout());
			jpOrganelle.add(jLabelOrganelle,new FlowLayout());
			jpOrganelle.add(jTextFieldOrganelle,new FlowLayout());
			jpSavedAsCsml.add(jLabelCsmlPath,new FlowLayout());
			jpSavedAsCsml.add(jTextFieldCsmlPath,new FlowLayout());
			
			jpGrowButton.add(jbGrow,new FlowLayout());
			jpImportCsml.add(jLabelCsmlToCyNet);
			jpImportCsml.add(jTextFieldCsmlToCyNet);
			jpImportCsml.add(jbFileBrowser);
			jpImportCsml.add(jbCsmlToCyNet);
			jpGetSpacc.add(jbGetSpacc);
			jpGetSpacc.add(jbRelatedSpaccCleaner);
			jpPpi.add(jbShowPpi);
			jpPpi.add(jbRemovePpi);
			jpPci.add(jbShowProteinChemicalInteraction);
			jpPci.add(jbRemoveProteinChemicalInteraction);
			jpOrganismFilterForProtein.add(jLabelOrganismFilter);
			jpOrganismFilterForProtein.add(jTextFieldTaxo);
			jpOrganismFilterForProtein.add(jbOrganismFilter);
//			jpHmlParser.add(jLabelParseCsml);
//			jpHmlParser.add(jTextFieldHml2v1ToParse);
//			jpHmlParser.add(jbFileBrowser);
//			jpHmlParser.add(jbParseHml2v1);
			jpQueryTerm.add(jLabelGetRelevantRef);
			jpQueryTerm.add(jTextFieldKeyword);
			jpEuOrBx.add(jcBiotext);
			jpEuOrBx.add(jcPubmed);
			jpEuOrBx.add(jcPmc);
			jpYear.add(jLabelYear);
			jpYear.add(jTextFieldYear);

//			jpDocReParameter.add(jpQueryTerm);
//			jpDocReParameter.add(jpEuOrBx);
//			jpDocReParameter.add(jpYear);
			
			jpDocReButtons.add(jbGetRefCount);
//			jp11.add(jbShowUid);
			jpDocReButtons.add(jbRefineUidList);
			jpDocReButtons.add(jbSummarizeLiteSearchResult);
			jpDocReButtons.add(jbShowHiddenNode);
//			jp11.add(jbSumDocToEntity);
//			jp11.add(jbScoreConverger);
//			new BoxLayout(this,BoxLayout.PAGE_AXIS);
			jpRefScoreCalculator.add(jLabelKeywordToConverge);
			jpRefScoreCalculator.add(jTextFieldKeywordForConverge);
			jpRefScoreCalculator.add(jLabelLayer);
			jpRefScoreCalculator.add(comboBoxLayer);
			jpRefScoreCalculator.add(jcBrowse);
			jpRefScoreCalculator.add(jcDiscover);
			jpRefScoreCalculator.add(jbScoreConverger);
			
			
		
			Border blackline=BorderFactory.createLineBorder(Color.black);
			Border raisedBevel=BorderFactory.createRaisedBevelBorder();
			jpEnzymeOrMetabolite.setBorder(raisedBevel);
			jpStartId.setBorder(raisedBevel);
			jpLevel.setBorder(raisedBevel);
			jpRelevantReactionNo.setBorder(raisedBevel);
			jpTissue.setBorder(raisedBevel);
			jpNetForTaxo.setBorder(raisedBevel);
			jpOrganelle.setBorder(raisedBevel);
			jpSavedAsCsml.setBorder(raisedBevel);
			jpGrowButton.setBorder(raisedBevel);
			jpImportCsml.setBorder(raisedBevel);
			jpGetSpacc.setBorder(raisedBevel);
			jpHmlParser.setBorder(raisedBevel);
			jpDocReParameter.setBorder(raisedBevel);
			jpQueryTerm.setBorder(raisedBevel);
			jpEuOrBx.setBorder(raisedBevel);
			jpYear.setBorder(raisedBevel);
			jpDocReButtons.setBorder(raisedBevel);
			jpOrganismFilterForProtein.setBorder(raisedBevel);
			jpPpi.setBorder(raisedBevel);
			jpPci.setBorder(raisedBevel);
			jpRefScoreCalculator.setBorder(raisedBevel);
			
//			jpSuperSuper.setVisible(true);
//			jpSuper.setVisible(true);
			jpEnzymeOrMetabolite.setVisible(true);
			jpStartId.setVisible(true);
			jpLevel.setVisible(true);
			jpTissue.setVisible(true);
			jpOrganelle.setVisible(true);
			jpSavedAsCsml.setVisible(true);
			jpGrowButton.setVisible(true);
			jpImportCsml.setVisible(true);
			jpGetSpacc.setVisible(true);
			jpHmlParser.setVisible(true);
			jpDocReParameter.setVisible(true);
			jpQueryTerm.setVisible(true);
			jpEuOrBx.setVisible(true);
			jpYear.setVisible(true);
			jpDocReButtons.setVisible(true);
			jpOrganismFilterForProtein.setVisible(true);
			jpPpi.setVisible(true);
			jpPci.setVisible(true);
			jpPart1.add(jpEnzymeOrMetabolite);  
			jpPart1.add(jpStartId);  
//			jpPart1.add(jpLevel);   //this option is for the reconstruction method
//			jpPart1.add(jpRelevantReactionNo);
//			jpPart1.add(jpNetForTaxo);   //this option is for the reconstruction method
//			jpPart1.add(jpTissue);  
//			jpPart1.add(jpOrganelle);  
//			jpPart1.add(jpSavedAsCsml);  //this option is for the reconstruction method
			jpPart1.add(jpGrowButton);  
			jpPart1.add(jpImportCsml);
			
//			jpPart2.add(jp9);  //jp9 is for parsing hml
			jpPart2.add(jpGetSpacc);  //jp8 is for getting related spacc
			jpPart2.add(jpPpi); //jp13 is for showing ppi 
			jpPart2.add(jpPci); //jp14 is for showing pci
			jpPart2.add(jpOrganismFilterForProtein); //jp12 is for filtering organism
			
			
			jpPart3.add(jpQueryTerm);
			jpPart3.add(jpEuOrBx);
			jpPart3.add(jpYear);
//			jpPart3.add(jpDocReParameter); //jp10 is for getting text mining parameters->query terms,literature search engine,year
			jpPart4.add(jpDocReButtons); //jp11 is for text mining submitting "buttons"
			jpPart4.add(jpRefScoreCalculator);
			//			jpPart3.add(jbShowHiddenNode);	
//			jpPart3.add(jpRefScoreCalculator);
//			jscrollPane1.setVisible(true);
//			jscrollPane1.setBounds(20,45,55,80);
//			jscrollPane2.setVisible(true);
//			jscrollPane1.setBounds(20,185,305,80);
//			jscrollPane3.setVisible(true);
//			jscrollPane1.setBounds(20,185,305,80);
//			jscrollPaneSuper.setVisible(true);
//			jpSuper.add(jpNetGrowComp,BorderLayout.CENTER);
//			jpSuper.add(jscrollPane2,BorderLayout.CENTER);
//			jpSuper.add(jpLiterSearchComp,BorderLayout.CENTER);
			
//			this.add(jpSuper);
			this.setVisible(true);
			this.updateUI();
			
		}
		
	}
	
	
	

	private class FileBrowserHandler implements ActionListener{

		public void actionPerformed(ActionEvent event){
			
			if(event.getSource()==jbFileBrowser){
				JFileChooser fc=new JFileChooser();
				fc.setCurrentDirectory(new File("."));
				int returnVal = fc.showOpenDialog(myPanel);

				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File file = fc.getSelectedFile();
					//This is where a real application would open the file.
					jTextFieldCsmlToCyNet.setText(file.getPath());
					
				} else {

				}
		    }


		}

	}
	
	private class KeggPathwayQueryHandler implements ActionListener{
		public void actionPerformed(ActionEvent event){
			if(checkBoxMoleType.equals("")){
				JOptionPane.showMessageDialog(new JFrame(), "please select molecular type");
				return;
			}else if(jTextFieldId.getText().equals("")){
				JOptionPane.showMessageDialog(new JFrame(), "please enter a starting id");
				return;
			}
			
			startId=jTextFieldId.getText().trim();
			CyNetwork cyNetwork = Cytoscape.createNetwork("network "+startId, true);
			
			
			CyAttributes nodeAttri=Cytoscape.getNodeAttributes();
			CyAttributes edgeAttri=Cytoscape.getEdgeAttributes();
			vs=new VizMapperBioentityLiteSearchNet1(cyNetwork,nodeAttri,edgeAttri).getVisualStyleBuilder();
			
//			Set<String> pathwayIds=null;
			HashMap<String,String> relatedPathwayForStartId=new HashMap<String,String>();
			
			CyNode startProteinOrMetaboliteNode=null;
		
			// get related pathway id and its commonName
			if(event.getSource().equals(jbGrow)&& checkBoxMoleType.equals("enzyme")){
							
				ArrayList<String> startSpaccS=SpidToSpaccWS.doit(startId);
				if(startSpaccS.size()==0){
					JOptionPane.showMessageDialog(new JFrame(), "no spacc for "+startId+" is found");
					return;
				}
				startProteinOrMetaboliteNode=Cytoscape.getCyNode(startSpaccS.get(0), true);
				
				cyNetwork.addNode(startProteinOrMetaboliteNode);
				
				nodeAttri.setAttribute(startProteinOrMetaboliteNode.getIdentifier(), "mainDbId",startSpaccS.get(0) );
				
				nodeAttri.setAttribute(startProteinOrMetaboliteNode.getIdentifier(), "displayName", startSpaccS.get(0));
				
				nodeAttri.setAttribute(startProteinOrMetaboliteNode.getIdentifier(),"nodeType","startId");
				
				nodeAttri.setAttribute(startProteinOrMetaboliteNode.getIdentifier(),"moleType","protein");
				
//				SpaccToPathwayId stp=new SpaccToPathwayId(startSpaccS.get(0));
//				relatedPathway=stp.getRelatedPathwayIdDigit();
//				pathwayIds=relatedPathway.keySet();
				
//				HashMap<String,String> relatedPathwayForStartId=new HashMap<String,String>();
				SpidToEcWS1 stew1=new SpidToEcWS1(startId);
				ArrayList<String> ecList=stew1.getEc();
				
				for(String ec:ecList){
					HashMap<String,String> relatedPathwayForOneEc=new EcTransformerWS(ec).getRelatedPathwayByKegg();
					relatedPathwayForStartId.putAll(relatedPathwayForOneEc);
				}
				
				
				
				
				
				
//				for(String pathwayId:pathwayIds){
//					
//
//					CyNode pathwayIdNode=Cytoscape.getCyNode(pathwayId, true);
//					
//					cyNetwork.addNode(pathwayIdNode);
//				
//					nodeAttri.setAttribute(pathwayIdNode.getIdentifier(), "pathwayId",pathwayId );
//					
//					nodeAttri.setAttribute(pathwayIdNode.getIdentifier(), "displayName", relatedPathway.get(pathwayId));
//				
//					vs.addProperty(pathwayIdNode.getIdentifier(), VisualPropertyType.NODE_SHAPE, NodeShape.ELLIPSE.getShapeName());
//				
//					vs.addProperty(pathwayIdNode.getIdentifier(),VisualPropertyType.NODE_LABEL,(String)nodeAttri.getAttribute(pathwayIdNode.getIdentifier(), "displayName"));
//
//					JMenuItem menuItem=new JMenuItem("show network for selected pathway");
//					
//					menuItem.addActionListener(new ShowPathwayNetworkHandler());
//					
//					AttributeBrowserPlugin.addMenuItem(browser.DataObjectType.NODES, menuItem);
//				}
			}else if(event.getSource().equals(jbGrow)&& checkBoxMoleType.equals("metabolite")){
				String keggCompId=jTextFieldId.getText().trim();
				startProteinOrMetaboliteNode=Cytoscape.getCyNode(keggCompId.toUpperCase(), true);
				
				cyNetwork.addNode(startProteinOrMetaboliteNode);
				
				nodeAttri.setAttribute(startProteinOrMetaboliteNode.getIdentifier(), "mainDbId",keggCompId );
				
				nodeAttri.setAttribute(startProteinOrMetaboliteNode.getIdentifier(), "displayName", keggCompId);
				
				nodeAttri.setAttribute(startProteinOrMetaboliteNode.getIdentifier(),"nodeType","startId");
				
				nodeAttri.setAttribute(startProteinOrMetaboliteNode.getIdentifier(),"moleType","metabolite");
				
				relatedPathwayForStartId=new KeggCompIdToPathwayId(keggCompId).getRelatedPathwayIdDigit();
			
				

			}
			
			//get related pathway id
			Set<String> pathwayIds=relatedPathwayForStartId.keySet();
			
			
			for(String pathwayId:pathwayIds){
				

				CyNode pathwayIdNode=Cytoscape.getCyNode(pathwayId, true);
				
				cyNetwork.addNode(pathwayIdNode);
			
				nodeAttri.setAttribute(pathwayIdNode.getIdentifier(), "pathwayId",pathwayId );
				
				nodeAttri.setAttribute(pathwayIdNode.getIdentifier(), "displayName", relatedPathwayForStartId.get(pathwayId));
				
				nodeAttri.setAttribute(pathwayIdNode.getIdentifier(),"nodeType","pathwayId");
				
				CyEdge edgeProtein_pathwayId=Cytoscape.getCyEdge(startProteinOrMetaboliteNode, pathwayIdNode, Semantics.INTERACTION, "annotation", true);
				
				cyNetwork.addEdge(edgeProtein_pathwayId);
				
				vs.addProperty(edgeProtein_pathwayId.getIdentifier(), VisualPropertyType.EDGE_LINE_STYLE, LineStyle.LONG_DASH.name());
				
//				vs.addProperty(pathwayIdNode.getIdentifier(), VisualPropertyType.NODE_SHAPE, NodeShape.ELLIPSE.getShapeName());
//				
//				vs.addProperty(pathwayIdNode.getIdentifier(),VisualPropertyType.NODE_LABEL,(String)nodeAttri.getAttribute(pathwayIdNode.getIdentifier(), "displayName"));

//				JMenuItem menuItem=new JMenuItem("show network for the selected pathway");
//
//				menuItem.addActionListener(new ShowPathwayNetworkHandler());
//
//				AttributeBrowserPlugin.addMenuItem(browser.DataObjectType.NODES, menuItem);
			}
			
			MyNodeContextMenuListener l = new MyNodeContextMenuListener();
			Cytoscape.getCurrentNetworkView().addNodeContextMenuListener(l);	
			
//			//Add a component to attribute browser content menu
//			JMenuItem menuItem = new JMenuItem("MyMenuItem_browser");
//
//			AttributeBrowserPlugin.getAttributeBrowser(browser.DataObjectType.NODES).getattributeTable().addMouseListener(new MouseAdapter() {
//				public void mouseClicked(MouseEvent e) {
//					//Remember the object clicked on attribute browser
//					AttributeBrowser attBrowser = AttributeBrowserPlugin.getAttributeBrowser(browser.DataObjectType.NODES);
//					final int column = attBrowser.getColumnModel().getColumnIndexAtX(e.getX());
//					final int row = e.getY() / attBrowser.getattributeTable().getRowHeight();
//					browser_clickedObject = attBrowser.getattributeTable().getValueAt(row, column);
//					browser_RowID = attBrowser.getattributeTable().getValueAt(row, 0);
//					browser_AttriName = attBrowser.getattributeTable().getTableHeader().getColumnModel().getColumn(column).getIdentifier();
//				}
//			});
//			menuItem.addActionListener(new ShowPathwayNetworkHandler());
//
//			AttributeBrowserPlugin.addMenuItem(browser.DataObjectType.NODES, menuItem);
//			MyNodeContextMenuListener l=new MyNodeContextMenuListener();
//			Cytoscape.getCurrentNetworkView().addNodeContextMenuListener(l);
			vs=new VizMapperBioentityLiteSearchNet1(cyNetwork,nodeAttri,edgeAttri).getVisualStyleBuilder();
			vs.buildStyle();
			Cytoscape.firePropertyChange(Cytoscape.ATTRIBUTES_CHANGED, null, null);
			Cytoscape.firePropertyChange(Cytoscape.NETWORK_MODIFIED, null, null);
			Cytoscape.getCurrentNetworkView().redrawGraph(false, true);
		}	
	}
	
	class MyNodeContextMenuListener implements NodeContextMenuListener {
		public void addNodeContextMenuItems(NodeView nodeView, JPopupMenu menu)	{
			JMenuItem myMenuItem = new JMenuItem("show pathway");

			myMenuItem.addActionListener(new ShowPathwayNetworkHandler());
			if (menu == null) {
				menu = new JPopupMenu();
			}
			menu.add(myMenuItem);
		}
	}
	
	private class ShowPathwayNetworkHandler implements ActionListener{
		public void actionPerformed(ActionEvent event){
		
			CyNetwork currentNet=Cytoscape.getCurrentNetwork();

			CyAttributes cyNodeAttr=Cytoscape.getNodeAttributes();
			CyAttributes cyEdgeAttr=Cytoscape.getEdgeAttributes();

			Set<CyNode> selectedPathwayNodeSet=currentNet.getSelectedNodes();
			
			List<String> cyNodeIdList=CyAttributesUtils.getIDListFromAttributeValue(CyAttributesUtils.AttributeType.NODE, "nodeType", "startId");
			
			CyNode startProteinOrMetaboliteNode=Cytoscape.getCyNode(cyNodeIdList.get(0), false);
		
			
			
			for(CyNode oneSelectedPathwayNode:selectedPathwayNodeSet){
				if(cyNodeAttr.hasAttribute(oneSelectedPathwayNode.getIdentifier(), "nodeType")){
					if(cyNodeAttr.getStringAttribute(oneSelectedPathwayNode.getIdentifier(), "nodeType").equals("pathwayId")){
						String pathwayIdDigit=cyNodeAttr.getStringAttribute(oneSelectedPathwayNode.getIdentifier(), "pathwayId");
						
						//01100,01110,01120 are maps, not pathway
						if(pathwayIdDigit.equalsIgnoreCase("01110")|| pathwayIdDigit.equalsIgnoreCase("01120")||pathwayIdDigit.equalsIgnoreCase("01100")){
							String pathwayName=cyNodeAttr.getStringAttribute(oneSelectedPathwayNode.getIdentifier(), "displayName");
							JOptionPane.showMessageDialog(new JFrame(), pathwayName+" refers to a big map. Please select another pathway");
							return;
						}
						
						String csmlOnePathway=new CsmlStringFromPathwayId("rn"+pathwayIdDigit).getCsmlString();
						System.out.println(csmlOnePathway);
//						String fileName="c:\\ncDocReSyCsmlTemp.csml";
						String tempCsmlPath=filePathStore.get("tempCsmlFullPath");
						System.out.println("tempCsmlPath:"+tempCsmlPath);
						try {
							BufferedWriter bw=new BufferedWriter(new FileWriter(tempCsmlPath));
							bw.write(csmlOnePathway);
							bw.flush();
							bw.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
						Csml19ToCyNet5 csmlTocyNet=new Csml19ToCyNet5(tempCsmlPath,currentNet);
						cyNodeAttr=csmlTocyNet.getCyNodeAttribute();
						
						CyNetwork newNetwork=csmlTocyNet.getCyNetwork();
						currentNet=newNetwork;
					}
				}
			}
			
			// now is to add an edge between the startingId node to the ec 
			if(checkBoxMoleType.equalsIgnoreCase("enzyme")){
				ArrayList<String> ecList=new SpidToEcWS1(startId).getEc();
				List<String> cyNodeIdListForEnzyme=CyAttributesUtils.getIDListFromAttributeValue(CyAttributesUtils.AttributeType.NODE, "moleType", "enzyme");
				for(String cyNodeIdForOneEnzyme:cyNodeIdListForEnzyme){
							
					if(ecList.contains(cyNodeAttr.getStringAttribute(cyNodeIdForOneEnzyme, "mainDbId"))){
						CyNode correspondentEcNode=Cytoscape.getCyNode(cyNodeIdForOneEnzyme, false);
						CyEdge newEdge=Cytoscape.getCyEdge(startProteinOrMetaboliteNode, correspondentEcNode,  Semantics.INTERACTION, "annotation", true);
						currentNet.addEdge(newEdge);
					}
				}
			}else if(checkBoxMoleType.equalsIgnoreCase("metabolite")){
				// TO DO
			}
			
			//hide unimportant metabolite nodes
			List<String> metaboliteNodeIdList=CyAttributesUtils.getIDListFromAttributeValue(CyAttributesUtils.AttributeType.NODE, "moleType", "metabolite");
			for(String metaboliteNodeId:metaboliteNodeIdList){
				String keggCompId=(String)cyNodeAttr.getAttribute(metaboliteNodeId, "mainDbId");
				if(new RejectMetabolite().isRejectedMetabolite(keggCompId)){
					currentNet.hideNode(Cytoscape.getCyNode(metaboliteNodeId, false));
				}
			}
			
			VizMapperBioentityLiteSearchNet1 vizmapperemp2=new VizMapperBioentityLiteSearchNet1(currentNet,cyNodeAttr,cyEdgeAttr);
			vs=vizmapperemp2.getVisualStyleBuilder();
			vs.buildStyle();
			Cytoscape.firePropertyChange(Cytoscape.ATTRIBUTES_CHANGED, null, null);
			Cytoscape.getCurrentNetworkView().redrawGraph(false, true);
		}	
		
		
	}
	
//	private class GrowNetHandler implements ActionListener{
//		public void actionPerformed(ActionEvent event){
//			
//			String startId=jTextFieldId.getText().trim();
//
//			String levelConstraint=(String)comboBoxLevel.getSelectedItem();
//
//			String savedCsmlPath=jTextFieldCsmlPath.getText().trim();
//
//			String savedHmlPath=savedCsmlPath.replaceAll(".csml", ".hml");
//
//			String savedNetworkPath=savedCsmlPath.replaceAll(".csml", ".txt");
//			String noOfReactionForEnzyme=jTextFieldReactionNoForEnzyme.getText();
//			String noOfReactionForComp=jTextFieldReactionNoForComp.getText();
//			String taxoUserWant=jTextFieldOrganism.getText();
//			
//			boolean growUpstream=true;
//			
//
//			
////			try{
////				JOptionPane.showMessageDialog(new JFrame(),"in try of GrowNetHandler");
//			if(event.getSource()==jbGrow){
//				
//				// remove the data stored in StartStaticVariable due to the previous run.
//				StartStaticVariable.processedKeggCompId.clear();
//				StartStaticVariable.processedReactionId.clear();
//				
//				
//				// check if user has provided the essential info for network creation.
//				boolean infoInsufficient=false;
//				if(checkBoxMoleType==null){
//					JOptionPane.showMessageDialog(new JFrame(), "please select the molecule type");
//					infoInsufficient=true;
//				}
//				if(startId==null||startId.isEmpty()){
//					JOptionPane.showMessageDialog(new JFrame(), "please type a starting id");
//					infoInsufficient=true;
//				}
//				if(savedCsmlPath==null||savedCsmlPath.isEmpty()){
//					JOptionPane.showMessageDialog(new JFrame(), "please specify the path to save CSML");
//					infoInsufficient=true;
//				}
////				if(noOfReactionForEnzyme==null||noOfReactionForEnzyme.isEmpty()){
////					JOptionPane.showMessageDialog(new JFrame(),"please specify the no. of related reaction for enzyme");
////					infoInsufficient=true;
////				}
////				if(noOfReactionForComp==null||noOfReactionForComp.isEmpty()){
////					JOptionPane.showMessageDialog(new JFrame(),"please specify the no. of related reaction for compound");
////					infoInsufficient=true;
////				}
//				if(taxoUserWant==null|| taxoUserWant.isEmpty()){
//					JOptionPane.showMessageDialog(new JFrame(),"please specify the taxonomy");
//					infoInsufficient=true;
//				}
//				if(infoInsufficient==true){
//					return;
//				}
//				
////				int maxReactionForEc=Integer.parseInt(noOfReactionForEnzyme);
////				
////				int maxReactionForMetabolite=Integer.parseInt(noOfReactionForComp);
//				//				jf1.dispose();
////				jf1.toBack();
////				JOptionPane.showMessageDialog(new JFrame(),"growButton is pressed");
//				String DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss";
//
//				Calendar cal = Calendar.getInstance();
//			    SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
////			    JOptionPane.showMessageDialog(new JFrame(),"growButton is pressed");
//				String startKeggCompId=jTextFieldId.getText();
//				
//				JTaskConfig jTaskConfig=new JTaskConfig();
//				jTaskConfig.setOwner(Cytoscape.getDesktop());
//				jTaskConfig.displayCloseButton(true);
//				jTaskConfig.displayCancelButton(true);
//				jTaskConfig.displayStatus(true);
//				jTaskConfig.setAutoDispose(true);
//				
//				MyTask task=new MyTask(startId,checkBoxMoleType,levelConstraint,savedNetworkPath,savedCsmlPath,savedHmlPath,true,999,999,taxoUserWant);
//				
//				TaskManager.executeTask(task,jTaskConfig);
//
//			}
//				
//		}
//		
//		
//	}
	
//	public class MyTask implements Task{
//		String startId;
//		String moleType;
//		String levelConstraint;
//		String savedNetworkPath;
//		String savedCsmlPath;
//		String savedHmlPath;
//		boolean growUpstream;
//		int maxReactionForEc;
//		int maxReactionForMetabolite;
//		boolean interrupted=false;
//		String taxo;
//		
//		public MyTask(String startId_,String moleType_,String levelConstraint_,String savedNetworkPath_,String savedCsmlPath_,String savedHmlPath_,boolean growUpstream_,int maxReactionForEc_,int maxReactionForMetabolite_,String taxo_){
////			JOptionPane.showMessageDialog(new JFrame(),"in the constructor of "+this.getClass().getName());
//			startId=startId_;
//			moleType=moleType_;
//			levelConstraint=levelConstraint_;
//			savedNetworkPath=savedNetworkPath_;
//			savedCsmlPath=savedCsmlPath_;
//			savedHmlPath=savedHmlPath_;
//			growUpstream=growUpstream_;
//			maxReactionForEc=maxReactionForEc_;
//			maxReactionForMetabolite=maxReactionForMetabolite_;
//			taxo=taxo_;
//		}
//		public void setTaskMonitor(TaskMonitor monitor)throws IllegalThreadStateException{
//			taskMonitor=monitor;
//			StartStaticVariable.taskMonitor=monitor;
//		}
//		
//		public void halt(){
//			this.interrupted=true;
//			System.exit(1);
//		}
//		
//		public String getTitle(){
//			return "progress bar";
//		}
//		
//		public void run(){
//			try{
//				taskMonitor.setStatus("....bbbbpppppppppbbb..");
//
//				taskMonitor.setPercentCompleted(50);
//				
////				JOptionPane.showMessageDialog(new JFrame(), "chosen molecule type is:");
//
//				// startId could be spid(the corresponding ec will be fetched before growing the network) or keggCompId
//				GrowNetEcWSUseStartBufferedWriter21 gn16=new GrowNetEcWSUseStartBufferedWriter21(startId,moleType,levelConstraint,savedNetworkPath,savedCsmlPath,savedHmlPath,true,maxReactionForEc,maxReactionForMetabolite,taxo);
//
//				StartStaticVariable.taskMonitor.setPercentCompleted(70);
//				
//				Hml2v1ToCyNet4 cyNetBuilder=new Hml2v1ToCyNet4(savedHmlPath);
//								
//				StartStaticVariable.taskMonitor.setPercentCompleted(80);
//				
//				CyNetwork cyNetwork=cyNetBuilder.getCyNetwork();
//				CyAttributes cyNodeAttr=Cytoscape.getNodeAttributes();
//				CyAttributes cyEdgeAttributes=Cytoscape.getEdgeAttributes();
//				
//				System.out.println("there are "+cyNetwork.getNodeCount()+" nodes in the network");
//			
//				// if the starting protein or metabolite is not associated with any reaction from kegg, there will be no node displayed in the network panel
//				if(cyNetwork.getNodeCount()==0){   //if there is no network is generated for the protein or metabolite, still add a node representing it in the network panel 
//					
//					if(moleType.equalsIgnoreCase("enzyme")){
//						ArrayList spaccList=SpidToSpaccWS.doit(startId);
//						if(!spaccList.isEmpty()){
//							String spacc=(String)spaccList.get(0);
//							cyNetwork.addNode(Cytoscape.getCyNode(spacc,true));
//							cyNodeAttr.setAttribute(spacc,"moleType","protein");
//							cyNodeAttr.setAttribute(spacc, "displayName", spacc);
//							cyNodeAttr.setAttribute(spacc, "mainDbId", spacc);
//							
//						}else{
//							cyNetwork.addNode(Cytoscape.getCyNode(startId,true));
//							cyNodeAttr.setAttribute(startId,"moleType","protein");
//							cyNodeAttr.setAttribute(startId, "displayName", startId);
//							cyNodeAttr.setAttribute(startId, "mainDbId", startId);
//						}
//					}else if(moleType.equalsIgnoreCase("metabolite")){
//						MetaboliteWS m=new MetaboliteWS(startId);
//						cyNetwork.addNode(Cytoscape.getCyNode(startId,true));
//						cyNodeAttr.setAttribute(startId,"moleType","metabolite");
//						cyNodeAttr.setAttribute(startId, "displayName", (String)m.getName().get(0));
//						cyNodeAttr.setAttribute(startId, "mainDbId", startId);
//					}
//					
//										
//				}else if(cyNetwork.getNodeCount()!=0){  // if a metabolic network is created for the input id
//					//v8 20110525: add the starting node to the constructed metabolic network.
//					// if the starting id is an enzyme, get its spacc
//					if(moleType.equalsIgnoreCase("enzyme")){
//						ArrayList<String> ecList = (new SpidToEcWS1(startId)).getEc();
//						System.out.println("ec:"+ecList.get(0)+" "+this.getClass().getName()+new Exception().getStackTrace()[0].getLineNumber());
//						ArrayList spaccList=SpidToSpaccWS.doit(startId);
//						
//						if(!spaccList.isEmpty()){
//							//spacc of the input id is found
//							String spacc=(String)spaccList.get(0);
//							//create a cynode for the spacc
//							CyNode spaccNodeForStartId=Cytoscape.getCyNode(spacc,true);
//							//add the new node of the starting id to the network
//							cyNetwork.addNode(spaccNodeForStartId);
//							//set attribute for the new node
//							cyNodeAttr.setAttribute(spacc,"moleType","protein");
//							cyNodeAttr.setAttribute(spacc, "displayName", spacc);
//							cyNodeAttr.setAttribute(spacc, "mainDbId", spacc);
//							
//							// now is going to get the ec node which the initial spid belongs to
//							//first is to go through all the node in the network and find the node which's mainDbId(ec) is as same as the ec of the starting spid
//							for(Object x:Cytoscape.getCyNodesList()){
//								String nodeIdentifier=((CyNode)x).getIdentifier();
//								System.out.println("nodeIdentifier:"+nodeIdentifier+" "+this.getClass().getName()+new Exception().getStackTrace()[0].getLineNumber());
//								String mainDbId=(String)cyNodeAttr.getAttribute(nodeIdentifier, "mainDbId");
//								System.out.println("mainDbId:"+mainDbId+" "+this.getClass().getName()+new Exception().getStackTrace()[0].getLineNumber());
//								if(mainDbId!=null && mainDbId.equalsIgnoreCase(ecList.get(0))){
//									System.out.println("ec is:"+ecList.get(0)+" "+this.getClass().getName()+" "+new Exception().getStackTrace()[0].getLineNumber());
//									CyEdge edgeOfStartingSpaccToEc=Cytoscape.getCyEdge((CyNode)x,spaccNodeForStartId ,  Semantics.INTERACTION, "reaction", true);
//									cyNetwork.addEdge(edgeOfStartingSpaccToEc);
//									
//								}
//							}
//							
//							
//							
//						}else{// if no metabolic network is created for the input spid and no spacc is found for this spid
//							cyNetwork.addNode(Cytoscape.getCyNode(startId,true));
//							cyNodeAttr.setAttribute(startId,"moleType","protein");
//							cyNodeAttr.setAttribute(startId, "displayName", startId);
//							cyNodeAttr.setAttribute(startId, "mainDbId", startId);
//						}
//					}
//					//v108 20110627: mark out this part
////					else if(moleType.equalsIgnoreCase("metabolite")){
////						MetaboliteWS m=new MetaboliteWS(startId);
////						
//////						CyNode startingMetaboliteNode=Cytoscape.getCyNode(startId,false);
////						cyNodeAttr.setAttribute(startId,"moleType","metabolite");
////						cyNodeAttr.setAttribute(startId, "displayName", (String)m.getName().get(0));
////						cyNodeAttr.setAttribute(startId, "mainDbId", startId);
////					}
//					
//					
//				}
//				VizMapperBioentityLiteSearchNet1 vizMapperEmp2=new VizMapperBioentityLiteSearchNet1(cyNetwork,cyNodeAttr,cyEdgeAttributes);
//				vs=vizMapperEmp2.getVisualStyleBuilder();
//	
//				//v108 20110627: set the node color of the starting metabolite to green
//				if(moleType.equalsIgnoreCase("metabolite")){
//					CyNode startingMetaboliteNode=Cytoscape.getCyNode(startId.toUpperCase(),false);
//					vs.addProperty(startingMetaboliteNode.getIdentifier(),VisualPropertyType.NODE_BORDER_COLOR,"#00FF00");
//				}
//				
//				StartStaticVariable.taskMonitor.setPercentCompleted(90);
//				vs.buildStyle();
//				//				GlobalAppearanceCalculator gac=Cytoscape.getVisualMappingManager().getVisualStyle().getGlobalAppearanceCalculator();
//				StartStaticVariable.taskMonitor.setPercentCompleted(100);
//				Cytoscape.firePropertyChange(Cytoscape.ATTRIBUTES_CHANGED, null, null);
//				Cytoscape.getCurrentNetworkView().redrawGraph(false, true);
//
//				
//				//for test the catch statement--------
//				if(startId.equalsIgnoreCase("xx")){
//					  throw new IOException("This is a Fake IO Exception");
//
//				}
//				
//				// export the create cynetwork to a cytoscape session file
//				
//				String sessionName="";
//				String[] csmlPathArray=savedCsmlPath.split("\\\\");
//				sessionName=csmlPathArray[csmlPathArray.length-1].replaceAll("\\.csml", "");
//				CytoscapeSessionWriter sessionWriter=new CytoscapeSessionWriter(sessionName);
//				try {
//					sessionWriter.writeSessionToDisk();
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//				
//				
//				
//	        }catch (IOException e) {
//	            taskMonitor.setException(e, "Counting aborted by fake exception");
//	        }
//
//
//		}
//	}
	public class CheckBoxHandler implements ItemListener{
		public void itemStateChanged(ItemEvent e){
			Object source = e.getSource();
			
			if(source==jcMetabolite ){
				if(jcMetabolite.isSelected()){
					checkBoxMoleType="metabolite";
					
				}
//				else if(!jcMetabolite.isSelected()){
//					checkBoxMoleType="";
//				}
			}else if(source==jcEnzyme ){
				if(jcEnzyme.isSelected()){
					checkBoxMoleType="enzyme";
					
				}
//				else if(!jcEnzyme.isSelected()){
//					checkBoxMoleType="";
//				}
			}
			if(source==jcBiotext){
				if(jcBiotext.isSelected()){
					useBiotext=true;
					System.out.println("biotext is selected");
				}else if(!jcBiotext.isSelected()){
					useBiotext=false;
				}
			}
			if(source==jcPubmed){
				if(jcPubmed.isSelected()){
					usePubmed=true;
					System.out.println("pubmed is selected");
				}else if(!jcPubmed.isSelected()){
					usePubmed=false;
				}	
			}
			if(source==jcPmc){
				if(jcPmc.isSelected()){
					usePmc=true;
					System.out.println("pmc is selected");
				}else if(!jcPmc.isSelected()){
					usePmc=false;
				}	
			}
			if(source==jcBrowse){
				if(jcBrowse.isSelected()){
					calculateBrowseScore=true;
//					calculateDiscoverScore=false;
					System.out.println("browse score mode is selected");
				}else{
					calculateBrowseScore=false;
				}
			}
			if(source==jcDiscover){
				if(jcDiscover.isSelected()){
					calculateDiscoverScore=true;
//					calculateBrowseScore=false;
					System.out.println("discover score mode is selected");
				}else{
					calculateDiscoverScore=false;
				}
			}
		}
		
	}
	
	
	public class CsmlParserHandler implements ActionListener{
//	public class Hml2v1ParserHandler implements ActionListener{
		public void actionPerformed(ActionEvent event){
			if(event.getSource()==jbCsmlToCyNet){
//			if(event.getSource()==jbParseHml2v1){
//				String hml2v1Path=jTextFieldCsmlToCyNet.getText();
//				Hml2v1ToCyNet2 cyNetBuilder=new Hml2v1ToCyNet2(hml2v1Path);
				String csmlPath=jTextFieldCsmlToCyNet.getText();
				System.out.println("csml file path:"+csmlPath);
				Csml19ToCyNet5 cyNetBuilder=new Csml19ToCyNet5(csmlPath);
				CyNetwork cyNetwork=cyNetBuilder.getCyNetwork();
				
				CyAttributes cyNodeAttr=Cytoscape.getNodeAttributes();
				CyAttributes cyEdgeAttributes=Cytoscape.getEdgeAttributes();

				VizMapperBioentityLiteSearchNet1 vizMapperEmp2=new VizMapperBioentityLiteSearchNet1(cyNetwork,cyNodeAttr,cyEdgeAttributes);
				vs=vizMapperEmp2.getVisualStyleBuilder();
				vs.buildStyle();
				
				GlobalAppearanceCalculator gac=Cytoscape.getVisualMappingManager().getVisualStyle().getGlobalAppearanceCalculator();
				
				Cytoscape.firePropertyChange(Cytoscape.ATTRIBUTES_CHANGED, null, null);
				Cytoscape.getCurrentNetworkView().redrawGraph(false, true);
			}
			
			
		}
		
	}
	
	
	public class AddRelatedSpaccHandler implements ActionListener{
		public void actionPerformed(ActionEvent event){
		
			if(event.getSource()==jbGetSpacc){
		
				CyNetwork currentNet=Cytoscape.getCurrentNetwork();
				
				CyAttributes cyNodeAttr=Cytoscape.getNodeAttributes();
				CyAttributes cyEdgeAttr=Cytoscape.getEdgeAttributes();
				
				
				Set selectedNodeSet=currentNet.getSelectedNodes();
				double xIncrement=50;
				double yIncrement=50;
				for(Object oneCyNode:selectedNodeSet){
					double xParentNode=Cytoscape.getCurrentNetworkView().getNodeView((CyNode)oneCyNode).getXPosition();
					double yParentNode=Cytoscape.getCurrentNetworkView().getNodeView((CyNode)oneCyNode).getYPosition();
					double newNodeX=xParentNode+xIncrement;
					double newNodeY=yParentNode+yIncrement;
					String moleTypeOfNode=cyNodeAttr.getStringAttribute(((CyNode)oneCyNode).getIdentifier(), "moleType");
					
//					JOptionPane.showMessageDialog(new JFrame(), "identifier of one selected node is:"+((CyNode)oneCyNode).getIdentifier());
					if(moleTypeOfNode.equalsIgnoreCase("enzyme")){
						String ec=cyNodeAttr.getStringAttribute(((CyNode)oneCyNode).getIdentifier(), "mainDbId");
//						JOptionPane.showMessageDialog(new JFrame(), "ec of one selected node is "+ec);
						EcToSpaccWS spaccBrewer=new EcToSpaccWS(ec);
						ArrayList relatedSpacc=spaccBrewer.getSpacc();
						for(Object spacc:relatedSpacc){
//							JOptionPane.showMessageDialog(new JFrame(), "one spacc for the ec is:"+spacc);
							System.out.println("one spacc for the ec is:"+spacc);
							String taxoUserWant=jTextFieldTaxo.getText();
							System.out.println("taxoUserWant:"+taxoUserWant);
							Boolean isOrganism=false;
							Boolean organismUnspecific=false;
							if(!taxoUserWant.equalsIgnoreCase("")){
								OrganismFilter3 of1=new OrganismFilter3((String)spacc,"spacc",taxoUserWant);
								isOrganism=of1.isOrganism();
								ArrayList taxoList=of1.getTaxonomyList();
							}else{
								organismUnspecific=true;
//								JOptionPane.showMessageDialog(new JFrame(), "Please specify one organism");
//								return;
							}
							//							JOptionPane.showMessageDialog(new JFrame(), (String)(taxoList.get(0)));
							
							// only the spacc which belongs to the use-specified taxo will be added to cynetwork
							if(isOrganism||organismUnspecific){
								CyNode nodeSpacc=Cytoscape.getCyNode((String)spacc,true);
								//							nodeSpacc.setIdentifier((String)spacc);
								CyEdge edge=Cytoscape.getCyEdge((CyNode)oneCyNode, nodeSpacc, Semantics.INTERACTION, "encode", true);

								//add node and edge to the network
								currentNet.addNode(nodeSpacc); //new node has to be added to the net before setting its coordinate
								currentNet.addEdge(edge);  //new node has to be added to the net before setting its coordinate
								//set node shape, color, label
								vs.addProperty(nodeSpacc.getIdentifier(),VisualPropertyType.NODE_SHAPE, NodeShape.DIAMOND.getShapeName());
								vs.addProperty(nodeSpacc.getIdentifier(),VisualPropertyType.NODE_BORDER_COLOR, "#00FF00");
								vs.addProperty(nodeSpacc.getIdentifier(),VisualPropertyType.NODE_LABEL,nodeSpacc.getIdentifier());


								// set the coordinate of new node
								Cytoscape.getCurrentNetworkView().getNodeView(nodeSpacc).setXPosition(newNodeX);
								Cytoscape.getCurrentNetworkView().getNodeView(nodeSpacc).setYPosition(newNodeY);
								newNodeX=newNodeX+xIncrement;  //the new node will be in a row, so y coordinate doen't need to be changed.
								//							newNodeY=newNodeY+yIncrement;

								//set attribute for the new node
								cyNodeAttr.setAttribute(nodeSpacc.getIdentifier(), "moleType", "protein");
								cyNodeAttr.setAttribute(nodeSpacc.getIdentifier(), "mainDbId", nodeSpacc.getIdentifier());
								cyNodeAttr.setAttribute(nodeSpacc.getIdentifier(), "displayName", nodeSpacc.getIdentifier());
								//set attribute for the new edge
								cyEdgeAttr.setAttribute(edge.getIdentifier(),"interaction" ,"encode" );
							}
						}
					}

				}
			
				vs.buildStyle();
				
				GlobalAppearanceCalculator gac=Cytoscape.getVisualMappingManager().getVisualStyle().getGlobalAppearanceCalculator();
//				Cytoscape.firePropertyChange(Cytoscape.VIZMAP_LOADED,null,null);
//				Cytoscape.firePropertyChange(Cytoscape.ATTRIBUTES_CHANGED, null, null);
				Cytoscape.getCurrentNetworkView().redrawGraph(false, false);
				
			}
			
			
		}
	} 
	
	public class RemoveRelatedSpaccHandler implements ActionListener{
		public void actionPerformed(ActionEvent event){
			ArrayList nodeToHide=new ArrayList();
			ArrayList edgeToHide=new ArrayList();
			if(event.getSource()==jbRelatedSpaccCleaner){
				CyNetwork currentNet=Cytoscape.getCurrentNetwork();
				CyAttributes cyEdgeAttr=Cytoscape.getEdgeAttributes();
				Set selectedNodeSet=currentNet.getSelectedNodes();
				Iterator iteratorNodeSet=selectedNodeSet.iterator();
				while(iteratorNodeSet.hasNext()){
					CyNode nextNode=(CyNode)iteratorNodeSet.next();
					currentNet.selectAllEdges();
					Set allEdgeSet=currentNet.getSelectedEdges();
					Iterator iteratorAllEdgeSet=allEdgeSet.iterator();
					
					while(iteratorAllEdgeSet.hasNext()){
						
						CyEdge nextEdge=(CyEdge)iteratorAllEdgeSet.next();
						if(cyEdgeAttr.getAttribute(((CyEdge)nextEdge).getIdentifier(), "interaction")=="encode"){
							
							if(((CyEdge)nextEdge).getSource().equals(nextNode)){
								CyNode targetNode=(CyNode)((CyEdge)nextEdge).getTarget();
								nodeToHide.add(targetNode);
								edgeToHide.add((CyEdge)nextEdge);

							}
						}
					}
				}
				for(Object x:nodeToHide){
					currentNet.hideNode((CyNode)x);
				}
				for(Object y:edgeToHide){
					currentNet.hideEdge((CyEdge)y);
				}
				currentNet.unselectAllEdges();
				Cytoscape.getCurrentNetworkView().redrawGraph(false, true);
				
				
			}
		}
			
	}
	
	
	public class ShowPpiHandler implements ActionListener{
		public void actionPerformed(ActionEvent event){
			
			if(event.getSource()==jbShowPpi){
				System.out.println("jbshow ppi is pressed");
				CyNetwork currentNet=Cytoscape.getCurrentNetwork();

				CyAttributes cyNodeAttr=Cytoscape.getNodeAttributes();
				CyAttributes cyEdgeAttr=Cytoscape.getEdgeAttributes();
				
				Set selectedNodeSet=currentNet.getSelectedNodes();
			
				double xIncrement=50;
				double yIncrement=50;
				
				for(Object baitNode:selectedNodeSet){
					System.out.println("one of the selected nodes is:"+((CyNode)baitNode).getIdentifier()+"/"+this.getClass().getName());
					System.out.println("the attribute of moleType is:"+cyNodeAttr.getStringAttribute(((CyNode)baitNode).getIdentifier(), "moleType")+"/"+this.getClass().getName());
					if(cyNodeAttr.hasAttribute(((CyNode)baitNode).getIdentifier(), "moleType")){
						String moleTypeString=cyNodeAttr.getStringAttribute(((CyNode)baitNode).getIdentifier(), "moleType");
						if(moleTypeString.equals("protein")){
							double xParentNode=Cytoscape.getCurrentNetworkView().getNodeView((CyNode)baitNode).getXPosition();
							double yParentNode=Cytoscape.getCurrentNetworkView().getNodeView((CyNode)baitNode).getYPosition();
							double newNodeX=xParentNode+xIncrement;
							double newNodeY=yParentNode+yIncrement;
							
							String mainDbId=cyNodeAttr.getStringAttribute(((CyNode)baitNode).getIdentifier(), "mainDbId");
							System.out.println("one of the node is protein and the mainDbId is: "+mainDbId);
							IntActAgent inta=new IntActAgent(mainDbId,"spacc",false);
							System.out.println("After IntActAgent");
							ParseIntActREST pir=new ParseIntActREST(inta.getResultInBufferedReader(),mainDbId);
							System.out.println("After ParseIntActREST");
							ArrayList interactStore=pir.getInteractPairs();
							
							for(Object interactPair:interactStore){
								String spaccCounterPart="";
								if(((InteractPair)interactPair).getIdA().equalsIgnoreCase(mainDbId)){
									spaccCounterPart=((InteractPair)interactPair).getIdB();
								
								}else if(((InteractPair)interactPair).getIdB().equalsIgnoreCase(mainDbId)){
									spaccCounterPart=((InteractPair)interactPair).getIdA();
									
								}
								// create node 
								CyNode preyNode=Cytoscape.getCyNode(spaccCounterPart, true);
								currentNet.addNode(preyNode);
								
								// add node attribute
								cyNodeAttr.setAttribute(preyNode.getIdentifier(), "moleType", "protein");
								cyNodeAttr.setAttribute(preyNode.getIdentifier(), "mainDbId", preyNode.getIdentifier());
								cyNodeAttr.setAttribute(preyNode.getIdentifier(), "displayName", preyNode.getIdentifier());
								// set the coordinate of new node
								Cytoscape.getCurrentNetworkView().getNodeView(preyNode).setXPosition(newNodeX);
								Cytoscape.getCurrentNetworkView().getNodeView(preyNode).setYPosition(newNodeY);
								newNodeX=newNodeX+xIncrement;  //the new node will be in a row, so y coordinate doen't need to be changed.
								
								// create edge and add edge attribute
								CyEdge baitPreyEdge=Cytoscape.getCyEdge((CyNode)baitNode, preyNode, Semantics.INTERACTION, "ppi", true);
								cyEdgeAttr.setAttribute(baitPreyEdge.getIdentifier(),"interaction", "ppi");
								currentNet.addEdge(baitPreyEdge);
								
								// specify the visual style of node and edge
								vs.addProperty(preyNode.getIdentifier(),VisualPropertyType.NODE_SHAPE, NodeShape.DIAMOND.getShapeName());
								vs.addProperty(preyNode.getIdentifier(),VisualPropertyType.NODE_BORDER_COLOR, "#00FF00"); // #00FF00 is green
								vs.addProperty(preyNode.getIdentifier(), VisualPropertyType.NODE_LABEL, cyNodeAttr.getStringAttribute(preyNode.getIdentifier(), "displayName"));
								vs.addProperty(baitPreyEdge.getIdentifier(), VisualPropertyType.EDGE_LINE_STYLE, "SOLID");
							
							}
						}
					}
				}
				// if VizMapperEMP is called, it will first remove old visual style and apply a new style coded in VizMapperEMP
//				VizMapperEMP newVizMapper=new VizMapperEMP(currentNet,Cytoscape.getNodeAttributes());
//				
//				vs=newVizMapper.getVisualStyleBuilder();
				
				vs.buildStyle();
				
				GlobalAppearanceCalculator gac=Cytoscape.getVisualMappingManager().getVisualStyle().getGlobalAppearanceCalculator();
//				Cytoscape.firePropertyChange(Cytoscape.VIZMAP_LOADED,null,null);
//				Cytoscape.firePropertyChange(Cytoscape.ATTRIBUTES_CHANGED, null, null);
				Cytoscape.getCurrentNetworkView().redrawGraph(false, true);
			}
		}
		
		
	}
	
	public class RemovePpiHandler implements ActionListener{
		public void actionPerformed(ActionEvent event){
			ArrayList nodeToHide=new ArrayList();
			ArrayList edgeToHide=new ArrayList();
			if(event.getSource()==jbRemovePpi){
				CyNetwork currentNet=Cytoscape.getCurrentNetwork();
				CyAttributes cyEdgeAttr=Cytoscape.getEdgeAttributes();
				Set selectedNodeSet=currentNet.getSelectedNodes();
				Iterator iteratorNodeSet=selectedNodeSet.iterator();
				while(iteratorNodeSet.hasNext()){
					CyNode nextNode=(CyNode)iteratorNodeSet.next();
					currentNet.selectAllEdges();
					Set allEdgeSet=currentNet.getSelectedEdges();
					Iterator iteratorAllEdgeSet=allEdgeSet.iterator();
					
					while(iteratorAllEdgeSet.hasNext()){
						
						CyEdge nextEdge=(CyEdge)iteratorAllEdgeSet.next();
						if(cyEdgeAttr.getAttribute(((CyEdge)nextEdge).getIdentifier(), "interaction")=="ppi"){
							
							if(((CyEdge)nextEdge).getSource().equals(nextNode)){
								CyNode targetNode=(CyNode)((CyEdge)nextEdge).getTarget();
								nodeToHide.add(targetNode);
								edgeToHide.add((CyEdge)nextEdge);

							}
						}
					}
				}
				for(Object x:nodeToHide){
					currentNet.hideNode((CyNode)x);
				}
				for(Object y:edgeToHide){
					currentNet.hideEdge((CyEdge)y);
				}
				currentNet.unselectAllEdges();
				Cytoscape.getCurrentNetworkView().redrawGraph(false, false); //if the second argument is true, visualStyle will be reset
				
				
			}
			
		}
	}
	
	public class ShowProChemInterHandler implements ActionListener{
		public void actionPerformed(ActionEvent event){	
			if(event.getSource()==jbShowProteinChemicalInteraction){

				System.out.println("Show Protein-Chemical Interaction button is pressed");
				CyNetwork currentNet=Cytoscape.getCurrentNetwork();

				CyAttributes cyNodeAttr=Cytoscape.getNodeAttributes();
				CyAttributes cyEdgeAttr=Cytoscape.getEdgeAttributes();
				
				Set selectedNodeSet=currentNet.getSelectedNodes();
			
				double xIncrement=50;
				double yIncrement=50;
				
				for(Object baitProteinNode:selectedNodeSet){
					System.out.println("one of the selected nodes is:"+((CyNode)baitProteinNode).getIdentifier()+"/"+this.getClass().getName());
					System.out.println("the attribute of moleType is:"+cyNodeAttr.getStringAttribute(((CyNode)baitProteinNode).getIdentifier(), "moleType")+"/"+this.getClass().getName());
					if(cyNodeAttr.hasAttribute(((CyNode)baitProteinNode).getIdentifier(), "moleType")){
						String moleTypeString=cyNodeAttr.getStringAttribute(((CyNode)baitProteinNode).getIdentifier(), "moleType");
						if(moleTypeString.equals("protein")){
							double xParentNode=Cytoscape.getCurrentNetworkView().getNodeView((CyNode)baitProteinNode).getXPosition();
							double yParentNode=Cytoscape.getCurrentNetworkView().getNodeView((CyNode)baitProteinNode).getYPosition();
							double newNodeX=xParentNode+xIncrement;
							double newNodeY=yParentNode+yIncrement;
							
							String mainDbId=cyNodeAttr.getStringAttribute(((CyNode)baitProteinNode).getIdentifier(), "mainDbId");
							System.out.println("one of the node is protein and the mainDbId is: "+mainDbId);
							OrganismFilter3 organismFilter=new OrganismFilter3(mainDbId,"spacc","9606");
							if(organismFilter.isOrganism()==true){
								ProteinChemicalInteractionAgent pcInteractionAgent=new ProteinChemicalInteractionAgent(mainDbId,"9606");
								System.out.println("After ProteinChemicalInteractionAgent");
								ArrayList cidStore=pcInteractionAgent.getChemicalsFilteredByEvidence(20, 20, 3000);
								
								System.out.println("After filtering by evidence");
								
								for(Object preyCid:cidStore){
										
									// create node 
									CyNode preyCidNode=Cytoscape.getCyNode((String)preyCid, true);
									currentNet.addNode(preyCidNode);

									// add node attribute
									cyNodeAttr.setAttribute(preyCidNode.getIdentifier(), "moleType", "compound");
									cyNodeAttr.setAttribute(preyCidNode.getIdentifier(), "mainDbId", preyCidNode.getIdentifier());

									// set the coordinate of new node
									Cytoscape.getCurrentNetworkView().getNodeView(preyCidNode).setXPosition(newNodeX);
									Cytoscape.getCurrentNetworkView().getNodeView(preyCidNode).setYPosition(newNodeY);
									newNodeX=newNodeX+xIncrement;  //the new node will be in a row, so y coordinate doen't need to be changed.

									// create edge and add edge attribute
									CyEdge proteinCidEdge=Cytoscape.getCyEdge((CyNode)baitProteinNode, preyCidNode, Semantics.INTERACTION, "pci", true);
									cyEdgeAttr.setAttribute(proteinCidEdge.getIdentifier(),"interaction", "pci");
									currentNet.addEdge(proteinCidEdge);

									// specify the visual style of node and edge
									vs.addProperty(preyCidNode.getIdentifier(),VisualPropertyType.NODE_SHAPE, NodeShape.DIAMOND.getShapeName());
									vs.addProperty(preyCidNode.getIdentifier(),VisualPropertyType.NODE_BORDER_COLOR, "#800080"); // #800080 is purple
									vs.addProperty(preyCidNode.getIdentifier(), VisualPropertyType.NODE_LABEL, preyCidNode.getIdentifier());
									vs.addProperty(proteinCidEdge.getIdentifier(), VisualPropertyType.EDGE_LINE_STYLE, "SOLID");

								}
							}else{
								JOptionPane.showMessageDialog(new JFrame(), ((CyNode)baitProteinNode).getIdentifier()+" is not human protein. Now only human protein is supported.");
							}
						}
					}
				}
				// if VizMapperEMP is called, it will first remove old visual style and apply a new style coded in VizMapperEMP
//				VizMapperEMP newVizMapper=new VizMapperEMP(currentNet,Cytoscape.getNodeAttributes());
//				vs=newVizMapper.getVisualStyleBuilder();
				
				vs.buildStyle();
				
				GlobalAppearanceCalculator gac=Cytoscape.getVisualMappingManager().getVisualStyle().getGlobalAppearanceCalculator();
//				Cytoscape.firePropertyChange(Cytoscape.VIZMAP_LOADED,null,null);
//				Cytoscape.firePropertyChange(Cytoscape.ATTRIBUTES_CHANGED, null, null);
				Cytoscape.getCurrentNetworkView().redrawGraph(false, true);
			
			
			
			
			
			}
		}
	}

	public class RemoveProChemInterHandler implements ActionListener{
		public void actionPerformed(ActionEvent event){
			ArrayList nodeToHide=new ArrayList();
			ArrayList edgeToHide=new ArrayList();
			if(event.getSource()==jbRemoveProteinChemicalInteraction){
				CyNetwork currentNet=Cytoscape.getCurrentNetwork();
				CyAttributes cyEdgeAttr=Cytoscape.getEdgeAttributes();
				Set selectedNodeSet=currentNet.getSelectedNodes();
				Iterator iteratorNodeSet=selectedNodeSet.iterator();
				while(iteratorNodeSet.hasNext()){
					CyNode nextNode=(CyNode)iteratorNodeSet.next();
					currentNet.selectAllEdges();
					Set allEdgeSet=currentNet.getSelectedEdges();
					Iterator iteratorAllEdgeSet=allEdgeSet.iterator();
					
					while(iteratorAllEdgeSet.hasNext()){
						
						CyEdge nextEdge=(CyEdge)iteratorAllEdgeSet.next();
						if(cyEdgeAttr.getAttribute(((CyEdge)nextEdge).getIdentifier(), "interaction")=="pci"){
							
							if(((CyEdge)nextEdge).getSource().equals(nextNode)){
								CyNode targetNode=(CyNode)((CyEdge)nextEdge).getTarget();
								nodeToHide.add(targetNode);
								edgeToHide.add((CyEdge)nextEdge);

							}
						}
					}
				}
				for(Object x:nodeToHide){
					currentNet.hideNode((CyNode)x);
				}
				for(Object y:edgeToHide){
					currentNet.hideEdge((CyEdge)y);
				}
				currentNet.unselectAllEdges();
				Cytoscape.getCurrentNetworkView().redrawGraph(false, false); //if the second argument is true, visualstyle will be reset
				
				
			}
			
		}
	}
	
	//v14 20110608: use CommonNameRootedTreeProducer1 to allow multiple keyword for biotext and eutil.
	//v20 20110621: use CommonNameRootedTreeProducer2 
	//v109_ 20110703: allow enzyme node(ec node) being selected for literature search
	//v118 20110724: use CommonNameRootedTreeProducer4
	public class GetRefCountHandler implements ActionListener{
		
		public void actionPerformed(ActionEvent event){

			boolean useDate=false;
			String yearLimit="";
			
			if(event.getSource()==jbGetRefCount){
				if(!jTextFieldYear.getText().isEmpty()){
					String year=jTextFieldYear.getText().trim();
					if(!year.matches("\\d{4}")){
						JOptionPane.showMessageDialog(new JFrame(), "please input the correct format of year, like 1997");
						return;
					}else{
						useDate=true;
						yearLimit=year;
					}
				}
				if(useBiotext==false && usePubmed==false && usePmc==false){
					JOptionPane.showMessageDialog(new JFrame(), "please select at least one docuemnt retrieval tool");

				}else{
					System.out.println("useBioText is:"+useBiotext+" NcDocReSy117");
					System.out.println("usePubmed is:"+usePubmed+" NcDocReSy117");
					System.out.println("usePmc is:"+usePmc+" NcDocReSy117");
					String originalKeywordString=jTextFieldKeyword.getText().trim();
//					String[] keywordArray=keywordString.split(",");
//					ArrayList keywordList=new ArrayList<String>();
//					for(String x:keywordArray){
//						keywordList.add(x);
//					}
					//				JOptionPane.showMessageDialog(new JFrame(), "in the constructor of GetRefCountHandler");
					CyNetwork currentNet=Cytoscape.getCurrentNetwork();

					CyAttributes cyNodeAttr=Cytoscape.getNodeAttributes();
					CyAttributes cyEdgeAttr=Cytoscape.getEdgeAttributes();

					Set selectedNodeSet=currentNet.getSelectedNodes();
					double xIncrement=50;
					double yIncrement=50;
					
					// save the ids of selected nodes into an array.THis is to overcome the problem of "ConcurrentModificationException" when using Set and Iterator
					ArrayList selectedNodeIdList=new ArrayList(); 
					for(Object s:selectedNodeSet){
						selectedNodeIdList.add(((CyNode)s).getIdentifier());
					}
					

					for(Object oneSelectedNodeId:selectedNodeIdList){						
						// oneSelectedNodeId should be the ID of swissprot node or metabolite node
						CyNode oneCyNode=Cytoscape.getCyNode((String)oneSelectedNodeId,false);

						//					JOptionPane.showMessageDialog(new JFrame(),"one selected node is:"+((CyNode)oneCyNode).getIdentifier());
						String moleTypeOfNode=cyNodeAttr.getStringAttribute(((CyNode)oneCyNode).getIdentifier(), "moleType");
						String nodeType=cyNodeAttr.getStringAttribute(((CyNode)oneCyNode).getIdentifier(), "nodeType");
						//					JOptionPane.showMessageDialog(new JFrame(),"moleType of one selected node is:"+moleTypeOfNode);
						System.out.println(moleTypeOfNode);
						
						if(moleTypeOfNode!=null && moleTypeOfNode.equalsIgnoreCase("enzyme")){
							//						JOptionPane.showMessageDialog(new JFrame(),"moleType is protein");
							String mainDbId=cyNodeAttr.getStringAttribute(((CyNode)oneCyNode).getIdentifier(), "mainDbId");
							System.out.println(mainDbId);
							EcTransformerWS ecTrans=new EcTransformerWS(mainDbId);
							ArrayList<String> commonNameStore=ecTrans.getCommonNameByKegg();
							
							//it is possible that no common name is gotten from brenda table, like 1.8.1.3	
							// brenda will return lots of synonyms. some of them is used rarely.
//							if(commonNameStore.size()==0){
//								commonNameStore=ecTrans.getCommonNameByBrenda();
//							}
							
							if(commonNameStore!=null && commonNameStore.size()>0){
								for(String oneName:commonNameStore){
									System.out.println("one name is:"+oneName);
									if(oneName.length()<6){
										continue;
									}
									//create new node and add to the net// oneCyNode here is an EC node
									CommonNameRootedTreeProducer7 cnrtp=new CommonNameRootedTreeProducer7(oneCyNode,(String)oneName, xIncrement,yIncrement,vs,originalKeywordString,useDate,useBiotext,usePubmed,yearLimit,usePmc);
								}
							}else{
								JOptionPane.showMessageDialog(new JFrame(), "no common name is found for "+cyNodeAttr.getStringAttribute(((CyNode)oneCyNode).getIdentifier(), "displayName"));
							}

						} else if(moleTypeOfNode!=null && moleTypeOfNode.equalsIgnoreCase("protein")){
							//						JOptionPane.showMessageDialog(new JFrame(),"moleType is protein");
							String mainDbId=cyNodeAttr.getStringAttribute(((CyNode)oneCyNode).getIdentifier(), "mainDbId");
							System.out.println(mainDbId);
							SpaccTransformerWS spaccTrans=new SpaccTransformerWS(mainDbId);
							ArrayList<String> commonNameStore=spaccTrans.getCommonName();
							//						JOptionPane.showMessageDialog(new JFrame(),"the number of commonName:"+commonNameStore.size());

							for(String oneName:commonNameStore){
								System.out.println("one name is:"+oneName);
								if(oneName.length()<6){
									continue;
								}
								//create new node and add to the net
								// the last argument is for using pmc in the e-utility or not using pmc in e-utility 
								CommonNameRootedTreeProducer7 cnrtp=new CommonNameRootedTreeProducer7(oneCyNode,(String)oneName, xIncrement,yIncrement,vs,originalKeywordString,useDate,useBiotext,usePubmed,yearLimit,usePmc);
							}


						}else if(moleTypeOfNode!=null && moleTypeOfNode.equalsIgnoreCase("metabolite")){
							//						
							String mainDbId=cyNodeAttr.getStringAttribute(((CyNode)oneCyNode).getIdentifier(), "mainDbId");
							System.out.println("main dbid:"+mainDbId);
							// if the selected metabolite is the abundent one, skip it.Otherwise the responding time is too long.
							if(new RejectMetabolite().isRejectedMetabolite(mainDbId)){
								continue;
							}

							KeggCompoundIdTransformer keggTransform=new KeggCompoundIdTransformer(mainDbId);

							ArrayList commonNameStore=keggTransform.getCommonName();
							//						JOptionPane.showMessageDialog(new JFrame(),"the number of commonName:"+commonNameStore.size());
							ArrayList totalUid=new ArrayList();
							for(Object oneName:commonNameStore){
								System.out.println("one name is:"+(String)oneName);
								// the last argument is for using pmc in the e-utility or not using pmc in e-utility 
								CommonNameRootedTreeProducer7 cnrtp=new CommonNameRootedTreeProducer7(oneCyNode,(String)oneName, xIncrement,yIncrement,vs,originalKeywordString,useDate,useBiotext,usePubmed,yearLimit,usePmc);

							}
							



						}else if(nodeType!=null && nodeType.equalsIgnoreCase("pathwayId")){
							String pathwayName=cyNodeAttr.getStringAttribute(((CyNode)oneCyNode).getIdentifier(), "displayName");
							// the last argument is for using pmc in the e-utility or not using pmc in e-utility 
							CommonNameRootedTreeProducer7 cnrtp=new CommonNameRootedTreeProducer7(oneCyNode,(String)pathwayName, xIncrement,yIncrement,vs,originalKeywordString,useDate,useBiotext,usePubmed,yearLimit,usePmc);
						}else if(nodeType!=null && nodeType.equalsIgnoreCase("commonName")){
							
							
						}

					}
					vs=new VizMapperBioentityLiteSearchNet1(Cytoscape.getCurrentNetwork(),Cytoscape.getNodeAttributes(),Cytoscape.getEdgeAttributes()).getVisualStyleBuilder();
					vs.buildStyle();
					currentNet.unselectAllNodes();
					Cytoscape.firePropertyChange(Cytoscape.ATTRIBUTES_CHANGED, null, null);
					Cytoscape.getCurrentNetworkView().redrawGraph(false, true);
					Cytoscape.getCurrentNetworkView().updateView();
					

				}
			}


		}
	
	}
	
	//v19 20110621: if eutility is used, the returned id is pmcid already.There is no need to do id conversion.
	//v21 20110621: if eutility is used, the attribute of literature list is "PMCID".If biotext is used, the attribute of literature list is "UID"
	//v103 20110624: see below
	//v109 20110703: allow ec node could be selected for literature search and literature refinement
	//v115_1 20110718: the refinedRefCount node for biotext or eutility will have only UID attribute,and the prefix of pubmed link is always "http://www.ncbi.nlm.nih.gov/pubmed/". 
	public class RefineUidListHandler implements ActionListener{
		public void actionPerformed(ActionEvent event){

			if(event.getSource()==jbRefineUidList){
				
				CyNetwork currentNet=Cytoscape.getCurrentNetwork();

				CyAttributes cyNodeAttr=Cytoscape.getNodeAttributes();

				Set selectedNodeSet=currentNet.getSelectedNodes();

				for(Object oneRefCountNode:selectedNodeSet){
					
						String nodeIdOfKwNode=cyNodeAttr.getStringAttribute(((CyNode)oneRefCountNode).getIdentifier(), "parentNodeId");// nodeIdOfParentNode contains the prefix "CN:"
						String nodeIdOfCommonNameNode=cyNodeAttr.getStringAttribute(nodeIdOfKwNode, "parentNodeId");
						String nodeIdOfBioentityNode=cyNodeAttr.getStringAttribute(nodeIdOfCommonNameNode, "parentNodeId");
						String moleTypeOfBioentityNode=cyNodeAttr.getStringAttribute(nodeIdOfBioentityNode, "moleType");//moleTypeOfBioentityNode could be "protein" or "metabolite", and also allows "enzyme"

						String validatedCommonName=(new ValidateProteinCommonName(((String)nodeIdOfCommonNameNode).replaceFirst("CN:", ""))).convert();

						String nodeType=cyNodeAttr.getStringAttribute(((CyNode)oneRefCountNode).getIdentifier(),"nodeType");
						//					System.out.println("nodeType:"+nodeType);
						if(nodeType.equalsIgnoreCase("refCountBiotext")||nodeType.equalsIgnoreCase("refCountEutil")){
							try{
								double xParentNode=Cytoscape.getCurrentNetworkView().getNodeView((CyNode)oneRefCountNode).getXPosition();
								double yParentNode=Cytoscape.getCurrentNetworkView().getNodeView((CyNode)oneRefCountNode).getYPosition();

								
								HashMap<String,ArrayList<String>> userQuery=new HashMap<String,ArrayList<String>>();

								ArrayList<String> bioentityQueyTerm=new ArrayList<String>();
								bioentityQueyTerm.add(validatedCommonName);
								JOptionPane.showMessageDialog(new JFrame(), "validataed common name:"+validatedCommonName+"/ncDocReSy106 line 1739");

								ArrayList<String> freeTextQueryTermList=new ArrayList<String>();
								
								//v15 20110609: since jTextFieldKeyword could contain comma seperated free text,the string in jTextFieldKeyword has to be parsed.
//								String freeTextQueryString=jTextFieldKeyword.getText().replaceAll(" ", "%20");
								String freeTextQueryString=jTextFieldKeyword.getText();
								//								freeTextTerm.add(freeText);
								String[] freeTextQueryTermArray=freeTextQueryString.split(",");
								for(String oneFreeTextQueryTerm:freeTextQueryTermArray){
									freeTextQueryTermList.add(oneFreeTextQueryTerm);
								}
								if(moleTypeOfBioentityNode.equalsIgnoreCase("metabolite")){
									userQuery.put("chemical", bioentityQueyTerm);
								}else if(moleTypeOfBioentityNode.equalsIgnoreCase("protein")||moleTypeOfBioentityNode.equalsIgnoreCase("enzyme")){  //v109_ 20110703: allow enyzme node(ec) to be selected
									userQuery.put("protein", bioentityQueyTerm);
								}
								userQuery.put("freeText", freeTextQueryTermList);

								ArrayList<String> refinedPmidList=new ArrayList<String>();
								
								//v21 20110621: if eutility is used, the attribute of literature list is "PMCID".If biotext is used, the attribute of literature list is "UID"
								List uidListOrig=null;
								uidListOrig=cyNodeAttr.getListAttribute(((CyNode)oneRefCountNode).getIdentifier(), "UID");
//								if(nodeType.equalsIgnoreCase("refCountBiotext")){
//									uidListOrig=cyNodeAttr.getListAttribute(((CyNode)oneRefCountNode).getIdentifier(), "UID");
//								}else if(nodeType.equalsIgnoreCase("refCountEutil")){
//									// if pmc is not used in the e-utility literature search, the eutilityRefCount node has attribute "UID", otherwise"PMCID"
//									uidListOrig=cyNodeAttr.getListAttribute(((CyNode)oneRefCountNode).getIdentifier(), "UID");
//								}
								
								
								System.out.println("number of reference of one selected node:"+uidListOrig.size());
								for(Object d:uidListOrig){

									String pmid=(String)d; //!!if eutility is used, the returned id by eutility is pmcid(depending on the value of "db" in the url query), and if biotext is used, returned id is pmid
									System.out.println("about to refine uid:"+pmid);
									try{
										StartStaticVariable.bw_messageRecorder.write("about to refine id:"+pmid+"\n");
										StartStaticVariable.bw_messageRecorder.flush();
									}catch(IOException ioex){
										ioex.printStackTrace();
									}
									
									// if the raw pmid list is retrieved from biotext, use full text to refine the pmid list
									// if the rew pmid list is from e-utility, just get the abstract to refine the pmid list
									if(nodeType.equalsIgnoreCase("refCountBiotext")){
										
										String pmcid=(new PmidToPmcid(pmid)).getPmcid();


										//if pmcid is not available for certain pmid, refinedUidList will be empty.
										if(!pmcid.equals("NotDefined")){
											try{
												RefineArticle1 reBiTeRe=new RefineArticle1
												(pmcid,userQuery);

												HashMap<String,Integer> bioentityTermCount=reBiTeRe.getMatchedTimesBioentityQueryTermIRD();

												HashMap<String,Integer> freeTextQueryTermCount=reBiTeRe.getMatchedTimesFreeTextQueryTermIRD();

												System.out.println("validatedCommonName:"+validatedCommonName);
												Set keysetBioentityTermCount=bioentityTermCount.keySet();
												for(Object mm:keysetBioentityTermCount){
													System.out.println("one key in keysetBioentityTermCount:"+(String)mm+" and the count is:"+bioentityTermCount.get(mm)+"/ncDocReSy104 line 1788");
												}
												Set keysetFreeTextCount=freeTextQueryTermCount.keySet();
												for(Object xx:keysetFreeTextCount){
													System.out.println("one key in keysetFreeTextCount:"+(String)xx+" and the count is:"+freeTextQueryTermCount.get(xx)+"/ncDocReSy104 line 1792");
												}
												//v103 20110624: add freeTextTermNotFound list
												ArrayList<String> freeTextTermNotFound=new ArrayList<String>();
												for(String oneFreeTextQueryTerm:freeTextQueryTermList){
													if(!freeTextQueryTermCount.containsKey(oneFreeTextQueryTerm)){
														freeTextTermNotFound.add(oneFreeTextQueryTerm);
													}else if(freeTextQueryTermCount.containsKey(oneFreeTextQueryTerm)){
														if(freeTextQueryTermCount.get(oneFreeTextQueryTerm)==0){
															freeTextTermNotFound.add(oneFreeTextQueryTerm);
														}
													}
												}

												//v103 20110624: check if the key exist in chemicalTermCount
												//v104 20110625:change chemicalTermCount to bioentityTermCount
												if(bioentityTermCount.containsKey(validatedCommonName)){
													if((Integer)(bioentityTermCount.get(validatedCommonName))>0 && freeTextTermNotFound.size()==0){
														refinedPmidList.add(pmid);
													}
												}
											}catch(WhatizitException_Exception ex){
												refinedPmidList.add("contact_failed");
											}
										}else{
											refinedPmidList.add("no_pmcid");
										}
									}else if(nodeType.equalsIgnoreCase("refCountEutil")){
										BufferedWriter bw=new BufferedWriter(new FileWriter("j:\\ForWork\\growNetwork\\taggedPmid"+pmid+".txt"));
										EutilityPubMed5 epm5=new EutilityPubMed5();
										String eutilAbstractInXml=epm5.getAbstractForPmid(pmid);
										
										byte[] bytes=eutilAbstractInXml.getBytes();
										
										JAXBContext jc=JAXBContext.newInstance("eUtilAbstract2135422Jaxb");
//										System.out.println(this.getClass().getName()+" line "+new Exception().getStackTrace()[0].getLineNumber());
										Unmarshaller u=jc.createUnmarshaller();
//										System.out.println(this.getClass().getName()+" line "+new Exception().getStackTrace()[0].getLineNumber());
										
										PubmedArticleSet pubmedArticleSet=(PubmedArticleSet)u.unmarshal(new ByteArrayInputStream(bytes));
//										System.out.println(this.getClass().getName()+" line "+new Exception().getStackTrace()[0].getLineNumber());
										
										ArrayList<AbstractText> abstractTextList=(ArrayList<AbstractText>)pubmedArticleSet.getPubmedArticle().getMedlineCitation().getArticle().getAbstract().getAbstractText();
										
										String rawTextInAbstract="";
										
										for(AbstractText a:abstractTextList){
											rawTextInAbstract=rawTextInAbstract+a.getContent();
											System.out.println(a.getLabel());
											System.out.println(a.getContent());
										}
										
										HashMap<String,Integer> matchedTimesBioentityTermIRCD=new HashMap<String,Integer>();
										HashMap<String,Integer> matchedTimesFreeTextIRCD=new HashMap<String,Integer>();
										
										if(userQuery.containsKey("chemical")){
											TerminologyAgent1 termAgent=new TerminologyAgent1(rawTextInAbstract,"","","","chemical",userQuery.get("chemical"),bw);
											//							
											matchedTimesBioentityTermIRCD=termAgent.getTermStatisticsOfUserDesire();
										}else if(userQuery.containsKey("protein")){
											TerminologyAgent1 termAgent=new TerminologyAgent1(rawTextInAbstract,"","","","protein",userQuery.get("protein"),bw);
											//							
											matchedTimesBioentityTermIRCD=termAgent.getTermStatisticsOfUserDesire();
										}

										if(userQuery.containsKey("freeText")){
											ArrayList freeTextTermUserWant=(ArrayList) userQuery.get("freeText");
											for(Object oneFreeTextTermUserWant:freeTextTermUserWant){
												matchedTimesFreeTextIRCD.put((String)oneFreeTextTermUserWant, 0);
												Pattern ptn=Pattern.compile(" "+(String)oneFreeTextTermUserWant+" ");
												Matcher mtch=ptn.matcher(rawTextInAbstract);

												while(mtch.find()){
													int oldCount=(Integer)(matchedTimesFreeTextIRCD.get(oneFreeTextTermUserWant));
													int newCount=oldCount+1;
													matchedTimesFreeTextIRCD.put((String)oneFreeTextTermUserWant, newCount);
												}
											}
										}
										
										ArrayList<String> freeTextTermNotFound=new ArrayList<String>();
										for(String oneFreeTextQueryTerm:freeTextQueryTermList){
											if(!matchedTimesFreeTextIRCD.containsKey(oneFreeTextQueryTerm)){
												freeTextTermNotFound.add(oneFreeTextQueryTerm);
											}else if(matchedTimesFreeTextIRCD.containsKey(oneFreeTextQueryTerm)){
												if(matchedTimesFreeTextIRCD.get(oneFreeTextQueryTerm)==0){
													freeTextTermNotFound.add(oneFreeTextQueryTerm);
												}
											}
										}

										//v103 20110624: check if the key exist in chemicalTermCount
										//v104 20110625:change chemicalTermCount to bioentityTermCount
										if(matchedTimesBioentityTermIRCD.containsKey(validatedCommonName)){
											if((Integer)(matchedTimesBioentityTermIRCD.get(validatedCommonName))>0 && freeTextTermNotFound.size()==0){
												refinedPmidList.add(pmid);
											}
										}
									}
								}

								int refCountAfterRefinement=0;  //number of left documents after refinement
								for(Object pmidAfterRefinement:refinedPmidList){
									if(!((String)pmidAfterRefinement).equalsIgnoreCase("no_pmcid") && !((String)pmidAfterRefinement).equalsIgnoreCase("contact_failed")){
										refCountAfterRefinement++;
									}
								}

								CyNode refinedUidNode=Cytoscape.getCyNode("RefinedRefCount:"+refCountAfterRefinement+"_"+StartStaticVariable.docCountTailing, true);
								currentNet.addNode(refinedUidNode);
								//assign the new node a position
								Cytoscape.getCurrentNetworkView().getNodeView(refinedUidNode).setXPosition(xParentNode);
								Cytoscape.getCurrentNetworkView().getNodeView(refinedUidNode).setYPosition(yParentNode+60);

								// set node attribute
								String nodeTypeForRefRefineNode="";
								String pubMedLink="";
								
								if(nodeType.equalsIgnoreCase("refCountBiotext")){
									nodeTypeForRefRefineNode="RefinedUidListForBioTx";								
								}else if(nodeType.equalsIgnoreCase("refCountPM")){
									nodeTypeForRefRefineNode="RefinedUidListForPM";									
								}else if(nodeType.equalsIgnoreCase("refCountPMC")){
									nodeTypeForRefRefineNode="RefinedUidListForPMC";	
								}
								
								cyNodeAttr.setAttribute(refinedUidNode.getIdentifier(), "nodeType", nodeTypeForRefRefineNode);
								cyNodeAttr.setAttribute(refinedUidNode.getIdentifier(),"displayName" ,"RefinedRefCount:"+refCountAfterRefinement);
								cyNodeAttr.setAttribute(refinedUidNode.getIdentifier(),"docCount" ,refCountAfterRefinement+"");
								//add parentNodeId attribute so that the parent node is tractable
								cyNodeAttr.setAttribute(refinedUidNode.getIdentifier(), "parentNodeId", ((CyNode)oneRefCountNode).getIdentifier());
								cyNodeAttr.setListAttribute(refinedUidNode.getIdentifier(), "UID", refinedPmidList);
								
								
								Collections.sort(refinedPmidList);
								
								pubMedLink="http://www.ncbi.nlm.nih.gov/pubmed/";
								for(Object pmcidOrPmid:refinedPmidList){
									pubMedLink=pubMedLink+(String)pmcidOrPmid+",";

								}
								cyNodeAttr.setAttribute(refinedUidNode.getIdentifier(), "PubMed link", pubMedLink);
								
								StartStaticVariable.docCountTailing++;

								CyEdge edgeRefCount_refined=Cytoscape.getCyEdge((CyNode)oneRefCountNode, refinedUidNode, Semantics.INTERACTION, "textMining", true);

								currentNet.addEdge(edgeRefCount_refined);

								//set visual properties
								vs=new VizMapperBioentityLiteSearchNet1(Cytoscape.getCurrentNetwork(),Cytoscape.getNodeAttributes(),Cytoscape.getEdgeAttributes()).getVisualStyleBuilder();
								vs.buildStyle();
//								vs.addProperty(edgeRefCount_refined.getIdentifier(), VisualPropertyType.EDGE_LINE_STYLE, "EQUAL_DASH");
//								vs.addProperty(refinedUidNode.getIdentifier(), VisualPropertyType.NODE_LABEL,cyNodeAttr.getStringAttribute(refinedUidNode.getIdentifier(), "displayName"));
//								vs.addProperty(refinedUidNode.getIdentifier(), VisualPropertyType.NODE_BORDER_COLOR, "#FF00FF");  //#FF00FF pink
//								vs.buildStyle();
								Cytoscape.firePropertyChange(Cytoscape.ATTRIBUTES_CHANGED, null, null);
								Cytoscape.getCurrentNetworkView().redrawGraph(false, true);
								Cytoscape.getCurrentNetworkView().updateView();
							}catch(Exception ex){
								ex.printStackTrace();
								JOptionPane.showMessageDialog(new JFrame(), "error in the refinement proccess");
							}
						}
					
				}
				
			}
		}
	}
	
	//v12 20110530: work on the "currently displayed" network
	public class SummarizeLiteSearchResultHandler implements ActionListener{
		public void actionPerformed(ActionEvent ae){
			if(ae.getSource()==jbSummarizeLiteSearchResult){
				CyNetworkView cyViewx=Cytoscape.getCurrentNetworkView();
				CyNetwork displayedNetwork=cyViewx.getNetwork();
//				CyNetwork cyNetwork=Cytoscape.getCurrentNetwork();
				CyAttributes cyNodeAttr=Cytoscape.getNodeAttributes();
				CyAttributes cyEdgeAttr=Cytoscape.getEdgeAttributes();
			
				// BioEntityScoreCalculator4 will calculate the refSearch score for "all" bioentity node and put this score in "score" attribute 
				SummarizeCitation3 bioCalc=new SummarizeCitation3(displayedNetwork,cyNodeAttr,cyEdgeAttr);

				vs=new VizMapperBioentityLiteSearchNet1(Cytoscape.getCurrentNetwork(),Cytoscape.getNodeAttributes(),Cytoscape.getEdgeAttributes()).getVisualStyleBuilder();
				vs.buildStyle();
				
				Cytoscape.firePropertyChange(Cytoscape.NETWORK_MODIFIED, null, null);
				Cytoscape.firePropertyChange(Cytoscape.ATTRIBUTES_CHANGED, null, null);
				Cytoscape.getCurrentNetworkView().redrawGraph(false, true);//first argument is for layout,second for vizmap
				Cytoscape.getCurrentNetworkView().updateView();
//				for(Object x:nodeIdSetToValidate){
//					String docCountOfSelectedNode=(String) cyNodeAttr.getAttribute((String)x, "docCount");
//					System.out.println("selected node id is:"+(String)x+" and doc count is:"+docCountOfSelectedNode);
//				}
			}
			
		}
		
		
	}
	
	
	public class ScoreCalculateHandler implements ActionListener{
		public void actionPerformed(ActionEvent ae){
			System.out.println("score calculator button is pressed");
			if(!jcBrowse.isSelected()&& !jcDiscover.isSelected()){
				JOptionPane.showMessageDialog(new JFrame(), "Please select the scoring mode");
				return;
			}
			
//			if(jcDiscover.isSelected()){
//				calculateDiscoverScore=true;
//			}else if(jcBrowse.isSelected()){
//				calculateBrowseScore=true;
//			}
			
			String attributeValueTailing="";
			Double[] layerScoreFactorBrowse={1.0,0.5,0.33,0.25};
			Double[] layerScoreFactorDiscover={0.0,0.5,0.33,0.25};
			
			Double[] layerScoreFactor=null;
			System.out.println("browse is selected:"+calculateBrowseScore);
			System.out.println("discover is selected:"+calculateDiscoverScore);
			
			
			CyNetworkView cyViewx=Cytoscape.getCurrentNetworkView();
			CyNetwork displayedNetwork=cyViewx.getNetwork();
			CyAttributes cyNodeAttr=Cytoscape.getNodeAttributes();
			CyAttributes cyEdgeAttr=Cytoscape.getEdgeAttributes();
			
			//to sumarize the literature search in case useer forgets to do so.
			SummarizeCitation3 bioCalc=new SummarizeCitation3(displayedNetwork,cyNodeAttr,cyEdgeAttr);
			
			displayedNetwork.selectAllNodes();/// has to determine to apply score caculation on the whole net or selected nodes
			
			Set<CyNode> selectedNodes=displayedNetwork.getSelectedNodes();
			int layerNumber=Integer.parseInt((String)comboBoxLayer.getSelectedItem());
			System.out.println("layer number:"+layerNumber);
			System.out.println("discover or browse score:"+attributeValueTailing);
			String keywordForConverge=jTextFieldKeywordForConverge.getText().trim();
			System.out.println("keywordForConverge:"+keywordForConverge);
			
			for(CyNode oneNode:selectedNodes){
				System.out.println("id of one selected node:"+oneNode.getIdentifier());

				if(calculateBrowseScore==true){
					layerScoreFactor=layerScoreFactorBrowse;
					attributeValueTailing="Browse";
					RefCountScoreCalculator1 rcsc=new RefCountScoreCalculator1(oneNode,keywordForConverge,layerNumber);
					double simpleScore=rcsc.calculateSimpleScore(layerScoreFactor);
					double modifiedScore=rcsc.calculateModifiedScore(layerScoreFactor);
					cyNodeAttr.setAttribute(oneNode.getIdentifier(), "simpleScore("+keywordForConverge+")("+layerNumber+")("+attributeValueTailing+")", simpleScore);
					cyNodeAttr.setAttribute(oneNode.getIdentifier(), "modifiedScore("+keywordForConverge+")("+layerNumber+")("+attributeValueTailing+")", modifiedScore);
				}
				if(calculateDiscoverScore==true){
					layerScoreFactor=layerScoreFactorDiscover;
					attributeValueTailing="Discover";
					RefCountScoreCalculator1 rcsc=new RefCountScoreCalculator1(oneNode,keywordForConverge,layerNumber);
					double simpleScore=rcsc.calculateSimpleScore(layerScoreFactor);
					double modifiedScore=rcsc.calculateModifiedScore(layerScoreFactor);
					cyNodeAttr.setAttribute(oneNode.getIdentifier(), "simpleScore("+keywordForConverge+")("+layerNumber+")("+attributeValueTailing+")", simpleScore);
					cyNodeAttr.setAttribute(oneNode.getIdentifier(), "modifiedScore("+keywordForConverge+")("+layerNumber+")("+attributeValueTailing+")", modifiedScore);
				}
			}
			
			displayedNetwork.unselectAllNodes();
//			//change the node color according to the score
//			Calculator nodeColorCalculator=NodeFilledColorCalculator.createCalculator("modifiedScore("+keywordForConverge+")");//choose this, or
////			Calculator nodeColorCalculator=NodeFilledColorCalculator.createCalculator("simpleScore("+keywordForConverge+")");//choose this
//			System.out.println("calculator id:"+nodeColorCalculator.ID);
//			Cytoscape.getVisualMappingManager().getVisualStyle().getNodeAppearanceCalculator().setCalculator(nodeColorCalculator);
			
			System.out.println("discover or browse score:"+attributeValueTailing);
			Cytoscape.firePropertyChange(Cytoscape.NETWORK_MODIFIED, null, null);
			Cytoscape.firePropertyChange(Cytoscape.ATTRIBUTES_CHANGED, null, null);
			Cytoscape.getCurrentNetworkView().redrawGraph(false, true);//first argument is for layout,second for vizmap
			Cytoscape.getCurrentNetworkView().updateView();
		}
	}
	
	
	//  --------unfinished-----------
	public class ScoreConvergerHandler implements ActionListener{
		public void actionPerformed(ActionEvent ae){
			CyNetwork currentNet=Cytoscape.getCurrentNetwork();
			int selectedNodeNumber=currentNet.getSelectedNodes().size();
		
			if(selectedNodeNumber!=1){
				JOptionPane.showMessageDialog(new JFrame(), "please select only one node and start again");
				
			}else{
				LayerNodeSet layerNodeSet=new LayerNodeSet(3);
				ArrayList layeredNodeSet=layerNodeSet.getLayeredNodeSet();
				int i=1;
				for(Object y:layeredNodeSet){
					System.out.println("nodes in "+i+"th layer are:");
					for(Object k:(Set)y){
						String nodeId=((CyNode)k).getIdentifier();
						System.out.println(nodeId);
					}
				}
			}
		}
		
	}
	
	public class ShowHiddenNodeHandler implements ActionListener{
		public void actionPerformed(ActionEvent ae){
			if(ae.getSource()==jbShowHiddenNode){
				System.out.println("inhide all node button is pressed"+" ncDocresy.showhiddenNodeHandler");
				CyNetworkView cyViewx=Cytoscape.getCurrentNetworkView();
				CyNetwork displayedNetwork=cyViewx.getNetwork();
				CyAttributes cyNodeAttr=Cytoscape.getNodeAttributes();
//				try {
					System.out.println("network id is:"+displayedNetwork.getIdentifier()+" ncDocresy.showhiddenNodeHandler");
//					CyNodeUtils.unhideAllNodes(displayedNetwork.getIdentifier());
					ArrayList<String> hiddenNodeIdList=new ArrayList<String>();
					hiddenNodeIdList.addAll(StartStaticVariable.hiddenNodeId);
					for(String hiddenNodeId:hiddenNodeIdList){
						displayedNetwork.restoreNode(Cytoscape.getCyNode(hiddenNodeId, false));
						StartStaticVariable.hiddenNodeId.remove(hiddenNodeId);
					}
					
					ArrayList<CyEdge> originalCyEdgeList=new ArrayList<CyEdge>();
					originalCyEdgeList.addAll(StartStaticVariable.originalCyEdgeList);
					for(CyEdge edge:originalCyEdgeList){
						displayedNetwork.restoreEdge(edge);
						StartStaticVariable.originalCyEdgeList.remove(edge);
					}
//				} catch (XmlRpcException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
			


					//hide unimportant metabolite nodes
					List<String> metaboliteNodeIdList=CyAttributesUtils.getIDListFromAttributeValue(CyAttributesUtils.AttributeType.NODE, "moleType", "metabolite");
					for(String metaboliteNodeId:metaboliteNodeIdList){
						String keggCompId=(String)cyNodeAttr.getAttribute(metaboliteNodeId, "mainDbId");
						if(new RejectMetabolite().isRejectedMetabolite(keggCompId)){
							displayedNetwork.hideNode(Cytoscape.getCyNode(metaboliteNodeId, false));
						}
					}
					
					// build visualstyle
					VizMapperBioentityLiteSearchNet1 vsNew=new VizMapperBioentityLiteSearchNet1(displayedNetwork,Cytoscape.getNodeAttributes(),Cytoscape.getEdgeAttributes());
					vsNew.getVisualStyleBuilder().buildStyle();
					Cytoscape.firePropertyChange(Cytoscape.NETWORK_MODIFIED, null, null);
					Cytoscape.firePropertyChange(Cytoscape.ATTRIBUTES_CHANGED, null, null);
					Cytoscape.getCurrentNetworkView().redrawGraph(false, true);//first argument is for layout,second for vizmap
					Cytoscape.getCurrentNetworkView().updateView();
			}
		}	
	}
	
 	public class OrganismFilterHandler implements ActionListener{
		public void actionPerformed(ActionEvent ae){
			if(ae.getSource()==jbOrganismFilter){
				String taxoUserWant=jTextFieldTaxo.getText().trim();
				CyNetwork currentNet=Cytoscape.getCurrentNetwork();
				CyAttributes cyNodeAttri=Cytoscape.getNodeAttributes();
//				currentNet.selectAllNodes();
				Set nodeSet=currentNet.getSelectedNodes();
				System.out.println("size of selected nodes:"+nodeSet.size());
				Iterator itNodeSet=nodeSet.iterator(); //if the modification of selected node is intended, a iterator has to be created for the selected nodes.
				while(itNodeSet.hasNext()){

					CyNode oneSelectedNode=(CyNode)itNodeSet.next();
//					System.out.println("=============="+((CyNode)oneSelectedNode).getIdentifier());
					if(cyNodeAttri.hasAttribute(((CyNode)oneSelectedNode).getIdentifier(), "moleType")){
//						System.out.println(((CyNode)oneSelectedNode).getIdentifier()+" has moleTYpe attribute");
						String moleType=cyNodeAttri.getStringAttribute(((CyNode)oneSelectedNode).getIdentifier(),"moleType");
//						System.out.println("moleType is :"+moleType);
						if(moleType.equals("enzyme")){
							String mainDbId=cyNodeAttri.getStringAttribute(((CyNode)oneSelectedNode).getIdentifier(), "mainDbId");
//							System.out.println("mainDbId is:"+mainDbId);
							OrganismFilter3 of=new OrganismFilter3(mainDbId,"ec",taxoUserWant); //the mainDbId here is ec number
							boolean isForOrganism=of.isOrganism();
//							System.out.println("isForOrganism is:"+isForOrganism);
							if(isForOrganism==false){
//								System.out.println("the rootgraphindex of the removing node is:"+((CyNode)oneSelectedNode).getRootGraphIndex());

								itNodeSet.remove(); // this step is important!!!!Otherwise java.util.ConcurrentModificationException will appear.
								currentNet.hideNode((CyNode)oneSelectedNode);
								
//								System.out.println("after hide node");
								Cytoscape.firePropertyChange(Cytoscape.NETWORK_MODIFIED, null, null);
//								currentNet.removeNode(((CyNode)oneNode).getRootGraphIndex(), true);
								
								
							}
						}else if(moleType.equals("protein")){
							String mainDbId=cyNodeAttri.getStringAttribute(((CyNode)oneSelectedNode).getIdentifier(), "mainDbId");
							OrganismFilter3 of=new OrganismFilter3(mainDbId,"spacc",taxoUserWant);// the mainDbId here is spacc
							boolean isForOrganism=of.isOrganism();
							if(isForOrganism==false){
//								System.out.println("the rootgraphindex of the removing node is:"+((CyNode)oneSelectedNode).getRootGraphIndex());

								itNodeSet.remove(); // this step is important!!!!Otherwise java.util.ConcurrentModificationException will appear.
								currentNet.hideNode((CyNode)oneSelectedNode);
								
//								System.out.println("after hide node");
								Cytoscape.firePropertyChange(Cytoscape.NETWORK_MODIFIED, null, null);
//								currentNet.removeNode(((CyNode)oneNode).getRootGraphIndex(), true);
								
								
							}
						}
					}


				}
				
				// remove isolated nodes
				CleanIsolatedNode cin=new CleanIsolatedNode(currentNet);
				currentNet=cin.getCleanNet();
				currentNet.unselectAllEdges();
				currentNet.unselectAllNodes();
				
				Cytoscape.getCurrentNetworkView().updateView();
				Cytoscape.getCurrentNetworkView().redrawGraph(false, false); // if the second argument is true, the visualStyle will be reset
			}
		
		}
		
		
	}
	
	public class CleanIsolatedNode{
		CyNetwork cleanedNet=null;
		public CleanIsolatedNode(CyNetwork cyNet){
			cyNet.selectAllEdges();
			cyNet.selectAllNodes();
			Set edgeSet=cyNet.getSelectedEdges();
			Set nodeSet=cyNet.getSelectedNodes();
//			cyNet.unselectAllEdges();
//			cyNet.unselectAllNodes();
			ArrayList connectedNodes=new ArrayList();
			for(Object oneEdge:edgeSet){
				CyNode sourceNode=(CyNode)((CyEdge)oneEdge).getSource();
				CyNode sinkNode=(CyNode)((CyEdge)oneEdge).getTarget();
				connectedNodes.add(sourceNode);
				connectedNodes.add(sinkNode);
				
			}
			
			// get unique connected ndoes
			ArrayList uniqueConnectedNodes=new ArrayList();
			Iterator itConnectedNodes=connectedNodes.iterator();
			while(itConnectedNodes.hasNext()){
				CyNode oneConnectedNode=(CyNode)itConnectedNodes.next();
				itConnectedNodes.remove();  //IMPORTANT!! use remove to unlock the index after an object is retrieved
				if(!uniqueConnectedNodes.contains(oneConnectedNode)){
					uniqueConnectedNodes.add(oneConnectedNode);

				}

			}
			
			// iterator all the nodes in the network.If the node is not belong to the connectedNode group, hide it.
			Iterator itNodeSet=nodeSet.iterator();
			while(itNodeSet.hasNext()){
				CyNode oneNodeInOriginNet=(CyNode)itNodeSet.next();
				itNodeSet.remove();     //IMPORTANT!! use remove to unlock the index after an object is retrieved
				if(!uniqueConnectedNodes.contains(oneNodeInOriginNet)){
					cyNet.hideNode(oneNodeInOriginNet);

				}

			}
			
			cyNet.unselectAllEdges();
			cyNet.unselectAllNodes();
			cleanedNet=cyNet;
			
		}
		
		public CyNetwork getCleanNet(){
			return cleanedNet;
		}
		
		
	}
	public class ShowUidInMyPanel implements ActionListener{
		public void actionPerformed(ActionEvent ae){
//			System.out.println("in the ShowUidInMyPanel.actionPerformed");
			CytoPanelImp ctrlPanel=(CytoPanelImp)Cytoscape.getDesktop().getCytoPanel(SwingConstants.WEST);
			
					
			if(ae.getSource()==jbShowUid){
//				System.out.println("pass event check");
				
//				jp11.removeAll();
				
				CyNetwork currentNet=Cytoscape.getCurrentNetwork();

				CyAttributes cyNodeAttr=Cytoscape.getNodeAttributes();

				Set selectedNodeSet=currentNet.getSelectedNodes();
//				System.out.println("number of selected node:"+selectedNodeSet.size());
				
				
				
				
				for(Object oneCyNode:selectedNodeSet){
					String nodeType=cyNodeAttr.getStringAttribute(((CyNode)oneCyNode).getIdentifier(),"nodeType");
//					System.out.println("nodeType:"+nodeType);
					if(nodeType.equalsIgnoreCase("refCountBiotext")|| nodeType.equalsIgnoreCase("refCountEutil")||nodeType.equalsIgnoreCase("RefinedUidListForBioTx")){
					
						List uidList=cyNodeAttr.getListAttribute(((CyNode)oneCyNode).getIdentifier(), "UID");
//						System.out.println("number in the uid list:"+uidList.size());					
		
						
						
						
//						Vector<Vector> rowData=new Vector<Vector>();
//						
//						for(Object s:uidList){
//							Vector<String> row=new Vector<String>();
//							row.addElement(s+"");
//							rowData.add(row);
//						}
//						
//						Vector<String> columnNames=new Vector<String>();
//						columnNames.addElement("UID");
//						JTable table=new JTable(rowData,columnNames);
//						JScrollPane scrollPane=new JScrollPane(table);
//						jp11.add(scrollPane);
//						myPanel.add(jp11);
						
						
						
						
						DefaultTableModel tableModel=new DefaultTableModel(uidList.size(),1);
						
						DefaultTableColumnModel columnModel=new DefaultTableColumnModel();
						
						TableColumn column=new TableColumn(0);
						column.setHeaderValue("UID("+((CyNode)oneCyNode).getIdentifier()+")");
//						column.setWidth(3);
						column.setPreferredWidth(1);
						column.setResizable(true);
						columnModel.addColumn(column);
						
						
						int rowIndex=0;
						for(Object s:uidList){
							System.out.println((String)s);
							tableModel.setValueAt(new String((String)s), rowIndex, 0);
							rowIndex++;
						}
						
//						System.out.println("take out one value from tablemodel:"+(String)tableModel.getValueAt(3, 0));
						tableModel.fireTableDataChanged();

						
						JTable jTable=new JTable(tableModel,columnModel);
//						jTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
						JScrollPane scrollPane=new JScrollPane(jTable);
						scrollPane.setBounds(2, 2, 2, 2);
						scrollPane.setSize(2,2);
						System.out.println("before displaying a new jFrame "+this.getClass().getName()+" "+new Exception().getStackTrace()[0].getLineNumber());
						JFrame jf1=new JFrame("UID list");
						jf1.getContentPane().add(scrollPane);
						jf1.setVisible(true);
//						jp11.add(scrollPane);
//						jp11.setPreferredSize(new Dimension(400, 400));
//					    jp11.setBorder(BorderFactory.createLineBorder (Color.blue, 2));

//						myPanel.add(jp11);
						
					}
					
					
				}
				
				
				
				
			}
		
//			jp11.setVisible(true);
//			myPanel.setVisible(true);
			
			
		}
			
		
	}
	
	
	
	
	
	
//	private class JButtonLoadNetworkHandler implements ActionListener{
//		
//		public void actionPerformed(ActionEvent event){
////			String path=((JTextField)event.getSource()).getText();
//			
////			if(event.getSource()==jb2){
//			xmlPath=jField1.getText();
////			NetDeserialization netDe=new NetDeserialization(path);
////			netObject=new NetDeserialization(serPath).getNetObject();
//			
//			
//			CytoscapeFingRootGraph rootGraph=new CytoscapeFingRootGraph();
////			JOptionPane.showMessageDialog(new JFrame(), "before going to XmlToCyNet");
//			HmlToCyNet cyNetBuilder=new HmlToCyNet(xmlPath);
//			
////			JOptionPane.showMessageDialog(new JFrame(), "after going to XmlToCyNet");
//			CyNetwork cyNetwork=cyNetBuilder.getCyNetwork();
//			
//			Collection nodeCollection=cyNetBuilder.getNodeCollection();
//			
//			Collection edgeCollection=cyNetBuilder.getEdgeCollection();
//			
//			VizMapperS vizMapperS=new VizMapperS(nodeCollection,edgeCollection,cyNetBuilder.getCyNodeMoleTypeMapping());
//			
//			VisualStyleBuilder vs=vizMapperS.createVisualStyleBuilder();
//			
//			vs.buildStyle();
//			GlobalAppearanceCalculator gac=Cytoscape.getVisualMappingManager().getVisualStyle().getGlobalAppearanceCalculator();
//			
////			CyNetworkView cyView=Cytoscape.createNetworkView(cyNetwork,"myNetwork");
//			
//			Cytoscape.getCurrentNetworkView().redrawGraph(false, true);
////			jf1.dispose();
////			}
//
//			
//			
//		}
//		
//	}

	
	
//	private class JButtonShowPpiHandler implements ActionListener{
//		
//		public void actionPerformed(ActionEvent event){
//			String hmlPath=jField1.getText();
//			JOptionPane.showMessageDialog(new JFrame(), "in showppihandler");
//			CyNetWithProteinInteractionA newNetBuilder=new CyNetWithProteinInteractionA(Cytoscape.getCurrentNetwork(),hmlPath);
//			CyNetwork newNetOfMetabolicAndPPI=newNetBuilder.getCyNetwork();
//			CytoscapeFingRootGraph rootGraph=new CytoscapeFingRootGraph();
////			JOptionPane.showMessageDialog(new JFrame(), "going to use CyNetWithProteinInteraction3");
////			CyNetWithProteinInteraction3 cyNetWPpiBuilder=new CyNetWithProteinInteraction3(hmlPath);
////			JOptionPane.showMessageDialog(new JFrame(), "in the between");
////			CyNetwork cyNetWPpi=cyNetWPpiBuilder.getCyNetwork();
////			JOptionPane.showMessageDialog(new JFrame(), "going to use VizMapper");
//			// since newNetBuilder.cyNodeMoleObjMapping returns a HashMap<CyNet,String>, vizMapperS is used instead of vizMapper
//			VizMapperS vizMapperS=new VizMapperS(newNetBuilder.getNodeCollection(),newNetBuilder.getEdgeCollection(),newNetBuilder.cyNodeMoleObjMapping);
//			VisualStyleBuilder vs=vizMapperS.createVisualStyleBuilder();
//			vs.buildStyle();
//			GlobalAppearanceCalculator gac=Cytoscape.getVisualMappingManager().getVisualStyle().getGlobalAppearanceCalculator();
//			
//						
////			CyNetworkView cyView=Cytoscape.createNetworkView(cyNetWPpi,"Net with PPI");
//			
//			Cytoscape.getCurrentNetworkView().redrawGraph(false, true);
//		
//		}
//	}
	
	
//	private class JButtonShowOmimHandler implements ActionListener{
//		
//		public void actionPerformed(ActionEvent event){
//			
//			RevealOmim1 ro=new RevealOmim1(serPath,Cytoscape.getCurrentNetwork());
//			CyNetwork newNetwork=ro.getNewCyNetwork();
////			VizMapper vizMapper=new VizMapper(cyNetWPpiBuilder.getNodeCollection(),cyNetWPpiBuilder.getEdgeCollection(),cyNetWPpiBuilder.cyNodeMoleObjMapping);
////			VisualStyleBuilder vs=vizMapper.createVisualStyleBuilder();
////			vs.buildStyle();
////			GlobalAppearanceCalculator gac=Cytoscape.getVisualMappingManager().getVisualStyle().getGlobalAppearanceCalculator();
//				
//			Cytoscape.getCurrentNetworkView().redrawGraph(false, true);
//			
//		}
//		
//		
//		
//	}
//	
//	private class JButtonHideOmimHandler implements ActionListener{
//		
//		public void actionPerformed(ActionEvent event){
//			Iterator it=omimNodeGroup.getNodeIterator();
//			CyNetwork net=Cytoscape.getCurrentNetwork();
////			Set selectedNodeSet=net.getSelectedNodes();
////			for(){
////				
////			}
//			while(it.hasNext()){
//			
//				net.removeNode(((CyNode)(it.next())).getRootGraphIndex(),false);
//			}	
//			Cytoscape.getCurrentNetworkView().redrawGraph(false, true);
//		}
//		
//	}

}


