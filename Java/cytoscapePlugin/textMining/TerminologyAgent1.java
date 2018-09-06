package textMining;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;


//TerminologyAgent-------------------------
//v0 20110625: querying to whatizit is carried out here. If user selects a compound node in Cytoscape, the "UkPmcChemicals" is used, protein "whatizitSwissprot"
//v1 20110704: add one more argument in the constructor signature- conclusion_C, so that the class will process the text in the "conclusion" sector(before there are only intro/background,result,discussion).

public class TerminologyAgent1 {
	HashMap<String,Integer> matchedTimesBioentityTermIRCD=new HashMap<String,Integer>(); //the generic type should be <string,int>
	
	public static void main(String[] args){
		String introText=args[0];
		String resultText=args[1];
		String conclusionText=args[2];
		String discussionText=args[3];
		String typeOfTermToTag=args[4];
		String termsOfCertainType=args[5];
		String[] termArray=termsOfCertainType.split(",");
		ArrayList<String> termArrayList=new ArrayList<String>();
		for(String term:termArray){
			termArrayList.add(term);
		}
		String tempFilePath=args[5];
		try{
			BufferedWriter bw=new BufferedWriter(new FileWriter(tempFilePath));
			TerminologyAgent1 ta=new TerminologyAgent1(introText,resultText,conclusionText,discussionText,typeOfTermToTag,termArrayList,bw);
		}catch(IOException ioex){
			ioex.printStackTrace();
		}
	}
	public TerminologyAgent1(String introduction_I,String result_R,String conclusion_C,String discussion_D,String terminologyType,ArrayList<String> termUserWantInCertainField,BufferedWriter bw) throws IOException{
		String tag1="";
		String tag2="";
		String tag3="";
		String tag4="";
		boolean tagChemical=false;
		boolean tagProtein=false;
		String tagNameForBioentityInWhatizit="";
		
		
		
		if(terminologyType.equalsIgnoreCase("chemical")){
			tagChemical=true;
			tag1="taggedIntrocuctionWithUkPmcChemicals";
			tag2="taggedResultWithUkPmcChemicals";
			tag3="taggedDiscussionWithUkPmcChemicals";
			tag4="taggedConclusionWithUkPmcChemicals";
			tagNameForBioentityInWhatizit="Chemical Entity of Biological Interest";
		}else if(terminologyType.equalsIgnoreCase("protein")){
			tagProtein=true;
			tag1="taggedIntrocuctionWithSwissprot";
			tag2="taggedResultWithSwissprot";
			tag3="taggedDiscussionWithSwissprot";
			tag4="taggedConclusionWithSwissprot";
			tagNameForBioentityInWhatizit="UniProt";
		}
		
		
		
		WhatizitAgent whatAgentForIntroduction=new WhatizitAgent(introduction_I);
		WhatizitAgent whatAgentForResult=new WhatizitAgent(result_R);
		WhatizitAgent whatAgentForDiscussion=new WhatizitAgent(discussion_D);
		WhatizitAgent whatAgentForConclusion=new WhatizitAgent(conclusion_C);

		
		
		
		
		
		
		
		//		String taggedIntroductionWithChemical=whatAgentForIntroduction.annotationWithChemicals();
		
		String taggedIntrocuctionWithTerminology="";
		if(tagChemical==true){
			taggedIntrocuctionWithTerminology=whatAgentForIntroduction.annotationWithUkPmcChemicals();
		}else if(tagProtein==true){
			taggedIntrocuctionWithTerminology=whatAgentForIntroduction.annotateWithSwissprot();
		}
		//						System.out.println(taggedIntroductionWithChemical);
		bw.write(tag1+":");
//		bw.write(taggedIntroductionWithChemical+"\n");
		bw.write(taggedIntrocuctionWithTerminology+"\n");
		bw.flush();
//		HashMap ontologyToTermSetInIntroduction=new WhatizitParser(taggedIntroductionWithChemical).getOntologyToTermSet();
		HashMap ontologyToTermSetInIntroduction=new WhatizitParser2(taggedIntrocuctionWithTerminology).getOntologyToTermSet();
		
		if(ontologyToTermSetInIntroduction.keySet().size()!=0){
			System.out.println("one ontology from the parsed whatizit for introduction:"+ontologyToTermSetInIntroduction.keySet().toArray()[0]);
		}else{
			System.out.println("no single ontolgy is found in the text of introduction");
		}


		
		
		
		
		
		
		
		
		
		
		
		
		
		// Deal with the Result text
		//		String taggedResultWithChemical=whatAgentForResult.annotationWithChemicals();
		String taggedResultWithTerminolgy="";
		
		if(tagChemical==true){
			taggedResultWithTerminolgy=whatAgentForResult.annotationWithUkPmcChemicals();
		}else if(tagProtein==true){
			taggedResultWithTerminolgy=whatAgentForResult.annotateWithSwissprot();
		}
		bw.write(tag2+":");
//		bw.write(taggedResultWithChemical+"\n");
		bw.write(taggedResultWithTerminolgy+"\n");
		bw.flush();
//		HashMap ontologyToTermSetInResult=new WhatizitParser(taggedResultWithChemical).getOntologyToTermSet();
		HashMap ontologyToTermSetInResult=new WhatizitParser2(taggedResultWithTerminolgy).getOntologyToTermSet();
		
		if(ontologyToTermSetInResult.keySet().size()!=0){
			System.out.println("one ontology from the parsed whatizit for result:"+ontologyToTermSetInResult.keySet().toArray()[0]);
		}else{
			System.out.println("no single ontolgy is found in the text of result");
		}


		
		// Deal with conclusion text
		String taggedConclusionWithTerminology="";
		if(tagChemical==true){
			taggedConclusionWithTerminology=whatAgentForConclusion.annotationWithUkPmcChemicals();
		}else if(tagProtein==true){
			taggedConclusionWithTerminology=whatAgentForConclusion.annotateWithSwissprot();
		}
		
		bw.write(tag4+":");
//		bw.write(taggedDiscussionWithChemical+"\n");
		bw.write(taggedConclusionWithTerminology+"\n");
		bw.flush();
//		HashMap ontologyToTermSetInDiscussion=new WhatizitParser(taggedDiscussionWithChemical).getOntologyToTermSet();
		HashMap ontologyToTermSetInConclusion=new WhatizitParser2(taggedConclusionWithTerminology).getOntologyToTermSet();
		if(ontologyToTermSetInConclusion.keySet().size()!=0){
			System.out.println("one ontology from the parsed whatizit for conclusion:"+ontologyToTermSetInConclusion.keySet().toArray()[0]);
		}else{
			System.out.println("no single ontolgy is found in the text of conclusion");
		}
		
		
		
		
		
		// Deal with discussion text
		//		String taggedDiscussionWithChemical=whatAgentForDiscussion.annotationWithChemicals();
		String taggedDiscussionWithTerminology="";
		if(tagChemical==true){
			taggedDiscussionWithTerminology=whatAgentForDiscussion.annotationWithUkPmcChemicals();
		}else if(tagProtein==true){
			taggedDiscussionWithTerminology=whatAgentForDiscussion.annotateWithSwissprot();
		}
		
		bw.write(tag3+":");
//		bw.write(taggedDiscussionWithChemical+"\n");
		bw.write(taggedDiscussionWithTerminology+"\n");
		bw.flush();
//		HashMap ontologyToTermSetInDiscussion=new WhatizitParser(taggedDiscussionWithChemical).getOntologyToTermSet();
		HashMap ontologyToTermSetInDiscussion=new WhatizitParser2(taggedDiscussionWithTerminology).getOntologyToTermSet();
		if(ontologyToTermSetInDiscussion.keySet().size()!=0){
			System.out.println("one ontology from the parsed whatizit for discussion:"+ontologyToTermSetInDiscussion.keySet().toArray()[0]);
		}else{
			System.out.println("no single ontolgy is found in the text of discussion");
		}

		
		
		// sum the result from introduction, result, conclusion, discussion
		
		HashMap ontologyToTermSetTotalText=new HashMap();

		ArrayList arrayOfHashMap=new ArrayList();

		arrayOfHashMap.add(ontologyToTermSetInIntroduction);
		arrayOfHashMap.add(ontologyToTermSetInResult);
		arrayOfHashMap.add(ontologyToTermSetInDiscussion);
		arrayOfHashMap.add(ontologyToTermSetInConclusion);
		// ontologyToTermSetTotalText contains the ontology(it might be "Chemical","UniProt",
		// "Enzyme",...) and the term set found in introduction, result, discussion
		ontologyToTermSetTotalText=MergeHashMap.merge(arrayOfHashMap);

		//				ArrayList chemicalTermInIntroduction=(ArrayList)ontologyToTermSetInIntroduction.get("Chemical");
		//				for(Object k:chemicalTermInIntroduction){
		//					System.out.println("||||||||||||||||||||||||");
		//					System.out.println("one chemical term in introduction:"+(String)k);
		//				}

		
//		ArrayList chemicalTermUserWant=(ArrayList) queryOntologyToSetOfTerm.get("chemical");
//for(Object s:chemicalTermUserWant){
//System.out.println("one chemical term user inputs is:"+(String)s);
//}

		ArrayList bioentityTermWhatizitFindInIRCD=new ArrayList();
		boolean bioentityTermIsFoundInIRCD=false;
//		if(ontologyToTermSetTotalText.containsKey("Chemical")){	//it is possible no Chemical term is found in IRD
		if(ontologyToTermSetTotalText.containsKey(tagNameForBioentityInWhatizit)){	//it is possible no Chemical term is found in IRD
			bioentityTermIsFoundInIRCD=true;
//			chemicalTermWhatizitFindInIRD=(ArrayList) ontologyToTermSetTotalText.get("Chemical");//"Chemical", not "chemical" // the terms in chemicalTermWhatizitFindInIRD are not unique(there are duplicate of one term)
			bioentityTermWhatizitFindInIRCD=(ArrayList) ontologyToTermSetTotalText.get(tagNameForBioentityInWhatizit);//"Chemical", not "chemical" // the terms in chemicalTermWhatizitFindInIRD are not unique(there are duplicate of one term)
			
			if(bioentityTermWhatizitFindInIRCD.size()>0){
				for(Object u:bioentityTermWhatizitFindInIRCD){
					System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
					System.out.println("one bioentity term found by whatizit in the whole IRD:"+(String)u);
				}
			}
		}
		for(Object oneTermUserWant:termUserWantInCertainField){
			System.out.println("one term user input:"+(String)oneTermUserWant);
			matchedTimesBioentityTermIRCD.put((String)oneTermUserWant, 0);
			if(bioentityTermIsFoundInIRCD==true){ //originally chmicalTermWhatizitFindInIRD.size() is used to judge the existence of found chemical terms. This will make error message if the previous if(){} doesn't pass.


				for(Object t:bioentityTermWhatizitFindInIRCD){
					System.out.println("one bioentity whatizit find in IRCD:"+(String)t);
					if(((String)oneTermUserWant).equalsIgnoreCase((String)t)){
						int oldCount=(Integer)(matchedTimesBioentityTermIRCD.get(oneTermUserWant));
						int newCount=oldCount+1;
						matchedTimesBioentityTermIRCD.put((String)oneTermUserWant, newCount);
					}
				}
			}
		}

	
	}
	
	public HashMap<String,Integer> getTermStatisticsOfUserDesire(){
		return matchedTimesBioentityTermIRCD;
	}
}
