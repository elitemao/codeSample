package plugin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

import webService.WrapperSimple;

public class ProteinChemicalInteractionAgent {
	ArrayList interactingChemicals=new ArrayList();
	ArrayList resultObjectStore=new ArrayList(); // resultObjectStore save the data of every column retrieved from stitch2_chemical_protein_inter_human table
	public ProteinChemicalInteractionAgent(String spacc,String taxo){
		
		
		String query1="select ensemblProtein from spacc_ensemblPro where spacc=?";
		
		
		
		String query2="select * from stitch2_chemical_protein_inter_human where proteinId=?";
		if(!taxo.equals("9606")){
			System.out.println("now only human is supported");
		}else{


			WrapperSimple w=new WrapperSimple();

			//spacc to ensp
			String[] seed1={spacc};

			Vector v1=w.getResults(2, query1, seed1);
			Iterator it1=v1.iterator();
			while(it1.hasNext()){
				String[] result1=(String[])it1.next();

				String ensp=result1[0];

				System.out.println("ensp for "+spacc+" is "+ensp);
				//ensp to cid
				String[] seed2={ensp};

				Vector v2=w.getResults(2, query2, seed2);

				Iterator it2=v2.iterator();

				while(it2.hasNext()){
					String[] result2=(String[])it2.next();
					resultObjectStore.add(result2);
					String pubChemId=result2[0];
					if(!pubChemId.equalsIgnoreCase("")){
						interactingChemicals.add(pubChemId);
					}
				}

			}
			
		}


	}
		
	public ArrayList getInteractingChemicals(){
		return interactingChemicals;
	}
	
	// cid fulfilling any of the requirement will be returned.each threshold should be integer
	//only do filtering on expEvidenceScore,dbEvidenceScore, textMiningEvidenceScore, not combinedScore
	public ArrayList getChemicalsFilteredByEvidence(double expEviScoreThreshold,double dbEviScoreThreshold,double textMiningEviScoreThreshold){
		ArrayList filteredChemicals=new ArrayList();
		for(Object oneArrayOfString:resultObjectStore){
			Double expEviScore=Double.parseDouble(((String[])oneArrayOfString)[2]);
			Double dbEviScore=Double.parseDouble(((String[])oneArrayOfString)[3]);
			Double textMiningEviScore=Double.parseDouble(((String[])oneArrayOfString)[4]);
			Double combinedEviScore=Double.parseDouble(((String[])oneArrayOfString)[5]);
			System.out.println(expEviScore);
			System.out.println(dbEviScore);
			System.out.println(textMiningEviScore);
			System.out.println(combinedEviScore);
			if(expEviScore>=expEviScoreThreshold){
				
				filteredChemicals.add(((String[])oneArrayOfString)[0]);
			}else if(dbEviScore>=dbEviScoreThreshold){
				filteredChemicals.add(((String[])oneArrayOfString)[0]);
			}else if(textMiningEviScore>=textMiningEviScoreThreshold){
				filteredChemicals.add(((String[])oneArrayOfString)[0]);
			}
		}
		return filteredChemicals;
	}
	
	
	public static void main(String[] args){
		String spacc=args[0];
		String taxo=args[1];
		ProteinChemicalInteractionAgent pcia=new ProteinChemicalInteractionAgent(spacc,taxo);
		ArrayList answer=pcia.getInteractingChemicals();
		ArrayList filteredAnswer=pcia.getChemicalsFilteredByEvidence(200, 200, 3000);
		System.out.println("no filter:"+answer.size());
		System.out.println("filtered:"+filteredAnswer.size());
		for(Object x:answer){
			System.out.println("ii_noFilter:"+(String)x);
		}
		for(Object u:filteredAnswer){
			System.out.println("ii_filtered:"+(String)u);
		}
	}
}
