package textMining;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.apache.xmlrpc.XmlRpcException;

import tudelft.CytoscapeRPC.CyNodeUtils;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.view.CyNetworkView;


//RefCountScoreCalculator-----------------
//v0 20110724:calculate the score for one node. User can define the number of layers
//v1 20110727: add another method for the score calculation
//v1 20110809: change sumUID to PMID in method "getUidListForKeyword"

public class RefCountScoreCalculator1 {

	CyNetwork displayedNetwork;
	CyAttributes cyNodeAttr;
	CyAttributes cyEdgeAttr;
	ArrayList<HashMap<String,ArrayList<String>>> totalLayerBioentityUidMap;
	HashMap<String,Integer> nodeIdRegionMark;
	String cyNodeId="";
	public RefCountScoreCalculator1(CyNode cynode, String keyword,int layers){
		cyNodeId=cynode.getIdentifier();
		totalLayerBioentityUidMap=new ArrayList<HashMap<String,ArrayList<String>>>();
		
		nodeIdRegionMark=new HashMap<String,Integer>();
		
		CyNetworkView cyViewx=Cytoscape.getCurrentNetworkView();
		displayedNetwork=cyViewx.getNetwork();
//		CyNetwork cyNetwork=Cytoscape.getCurrentNetwork();
		cyNodeAttr=Cytoscape.getNodeAttributes();
		cyEdgeAttr=Cytoscape.getEdgeAttributes();
		ArrayList<String> nodeIdBioentityCurrentLayer=new ArrayList<String>();

		// this is to tract the node id which's uid list has been saved, to avoid the looping and duplication.
		ArrayList<String> layerredNodeId=new ArrayList<String>();
		
		nodeIdBioentityCurrentLayer.add(cynode.getIdentifier());
		
		
		
		Set<String> parentNodeIdsOfStartingNode=null;
		List<String> childNodeIdsOfStartingNode=null;
		for(int i=0;i<=layers;i++){
			HashMap<String,ArrayList<String>> oneLayerStore=new HashMap<String,ArrayList<String>>();
			ArrayList<String> nodeIdBioentityNextLayer=new ArrayList<String>();
			
			for(String currentNodeId:nodeIdBioentityCurrentLayer){
				
				System.out.println("currentNodeId:"+currentNodeId);
				
					String nodeType=(String)cyNodeAttr.getAttribute(currentNodeId, "nodeType");
					
					if(nodeType.equals("enzyme")||nodeType.equals("metabolite")||nodeType.equals("protein")){
						
						//only the nodes in the first layer require the parent node and child node information to determine the region mark.
						//As for the nodes in the other layer, the node region mark is determined by the node which produces the neighbor nodes 
						if(i==0){
							// the starting node has the region mark "0"
							nodeIdRegionMark.put(currentNodeId, 0);
							try {
								parentNodeIdsOfStartingNode=CyNodeUtils.getNodeParents(displayedNetwork.getIdentifier(), currentNodeId, false);
								for(String parentNodeId:parentNodeIdsOfStartingNode){
									nodeIdRegionMark.put(parentNodeId,1);
									System.out.println("put key:"+parentNodeId+"\t value:1 "+new Exception().getStackTrace()[0].getLineNumber());
								}
								
								childNodeIdsOfStartingNode=CyNodeUtils.getNodeChildren(displayedNetwork.getIdentifier(), currentNodeId,false);
								for(String childNodeId:childNodeIdsOfStartingNode){
									nodeIdRegionMark.put(childNodeId, -1);
									System.out.println("put key:"+childNodeId+"\t value:-1 "+new Exception().getStackTrace()[0].getLineNumber());
								}
							} catch (XmlRpcException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
//						else if(i==1){
//							if(parentNodeIdsOfStartingNode.contains(currentNodeId)){
//								nodeIdRegionMark.put(currentNodeId, 1);
//								System.out.println("put key:"+currentNodeId+"\t value:1 "+new Exception().getStackTrace()[0].getLineNumber());
//								
//							}else if(childNodeIdsOfStartingNode.contains(currentNodeId)){
//								nodeIdRegionMark.put(currentNodeId, -1);
//								System.out.println("put key:"+currentNodeId+"\t value:-1 "+new Exception().getStackTrace()[0].getLineNumber());
//							}
//						}
						
						
						ArrayList<String> uidListForOneBioentityKeyword=getUidListForKeyword(currentNodeId,keyword);
						oneLayerStore.put(currentNodeId,uidListForOneBioentityKeyword);
						layerredNodeId.add(currentNodeId);
						
						System.out.println("i is:"+i);
						for(String xx:nodeIdRegionMark.keySet()){
							System.out.println("key is:"+xx+"\t");
							System.out.println("value is:"+nodeIdRegionMark.get(xx)+" "+new Exception().getStackTrace()[0].getLineNumber());
						}
						
						//for one node in the current layer
						try{
							List<String> neighborNodeIdList=CyNodeUtils.getNodeNeighbors(displayedNetwork.getIdentifier(),currentNodeId);

							//check if the returned neighbor node has been processed before.						
							for(String neighborNodeId:neighborNodeIdList){
								String nodeTypeNeighborNode=(String)cyNodeAttr.getAttribute(neighborNodeId, "nodeType");
								if(nodeTypeNeighborNode.equalsIgnoreCase("metabolite")||nodeTypeNeighborNode.equalsIgnoreCase("enzyme")||nodeTypeNeighborNode.equalsIgnoreCase("protein")){
									// if the neighbor node has not been processed before, then adds it to the nextLayer array
									if(!layerredNodeId.contains(neighborNodeId)){
										
										//create a list of node of the next layer
										nodeIdBioentityNextLayer.add(neighborNodeId);
										System.out.println("node id: "+currentNodeId+" "+new Exception().getStackTrace()[0].getLineNumber());
										
										// set the region mark for the node in the next layer
										if(nodeIdRegionMark.get(currentNodeId)==1){											
											nodeIdRegionMark.put(neighborNodeId, 1);
											System.out.println("put key:"+neighborNodeId+"\t value:1 "+new Exception().getStackTrace()[0].getLineNumber());
										}else if(nodeIdRegionMark.get(currentNodeId)==-1){
											nodeIdRegionMark.put(neighborNodeId, -1);
											System.out.println("put key:"+neighborNodeId+"\t value:-1 "+new Exception().getStackTrace()[0].getLineNumber());
										}
									}

								}
							}

						}catch(XmlRpcException ex){
							ex.printStackTrace();
						}
					}
					
			}
			
			totalLayerBioentityUidMap.add(oneLayerStore);
			nodeIdBioentityCurrentLayer=nodeIdBioentityNextLayer;
		}
	}
	
	public ArrayList<String> getUidListForKeyword(String cyNodeId,String keyword){
		ArrayList<String> uidListForKeyword=new ArrayList<String>();
		try {
			List<String> neighborNodeId=CyNodeUtils.getNodeNeighbors(displayedNetwork.getIdentifier(),cyNodeId);
			for(String nodeId:neighborNodeId){
				if(cyNodeAttr.getAttribute(nodeId, "nodeType").equals("liteSearchSummary")){
					if(cyNodeAttr.getAttribute(nodeId, "keyword").equals(keyword)){
						uidListForKeyword=new ArrayList<String>(cyNodeAttr.getListAttribute(nodeId, "PMID"));
					}
				}
			}
		} catch (XmlRpcException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return uidListForKeyword;
		
	}
	
	public ArrayList<HashMap<String,ArrayList<String>>> getLayerredBioentityUidListMap(){
		return totalLayerBioentityUidMap;
	}
	
	//return the simple score for one specific bioentity in term of certain keyword
	public double calculateSimpleScore(Double[] layerFactor){
		double score=0;
		int pointerLayerFactor=0;
		for(HashMap<String,ArrayList<String>> oneLayerBioentityUidListMap:totalLayerBioentityUidMap){
			Set<String> nodeIdOfThisLayer=oneLayerBioentityUidListMap.keySet();
			for(String oneBioentityId:nodeIdOfThisLayer){
				ArrayList<String> uidListOfOneBioentity=oneLayerBioentityUidListMap.get(oneBioentityId);
				score=score+uidListOfOneBioentity.size()*layerFactor[pointerLayerFactor];
//				System.out.println("score="+score+"+"+uidListOfOneBioentity.size()+"*"+layerFactor[pointerLayerFactor]);
			}
			pointerLayerFactor++;
		}
		return score;
	}
	
	//return the modified score for one specific bioentity in term of certain keyword
	public double calculateModifiedScore(Double[] layerFactor){
		double modifiedScore=0;
		double scorePositiveRegion=1;
		double scoreNegativeRegion=1;
		
		int pointerLayerFactor=0;
		for(HashMap<String,ArrayList<String>> oneLayerBioentityUidListMap:totalLayerBioentityUidMap){
			Set<String> nodeIdOfThisLayer=oneLayerBioentityUidListMap.keySet();
			for(String oneBioentityId:nodeIdOfThisLayer){
				ArrayList<String> uidListOfOneBioentity=oneLayerBioentityUidListMap.get(oneBioentityId);
				
				if(nodeIdRegionMark.get(oneBioentityId)==1){
					scorePositiveRegion=scorePositiveRegion+uidListOfOneBioentity.size()*layerFactor[pointerLayerFactor];
					if(cyNodeId=="C00068"){
						System.out.println("scorePositiveRegion="+scorePositiveRegion);
					}
				}else if(nodeIdRegionMark.get(oneBioentityId)==-1){
					scoreNegativeRegion=scoreNegativeRegion+uidListOfOneBioentity.size()*layerFactor[pointerLayerFactor];
					if(cyNodeId=="C00068"){
						System.out.println("scoreNegativeRegion="+scoreNegativeRegion);
					}
				}else if(nodeIdRegionMark.get(oneBioentityId)==0){  //the starting node has regionMark "0"
//					scorePositiveRegion=scorePositiveRegion+uidListOfOneBioentity.size()/2;
//					scoreNegativeRegion=scoreNegativeRegion+uidListOfOneBioentity.size()/2;
					modifiedScore=(uidListOfOneBioentity.size()/2)*(uidListOfOneBioentity.size()/2);
					if(cyNodeId=="C00068"){
						System.out.println("modified score(level 0):"+modifiedScore);
					}
				}
			}
			pointerLayerFactor++;
		}
		if(cyNodeId=="C00068"){
			System.out.println("finalPositveRegionScore="+scorePositiveRegion);
			System.out.println("finalNegativeRegionScore="+scoreNegativeRegion);
		}
		modifiedScore=Math.log(modifiedScore+scorePositiveRegion*scoreNegativeRegion);
		return modifiedScore;
	}
}
