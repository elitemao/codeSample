package plugin;

import edu.emory.mathcs.backport.java.util.Collections;
import growNetwork.StartStaticVariable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import textMining.EutilityPubMed5;
import textMining.ESearchResultParser2;

import textMining.PmcidPmidConverter1;
import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.data.Semantics;
import cytoscape.data.readers.VisualStyleBuilder;
import cytoscape.visual.VisualPropertyType;

//EutilNodeProducer------------------
//v0 20110529: Copied and modified from the Eutility related codes in NcDocReSy9
//v0 20110529: use EutilityPubMed4
//v1 20110616: add one more argument in the signature(usePmc)
//v2 20110621: if pmc is used in the eutility, the returned list is a pmcid list.The url for a list of pmcid is: http://www.ncbi.nlm.nih.gov/pmc/
//v2 20110718: use EutilityPubMed5
//v2 20110721: add mesh tag after the protein/metabolite name in the url to eutility
//v3 20110721: make the display name of the eutility node changes according to pubmed or pmc is used.
//v3 20110721: if no ref is found for the mesh tagged protein/metabolite and keyword(it might because no mesh term is found for the input bioentity name),use phrase searching
//v3 20110722: the nodeType attribute of refCount node will change according to pmc or pm.
//v4 20110724: use ESearchResultParser1
//v4 20110724: use ESearchResultParser2
//v4 20110726: use PmcidPmidConverter to transform pmcid to pmid if PMC is used for document retrieval
//v4 20110726: use PmcidPmidConverter1
//v4 20110729: remove the code vs.addProperty(...) because this step will be carried out in VizMapperBioentityLiteSearchNet
//v4 20110807: add attribute "usePhraseSearch" to the docCountEUtil node

public class EutilNodeProducer4 {

	public EutilNodeProducer4(String keyword,String oneCommonName_hex,boolean usePmc,boolean hasDateRestriction,String yearLimit,double keywordNodeX,double keywordNodeY,double xIncrement,double yIncrement,VisualStyleBuilder vs,CyNode nodeKeyword){
		boolean fromPhraseSearch=false;
		CyNetwork currentNet=Cytoscape.getCurrentNetwork();
		CyAttributes cyNodeAttr=Cytoscape.getNodeAttributes();
		CyAttributes cyEdgeAttr=Cytoscape.getEdgeAttributes();
		double newNodeX=keywordNodeX+xIncrement;  //the new coordinate is for the next node(refCount node)
		double newNodeY=keywordNodeY+yIncrement;
		
		String literatureDb="";
		String dbName="";
		if(usePmc==true){
			literatureDb="pmc";
			dbName="pmc";
		}else{
			literatureDb="pubmed";
			dbName="pm";
		}
		
		String queryInUrlEutilMeshTagged="";
		String queryInUrlEutilPhraseQuery="";
		if(hasDateRestriction==false){
//			queryInUrlEutilMeshTagged="term=%22"+oneCommonName_hex+"%22%5Bmesh%5D"+keyword+"&db="+literatureDb+"&retmax=1000000";//the special character should be encoded, but '"' doens't need
			queryInUrlEutilMeshTagged="term="+oneCommonName_hex+"%5Bmesh%5D"+keyword+"&db="+literatureDb+"&retmax=1000000";
			queryInUrlEutilPhraseQuery="term=%22"+oneCommonName_hex+"%22"+keyword+"&db="+literatureDb+"&retmax=1000000";
		}else{
//			queryInUrlEutilMeshTagged="term=%22"+oneCommonName_hex+"%22%5Bmesh%5D"+keyword+"&db="+literatureDb+"&datetype=pdat&mindate=1900&maxdate="+yearLimit+"&retmax=1000000";
			queryInUrlEutilMeshTagged="term="+oneCommonName_hex+"%5Bmesh%5D"+keyword+"&db="+literatureDb+"&datetype=pdat&mindate=1900&maxdate="+yearLimit+"&retmax=1000000";
			queryInUrlEutilPhraseQuery="term=%22"+oneCommonName_hex+"%22"+keyword+"&db="+literatureDb+"&datetype=pdat&mindate=1900&maxdate="+yearLimit+"&retmax=1000000";
		}
		System.out.println("queryInUrlEutilMeshTagged:"+queryInUrlEutilMeshTagged+"/"+this.getClass().getName()+" "+new Exception().getStackTrace()[0].getLineNumber());
		EutilityPubMed5 eutil=new EutilityPubMed5();

		String returnedXml;
		try{
			returnedXml=eutil.returnGetResult(queryInUrlEutilMeshTagged);
		}catch(java.lang.IllegalArgumentException exx){
			//Some common name will cause problem when eutility processes it, such like the common name for ec 2.3.3.1.
			exx.printStackTrace();
			return;
		}
		ESearchResultParser2 eutilParser=new ESearchResultParser2();
		eutilParser.parsePubMedResult(returnedXml);
		
		// if there is no result by adding mesh tag, use phrase searching
		if(eutilParser.getRefCount().equals("0")){
			returnedXml=eutil.returnGetResult(queryInUrlEutilPhraseQuery);
			eutilParser.parsePubMedResult(returnedXml);
			fromPhraseSearch=true;
		}
		
		//dbName here could be pmc or pm
		CyNode nodeDocCountEUtil=Cytoscape.getCyNode(dbName+":"+eutilParser.getRefCount()+"_"+StartStaticVariable.docCountTailing,true);
		currentNet.addNode(nodeDocCountEUtil);	//new node has to be added to the net before setting its coordinate
		// set node attribute.the nodeType attribute will change according to pmc or pm.
		cyNodeAttr.setAttribute(nodeDocCountEUtil.getIdentifier(), "displayName", dbName+":"+eutilParser.getRefCount());
		cyNodeAttr.setAttribute(nodeDocCountEUtil.getIdentifier(), "nodeType", "refCount"+dbName.toUpperCase());
		cyNodeAttr.setAttribute(nodeDocCountEUtil.getIdentifier(), "moleType", "refCount"+dbName.toUpperCase());
		cyNodeAttr.setAttribute(nodeDocCountEUtil.getIdentifier(), "docCount", eutilParser.getRefCount()+"");
		//add parentNodeId attribute so that the parent node is tractable
//		cyNodeAttr.setAttribute(nodeDocCountEUtil.getIdentifier(), "parentNodeId", nodeCommonName.getIdentifier());
		cyNodeAttr.setAttribute(nodeDocCountEUtil.getIdentifier(), "parentNodeId", nodeKeyword.getIdentifier());
		cyNodeAttr.setAttribute(nodeDocCountEUtil.getIdentifier(), "usePhraseSearch", fromPhraseSearch);
		
		
		
		// add attribute to eutility doc count node
		ArrayList<String> uidOrPmcidFrEutil=eutilParser.getUID();
		Collections.sort(uidOrPmcidFrEutil);
		String pubMedLink="";
		
		if(usePmc==false){
			cyNodeAttr.setListAttribute(nodeDocCountEUtil.getIdentifier(), "PMID", uidOrPmcidFrEutil);
			// add pubmed link attribute
			pubMedLink="http://www.ncbi.nlm.nih.gov/pubmed/";
		}else if(usePmc==true){
			ArrayList<String> transformedUidList=new ArrayList<String>();
//			for(String pmcidDigit:uidOrPmcidFrEutil){
//				String transformedPmid=new PmcidPmidConverter().pmcidToPmid(pmcidDigit);
//				transformedUidList.add(transformedPmid);
//			}
			HashMap<String,String> transformedPmcidPmidMap=new PmcidPmidConverter1().pmcidToPmid(uidOrPmcidFrEutil);
			Collection<String> values=transformedPmcidPmidMap.values();
			for(String x:values){
				transformedUidList.add(x);
			}
			
			cyNodeAttr.setListAttribute(nodeDocCountEUtil.getIdentifier(), "PMCID", uidOrPmcidFrEutil);
			cyNodeAttr.setListAttribute(nodeDocCountEUtil.getIdentifier(), "PMID", transformedUidList);
			// add pubmed link attribute
			pubMedLink="http://www.ncbi.nlm.nih.gov/pmc/";
		}
		
		for(Object id:uidOrPmcidFrEutil){
			pubMedLink=pubMedLink+(String)id+",";

		}
		
		cyNodeAttr.setAttribute(nodeDocCountEUtil.getIdentifier(), "PubMed link", pubMedLink);


		Cytoscape.getCurrentNetworkView().getNodeView(nodeDocCountEUtil).setXPosition(newNodeX);
		Cytoscape.getCurrentNetworkView().getNodeView(nodeDocCountEUtil).setYPosition(newNodeY);
//		newNodeX=xParentNode; //the coordinate is switched back to the position swissprot node
//		newNodeY=yParentNode;
//		newNodeX=newNodeX+xIncrement;
		// set node visualStyle
//		vs.addProperty(nodeDocCountEUtil.getIdentifier(),VisualPropertyType.NODE_BORDER_COLOR, "#008000"); //#FFFF00 yellow ; #008000 green
//		vs.addProperty(nodeDocCountEUtil.getIdentifier(), VisualPropertyType.NODE_FONT_SIZE, "6.0");
//		vs.addProperty(nodeDocCountEUtil.getIdentifier(), VisualPropertyType.NODE_LABEL, cyNodeAttr.getStringAttribute(nodeDocCountEUtil.getIdentifier(),"displayName"));

		StartStaticVariable.docCountTailing++;
		// create edge
		CyEdge edgeKeyword_docCountEutil=Cytoscape.getCyEdge(nodeKeyword, nodeDocCountEUtil, Semantics.INTERACTION, "textMining", true);
		// set edge attribute
		cyEdgeAttr.setAttribute(edgeKeyword_docCountEutil.getIdentifier(), "edgeType", "textMining");
		currentNet.addEdge(edgeKeyword_docCountEutil);

		// set edge visualStyle
//		vs.addProperty(edgeKeyword_docCountEutil.getIdentifier(), VisualPropertyType.EDGE_LINE_STYLE, "EQUAL_DASH");

	
	}
}
