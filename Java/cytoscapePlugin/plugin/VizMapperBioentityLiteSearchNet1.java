package plugin;

import java.awt.Font;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.data.CyAttributes;
import cytoscape.data.readers.VisualStyleBuilder;
import cytoscape.visual.NodeShape;
import cytoscape.visual.VisualPropertyType;


//VizMapperEMP(EMP means enzyme,metabolite,protein)--------------
//v0 20110120: deal with the layout of a network containing protein,enzyme,metabolite
//v0 20110120: add "currentNet_.selectAllNodes()" and "currentNet_.selectAllEdges()"
//v0 20110307: add conditional expression: "if(cyAttri_.has Attribute()){.......}",since not all node will have value for every attribute
//v0 20110411: change the variable name:  cyAttri_ to cyNodeAttri_
//v1 20110411: set "NODE_LABEL" attribute in the visualstyle.
//v2 20110414: explicitly set the visualStyle of more nodes and edges(including the nodes and edges generated from text-mining)
//v2 20110702: if nodeType==pathwayId, set the node label
//v3 20110711: if the edge id has the pattern xxx(annotation)xxx, make the edge "equal_dash" and no arrow

//VizMapperBioentityLiteSearchNet----------------
//v0 20110728: modified from VizMapperEMP3. add visualstyle to textMining nodes and edges.The visualstyle for nodes and edges should be set in this file
//v1 20110806: change the label font of refCountPM and refCountPMC node according to phrase search is used or not.
//v1 20110807: set visualStyle for node having nodeType RefinedUidListForBioTx,RefinedUidListForPM,RefinedUidListForPMC

public class VizMapperBioentityLiteSearchNet1 {
	public VisualStyleBuilder newGraphStyle;
	
	public VizMapperBioentityLiteSearchNet1(CyNetwork currentNet_,CyAttributes cyNodeAttri_, CyAttributes cyEdgeAttri_){
		currentNet_.selectAllNodes();
		currentNet_.selectAllEdges();
		Collection nodeCollection_=currentNet_.getSelectedNodes();
		Collection edgeCollection_=currentNet_.getSelectedEdges();
		newGraphStyle=new VisualStyleBuilder("vizmapperEMP3",true);
		for(Object i:nodeCollection_){
//			JOptionPane.showMessageDialog(new JFrame(),((CyNode)i).getIdentifier() );
			//								graphStyle.addProperty(((CyNode)i).getIdentifier(),VisualPropertyType.NODE_LABEL,((Enzyme)(cyNodeMoleObjectMapping.get((CyNode)i))).getSpid());
//			newGraphStyle.addProperty(((CyNode)i).getIdentifier(),VisualPropertyType.NODE_LABEL,((CyNode)i).getIdentifier());
			
//			JOptionPane.showMessageDialog(new JFrame(),(String)(cyAttri_.getAttribute(((CyNode)i).getIdentifier(), "moleType"))+"/"+this.getClass().getName()+" line "+new Exception().getStackTrace()[0].getLineNumber());
			String nodeId=((CyNode)i).getIdentifier();
			if(cyNodeAttri_.hasAttribute(nodeId, "moleType")){  //it is not that every node will have "moleType" attribute
				if(((String)(cyNodeAttri_.getAttribute(nodeId, "moleType"))).equalsIgnoreCase("Metabolite")){
					System.out.println("one of all selected node is metabolite/"+this.getClass().getName());
					newGraphStyle.addProperty(((CyNode)i).getIdentifier(),VisualPropertyType.NODE_SHAPE, NodeShape.ELLIPSE.getShapeName());
					newGraphStyle.addProperty(((CyNode)i).getIdentifier(),VisualPropertyType.NODE_BORDER_COLOR, "#000000");
					newGraphStyle.addProperty(((CyNode)i).getIdentifier(),VisualPropertyType.NODE_LABEL,(String) cyNodeAttri_.getAttribute(nodeId, "displayName"));
					newGraphStyle.addProperty(nodeId,VisualPropertyType.NODE_LABEL_COLOR,"#000000");
				}else if(((String)(cyNodeAttri_.getAttribute(nodeId, "moleType"))).equalsIgnoreCase("Enzyme")){
					newGraphStyle.addProperty(((CyNode)i).getIdentifier(),VisualPropertyType.NODE_SHAPE, "RECT");
					System.out.println("one of all selected node is enzyme/"+this.getClass().getName());
					newGraphStyle.addProperty(((CyNode)i).getIdentifier(),VisualPropertyType.NODE_BORDER_COLOR, "#000000");
					newGraphStyle.addProperty(((CyNode)i).getIdentifier(),VisualPropertyType.NODE_LABEL,(String) cyNodeAttri_.getAttribute(nodeId, "displayName"));
					newGraphStyle.addProperty(nodeId,VisualPropertyType.NODE_LABEL_COLOR,"#000000");
				}else if(((String)(cyNodeAttri_.getAttribute(nodeId, "moleType"))).equalsIgnoreCase("protein")){
					System.out.println("one of all selected node is protein/"+this.getClass().getName());
					newGraphStyle.addProperty(((CyNode)i).getIdentifier(),VisualPropertyType.NODE_SHAPE, NodeShape.DIAMOND.getShapeName());
					newGraphStyle.addProperty(((CyNode)i).getIdentifier(),VisualPropertyType.NODE_BORDER_COLOR, "#00FF00");
					newGraphStyle.addProperty(((CyNode)i).getIdentifier(),VisualPropertyType.NODE_LABEL,(String) cyNodeAttri_.getAttribute(nodeId, "displayName"));
					newGraphStyle.addProperty(nodeId,VisualPropertyType.NODE_LABEL_COLOR,"#000000");
				}
			}
			if(cyNodeAttri_.hasAttribute(nodeId, "nodeType")){
				if(((String)(cyNodeAttri_.getAttribute(nodeId, "nodeType"))).equalsIgnoreCase("commonName")){
					System.out.println("one of all selected node is commonName/"+this.getClass().getName());
					newGraphStyle.addProperty(nodeId,VisualPropertyType.NODE_SHAPE, NodeShape.OCTAGON.getShapeName());
					newGraphStyle.addProperty(nodeId,VisualPropertyType.NODE_BORDER_COLOR, "#FF0000");
					newGraphStyle.addProperty(nodeId,VisualPropertyType.NODE_LABEL,(String) cyNodeAttri_.getAttribute(nodeId, "displayName"));
					newGraphStyle.addProperty(nodeId,VisualPropertyType.NODE_LINE_WIDTH, "1");
					newGraphStyle.addProperty(nodeId,VisualPropertyType.NODE_LABEL_COLOR,"#000000");
				}else if(((String)(cyNodeAttri_.getAttribute(((CyNode)i).getIdentifier(), "nodeType"))).equalsIgnoreCase("freeText")){
					newGraphStyle.addProperty(nodeId,VisualPropertyType.NODE_SHAPE, NodeShape.TRAPEZOID.getShapeName());
					System.out.println("one of all selected node is freeText/"+this.getClass().getName());
					newGraphStyle.addProperty(nodeId,VisualPropertyType.NODE_BORDER_COLOR, "#804000");
					newGraphStyle.addProperty(nodeId,VisualPropertyType.NODE_LINE_WIDTH, "1");
					newGraphStyle.addProperty(nodeId,VisualPropertyType.NODE_LABEL,(String) cyNodeAttri_.getAttribute(nodeId, "displayName"));
					newGraphStyle.addProperty(nodeId,VisualPropertyType.NODE_LABEL_COLOR,"#000000");
				}else if(((String)(cyNodeAttri_.getAttribute(nodeId, "nodeType"))).equalsIgnoreCase("refCountBiotext")){
					System.out.println("one of all selected node is refCountBiotext/"+this.getClass().getName());
					newGraphStyle.addProperty(nodeId,VisualPropertyType.NODE_SHAPE, NodeShape.ROUND_RECT.getShapeName());
					newGraphStyle.addProperty(nodeId,VisualPropertyType.NODE_BORDER_COLOR, "#0000FF");
					newGraphStyle.addProperty(nodeId,VisualPropertyType.NODE_LINE_WIDTH, "1");
					newGraphStyle.addProperty(nodeId,VisualPropertyType.NODE_LABEL,(String) cyNodeAttri_.getAttribute(nodeId, "displayName"));
					newGraphStyle.addProperty(nodeId,VisualPropertyType.NODE_LABEL_COLOR,"#000000");
				}else if(((String)(cyNodeAttri_.getAttribute(nodeId, "nodeType"))).equalsIgnoreCase("refCountPM")){
					System.out.println("one of all selected node is refCountEutil/"+this.getClass().getName());
					if((boolean)cyNodeAttri_.hasAttribute(nodeId, "usePhraseSearch")){
						if(((Boolean)(cyNodeAttri_.getAttribute(nodeId, "usePhraseSearch")))==true){
							System.out.println(nodeId+" is created by phraseSearch");
							newGraphStyle.addProperty(nodeId,VisualPropertyType.NODE_LABEL_COLOR,"#FF00FF"); //#FF00FF Fuchsia
						}else{
							newGraphStyle.addProperty(nodeId,VisualPropertyType.NODE_LABEL_COLOR,"#000000");
						}
					}else{
						newGraphStyle.addProperty(nodeId,VisualPropertyType.NODE_LABEL_COLOR,"#000000");
					}
					newGraphStyle.addProperty(nodeId,VisualPropertyType.NODE_SHAPE, NodeShape.ROUND_RECT.getShapeName());
					newGraphStyle.addProperty(nodeId,VisualPropertyType.NODE_BORDER_COLOR, "#008000");
					newGraphStyle.addProperty(nodeId,VisualPropertyType.NODE_LINE_WIDTH, "1");
					newGraphStyle.addProperty(nodeId,VisualPropertyType.NODE_LABEL,(String) cyNodeAttri_.getAttribute(nodeId, "displayName"));
				}else if(((String)(cyNodeAttri_.getAttribute(nodeId, "nodeType"))).equalsIgnoreCase("refCountPMC")){
					System.out.println("one of all selected node is refCountEutil/"+this.getClass().getName());
					if((boolean)cyNodeAttri_.hasAttribute(nodeId, "usePhraseSearch")){
						if(((Boolean)(cyNodeAttri_.getAttribute(nodeId, "usePhraseSearch")))==true){
							System.out.println(nodeId+" is created by phraseSearch");
							newGraphStyle.addProperty(nodeId,VisualPropertyType.NODE_LABEL_COLOR,"#FF00FF"); //#FF00FF Fuchsia
						}else{
							newGraphStyle.addProperty(nodeId,VisualPropertyType.NODE_LABEL_COLOR,"#000000");
						}
					}else{
						newGraphStyle.addProperty(nodeId,VisualPropertyType.NODE_LABEL_COLOR,"#000000");
					}
					newGraphStyle.addProperty(nodeId,VisualPropertyType.NODE_SHAPE, NodeShape.ROUND_RECT.getShapeName());
					newGraphStyle.addProperty(nodeId,VisualPropertyType.NODE_BORDER_COLOR, "#008000");
					newGraphStyle.addProperty(nodeId,VisualPropertyType.NODE_LINE_WIDTH, "1");
					newGraphStyle.addProperty(nodeId,VisualPropertyType.NODE_LABEL,(String) cyNodeAttri_.getAttribute(nodeId, "displayName"));
				}else if(((String)(cyNodeAttri_.getAttribute(nodeId, "nodeType"))).equalsIgnoreCase("pathwayId")){
					System.out.println("one of all selected node is refCountEutil/"+this.getClass().getName());
					newGraphStyle.addProperty(nodeId,VisualPropertyType.NODE_SHAPE, NodeShape.PARALLELOGRAM.getShapeName());
					newGraphStyle.addProperty(nodeId,VisualPropertyType.NODE_BORDER_COLOR, "#008000");
					newGraphStyle.addProperty(nodeId,VisualPropertyType.NODE_LINE_WIDTH, "1");
					newGraphStyle.addProperty(nodeId,VisualPropertyType.NODE_LABEL,(String) cyNodeAttri_.getAttribute(nodeId, "displayName"));
					newGraphStyle.addProperty(nodeId,VisualPropertyType.NODE_LABEL_COLOR,"#000000");
				}else if(((String)(cyNodeAttri_.getAttribute(nodeId, "nodeType"))).equalsIgnoreCase("liteSearchSummary")){
					System.out.println("one of all selected node is refCountEutil/"+this.getClass().getName());
					newGraphStyle.addProperty(nodeId,VisualPropertyType.NODE_SHAPE, NodeShape.ROUND_RECT.getShapeName());
					newGraphStyle.addProperty(nodeId,VisualPropertyType.NODE_BORDER_COLOR, "#008000");
					newGraphStyle.addProperty(nodeId,VisualPropertyType.NODE_LABEL,(String) cyNodeAttri_.getAttribute(nodeId, "displayName"));
					newGraphStyle.addProperty(nodeId,VisualPropertyType.NODE_LINE_WIDTH, "6");
					newGraphStyle.addProperty(nodeId,VisualPropertyType.NODE_LABEL_COLOR,"#000000");
				}else if(((String)(cyNodeAttri_.getAttribute(nodeId, "nodeType"))).equalsIgnoreCase("freeText")){
					System.out.println("one of all selected node is freeTextNode/"+this.getClass().getName());
					newGraphStyle.addProperty(nodeId, VisualPropertyType.NODE_SHAPE, NodeShape.TRAPEZOID.getShapeName());
					newGraphStyle.addProperty(nodeId, VisualPropertyType.NODE_BORDER_COLOR, "#804000"); //#804000 brown
					//							vs.addProperty(nodeKeyword.getIdentifier(), VisualPropertyType.NODE_LABEL, nodeKeyword.getIdentifier());
					newGraphStyle.addProperty(nodeId, VisualPropertyType.NODE_LABEL, cyNodeAttri_.getStringAttribute(nodeId, "displayName"));
					newGraphStyle.addProperty(nodeId, VisualPropertyType.NODE_FONT_SIZE, "6.0");
					newGraphStyle.addProperty(nodeId,VisualPropertyType.NODE_LABEL_COLOR,"#000000");
				}else if(((String)(cyNodeAttri_.getAttribute(nodeId, "nodeType"))).equalsIgnoreCase("RefinedUidListForBioTx")){
					newGraphStyle.addProperty(nodeId,VisualPropertyType.NODE_SHAPE, NodeShape.ROUND_RECT.getShapeName());
					newGraphStyle.addProperty(nodeId,VisualPropertyType.NODE_BORDER_COLOR, "#FF00FF");
					newGraphStyle.addProperty(nodeId,VisualPropertyType.NODE_LABEL,(String) cyNodeAttri_.getAttribute(nodeId, "displayName"));
					newGraphStyle.addProperty(nodeId,VisualPropertyType.NODE_LINE_WIDTH, "6");
					newGraphStyle.addProperty(nodeId,VisualPropertyType.NODE_LABEL_COLOR,"#000000");
				}else if(((String)(cyNodeAttri_.getAttribute(nodeId, "nodeType"))).equalsIgnoreCase("RefinedUidListForPM")){
					newGraphStyle.addProperty(nodeId,VisualPropertyType.NODE_SHAPE, NodeShape.ROUND_RECT.getShapeName());
					newGraphStyle.addProperty(nodeId,VisualPropertyType.NODE_BORDER_COLOR, "#FF00FF");
					newGraphStyle.addProperty(nodeId,VisualPropertyType.NODE_LABEL,(String) cyNodeAttri_.getAttribute(nodeId, "displayName"));
					newGraphStyle.addProperty(nodeId,VisualPropertyType.NODE_LINE_WIDTH, "6");
					newGraphStyle.addProperty(nodeId,VisualPropertyType.NODE_LABEL_COLOR,"#000000");
				}else if(((String)(cyNodeAttri_.getAttribute(nodeId, "nodeType"))).equalsIgnoreCase("RefinedUidListForPMC")){
					newGraphStyle.addProperty(nodeId,VisualPropertyType.NODE_SHAPE, NodeShape.ROUND_RECT.getShapeName());
					newGraphStyle.addProperty(nodeId,VisualPropertyType.NODE_BORDER_COLOR, "#FF00FF");
					newGraphStyle.addProperty(nodeId,VisualPropertyType.NODE_LABEL,(String) cyNodeAttri_.getAttribute(nodeId, "displayName"));
					newGraphStyle.addProperty(nodeId,VisualPropertyType.NODE_LINE_WIDTH, "6");
					newGraphStyle.addProperty(nodeId,VisualPropertyType.NODE_LABEL_COLOR,"#000000");
				}
			}
			
		}
					
		Pattern ptn1=Pattern.compile(".*\\(pp\\).*");
		Pattern ptn2=Pattern.compile(".*\\(reaction\\).*");
		Pattern ptn3=Pattern.compile(".*\\(annotation\\).*");
		Pattern ptn4=Pattern.compile(".*\\(textMining\\).*");
		Pattern ptn5=Pattern.compile(".*\\(summaryCitationForKw\\).*");
		
		for(Object j:edgeCollection_){
			String edgeIdentifier=((CyEdge)j).getIdentifier();
			Matcher mtch1=ptn1.matcher(edgeIdentifier);
			Matcher mtch2=ptn2.matcher(edgeIdentifier);
			Matcher mtch3=ptn3.matcher(edgeIdentifier);
//			Matcher mtch4=ptn4.matcher(edgeIdentifier);
//			Matcher mtch5=ptn5.matcher(edgeIdentifier);
			
//			if(mtch1.matches()){
//				//									JOptionPane.showMessageDialog(new JFrame(), "pp:"+edgeIdentifier);
//				newGraphStyle.addProperty(edgeIdentifier,VisualPropertyType.EDGE_LINE_STYLE,"LONG_DASH"); // this property has to be specified for each edge even if only some edges needs to specify this property
//				newGraphStyle.addProperty(edgeIdentifier,VisualPropertyType.EDGE_TGTARROW_SHAPE, "No Arrow"); // this property has to be specified for each edge even if only some edges needs to specify this property
//			}else if(mtch2.matches()){
//				//									JOptionPane.showMessageDialog(new JFrame(), "reaction:"+edgeIdentifier);
//				newGraphStyle.addProperty(edgeIdentifier,VisualPropertyType.EDGE_LINE_STYLE,"SOLID"); // this property has to be specified for each edge even if only some edges needs to specify this property
//				newGraphStyle.addProperty(edgeIdentifier,VisualPropertyType.EDGE_TGTARROW_SHAPE, "ARROW"); // this property has to be specified for each edge even if only some edges needs to specify this property
//			}else if(mtch3.matches()){
//				newGraphStyle.addProperty(edgeIdentifier,VisualPropertyType.EDGE_LINE_STYLE,"EQUAL_DASH"); // this property has to be specified for each edge even if only some edges needs to specify this property
//				newGraphStyle.addProperty(edgeIdentifier,VisualPropertyType.EDGE_TGTARROW_SHAPE, "No ARROW"); // this property has to be specified for each edge even if only some edges needs to specify this property
//			}

//			if(cyEdgeAttri_.hasAttribute(((CyEdge)j).getIdentifier(), "edgeType")){
//				if(((String)(cyEdgeAttri_.getAttribute(((CyEdge)j).getIdentifier(), "edgeType"))).equalsIgnoreCase("annotation")){
//					newGraphStyle.addProperty(((CyEdge)j).getIdentifier(),VisualPropertyType.EDGE_LINE_STYLE, "LONG_DASH");
//					newGraphStyle.addProperty(((CyEdge)j).getIdentifier(),VisualPropertyType.EDGE_TGTARROW_SHAPE, "ARROW");
//				}else if(((String)(cyEdgeAttri_.getAttribute(((CyEdge)j).getIdentifier(), "edgeType"))).equalsIgnoreCase("textMining")){
//					newGraphStyle.addProperty(((CyEdge)j).getIdentifier(),VisualPropertyType.EDGE_LINE_STYLE, "LONG_DASH");
//					newGraphStyle.addProperty(((CyEdge)j).getIdentifier(),VisualPropertyType.EDGE_TGTARROW_SHAPE, "ARROW");
//				}
//			}
			
			if(cyEdgeAttri_.hasAttribute(edgeIdentifier, "interaction")){
				if(((String)(cyEdgeAttri_.getAttribute(edgeIdentifier, "interaction"))).equalsIgnoreCase("annotation")){
					newGraphStyle.addProperty(edgeIdentifier,VisualPropertyType.EDGE_LINE_STYLE, "LONG_DASH");
					newGraphStyle.addProperty(edgeIdentifier,VisualPropertyType.EDGE_TGTARROW_SHAPE, "NO ARROW");
				}else if(((String)(cyEdgeAttri_.getAttribute(edgeIdentifier, "interaction"))).equalsIgnoreCase("textMining")){
					newGraphStyle.addProperty(edgeIdentifier,VisualPropertyType.EDGE_LINE_STYLE, "EQUAL_DASH");
					newGraphStyle.addProperty(edgeIdentifier,VisualPropertyType.EDGE_TGTARROW_SHAPE, "ARROW");
				}else if(((String)(cyEdgeAttri_.getAttribute(edgeIdentifier, "interaction"))).equalsIgnoreCase("reaction")){
					newGraphStyle.addProperty(edgeIdentifier,VisualPropertyType.EDGE_LINE_STYLE, "SOLID");
					newGraphStyle.addProperty(edgeIdentifier,VisualPropertyType.EDGE_TGTARROW_SHAPE, "ARROW");
				}else if(((String)(cyEdgeAttri_.getAttribute(edgeIdentifier, "interaction"))).equalsIgnoreCase("summaryCitationForKw")){
					newGraphStyle.addProperty(edgeIdentifier,VisualPropertyType.EDGE_LINE_STYLE, "EQUAL_DASH");
					newGraphStyle.addProperty(edgeIdentifier,VisualPropertyType.EDGE_TGTARROW_SHAPE, "ARROW");
				}else if(((String)(cyEdgeAttri_.getAttribute(edgeIdentifier, "interaction"))).equalsIgnoreCase("pp")){
					newGraphStyle.addProperty(edgeIdentifier,VisualPropertyType.EDGE_LINE_STYLE, "LONG_DASH");
					newGraphStyle.addProperty(edgeIdentifier,VisualPropertyType.EDGE_TGTARROW_SHAPE, "NO ARROW");
				}
			}
		}
		currentNet_.unselectAllEdges();
		currentNet_.unselectAllNodes();
	}
	
	public VisualStyleBuilder getVisualStyleBuilder(){
		return newGraphStyle;
	}

}
