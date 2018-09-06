package plugin;

import java.util.ArrayList;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import textMining.ValidateProteinCommonName;
import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.data.Semantics;
import cytoscape.data.readers.VisualStyleBuilder;
import cytoscape.view.CyNetworkView;
import cytoscape.visual.LineStyle;
import cytoscape.visual.NodeShape;
import cytoscape.visual.VisualPropertyType;

// CommonNameRootedTreeProducer-------------------------
//v0 20110530: extract the codes for producing commonNameNode and its descendant nodes from NcDocReSy10.
//v1 20110608: use BioTextNodeProducer1 and change the argument name in the constructor(keyword->originalKeywordString) and add new variable(encodedKeywordString)
//             All these changes is to allow multiple keywords sent to biotext and eutil.
//v1 20110616: add one more argument(usePmc) in the signature of constructor. Use EutilNodeProducer1
//v2 20110621: use EutilNodeProducer2
//v3 20110721: change the parameter name useEutil to usePubmed in the constructor signature.This new class needs the specification of pmc,pubmed,biotext. Use EutilNodeProducer3.
//v3 20110722: change the name of variables which concern the judgment of existence of biotext,pmc, pm nodes.
//v4 20110724: use EutilNodeProducer4
//v4 20110729: remove the code of vs.addProperty(..). This process will be carried out in VizMapperBioentityLiteSearchNet later.
//v4 20110807: To determine if pm,biotext,pmc have been used for a commonName and keyword combination, the code detect the content("refCountBiotext" or "refCountEutil") of nodeType attribute. 
//             But after EutilNodeProducer3 is used, the content of eutilityNode became "refCountPM" or "refCountPMC". So the code around line 150 is changed accordingly.
//v7 20110809: modified from v4. Use BioTextNodeProducer2

public class CommonNameRootedTreeProducer7 {
	
	// parentCyNode in the constructor could be an enzyme node or spacc node
	public CommonNameRootedTreeProducer7(CyNode parentCyNode,String oneName,double xIncrement,double yIncrement, VisualStyleBuilder vs, String originalKeywordString,boolean useDate, boolean useBiotext,boolean usePubmed,String yearLimit, boolean usePmc){

		Pattern ptnBioTxCountIdetifier=Pattern.compile("Bx:.*");
		Pattern ptnEutilPmCountIdentifier=Pattern.compile("pm:.*");
		Pattern ptnEutilPmcCountIdentifier=Pattern.compile("pmc:.*");
		CyNetwork currentNet=Cytoscape.getCurrentNetwork();
		double xParentNode=Cytoscape.getCurrentNetworkView().getNodeView((CyNode)parentCyNode).getXPosition();
		double yParentNode=Cytoscape.getCurrentNetworkView().getNodeView((CyNode)parentCyNode).getYPosition();
		CyAttributes cyNodeAttr=Cytoscape.getNodeAttributes();
		CyAttributes cyEdgeAttr=Cytoscape.getEdgeAttributes();
		double newNodeX=xParentNode+xIncrement;
		double newNodeY=yParentNode+yIncrement;
			System.out.println("one name is kdjslkjfklsdjlkjl:"+(String)oneName);
			//create new node and add to the net
			CyNode nodeCommonName=Cytoscape.getCyNode("CN:"+(String)oneName,true);
			currentNet.addNode(nodeCommonName); //new node has to be added to the net before setting its coordinate

			// assign a coordinate to the common name node
			Cytoscape.getCurrentNetworkView().getNodeView(nodeCommonName).setXPosition(newNodeX);
			Cytoscape.getCurrentNetworkView().getNodeView(nodeCommonName).setYPosition(newNodeY);
			newNodeX=newNodeX+0;  //the next new node will be the keyword node. It has the same x coordinate but a new y coordinate
			newNodeY=newNodeY+yIncrement;

			//add attribute of the common name node
			cyNodeAttr.setAttribute(nodeCommonName.getIdentifier(), "nodeType", "commonName");
			cyNodeAttr.setAttribute(nodeCommonName.getIdentifier(), "moleType", "commonName");
			cyNodeAttr.setAttribute(nodeCommonName.getIdentifier(), "commonName", (String)oneName);
			cyNodeAttr.setAttribute(nodeCommonName.getIdentifier(), "displayName", nodeCommonName.getIdentifier());
			cyNodeAttr.setAttribute(nodeCommonName.getIdentifier(), "parentNodeId", ((CyNode)parentCyNode).getIdentifier());
			
			//create new edge between the ec(or spacc) node and common name node and add to the net
			CyEdge edgeSpacc_commonName=Cytoscape.getCyEdge((CyNode)parentCyNode, nodeCommonName, Semantics.INTERACTION, "textMining", true);
			System.out.println(edgeSpacc_commonName.getIdentifier());
			currentNet.addEdge(edgeSpacc_commonName);
			// set edge attribute
			cyEdgeAttr.setAttribute(edgeSpacc_commonName.getIdentifier(), "edgeType", "annotation");
			
			// set the visualstyle of the common name node and the spacc-common_name edge
			
//			vs.addProperty(edgeSpacc_commonName.getIdentifier(), VisualPropertyType.EDGE_LINE_STYLE, "EQUAL_DASH");
//			vs.addProperty(nodeCommonName.getIdentifier(), VisualPropertyType.NODE_SHAPE, NodeShape.OCTAGON.getShapeName());
//			vs.addProperty(nodeCommonName.getIdentifier(), VisualPropertyType.NODE_BORDER_COLOR, "#FF0000"); //#FF0000 red
//			vs.addProperty(nodeCommonName.getIdentifier(), VisualPropertyType.NODE_LABEL, cyNodeAttr.getStringAttribute(nodeCommonName.getIdentifier(), "displayName"));
//			vs.addProperty(nodeCommonName.getIdentifier(), VisualPropertyType.NODE_FONT_SIZE, "6.0");
//			vs.buildStyle();
			/////////////////////////////////
			Cytoscape.createNetworkView(currentNet);
			/////////////////////////////////
//			Cytoscape.firePropertyChange(Cytoscape.ATTRIBUTES_CHANGED, null, null);
//			Cytoscape.getCurrentNetworkView().redrawGraph(false, true);
//			CyNetworkView cyView=Cytoscape.getCurrentNetworkView();
//			cyView.updateView();

			// create keyword node
//			keyword=keyword.replaceAll(" ","%20");
//			
			CyNode nodeKeyword=Cytoscape.getCyNode("Kw:"+originalKeywordString+"|"+nodeCommonName,true);
			currentNet.addNode(nodeKeyword);
			// set keyword node attribute
			cyNodeAttr.setAttribute(nodeKeyword.getIdentifier(), "nodeType", "freeText");
			cyNodeAttr.setAttribute(nodeKeyword.getIdentifier(), "moleType", "freeText");
			cyNodeAttr.setAttribute(nodeKeyword.getIdentifier(), "displayName", "Kw:"+originalKeywordString);
			cyNodeAttr.setAttribute(nodeKeyword.getIdentifier(), "parentNodeId", nodeCommonName.getIdentifier());
			Cytoscape.getCurrentNetworkView().getNodeView(nodeKeyword).setXPosition(newNodeX);
			Cytoscape.getCurrentNetworkView().getNodeView(nodeKeyword).setYPosition(newNodeY);
			newNodeX=newNodeX+xIncrement;  //the new coordinate is for the next node(refCount node)
			newNodeY=newNodeY+yIncrement;
			
			// create edge between commonName node and keyword node
			CyEdge edgeCommonName_Kw=Cytoscape.getCyEdge(nodeCommonName,nodeKeyword , Semantics.INTERACTION, "textMining", true);
			currentNet.addEdge(edgeCommonName_Kw);
			
			//set edge(commonName node to keyword node) attribute
			cyEdgeAttr.setAttribute(edgeCommonName_Kw.getIdentifier(), "edgeType", "textMining");

			// set visualStyle of keyword node and edge(commonName node to keyword node) 
//			vs.addProperty(edgeCommonName_Kw.getIdentifier(), VisualPropertyType.EDGE_LINE_STYLE,"EQUAL_DASH");
//			vs.addProperty(nodeKeyword.getIdentifier(), VisualPropertyType.NODE_SHAPE, NodeShape.TRAPEZOID.getShapeName());
//			vs.addProperty(nodeKeyword.getIdentifier(), VisualPropertyType.NODE_BORDER_COLOR, "#804000"); //#804000 brown
//			//							vs.addProperty(nodeKeyword.getIdentifier(), VisualPropertyType.NODE_LABEL, nodeKeyword.getIdentifier());
//			vs.addProperty(nodeKeyword.getIdentifier(), VisualPropertyType.NODE_LABEL, cyNodeAttr.getStringAttribute(nodeKeyword.getIdentifier(), "displayName"));
//			vs.addProperty(nodeKeyword.getIdentifier(), VisualPropertyType.NODE_FONT_SIZE, "6.0");
//			vs.buildStyle();

			

			//Check if the biotext or eutil has been used to search for the commonName and "redox" of the selected node.
			//The purpose of doing this is that more than one swissprot acc can map to the same common name.
			//For instance P0AEP8 and P0AEP7 both have common name "Tartronate-semialdehyde synthase" and "Glyoxylate carboligase". If "get ref" is used on both spacc,
			//there will be duplicated "Bx:*_*" and "eU:*_*" nodes for P0AEP8 and P0AEP7.
			boolean existDocCountBioText=false;
			boolean existDocCountPm=false;
			boolean existDocCountPmc=false;
			// get the current "displayed" nodeList, then check if the refCountNode exists or not.
			// The method for checking if a refCountNode has existed(by using "refCountNodeList") in the previous version can't keep track of the change made by deleting the node in the network panel
			CyNetworkView cyViewx=Cytoscape.getCurrentNetworkView();
			CyNetwork displayedNetwork=cyViewx.getNetwork();
			displayedNetwork.selectAllNodes();
			Set displayedNodeList=displayedNetwork.getSelectedNodes();
			//if the dispalyedNetwork is unselect all nodes, displayedNodeList will be updated immediately so that it will become empty.
			//So... save all the id of displayed nodes in an arraylist.
			ArrayList idListOfDisplayedNode=new ArrayList();
			
			for(Object oneDisplayedNode:displayedNodeList){
				idListOfDisplayedNode.add(((CyNode)oneDisplayedNode).getIdentifier());
			}
			displayedNetwork.unselectAllNodes();
			
			for(Object oneDisplayedNodeId:idListOfDisplayedNode){
				
				System.out.println("one node in current net is:"+oneDisplayedNodeId);
				if(cyNodeAttr.hasAttribute((String)oneDisplayedNodeId, "nodeType")){

					String nodeTypexx=cyNodeAttr.getStringAttribute((String)oneDisplayedNodeId, "nodeType");
					if(nodeTypexx.equalsIgnoreCase("refCountBiotext")||nodeTypexx.equalsIgnoreCase("refCountPM")||nodeTypexx.equalsIgnoreCase("refCountPMC")){
						// check if the node id with the attribute value "refCountBiotext" or "refCountEutil" start with "Bx:" or "eU:"
						Matcher mtchEutilPmCountNode=ptnEutilPmCountIdentifier.matcher((String)oneDisplayedNodeId);						
						Matcher mtchEutilPmcCountNode=ptnEutilPmcCountIdentifier.matcher((String)oneDisplayedNodeId);
						Matcher mtchBioTxCountNode=ptnBioTxCountIdetifier.matcher((String)oneDisplayedNodeId);
						// get the upstream commonName node value for "Bx:" or "eu:" node
						String parentNodeAttri=cyNodeAttr.getStringAttribute((String)oneDisplayedNodeId, "parentNodeId");
						System.out.println("parentNodeAttri is:"+parentNodeAttri);
						System.out.println("one name is:"+(String)oneName);
//						if (parentNodeAttri.equals("CN:"+(String)oneName)){
						if (parentNodeAttri.equals(nodeKeyword.getIdentifier())){
							if(mtchBioTxCountNode.matches() ){
								System.out.println("mtadchBioTxCountNode matches");

								System.out.println(parentNodeAttri +" is equal to "+oneName);
								existDocCountBioText=true;
							}
							if(mtchEutilPmCountNode.matches() ){
								existDocCountPm=true;
							}
							if(mtchEutilPmcCountNode.matches() ){
								existDocCountPmc=true;
							}
						}
					}
				}
			}
			
			// it's about to get the ref count from Biotext and eUtil for one common name
			String validatedCommonName=(new ValidateProteinCommonName((String)oneName)).convert();
			String oneCommonName_hex=validatedCommonName.replaceAll(" ", "%20");

			String encodedKeywordString=originalKeywordString.replaceAll(" ","%20");
//			encodedKeywordString=originalKeywordString.replaceAll(",", "%22%2B%22");

			// prepare for E-utility "pubmed" query
			if(usePubmed==true){
				System.out.println("E-utility pubmed is used");

				if(existDocCountPm==false){
//					EutilNodeProducer euNodeProd=new EutilNodeProducer(keyword,oneCommonName_hex,useDate,yearLimit,newNodeX,newNodeY,xIncrement,yIncrement,vs,nodeKeyword);
					EutilNodeProducer4 euNodeProd=new EutilNodeProducer4(encodedKeywordString,oneCommonName_hex,false,useDate,yearLimit,newNodeX,newNodeY,xIncrement,yIncrement,vs,nodeKeyword);

				}
			}
			
			// prepare for E-utility "pmc" query
			if(usePmc==true){
				System.out.println("E-utility pmc is used");

				if(existDocCountPmc==false){
//					EutilNodeProducer euNodeProd=new EutilNodeProducer(keyword,oneCommonName_hex,useDate,yearLimit,newNodeX,newNodeY,xIncrement,yIncrement,vs,nodeKeyword);
					EutilNodeProducer4 euNodeProd=new EutilNodeProducer4(encodedKeywordString,oneCommonName_hex,true,useDate,yearLimit,newNodeX,newNodeY,xIncrement,yIncrement,vs,nodeKeyword);

				}
			}
			
			//// prepare for biotext query						
			if(useBiotext==true){
				System.out.println("biotext is used");
				String[] keywordArray=originalKeywordString.split(",");
				ArrayList<String> keywordList=new ArrayList<String>();
				for(String y:keywordArray){
					keywordList.add(y);
				}
				if(existDocCountBioText==false){
//					BioTextNodeProducer bioTxNodeProd=new BioTextNodeProducer(keyword,oneCommonName_hex,useDate,yearLimit,newNodeX,newNodeY,xIncrement,yIncrement,vs,nodeKeyword);										
					BioTextNodeProducer2 bioTxNodeProd=new BioTextNodeProducer2(keywordList,oneCommonName_hex,useDate,yearLimit,newNodeX,newNodeY,xIncrement,yIncrement,vs,nodeKeyword);										
				}
			}
			//							String queryInUrl="term=%22redox%22+"+"%22"+(String)oneName+"%22"+"&db=pubmed&retmax=1000000";
			//							HashMap queryKeyValuePair=new HashMap();
			//							queryKeyValuePair.put("term", "%22redox%22+"+"%22"+(String)oneName+"%22");
			//							queryKeyValuePair.put("db", "pubmed");
			//							queryKeyValuePair.put("retmax","10000000");

			
//			vs.buildStyle();
			Cytoscape.firePropertyChange(Cytoscape.ATTRIBUTES_CHANGED, null, null);
			Cytoscape.getCurrentNetworkView().redrawGraph(false, true);
			Cytoscape.getCurrentNetworkView().updateView();
			//							totalUid.addAll(eutilParser.getUID());
		
		
	}

}
