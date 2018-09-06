package plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.data.CyAttributes;
import cytoscape.data.readers.VisualStyleBuilder;
import cytoscape.visual.NodeShape;
import cytoscape.visual.VisualPropertyType;
import edu.emory.mathcs.backport.java.util.Collections;

// BioEntityScoreCalculator-----------
//v1: add list attribute "cumulatedUidList" to all the bioentity node.
//v2 20110502: add VisualStyleBuilder to the constructor. The bioentity node size will change according to the summed ref count.
//v2 20110502: check if the cumulatedUidList attribute exists or not, before getting the value of this attribute(line 81,123)
//v3 20110530: change the way of getting the bioenity id from the refCount node. This class will transmit "all" the docCount and docList to the bioentity node.It doesn't matter if any node is selected in the network panel
//v3 20110712: rename the method sumDocCountToBioEntityNode to transmitDocCountToBioEntityNode

public class BioEntityScoreCalculator3 {
	CyAttributes cyNodeAttr;
	CyAttributes cyEdgeAttr;
	VisualStyleBuilder vs;

	public BioEntityScoreCalculator3(CyNetwork cyNetwork, CyAttributes cyNodeAttr_, CyAttributes cyEdgeAttr_, VisualStyleBuilder vs_){
		cyNodeAttr=cyNodeAttr_;
		cyEdgeAttr=cyEdgeAttr_;
		vs=vs_;

//		ArrayList<String> nodeIdSetToValidate=new ArrayList();

//		Set<CyNode> nodeSetToValidate=cyNetwork.getSelectedNodes();
//
//		for(Object x:nodeSetToValidate){
//			nodeIdSetToValidate.add(((CyNode)x).getIdentifier());
//			System.out.println("initial selected ndoe id:"+((CyNode)x).getIdentifier());
//		}
		
		
		// remove the summedDocCount attribute if it is found in any node of the network
		cyNetwork.selectAllNodes();
		Set allNodeSet=cyNetwork.getSelectedNodes();

		for(Object oneNode:allNodeSet){
			// remove the existing summedDocCount value for the selected nodes in case the summing-doc operation has been performed before.
			if(cyNodeAttr.hasAttribute(((CyNode)oneNode).getIdentifier(), "summedDocCount")){
				System.out.println("is going to remove summedDocCount attribute of node :"+((CyNode)oneNode).getIdentifier());
				cyNodeAttr.deleteAttribute(((CyNode)oneNode).getIdentifier(), "summedDocCount");
			}
		}
		
		
		//go through all the nodes in the current net and find the refCount node,then send the refCount node to method "sumDocCountToBioEntityNode"
		for(Object oneNode:allNodeSet){
			
			// set the size for all nodes to the default value
			vs_.addProperty(((CyNode)oneNode).getIdentifier(), VisualPropertyType.NODE_SIZE, "8");

			// get the refCount node and transmit its docCount to the bioentity node
			// the node size of bioentity nodes with accumulated ref count attribute will be changed after the call to "sumDocCountToBioEntityNode"
			if(cyNodeAttr.hasAttribute(((CyNode)oneNode).getIdentifier(), "nodeType")){
				String nodeType=cyNodeAttr.getStringAttribute(((CyNode)oneNode).getIdentifier(), "nodeType");
				if(nodeType.equalsIgnoreCase("refCountBiotext")||nodeType.equalsIgnoreCase("refCountEutil")||nodeType.equalsIgnoreCase("RefinedUidListForBioTx")||nodeType.equalsIgnoreCase("RefinedUidListForEutil")){
					// "oneNode" here is the refCount node(from BioText or Eutility)
					transmitDocCountToBioEntityNode((CyNode)oneNode);

				}
			}

		}
	
	}

	// the bioentity node size will be changed here
	public void transmitDocCountToBioEntityNode(CyNode literatureListNode){
		String nodeType=cyNodeAttr.getStringAttribute(((CyNode)literatureListNode).getIdentifier(), "nodeType");
		if(nodeType.equalsIgnoreCase("refCountBiotext")||nodeType.equalsIgnoreCase("refCountEutil")){
			int docCount=Integer.parseInt(cyNodeAttr.getStringAttribute(((CyNode)literatureListNode).getIdentifier(), "docCount"));
			List<String> uidList=cyNodeAttr.getListAttribute(((CyNode)literatureListNode).getIdentifier(), "UID");
			System.out.println(((CyNode)literatureListNode).getIdentifier()+" doc count is:"+docCount);

			String keywordNodeId=cyNodeAttr.getStringAttribute(((CyNode)literatureListNode).getIdentifier(), "parentNodeId");
			String commonNameNodeId=cyNodeAttr.getStringAttribute(keywordNodeId, "parentNodeId");
			String bioEntityNodeId=cyNodeAttr.getStringAttribute(commonNameNodeId, "parentNodeId");
			System.out.println("selected bio entity id:"+bioEntityNodeId);
			if(cyNodeAttr.hasAttribute(bioEntityNodeId, "summedDocCount")){
				System.out.println("iiiiiiii");
				
				List<String> updatedUidList;

				//check if the cumulatedUidList attribute exists or not, then update "updatedUidList"
				if(cyNodeAttr.hasAttribute(bioEntityNodeId, "cumulatedUidList")){
					(updatedUidList=(List)cyNodeAttr.getListAttribute(bioEntityNodeId, "cumulatedUidList")).addAll(uidList);
				}else{
					updatedUidList=uidList;
				}

				// get the unique value in updatedUidList
				ArrayList uniqueVals=new ArrayList();
				for(Object x:updatedUidList){
					if(!uniqueVals.contains(x)){
						uniqueVals.add(x);
					}
				}
				
				//if uniqueVals contains no element, sort() will generate error
				if(uniqueVals.size()>0){
					Collections.sort(uniqueVals);
				}
				
				cyNodeAttr.setListAttribute(bioEntityNodeId, "cumulatedUidList", uniqueVals);

				cyNodeAttr.setAttribute(bioEntityNodeId, "summedDocCount", uniqueVals.size());

				// reset the node size for the bioentity after summedDocCount is updated.
				vs.addProperty(bioEntityNodeId, VisualPropertyType.NODE_SIZE, (8+(int)(uniqueVals.size()/10)+1)+"");
				vs.addProperty(bioEntityNodeId, VisualPropertyType.NODE_FILL_COLOR, "#FFD700");
			}else{   // if the "summedDocCount" attribute has never been initiated
				System.out.println("ppppppppp");
				cyNodeAttr.setAttribute(bioEntityNodeId, "summedDocCount", docCount);
				ArrayList uidListOfTextMiningNode= (ArrayList)cyNodeAttr.getListAttribute(((CyNode)literatureListNode).getIdentifier(), "UID");
				
				if(uidListOfTextMiningNode.size()>0){
					Collections.sort(uidListOfTextMiningNode);
				}
				cyNodeAttr.setListAttribute(bioEntityNodeId, "cumulatedUidList", uidListOfTextMiningNode);

				vs.addProperty(bioEntityNodeId, VisualPropertyType.NODE_SIZE, (8+(int)(docCount/10)+1)+"");
				vs.addProperty(bioEntityNodeId, VisualPropertyType.NODE_FILL_COLOR, "#FFD700");
			}



		}else if(nodeType.equalsIgnoreCase("RefinedUidListForBioTx")||nodeType.equalsIgnoreCase("RefinedUidListForEutil")){
			//here the textMiningNode is the refinedUidList node
			int docCount=Integer.parseInt(cyNodeAttr.getStringAttribute(((CyNode)literatureListNode).getIdentifier(), "docCount"));
			List<String> uidList=cyNodeAttr.getListAttribute(((CyNode)literatureListNode).getIdentifier(), "UID");
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
				vs.addProperty(bioEntityNodeId, VisualPropertyType.NODE_SIZE, (8+(int)(uniqueVals.size()/10)+1)+"");
				vs.addProperty(bioEntityNodeId, VisualPropertyType.NODE_FILL_COLOR, "#FFD700");
			}else{  // if the "summedDocCount" attribute has never been initiated
				System.out.println("ssssssssssssssssssssssss");
				cyNodeAttr.setAttribute(bioEntityNodeId, "summedDocCount", docCount);
				ArrayList uidListOfTextMiningNode=(ArrayList)cyNodeAttr.getListAttribute(((CyNode)literatureListNode).getIdentifier(), "UID");
				
				if(uidListOfTextMiningNode.size()>0){
					Collections.sort(uidListOfTextMiningNode);
				}
				
				cyNodeAttr.setListAttribute(bioEntityNodeId, "cumulatedUidList", uidListOfTextMiningNode);

				vs.addProperty(bioEntityNodeId, VisualPropertyType.NODE_SIZE, (8+(int)(docCount/10)+1)+"");
				vs.addProperty(bioEntityNodeId, VisualPropertyType.NODE_FILL_COLOR, "#FFD700");
			}
		}
	}

	public CyAttributes getNodeAttributes(){
		return cyNodeAttr;
	}

	public VisualStyleBuilder getVisualStyleBuilder(){
		return vs;
	}
}
