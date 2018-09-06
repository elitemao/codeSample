package gonParser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import org.csml.parser.csml19.CSML19IO;
import org.csml.parser.csml19.base.CSML19ConnectorElement;
import org.csml.parser.csml19.base.CSML19EntityElement;
import org.csml.parser.csml19.base.CSML19NetElement;
import org.csml.parser.csml19.base.CSML19ProcessElement;
import org.csml.parser.csml19.base.CSML19StartModel;
import org.csml.parser.csml19.base.ICSML19InnerNetElementChoice;

import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.data.Semantics;

//Csml19ToCyNet-------------
//v0 20110621: a testing code for using csml19 parser from CSML.org
//v0 20110622: the code structure is similar to Hml2v1ToCyNet2.This algorithm assumes the entity element are all listed before process element in the CSML file
//v1 20110624: the moleType for EC is set to "enzyme"
//v1 20110624: add .* to all regular expression
//v2 20110630: set list attribute involvedPathwayId to the enzyme node
//v3 20110702: add another constructor so that the generated cynetwork from the csml file can be superimposed to the existing cynetwork
//v4 20110707: set "nodeType" attribute for the generated network
//v5 20110711: use keggCompId as the node id

public class Csml19ToCyNet5 {
	Pattern ptnMoleType=Pattern.compile(".*\\<moleType\\>(.*)\\<\\/moleType\\>.*");// "." doesn't match new line
	Pattern ptnKeggCompId=Pattern.compile(".*\\<keggCompId\\>(.*)\\<\\/keggCompId\\>.*");// "." doesn't match new line
	Pattern ptnName=Pattern.compile(".*\\<name\\>(.*)\\<\\/name\\>.*");// "." doesn't match new line
	Pattern ptnEc=Pattern.compile(".*\\<ec\\>(.*)\\<\\/ec\\>.*");// "." doesn't match new line
	Pattern ptnKeggReactionId=Pattern.compile(".*\\<keggReactionId\\>(.*)\\<\\/keggReactionId\\>.*");// "." doesn't match new line
	Pattern ptnKeggPathwayId=Pattern.compile(".*\\<keggPathwayId\\>(.*)\\<\\/keggPathwayId\\>.*");
	ArrayList<CyNode> nodeCollection=new ArrayList<CyNode>();
	ArrayList<CyEdge> edgeCollection=new ArrayList<CyEdge>();
	CyNetwork cyNetwork;
	CyAttributes cyNodeAttrs;
	HashMap<CyNode,String> cyNodeMoleTypeMapping=new HashMap<CyNode,String>();
	
	public static void main(String[] args){
		String csmlPath=args[0];
		new Csml19ToCyNet5(csmlPath);
	}
	
	
	// there is another constructor below
	public Csml19ToCyNet5(String csmlPath){
		System.out.println("csml path:"+csmlPath);
		HashMap<String, CSML19EntityElement> entityLabelMapObjectStore=new HashMap<String, CSML19EntityElement>();
		CSML19StartModel io=new CSML19IO().loadCSMLModel(csmlPath);

		CSML19NetElement net=io.getNetElement();

		ICSML19InnerNetElementChoice[] elementStore=net.getInnerNetElement();

		CyNetwork cyNetwork_ = Cytoscape.createNetwork("network "+csmlPath, true);
		cyNodeAttrs=Cytoscape.getNodeAttributes();
		for(Object s:elementStore){
			if(s instanceof CSML19EntityElement){
				String label=((CSML19EntityElement)s).getLabel();
				String entityName=((CSML19EntityElement)s).getName();
				System.out.println("entity name:"+entityName);
//				String catenatedComment=((CSML19EntityElement)s).getComment();
//				Matcher mtchMoleType=ptnMoleType.matcher(catenatedComment);
//				Matcher mtchKeggCompId=ptnKeggCompId.matcher(catenatedComment);
//				Matcher mtchName=ptnName.matcher(catenatedKeggComment);
//				
//				if(mtchMoleType.matches()){
//					
//				}
				
				entityLabelMapObjectStore.put(label, (CSML19EntityElement)s);
			}else if(s instanceof CSML19ProcessElement){
				String ec="";
				String keggReactionId="";
				ArrayList<String> involvedPathwayId=new ArrayList<String>();
				String processLabel=((CSML19ProcessElement)s).getLabel();
				String processName=((CSML19ProcessElement)s).getName();
//				String processComment=((CSML19ProcessElement)s).getComment();
				CSML19ConnectorElement[] connectorStore=((CSML19ProcessElement)s).getFunctionElement().getConnectorElement();
				
				String catenatedComment=((CSML19ProcessElement)s).getComment().replace("\n", "");
				System.out.println("comment for one process is:"+catenatedComment);
				if(catenatedComment!=null){
					Matcher mtchEc=ptnEc.matcher(catenatedComment);
					Matcher mtchKeggReactionId=ptnKeggReactionId.matcher(catenatedComment);
					Matcher mtchKeggPathwayId=ptnKeggPathwayId.matcher(catenatedComment);
					
					if(mtchEc.matches()){
						ec=mtchEc.group(1);
						System.out.println("ec:"+ec);
					}
					if(mtchKeggReactionId.matches()){
						keggReactionId=mtchKeggReactionId.group(1);
						System.out.println("keggReactionId:"+keggReactionId);
					}
					
					if(mtchKeggPathwayId.matches()){
						String catenatedPathwayId=mtchKeggPathwayId.group(1);
						String[] pathwayIdArray=catenatedPathwayId.split(",");
						for(String pathwayId:pathwayIdArray){
							if(!pathwayId.equals("")){
								involvedPathwayId.add(pathwayId);
							}
						}
					}
				}
				for(CSML19ConnectorElement i:connectorStore){
					String fromLabel=i.getFrom();
					String toLabel=i.getTo();
					String moleTypeFrom="";
					String moleTypeTo="";
					CyNode node1=null;
					CyNode node2=null;
					
					if(fromLabel.equals(processLabel)){
						moleTypeFrom="enzyme";
						moleTypeTo="metabolite";
					}else{
						moleTypeFrom="metabolite";
						moleTypeTo="enzyme";
					}
					
					// deal with the entity where the edge is from
					if(moleTypeFrom.equalsIgnoreCase("enzyme")){
//						Protein e_from=(Protein)proteinEntityLabelMapObject.get(from);
//						moleType_from=e_from.getAnnotationP().getMoleType();//getAnnotationP() P means protein
//						String ec_from=e_from.getAnnotationP().getEcStore().getEc();
						{
							node1=Cytoscape.getCyNode(processName,true);
//							JOptionPane.showMessageDialog(new JFrame(), "from node name is:"+e_from.getName());
							nodeCollection.add(node1);
							cyNetwork_.addNode(node1);
							cyNodeMoleTypeMapping.put(node1, moleTypeFrom);
							cyNodeAttrs.setAttribute(node1.getIdentifier(),"moleType",moleTypeFrom);
							cyNodeAttrs.setAttribute(node1.getIdentifier(), "mainDbId", ec);
							cyNodeAttrs.setAttribute(node1.getIdentifier(), "displayName", ec);
							cyNodeAttrs.setAttribute(node1.getIdentifier(),"nodeType", moleTypeFrom);
							cyNodeAttrs.setListAttribute(node1.getIdentifier(), "involvedPathwayId", involvedPathwayId);
							
							//							nodeNum--;
						}
						{
							CSML19EntityElement entityTo=entityLabelMapObjectStore.get(toLabel);
							String keggCompId="";
							
							String commentCatenated=entityTo.getComment();
							if(commentCatenated!=null){
								Matcher mtchKeggCompId=ptnKeggCompId.matcher(commentCatenated);

								if(mtchKeggCompId.matches()){
									keggCompId=mtchKeggCompId.group(1);
								}
							}
//							node2=Cytoscape.getCyNode(entityTo.getName(),true);
							node2=Cytoscape.getCyNode(keggCompId,true);
//							JOptionPane.showMessageDialog(new JFrame(), "from node name is:"+e_from.getName());
							nodeCollection.add(node2);
							cyNetwork_.addNode(node2);
							cyNodeMoleTypeMapping.put(node2, moleTypeFrom);
							cyNodeAttrs.setAttribute(node2.getIdentifier(),"moleType",moleTypeTo);
							cyNodeAttrs.setAttribute(node2.getIdentifier(),"nodeType", moleTypeTo);
							cyNodeAttrs.setAttribute(node2.getIdentifier(), "mainDbId", keggCompId);
							cyNodeAttrs.setAttribute(node2.getIdentifier(), "displayName", entityTo.getName());
							
							
						}
						
						
					}else if(moleTypeFrom.equalsIgnoreCase("metabolite")){
						CSML19EntityElement entityFrom=entityLabelMapObjectStore.get(fromLabel);
			
						System.out.println(entityFrom.getName()+" "+this.getClass().getName()); 
//						moleType_from=e_from.getAnnotationM().getMoleType();// getAnnotationM() M means metabolite
						String keggCompId="";
						
						String commentCatenated=entityFrom.getComment();
//						Matcher mtchMoleType=ptnMoleType.matcher(commentCatenated);
						if(commentCatenated!=null){
							Matcher mtchKeggCompId=ptnKeggCompId.matcher(commentCatenated);

							if(mtchKeggCompId.matches()){
								keggCompId=mtchKeggCompId.group(1);
							}
						}
//						String kegg_from=e_from.getAnnotationM().getKeggCompoundId();
//						String oneCommonName=e_from.getAnnotationM().getCommonNameStore().getCommonName().get(0);
						{
//							node1=Cytoscape.getCyNode(entityFrom.getName(),true);
							node2=Cytoscape.getCyNode(keggCompId,true);
//							JOptionPane.showMessageDialog(new JFrame(), "from node name is:"+e_from.getName());
							nodeCollection.add(node1);
							cyNetwork_.addNode(node1);
							cyNodeMoleTypeMapping.put(node1, moleTypeFrom);
							cyNodeAttrs.setAttribute(node1.getIdentifier(),"moleType",moleTypeFrom);
							cyNodeAttrs.setAttribute(node1.getIdentifier(),"nodeType", moleTypeFrom);
							cyNodeAttrs.setAttribute(node1.getIdentifier(), "mainDbId", keggCompId);
							cyNodeAttrs.setAttribute(node1.getIdentifier(), "displayName", entityFrom.getName());
//							nodeNum--;
						}
						{

							node2=Cytoscape.getCyNode(processName,true);
							//								JOptionPane.showMessageDialog(new JFrame(), "from node name is:"+e_from.getName());
							nodeCollection.add(node2);
							cyNetwork_.addNode(node2);
							cyNodeMoleTypeMapping.put(node2, moleTypeFrom);
							cyNodeAttrs.setAttribute(node2.getIdentifier(),"moleType",moleTypeTo);
							cyNodeAttrs.setAttribute(node2.getIdentifier(),"nodeType", moleTypeTo);
							cyNodeAttrs.setAttribute(node2.getIdentifier(), "mainDbId", ec);
							cyNodeAttrs.setAttribute(node2.getIdentifier(), "displayName", ec);
							cyNodeAttrs.setListAttribute(node2.getIdentifier(), "involvedPathwayId", involvedPathwayId);
							//							nodeNum--;


						}
						
						
					}
					
//					// deal with the entity where the edge is to
//					if(classType_to.equalsIgnoreCase("protein")){
//						Protein e_to=(Protein)proteinEntityLabelMapObject.get(to);  //*v1 20110119
//						System.out.println(e_to.getName()+" "+this.getClass().getName()); 
//						moleType_to=e_to.getAnnotationP().getMoleType();
//						String ec_to=e_to.getAnnotationP().getEcStore().getEc();
//						{
//							node2=Cytoscape.getCyNode(e_to.getName(),true);
////							JOptionPane.showMessageDialog(new JFrame(), "to node name is:"+e_from.getName());
//							nodeCollection.add(node2);
//							cyNetwork_.addNode(node2);
//							cyNodeMoleTypeMapping.put(node2, moleType_to);
//							cyNodeAttrs.setAttribute(node2.getIdentifier(), "moleType", moleType_to);
//							cyNodeAttrs.setAttribute(node2.getIdentifier(), "mainDbId", ec_to);
//							cyNodeAttrs.setAttribute(node2.getIdentifier(),"displayName",ec_to);
////							nodeNum
//						}
//					}else if(classType_to.equalsIgnoreCase("metabolite")){
//						Metabolite e_to=(Metabolite)metaboliteEntityLabelMapObject.get(to);  //*v1 20110119
//						moleType_to=e_to.getAnnotationM().getMoleType();
//						String kegg_to=e_to.getAnnotationM().getKeggCompoundId();
//						{
//							node2=Cytoscape.getCyNode(e_to.getName(),true);
////							JOptionPane.showMessageDialog(new JFrame(), "to node name is:"+e_from.getName());
//							nodeCollection.add(node2);
//							cyNetwork_.addNode(node2);
//							cyNodeMoleTypeMapping.put(node2, moleType_to);
//							cyNodeAttrs.setAttribute(node2.getIdentifier(), "moleType", moleType_to);
//							cyNodeAttrs.setAttribute(node2.getIdentifier(), "mainDbId", kegg_to);
//							cyNodeAttrs.setAttribute(node2.getIdentifier(), "displayName", e_to.getName()); 
////							nodeNum
//						
//						}
//					}
					
					CyEdge edge=Cytoscape.getCyEdge(node1, node2, Semantics.INTERACTION, "reaction", true);
					cyNetwork_.addEdge(edge);
					edgeCollection.add(edge);
					
				}
			}

		}
		cyNetwork=cyNetwork_;
		Cytoscape.firePropertyChange(Cytoscape.ATTRIBUTES_CHANGED, null, null);
	}
	
	
	
	public Csml19ToCyNet5(String csmlPath, CyNetwork currentCyNetwork_){
		HashMap<String, CSML19EntityElement> entityLabelMapObjectStore=new HashMap<String, CSML19EntityElement>();
		CSML19StartModel io=new CSML19IO().loadCSMLModel(csmlPath);

		CSML19NetElement net=io.getNetElement();

		ICSML19InnerNetElementChoice[] elementStore=net.getInnerNetElement();

		CyNetwork cyNetwork_ = currentCyNetwork_;
		
		cyNodeAttrs=Cytoscape.getNodeAttributes();
		for(Object s:elementStore){
			if(s instanceof CSML19EntityElement){
				String label=((CSML19EntityElement)s).getLabel();
				String entityName=((CSML19EntityElement)s).getName();
				System.out.println("entity name:"+entityName);
//				String catenatedComment=((CSML19EntityElement)s).getComment();
//				Matcher mtchMoleType=ptnMoleType.matcher(catenatedComment);
//				Matcher mtchKeggCompId=ptnKeggCompId.matcher(catenatedComment);
//				Matcher mtchName=ptnName.matcher(catenatedKeggComment);
//				
//				if(mtchMoleType.matches()){
//					
//				}
				
				entityLabelMapObjectStore.put(label, (CSML19EntityElement)s);
			}else if(s instanceof CSML19ProcessElement){
				String ec="";
				String keggReactionId="";
				ArrayList<String> involvedPathwayId=new ArrayList<String>();
				String processLabel=((CSML19ProcessElement)s).getLabel();
				String processName=((CSML19ProcessElement)s).getName();
//				String processComment=((CSML19ProcessElement)s).getComment();
				CSML19ConnectorElement[] connectorStore=((CSML19ProcessElement)s).getFunctionElement().getConnectorElement();
				
				String catenatedComment=((CSML19ProcessElement)s).getComment().replace("\n", "");
				System.out.println("comment for one process is:"+catenatedComment);
				if(catenatedComment!=null){
					Matcher mtchEc=ptnEc.matcher(catenatedComment);
					Matcher mtchKeggReactionId=ptnKeggReactionId.matcher(catenatedComment);
					Matcher mtchKeggPathwayId=ptnKeggPathwayId.matcher(catenatedComment);
					
					if(mtchEc.matches()){
						ec=mtchEc.group(1);
						System.out.println("ec:"+ec);
					}
					if(mtchKeggReactionId.matches()){
						keggReactionId=mtchKeggReactionId.group(1);
						System.out.println("keggReactionId:"+keggReactionId);
					}
					
					if(mtchKeggPathwayId.matches()){
						String catenatedPathwayId=mtchKeggPathwayId.group(1);
						String[] pathwayIdArray=catenatedPathwayId.split(",");
						for(String pathwayId:pathwayIdArray){
							if(!pathwayId.equals("")){
								involvedPathwayId.add(pathwayId);
							}
						}
					}
				}
				for(CSML19ConnectorElement i:connectorStore){
					String fromLabel=i.getFrom();
					String toLabel=i.getTo();
					String moleTypeFrom="";
					String moleTypeTo="";
					CyNode node1=null;
					CyNode node2=null;
					
					if(fromLabel.equals(processLabel)){
						moleTypeFrom="enzyme";
						moleTypeTo="metabolite";
					}else{
						moleTypeFrom="metabolite";
						moleTypeTo="enzyme";
					}
					
					// deal with the entity where the edge is from
					if(moleTypeFrom.equalsIgnoreCase("enzyme")){
//						Protein e_from=(Protein)proteinEntityLabelMapObject.get(from);
//						moleType_from=e_from.getAnnotationP().getMoleType();//getAnnotationP() P means protein
//						String ec_from=e_from.getAnnotationP().getEcStore().getEc();
						{
							node1=Cytoscape.getCyNode(processName,true);
//							JOptionPane.showMessageDialog(new JFrame(), "from node name is:"+e_from.getName());
							nodeCollection.add(node1);
							cyNetwork_.addNode(node1);
							cyNodeMoleTypeMapping.put(node1, moleTypeFrom);
							cyNodeAttrs.setAttribute(node1.getIdentifier(),"moleType",moleTypeFrom);
							cyNodeAttrs.setAttribute(node1.getIdentifier(),"nodeType", moleTypeFrom);
							cyNodeAttrs.setAttribute(node1.getIdentifier(), "mainDbId", ec);
							cyNodeAttrs.setAttribute(node1.getIdentifier(), "displayName", ec);
							cyNodeAttrs.setListAttribute(node1.getIdentifier(), "involvedPathwayId", involvedPathwayId);
							
							//							nodeNum--;
						}
						{
							CSML19EntityElement entityTo=entityLabelMapObjectStore.get(toLabel);
							String keggCompId="";
							
							String commentCatenated=entityTo.getComment();
							if(commentCatenated!=null){
								Matcher mtchKeggCompId=ptnKeggCompId.matcher(commentCatenated);

								if(mtchKeggCompId.matches()){
									keggCompId=mtchKeggCompId.group(1);
								}
							}
							
//							node2=Cytoscape.getCyNode(entityTo.getName(),true);
							node2=Cytoscape.getCyNode(keggCompId,true);
//							JOptionPane.showMessageDialog(new JFrame(), "from node name is:"+e_from.getName());
							nodeCollection.add(node2);
							cyNetwork_.addNode(node2);
							cyNodeMoleTypeMapping.put(node2, moleTypeFrom);
							cyNodeAttrs.setAttribute(node2.getIdentifier(),"moleType",moleTypeTo);
							cyNodeAttrs.setAttribute(node2.getIdentifier(),"nodeType", moleTypeTo);
							cyNodeAttrs.setAttribute(node2.getIdentifier(), "mainDbId", keggCompId);
							cyNodeAttrs.setAttribute(node2.getIdentifier(), "displayName", entityTo.getName());
							
							
						}
						
						
					}else if(moleTypeFrom.equalsIgnoreCase("metabolite")){
						CSML19EntityElement entityFrom=entityLabelMapObjectStore.get(fromLabel);
			
						System.out.println(entityFrom.getName()+" "+this.getClass().getName()); 
//						moleType_from=e_from.getAnnotationM().getMoleType();// getAnnotationM() M means metabolite
						String keggCompId="";
						
						String commentCatenated=entityFrom.getComment();
//						Matcher mtchMoleType=ptnMoleType.matcher(commentCatenated);
						if(commentCatenated!=null){
							Matcher mtchKeggCompId=ptnKeggCompId.matcher(commentCatenated);

							if(mtchKeggCompId.matches()){
								keggCompId=mtchKeggCompId.group(1);
							}
						}
//						String kegg_from=e_from.getAnnotationM().getKeggCompoundId();
//						String oneCommonName=e_from.getAnnotationM().getCommonNameStore().getCommonName().get(0);
						{
//							node1=Cytoscape.getCyNode(entityFrom.getName(),true);
							node1=Cytoscape.getCyNode(keggCompId,true);
//							JOptionPane.showMessageDialog(new JFrame(), "from node name is:"+e_from.getName());
							nodeCollection.add(node1);
							cyNetwork_.addNode(node1);
							cyNodeMoleTypeMapping.put(node1, moleTypeFrom);
							cyNodeAttrs.setAttribute(node1.getIdentifier(),"moleType",moleTypeFrom);
							cyNodeAttrs.setAttribute(node1.getIdentifier(),"nodeType", moleTypeFrom);
							cyNodeAttrs.setAttribute(node1.getIdentifier(), "mainDbId", keggCompId);
							cyNodeAttrs.setAttribute(node1.getIdentifier(), "displayName", entityFrom.getName());
//							nodeNum--;
						}
						{

							node2=Cytoscape.getCyNode(processName,true);
							//								JOptionPane.showMessageDialog(new JFrame(), "from node name is:"+e_from.getName());
							nodeCollection.add(node2);
							cyNetwork_.addNode(node2);
							cyNodeMoleTypeMapping.put(node2, moleTypeFrom);
							cyNodeAttrs.setAttribute(node2.getIdentifier(),"moleType",moleTypeTo);
							cyNodeAttrs.setAttribute(node2.getIdentifier(),"nodeType", moleTypeTo);
							cyNodeAttrs.setAttribute(node2.getIdentifier(), "mainDbId", ec);
							cyNodeAttrs.setAttribute(node2.getIdentifier(), "displayName", ec);
							cyNodeAttrs.setListAttribute(node2.getIdentifier(), "involvedPathwayId", involvedPathwayId);
							//							nodeNum--;


						}
						
						
					}
					
//					// deal with the entity where the edge is to
//					if(classType_to.equalsIgnoreCase("protein")){
//						Protein e_to=(Protein)proteinEntityLabelMapObject.get(to);  //*v1 20110119
//						System.out.println(e_to.getName()+" "+this.getClass().getName()); 
//						moleType_to=e_to.getAnnotationP().getMoleType();
//						String ec_to=e_to.getAnnotationP().getEcStore().getEc();
//						{
//							node2=Cytoscape.getCyNode(e_to.getName(),true);
////							JOptionPane.showMessageDialog(new JFrame(), "to node name is:"+e_from.getName());
//							nodeCollection.add(node2);
//							cyNetwork_.addNode(node2);
//							cyNodeMoleTypeMapping.put(node2, moleType_to);
//							cyNodeAttrs.setAttribute(node2.getIdentifier(), "moleType", moleType_to);
//							cyNodeAttrs.setAttribute(node2.getIdentifier(), "mainDbId", ec_to);
//							cyNodeAttrs.setAttribute(node2.getIdentifier(),"displayName",ec_to);
////							nodeNum
//						}
//					}else if(classType_to.equalsIgnoreCase("metabolite")){
//						Metabolite e_to=(Metabolite)metaboliteEntityLabelMapObject.get(to);  //*v1 20110119
//						moleType_to=e_to.getAnnotationM().getMoleType();
//						String kegg_to=e_to.getAnnotationM().getKeggCompoundId();
//						{
//							node2=Cytoscape.getCyNode(e_to.getName(),true);
////							JOptionPane.showMessageDialog(new JFrame(), "to node name is:"+e_from.getName());
//							nodeCollection.add(node2);
//							cyNetwork_.addNode(node2);
//							cyNodeMoleTypeMapping.put(node2, moleType_to);
//							cyNodeAttrs.setAttribute(node2.getIdentifier(), "moleType", moleType_to);
//							cyNodeAttrs.setAttribute(node2.getIdentifier(), "mainDbId", kegg_to);
//							cyNodeAttrs.setAttribute(node2.getIdentifier(), "displayName", e_to.getName()); 
////							nodeNum
//						
//						}
//					}
					
					CyEdge edge=Cytoscape.getCyEdge(node1, node2, Semantics.INTERACTION, "reaction", true);
					cyNetwork_.addEdge(edge);
					edgeCollection.add(edge);
					
				}
			}

		}
		cyNetwork=cyNetwork_;
		Cytoscape.firePropertyChange(Cytoscape.ATTRIBUTES_CHANGED, null, null);
	}
	public CyNetwork getCyNetwork(){
		return cyNetwork;
		
	}
	
	public ArrayList getNodeCollection(){
		return nodeCollection;
	}
	
	public Collection getEdgeCollection(){
		return edgeCollection;
		
	}
	
	public HashMap<CyNode,String> getCyNodeMoleTypeMapping(){
		return cyNodeMoleTypeMapping;
	}

	public CyAttributes getCyNodeAttribute(){
		return cyNodeAttrs;
	}
}
