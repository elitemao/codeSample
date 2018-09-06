package plugin;

import edu.emory.mathcs.backport.java.util.Collections;
import growNetwork.StartStaticVariable;

import java.util.ArrayList;
import java.util.HashMap;

import textMining.BioTextAgent;
import textMining.BioTextParser;
import textMining.ESummaryResultParser;
import textMining.EutilityPubMed5;
import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.data.Semantics;
import cytoscape.data.readers.VisualStyleBuilder;
import cytoscape.visual.VisualPropertyType;

//BioTextTreeProducer------------------
//v0 20110529: Copied and modified from the biotext related codes in NcDocReSy9
//v1 20110608: change the signature of the constructor to allow multiple keywords for BioText search
//v1 20110718: use EutilityPubMed5
//v2 20110809: change the attribute name "UID" to "PMID"

public class BioTextNodeProducer2 {

	public BioTextNodeProducer2(ArrayList<String> keywords,String oneCommonName_hex,boolean hasDateRestriction,String yearLimit,double keywordNodeX,double keywordNodeY,double xIncrement,double yIncrement,VisualStyleBuilder vs,CyNode nodeKeyword){
		System.out.println("biotext is used");
		CyNetwork currentNet=Cytoscape.getCurrentNetwork();
		CyAttributes cyNodeAttr=Cytoscape.getNodeAttributes();
		CyAttributes cyEdgeAttr=Cytoscape.getEdgeAttributes();
		double newNodeX=keywordNodeX+xIncrement;  //the new coordinate is for the next node(refCount node)
		double newNodeY=keywordNodeY+yIncrement;
	
		ArrayList queryArrayBioText=new ArrayList();
		for(Object keyword:keywords){
			queryArrayBioText.add(keyword);
		}
		queryArrayBioText.add(oneCommonName_hex);
		
		System.out.println("now is sending query to BioText");
		BioTextAgent bioTxAgent=new BioTextAgent(queryArrayBioText);
		System.out.println("now is parsing the biotext result");
		BioTextParser bioTxParser=new BioTextParser(bioTxAgent.getHtml());

		ArrayList uidFrBioTx=bioTxParser.getUids();

		//if user has set the publication date restriction, do it now
		if(hasDateRestriction==true){
			EutilityPubMed5 euPubMed4=new EutilityPubMed5();
			String eSummaryResultInXml=euPubMed4.getESummaryResult(uidFrBioTx);
			ESummaryResultParser eSummaryParser=new ESummaryResultParser(eSummaryResultInXml);
			HashMap ePubDate=eSummaryParser.getEPubDate();
			// the value in ePubDate is like "2007 Oct 29"
			for(Object pmid:ePubDate.keySet()){
				String pubYear=((String)ePubDate.get((String)pmid)).split(" ")[0];
				if(Integer.parseInt(pubYear)>Integer.parseInt(yearLimit)){
					// if the publication year is later than the user input, remove it form uidFrBioTx
					uidFrBioTx.remove((String)pmid);
				}
			} 
		}

		CyNode nodeDocCountBioTx=Cytoscape.getCyNode("Bx:"+uidFrBioTx.size()+"_"+StartStaticVariable.docCountTailing,true);
		currentNet.addNode(nodeDocCountBioTx); //new node has to be added to the net before setting its coordinate
		// set node attribute
		cyNodeAttr.setAttribute(nodeDocCountBioTx.getIdentifier(), "displayName", "Bx:"+uidFrBioTx.size());
		cyNodeAttr.setAttribute(nodeDocCountBioTx.getIdentifier(), "nodeType", "refCountBiotext");
		cyNodeAttr.setAttribute(nodeDocCountBioTx.getIdentifier(), "moleType", "refCountBiotext");
		cyNodeAttr.setAttribute(nodeDocCountBioTx.getIdentifier(), "docCount", uidFrBioTx.size()+"");
		//add parentNodeId attribute so that the parent node is tractable
		//		cyNodeAttr.setAttribute(nodeDocCountBioTx.getIdentifier(), "parentNodeId", nodeCommonName.getIdentifier());
		cyNodeAttr.setAttribute(nodeDocCountBioTx.getIdentifier(), "parentNodeId", nodeKeyword.getIdentifier());
		Collections.sort(uidFrBioTx);
		cyNodeAttr.setListAttribute(nodeDocCountBioTx.getIdentifier(), "PMID", uidFrBioTx);
		// add pubmed link attribute
		String pubMedLink="http://www.ncbi.nlm.nih.gov/pubmed/";
		for(Object uid:uidFrBioTx){
			pubMedLink=pubMedLink+(String)uid+",";

		}
		cyNodeAttr.setAttribute(nodeDocCountBioTx.getIdentifier(), "PubMed link", pubMedLink);

		Cytoscape.getCurrentNetworkView().getNodeView(nodeDocCountBioTx).setXPosition(newNodeX);
		Cytoscape.getCurrentNetworkView().getNodeView(nodeDocCountBioTx).setYPosition(newNodeY);
//		newNodeX=newNodeX+xIncrement;  //the new node will be in a row, so y coordinate doen't need to be changed.

		// set node visualStyle
		vs.addProperty(nodeDocCountBioTx.getIdentifier(), VisualPropertyType.NODE_BORDER_COLOR, "#0000FF");//#0000FF light blue
		vs.addProperty(nodeDocCountBioTx.getIdentifier(), VisualPropertyType.NODE_LABEL, cyNodeAttr.getStringAttribute(nodeDocCountBioTx.getIdentifier(), "displayName"));
		vs.addProperty(nodeDocCountBioTx.getIdentifier(), VisualPropertyType.NODE_FONT_SIZE, "6.0");

		StartStaticVariable.docCountTailing++;
		// create edge
		CyEdge edgeKeyword_docCountBioTx=Cytoscape.getCyEdge(nodeKeyword, nodeDocCountBioTx, Semantics.INTERACTION, "textMining", true);
		// set edge attribute
		cyEdgeAttr.setAttribute(edgeKeyword_docCountBioTx.getIdentifier(),"edgeType", "textMining");
		currentNet.addEdge(edgeKeyword_docCountBioTx);
		// set edge visualStyle
		vs.addProperty(edgeKeyword_docCountBioTx.getIdentifier(), VisualPropertyType.EDGE_LINE_STYLE, "EQUAL_DASH");
	}


}
