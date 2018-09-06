package plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.data.CyAttributesUtils;
import cytoscape.data.Semantics;

import edu.emory.mathcs.backport.java.util.Collections;
import growNetwork.StartStaticVariable;

// BioEntityScoreCalculator-----------
//v1: add list attribute "cumulatedUidList" to all the bioentity node.
//v2 20110502: add VisualStyleBuilder to the constructor. The bioentity node size will change according to the summed ref count.
//v2 20110502: check if the cumulatedUidList attribute exists or not, before getting the value of this attribute(line 81,123)
//v3 20110530: change the way of getting the bioenity id from the refCount node. This class will transmit "all" the docCount and docList to the bioentity node.It doesn't matter if any node is selected in the network panel
//v3 20110712: rename the method sumDocCountToBioEntityNode to transmitDocCountToBioEntityNode
//SummarizeCitation--------------
//v0 20110722: modified from BioEntityScoreCalculator3
//v0 20110722: use CyAttributesUtils to get the refCount node
//v0 20110722: when summing the uid list, only the refList of the same keyword under the same bioentity can be merged. 
//			   Hide the old commonNameNode, keywordNode, refCountNode, and create new refCountNode as the child of bioentity node.
//v1 20110727: since the attribute name "UID" of refCountNode is changed to "PMID", change "UID" to "PMID" when using getListAttribute
//v2 20110728: use StartStaticVariable.hiddenNodeId to record the node id of hidden nodes
//v2 20110729: remove the code vs.addProperty(..). The dealing with visul style should be implemented in another class, like VizMapperBioentityLiteSearchNet
//v2 20110807: remove the VisualStyleBuilder argument from the constructor signature
//v3 20110809: iterate through every node in the cynetwork to find the refCount node, so that only the displayed node will be processed(?)

public class SummarizeCitation3 {
	CyNetwork cyNetwork;
	CyAttributes cyNodeAttr;
	CyAttributes cyEdgeAttr;
//	VisualStyleBuilder vs;
	HashMap<String,HashMap<String,ArrayList<String>>> wholeNetRefStore=new HashMap<String,HashMap<String,ArrayList<String>>>();
	
//	HashMap<StringArrayWrapper,ArrayList<String>> refListForCnKwPair=new HashMap<StringArrayWrapper,ArrayList<String>>();
//	HashMap<String,ArrayList<HashMap<StringArrayWrapper,ArrayList<String>>>> liteSearchMetaStore=new HashMap<String,ArrayList<HashMap<StringArrayWrapper,ArrayList<String>>>>();
	
	public SummarizeCitation3(CyNetwork cyNetwork_, CyAttributes cyNodeAttr_, CyAttributes cyEdgeAttr_){
		cyNetwork=cyNetwork_;
		cyNodeAttr=cyNodeAttr_;
		cyEdgeAttr=cyEdgeAttr_;
//		vs=vs_;
		StartStaticVariable.originalCyNodeList=(ArrayList<CyNode>) Cytoscape.getCyNodesList();
		StartStaticVariable.originalCyEdgeList=(ArrayList<CyEdge>) Cytoscape.getCyEdgesList();
		
		
//		ArrayList<String> nodeIdSetToValidate=new ArrayList();

//		Set<CyNode> nodeSetToValidate=cyNetwork.getSelectedNodes();
//
//		for(Object x:nodeSetToValidate){
//			nodeIdSetToValidate.add(((CyNode)x).getIdentifier());
//			System.out.println("initial selected ndoe id:"+((CyNode)x).getIdentifier());
//		}
		
		
		// remove the summedDocCount attribute if it is found in any node of the network
		// by iterating through all nodes in cyNetwork, it can skip the deleted node(?) 
		cyNetwork.selectAllNodes();
		Set allNodeSet=cyNetwork.getSelectedNodes();
		ArrayList<String> refCountNodeDisplayedIdSet=new ArrayList();
	
		for(Object oneNode:allNodeSet){
			// remove the existing summedDocCount value for the selected nodes in case the summing-doc operation has been performed before.
			if(cyNodeAttr.hasAttribute(((CyNode)oneNode).getIdentifier(), "sumUID")){
				System.out.println("is going to remove sumUID attribute of node :"+((CyNode)oneNode).getIdentifier()+"/"+this.getClass().getName());
				cyNodeAttr.deleteAttribute(((CyNode)oneNode).getIdentifier(), "sumUID");
			}
			if(cyNodeAttr.hasAttribute(((CyNode)oneNode).getIdentifier(), "nodeType")){
				String nodeTypeValue= (String)cyNodeAttr.getAttribute(((CyNode)oneNode).getIdentifier(), "nodeType");
				if(nodeTypeValue.startsWith("refCount")){
					refCountNodeDisplayedIdSet.add(((CyNode)oneNode).getIdentifier());
				}
			}
		}
		
		for(String refCountIdDisplayed:refCountNodeDisplayedIdSet){
			transmitDocCountToBioEntityNode(Cytoscape.getCyNode(refCountIdDisplayed, false));
		}
		
		/*
		//go through all the nodes in the current net and find the refCount node,then send the refCount node to method "sumDocCountToBioEntityNode"
		//use CyAttributesUtils to get all the identifier refCountNode.Using CyAttributesUtils might get the node which is deleted from the network view.
		List<String> refCountNodeIdList=CyAttributesUtils.getIDListFromAttributeValue(CyAttributesUtils.AttributeType.NODE, "nodeType", "refCountBiotext");
		refCountNodeIdList.addAll(CyAttributesUtils.getIDListFromAttributeValue(CyAttributesUtils.AttributeType.NODE, "nodeType", "refCountPM"));
		refCountNodeIdList.addAll(CyAttributesUtils.getIDListFromAttributeValue(CyAttributesUtils.AttributeType.NODE, "nodeType", "refCountPMC"));
		
		for(String oneRefNodeId:refCountNodeIdList){
			transmitDocCountToBioEntityNode(Cytoscape.getCyNode(oneRefNodeId, false));
		}
		*/
		
		
		// until this step, the commonName nodes and keyword nodes all should be hidden.If there is any such nodes not hidden, hide them now.
		List<String> commonNameNodeIdList=CyAttributesUtils.getIDListFromAttributeValue(CyAttributesUtils.AttributeType.NODE, "nodeType", "commonName");
		for(String nodeId:commonNameNodeIdList){
			cyNetwork.hideNode(Cytoscape.getCyNode(nodeId, false));
			StartStaticVariable.hiddenNodeId.add(nodeId);
//			try {
//				CyNodeUtils.hideNode(cyNetwork.getIdentifier(), nodeId);
//			} catch (XmlRpcException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
		}
		
		List<String> keywordNodeIdList=CyAttributesUtils.getIDListFromAttributeValue(CyAttributesUtils.AttributeType.NODE, "nodeType", "freeText");
		for(String nodeId:keywordNodeIdList){
			cyNetwork.hideNode(Cytoscape.getCyNode(nodeId,false));
			StartStaticVariable.hiddenNodeId.add(nodeId);
//			try {
//				CyNodeUtils.hideNode(cyNetwork.getIdentifier(), nodeId);
//			} catch (XmlRpcException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
		}
		
		// now the uid lists under the same bioentity and keyword have been merged together.
		// then is to create new node for the summed uid list and edges between the new node and bioentity.
		for(String bioentityNodeId:wholeNetRefStore.keySet()){
			System.out.println("one bioentity id in wholeNetRefStore is "+bioentityNodeId+"/"+this.getClass().getName());
			HashMap<String,ArrayList<String>> oneBioentityKwRefMap=wholeNetRefStore.get(bioentityNodeId);
			for(String keyword:oneBioentityKwRefMap.keySet()){
				String pubmedLink="http://www.ncbi.nlm.nih.gov/pubmed/";
				System.out.println("one keyword in oneBioentityRefStore is "+keyword+"/"+this.getClass().getName());
				ArrayList<String> uidList=oneBioentityKwRefMap.get(keyword);
				System.out.println("nubmer of redundent uid is:"+uidList.size()+this.getClass().getName()+" "+new Exception().getStackTrace()[0].getLineNumber());
				ArrayList<String> distinctUidList=new ArrayList<String>();
				int uidCount=0;
				for(String uid:uidList){
					System.out.println("one uid from redundent list is:"+uid);
					if(!distinctUidList.contains(uid)){
						distinctUidList.add(uid);
						if(uidCount<30){
							pubmedLink=pubmedLink+uid+",";
						}
					}
				}
				System.out.println("catenated pubmed link:"+pubmedLink+" "+this.getClass().getName());
				System.out.println(this.getClass().getName()+" "+new Exception().getStackTrace()[0].getLineNumber());
				CyNode oneKeywordOfOneBioentity=Cytoscape.getCyNode(keyword+"_"+bioentityNodeId, true);
				cyNetwork.addNode(oneKeywordOfOneBioentity);
				//set node attribute
				cyNodeAttr.setAttribute(oneKeywordOfOneBioentity.getIdentifier(), "keyword", keyword);
				cyNodeAttr.setListAttribute(oneKeywordOfOneBioentity.getIdentifier(), "PMID",distinctUidList);
				cyNodeAttr.setAttribute(oneKeywordOfOneBioentity.getIdentifier(), "refCount", distinctUidList.size());
				cyNodeAttr.setAttribute(oneKeywordOfOneBioentity.getIdentifier(), "displayName", keyword+":"+distinctUidList.size());
				cyNodeAttr.setAttribute(oneKeywordOfOneBioentity.getIdentifier(), "PubMed link", pubmedLink);
				cyNodeAttr.setAttribute(oneKeywordOfOneBioentity.getIdentifier(), "nodeType", "liteSearchSummary");
				//get x,y of bioentity node
				double x_bioentityNode=Cytoscape.getCurrentNetworkView().getNodeView(Cytoscape.getCyNode(bioentityNodeId, false)).getXPosition();
				double y_bioentityNode=Cytoscape.getCurrentNetworkView().getNodeView(Cytoscape.getCyNode(bioentityNodeId, false)).getYPosition();				
				//set x,y for the newly created keyword node
				Cytoscape.getCurrentNetworkView().getNodeView(oneKeywordOfOneBioentity).setXPosition(x_bioentityNode+25);
				Cytoscape.getCurrentNetworkView().getNodeView(oneKeywordOfOneBioentity).setYPosition(y_bioentityNode+25);
				System.out.println(this.getClass().getName()+" "+new Exception().getStackTrace()[0].getLineNumber());
				CyEdge edgeBioentity_uidListOfOneKeyword=Cytoscape.getCyEdge(Cytoscape.getCyNode(bioentityNodeId, false), oneKeywordOfOneBioentity, Semantics.INTERACTION, "summaryCitationForKw", true);
				cyNetwork.addEdge(edgeBioentity_uidListOfOneKeyword);
				System.out.println(this.getClass().getName()+" "+new Exception().getStackTrace()[0].getLineNumber());
				// set the visual style
//				vs.addProperty(edgeBioentity_uidListOfOneKeyword.getIdentifier(), VisualPropertyType.EDGE_LINE_STYLE, "EQUAL_DASH");
//				vs.addProperty(oneKeywordOfOneBioentity.getIdentifier(), VisualPropertyType.NODE_LABEL, cyNodeAttr.getStringAttribute(oneKeywordOfOneBioentity.getIdentifier(), "displayName"));
//				vs.addProperty(oneKeywordOfOneBioentity.getIdentifier(), VisualPropertyType.NODE_LINE_WIDTH, "6");
//				vs.buildStyle();
			}
		}
		
//		for(Object oneNode:allNodeSet){
//			
//			// set the size for all nodes to the default value
//			vs_.addProperty(((CyNode)oneNode).getIdentifier(), VisualPropertyType.NODE_SIZE, "8");
//
//			// get the refCount node and transmit its docCount to the bioentity node
//			// the node size of bioentity nodes with accumulated ref count attribute will be changed after the call to "sumDocCountToBioEntityNode"
//			if(cyNodeAttr.hasAttribute(((CyNode)oneNode).getIdentifier(), "nodeType")){
//				String nodeType=cyNodeAttr.getStringAttribute(((CyNode)oneNode).getIdentifier(), "nodeType");
//				if(nodeType.equalsIgnoreCase("refCountBiotext")||nodeType.equalsIgnoreCase("refCountEutil")||nodeType.equalsIgnoreCase("RefinedUidListForBioTx")||nodeType.equalsIgnoreCase("RefinedUidListForEutil")){
//					// "oneNode" here is the refCount node(from BioText or Eutility)
//					transmitDocCountToBioEntityNode((CyNode)oneNode);
//
//				}
//			}
//
//		}
	
	}

	// the bioentity node size will be changed here
	// the refCount, commonName and keyword nodes are set to be hidden.
	public void transmitDocCountToBioEntityNode(CyNode literatureListNode){
		String nodeType=cyNodeAttr.getStringAttribute(((CyNode)literatureListNode).getIdentifier(), "nodeType");
		cyNetwork.hideNode(literatureListNode);
		
		if(nodeType.equalsIgnoreCase("refCountBiotext")||nodeType.equalsIgnoreCase("refCountPM")||nodeType.equalsIgnoreCase("refCountPMC")){

//			int docCount=Integer.parseInt(cyNodeAttr.getStringAttribute(((CyNode)literatureListNode).getIdentifier(), "docCount"));
			List<String> uidList=cyNodeAttr.getListAttribute(((CyNode)literatureListNode).getIdentifier(), "PMID");
//			System.out.println(((CyNode)literatureListNode).getIdentifier()+" doc count is:"+docCount);
			
			//keywordNodeId is like "Kw:redox|CN:L-2-Aminopropionic acid"
			String keywordNodeId=cyNodeAttr.getStringAttribute(((CyNode)literatureListNode).getIdentifier(), "parentNodeId");
			String keyword="";
			if(keywordNodeId.startsWith("Kw:")){
				//kwNodeDisplayName is like "Kw:redox"
				String kwNodeDisplayName=cyNodeAttr.getStringAttribute(keywordNodeId, "displayName");
				keyword=kwNodeDisplayName.replaceFirst("Kw:", "");
				//now keyword is like "redox"
			}
			cyNetwork.hideNode(Cytoscape.getCyNode(keywordNodeId, false));
			String commonNameNodeId=cyNodeAttr.getStringAttribute(keywordNodeId, "parentNodeId");
			cyNetwork.hideNode(Cytoscape.getCyNode(commonNameNodeId, false));
			String bioEntityNodeId=cyNodeAttr.getStringAttribute(commonNameNodeId, "parentNodeId");
			System.out.println("selected bio entity id:"+bioEntityNodeId);
			
			// create a data structure which contains the refList from each search engine for each bioentity
			if(wholeNetRefStore.containsKey(bioEntityNodeId)){
				HashMap<String,ArrayList<String>> oneBioentityRefStore=wholeNetRefStore.get(bioEntityNodeId);
				if(oneBioentityRefStore.containsKey(keyword)){
					ArrayList<String> oldUidList=oneBioentityRefStore.get(keyword);
					oldUidList.addAll(uidList);
					oneBioentityRefStore.put(keyword,oldUidList);
				}else{
					oneBioentityRefStore.put(keyword, new ArrayList(uidList));
				}
			}else{
				HashMap<String,ArrayList<String>> oneBioentityRefStore=new HashMap<String,ArrayList<String>>();
				oneBioentityRefStore.put(keyword, new ArrayList(uidList));
				wholeNetRefStore.put(bioEntityNodeId, oneBioentityRefStore);
			}
			
			// the code below is marked out because the ref search result from different keywords can't be merge to one single list
//			if(cyNodeAttr.hasAttribute(bioEntityNodeId, "summedDocCount")){
//				System.out.println("iiiiiiii");
//				
//				List<String> updatedUidList;
//
//				//check if the cumulatedUidList attribute exists or not, then update "updatedUidList"
//				if(cyNodeAttr.hasAttribute(bioEntityNodeId, "cumulatedUidList")){
//					(updatedUidList=(List)cyNodeAttr.getListAttribute(bioEntityNodeId, "cumulatedUidList")).addAll(uidList);
//				}else{
//					updatedUidList=uidList;
//				}
//
//				// get the unique value in updatedUidList
//				ArrayList uniqueVals=new ArrayList();
//				for(Object x:updatedUidList){
//					if(!uniqueVals.contains(x)){
//						uniqueVals.add(x);
//					}
//				}
//				
//				//if uniqueVals contains no element, sort() will generate error
//				if(uniqueVals.size()>0){
//					Collections.sort(uniqueVals);
//				}
//				
//				cyNodeAttr.setListAttribute(bioEntityNodeId, "cumulatedUidList", uniqueVals);
//
//				cyNodeAttr.setAttribute(bioEntityNodeId, "summedDocCount", uniqueVals.size());
//
//				// reset the node size for the bioentity after summedDocCount is updated.
//				vs.addProperty(bioEntityNodeId, VisualPropertyType.NODE_SIZE, (8+(int)(uniqueVals.size()/10)+1)+"");
//				vs.addProperty(bioEntityNodeId, VisualPropertyType.NODE_FILL_COLOR, "#FFD700");
//			}else{   // if the "summedDocCount" attribute has never been initiated
//				System.out.println("ppppppppp");
//				cyNodeAttr.setAttribute(bioEntityNodeId, "summedDocCount", docCount);
//				ArrayList uidListOfTextMiningNode= (ArrayList)cyNodeAttr.getListAttribute(((CyNode)literatureListNode).getIdentifier(), "UID");
//				
//				if(uidListOfTextMiningNode.size()>0){
//					Collections.sort(uidListOfTextMiningNode);
//				}
//				cyNodeAttr.setListAttribute(bioEntityNodeId, "cumulatedUidList", uidListOfTextMiningNode);
//
//				vs.addProperty(bioEntityNodeId, VisualPropertyType.NODE_SIZE, (8+(int)(docCount/10)+1)+"");
//				vs.addProperty(bioEntityNodeId, VisualPropertyType.NODE_FILL_COLOR, "#FFD700");
//			}



		}else if(nodeType.equalsIgnoreCase("RefinedUidListForBioTx")||nodeType.equalsIgnoreCase("RefinedUidListForEutil")){
			//here the textMiningNode is the refinedUidList node
			int docCount=Integer.parseInt(cyNodeAttr.getStringAttribute(((CyNode)literatureListNode).getIdentifier(), "docCount"));
			List<String> uidList=cyNodeAttr.getListAttribute(((CyNode)literatureListNode).getIdentifier(), "PMID");
			System.out.println(((CyNode)literatureListNode).getIdentifier()+" doc count is:"+docCount);
			String preRefinedNodeId=cyNodeAttr.getStringAttribute(((CyNode)literatureListNode).getIdentifier(), "parentNodeId");
			String keywordNodeId=cyNodeAttr.getStringAttribute(preRefinedNodeId, "parentNodeId");
			String commonNameNodeId=cyNodeAttr.getStringAttribute(keywordNodeId, "parentNodeId");
			String bioEntityNodeId=cyNodeAttr.getStringAttribute(commonNameNodeId, "parentNodeId");
			if(cyNodeAttr.hasAttribute(bioEntityNodeId, "summedDocCount")){
				System.out.println("ddddddddddddddddddd");
				
				List<String> updatedUidList;

				//check if the cumulatedUidList attribute exists or not, then update "updatedUidList"
				if(cyNodeAttr.hasAttribute(bioEntityNodeId, "cumulatedUidList")){
					(updatedUidList=(List<String>)cyNodeAttr.getListAttribute(bioEntityNodeId, "cumulatedUidList")).addAll(uidList);
				}else{
					updatedUidList=uidList;
				}

				// get the unique value in updatedUidList
				ArrayList<String> uniqueVals=new ArrayList();
				for(Object x:updatedUidList){
					if(!uniqueVals.contains(x)){
						uniqueVals.add((String)x);
					}
				}
				
				if(uniqueVals.size()>0){
					Collections.sort(uniqueVals);
				}
				cyNodeAttr.setListAttribute(bioEntityNodeId, "cumulatedUidList", uniqueVals);
				cyNodeAttr.setAttribute(bioEntityNodeId, "summedDocCount", uniqueVals.size());

				// reset the node size for the bioentity after summedDocCount is updated.
//				vs.addProperty(bioEntityNodeId, VisualPropertyType.NODE_SIZE, (8+(int)(uniqueVals.size()/10)+1)+"");
//				vs.addProperty(bioEntityNodeId, VisualPropertyType.NODE_FILL_COLOR, "#FFD700");
			}else{  // if the "summedDocCount" attribute has never been initiated
				System.out.println("ssssssssssssssssssssssss");
				cyNodeAttr.setAttribute(bioEntityNodeId, "summedDocCount", docCount);
				ArrayList uidListOfTextMiningNode=(ArrayList)cyNodeAttr.getListAttribute(((CyNode)literatureListNode).getIdentifier(), "PMID");
				
				if(uidListOfTextMiningNode.size()>0){
					Collections.sort(uidListOfTextMiningNode);
				}
				
				cyNodeAttr.setListAttribute(bioEntityNodeId, "cumulatedUidList", uidListOfTextMiningNode);

//				vs.addProperty(bioEntityNodeId, VisualPropertyType.NODE_SIZE, (8+(int)(docCount/10)+1)+"");
//				vs.addProperty(bioEntityNodeId, VisualPropertyType.NODE_FILL_COLOR, "#FFD700");
			}
		}
	}

	public CyAttributes getNodeAttributes(){
		return cyNodeAttr;
	}

//	public VisualStyleBuilder getVisualStyleBuilder(){
//		return vs;
//	}
}
