package growNetwork;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import cytoscape.CyEdge;
import cytoscape.CyNode;
import cytoscape.task.TaskMonitor;

import webService.WrapperSimple;

// StartStaticVariable--------
// v0: to save some global variables which are shared by several classes during runtime
// v0: change the filterItem to tissue, and filterValue to organelle
// v0 20101103:add one more variable- relatedReactionForEnzyme
//v0 20110111: add one more static variable- TaskMonitor
//v0 20110204: add one more static variable- wrapperSimple
//v0 20110302: add one more static variable- bw_messageRecorder

public class StartStaticVariable {
	public static ArrayList processedReactionId=new ArrayList();
	public static ArrayList processedKeggCompId=new ArrayList();
	public  static BufferedWriter bwNetwork;
	public  static BufferedWriter bwMoleType;
	public  static BufferedWriter bwCommonName;
	public static  BufferedWriter bwOmim;
	public  static BufferedWriter bwHml;
	
	public static String savedNetworkPath;
	public static String savedGonPath;
	public static String savedHmlPath;
	public static String tissue;
	public static String organelle;
	public static int relatedReactionForMetabolite;
	public static int relatedReactionForEnzyme;
	public static ArrayList psudoArrayEqualMoleculePairStoreForWholeNet=new ArrayList();
	public static cytoscape.task.TaskMonitor taskMonitor;
	
	public static WrapperSimple wrapperSimple;
	
	public static int docCountTailing;
	
	public static BufferedWriter bw_messageRecorder;
	
	public static ArrayList<String> hiddenNodeId=new ArrayList<String>();
	
	public static ArrayList<CyNode> originalCyNodeList=new ArrayList<CyNode>();
	
	public static ArrayList<CyEdge> originalCyEdgeList=new ArrayList<CyEdge>();
	
	public StartStaticVariable(){
		wrapperSimple=new WrapperSimple();
	}
	
	public StartStaticVariable(String savedNetworkPath,String savedGonPath,String savedHmlPath,String tissue,String organelle,int reactionNoMetabolite,int reactionNoEnzyme){
//		String serPath=savedNetworkPath.replaceFirst(".txt", "")+".ser";
		
		StartStaticVariable.savedNetworkPath=savedNetworkPath;
		StartStaticVariable.savedGonPath=savedGonPath;
		StartStaticVariable.savedHmlPath=savedHmlPath;
		StartStaticVariable.tissue=tissue;
		StartStaticVariable.organelle=organelle;
		StartStaticVariable.relatedReactionForEnzyme=reactionNoEnzyme;
		StartStaticVariable.relatedReactionForMetabolite=reactionNoMetabolite;
		String moleculeTypeAttributeFilePath=savedNetworkPath.replaceFirst(".txt", "")+"_moleType.txt";
		String omimAttributeFilePath=savedNetworkPath.replaceFirst(".txt", "")+"_omim.txt";
		String commonNameAttributeFilePath=savedNetworkPath.replaceFirst(".txt","")+"_commonName.txt";
		String messageRecorderPath=savedNetworkPath.replaceFirst("\\/.*?\\.txt", "messageRecorder.txt");
		docCountTailing=0;
		wrapperSimple=new WrapperSimple();
		try{
			System.out.println("savvedNetworkPath:"+savedNetworkPath+" "+this.getClass().getName()+new Exception().getStackTrace()[0].getLineNumber());
			bwNetwork=new BufferedWriter(new FileWriter(savedNetworkPath));
			bwMoleType=new BufferedWriter(new FileWriter(moleculeTypeAttributeFilePath));
			bwMoleType.write("molecular type"+"\n");
			bwCommonName=new BufferedWriter(new FileWriter(commonNameAttributeFilePath));
			bwCommonName.write("common name"+"\n");
			bwOmim=new BufferedWriter(new FileWriter(omimAttributeFilePath));
			bwOmim.write("omim"+"\n");
			bwHml=new BufferedWriter(new FileWriter(savedHmlPath));
			//		BufferedWriter bw=new BufferedWriter(new FileWriter(savedGonPath));
			bw_messageRecorder=new BufferedWriter(new FileWriter(messageRecorderPath));
		}catch(IOException ioex){
			ioex.printStackTrace();
		}
	
	
	
	
	}
	
}
