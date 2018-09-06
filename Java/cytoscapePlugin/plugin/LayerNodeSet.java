package plugin;

import java.util.ArrayList;
import java.util.Set;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.actions.SelectFirstNeighborsAction;
import cytoscape.data.CyAttributes;

public class LayerNodeSet {
	CyNetwork currentNet;
//	CyAttributes cyNodeAttr,cyEdgeAttr;
	ArrayList layeredNodeSet=new ArrayList();
	
	public LayerNodeSet(int level){
		currentNet=Cytoscape.getCurrentNetwork();
//		cyNodeAttr=cyNodeAttr_;
//		cyEdgeAttr=cyEdgeAttr_;
//		Set selectedCyNodeSet=currentNet.getSelectedNodes();
		for(int s=0;s<level;s++){
			Set selectedCyNodeSet_pre=currentNet.getSelectedNodes();
			SelectFirstNeighborsAction action=new SelectFirstNeighborsAction();
			layeredNodeSet.add(currentNet.getSelectedNodes().removeAll(selectedCyNodeSet_pre));
		}
		
	}

	public ArrayList getLayeredNodeSet(){
		return layeredNodeSet;
	}
	
}
