package textMining;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//RefineBioTextResult---------------------
//v0 20110218:use the named entity recognition function of Whatizit
//v0 20110221: the putAll method of HashMap will over-writer the former by latter--don't use that
//v1 20110223: use PmcFullTextParserByMixedSchema2
//v1 20110301: change the variable name "matchedTimesChemicalTerm" to "matchedTimesChemicalTermIRD","matchedTimesFreeText" to "matchedTimesFreeTextIRD" 
//              "getMatchedTimesChemicalTerm()" to "getMatchedTimesChemicalTermIRD()", "getMatchedTimesFreeTextTerm()" to "getMatchedTimesFreeTextTermIR()" to avoid ambiguity
//v2 20110301: use PmcFullTextParserByMixedSchema3.
//v3 20110302: use PmcFullTextParserByMixedSchema4
//v4 20110302: use PmcFullTextParserByMixedSchema5
//v4 20110302: check the number of element in arraylist chemicalTermWhatizitFindInIRD before iterating its elements
//v5 20110302: check if hashmap-ontologyToTermSetTotalText contains "Chemical" key before retrieving its value.To cope with the problem while parsing pmid 17397529
//v6 20110302: use PmcFullTextParserByMixedSchema6 to cope with pmcid:1852094(a review article).Distinguish the article type between research-article and review-article.
//             If it is the review article to parse, get the text in the body tag and parse it.
//v7 20110303: change the way to test if any chemical term is found from Whatizit: using a boolean variable- chemicalTermIsFoundInBody,chemicalTermIsFoundInIRD
//v7 20110303: add "if(!pmcid.equalsIgnoreCase("NotDefined")){...}"
//v8 20110308: modify the signature of the constructor:input pmcid, not pmid
//v9 20110413: use PmcFullTextParserByMixedSchema7 - for pmcid 2233667
//v9 20110413: deal with the article with article-type "announcement"- for pmcid 2428068
//v10 20110413: deal with the article-type except "research-article","announcement","review-article". There are more article-types needed to process individually.
//             This version only deal with research-article, announcement, review-article.If the other article-type is met, it will return 0 for the chemical and free-text phrases count.
//v10 20110418: remove the try... catch in the constructor and use "throws Exception" instead
//v11 20110608: use whatizitUkPmcChemicals to tag chemicals.
//v12 20110609: use generic in the signature of the constructor
//v12 20110609: use WhatizitParser1
//v13 20110610: use generic for the HashMaps matchedTimesChemicalTermIRD, matchedTimesFreeTextIRD, and for the Set ontologyUserWant
//v14 20110610: user WhatizitParser2
//v14 20110621: if pmcid is 2206488, skip it
//v15 20110625: To make the code simple, move the duplicated code to a new class "TerminologyAgent" and uses it in the code.
//v15 20110625: in version 14, the code used UkPmcChemicals to tag both protein and compound. In this version, use whatizitSwissprot for tagging protein name
//v16 20110626: change the method names.

//RefineArticle----------------------
//v0 20110626: renamed from RefineBioTextResult16.Just to make the name consisting with the function of this class
//v1 20110704: use PmcFullTextParserByMixedSchema8,TerminologyAgent1 so that this class process also the text in the "conclusion" sector.
//v1 20110712: use EutilityPubMed4
//v1 20110718: use EutilityPubMed5

public class RefineArticle1 {
	HashMap<String,Integer> matchedTimesBioentityTermIRCD=new HashMap<String,Integer>(); //the generic type should be <string,int>
	HashMap<String,Integer> matchedTimesFreeTextIRCD=new HashMap<String,Integer>();
	Set<String> ontologyUserWant;

	public static void main(String[] args){
		HashMap<String,ArrayList<String>> userQuery=new HashMap<String,ArrayList<String>>();	
		ArrayList<String> bioentityTermList=new ArrayList<String>();
		ArrayList<String> freeTextQueryList=new ArrayList<String>();

		String pmcid=args[0];
		String bioentityType=args[1];//could only be "chemical" or "protein"
		String bioentityTerm=args[2];
		String originalFreeTextQueryTerm=args[3];//this free text can be comma seperated

		bioentityTermList.add(bioentityTerm);

		String[] originalFreeTextQueryTermArray=originalFreeTextQueryTerm.split(",");

		for(String x:originalFreeTextQueryTermArray){
			freeTextQueryList.add(x);
		}
		userQuery.put(bioentityType,bioentityTermList);
		userQuery.put("freeText", freeTextQueryList);
		try {
			RefineArticle1 rfbtr13=new RefineArticle1(pmcid,userQuery);
			HashMap<String,Integer> bioentityTermCountForUserInput=rfbtr13.getMatchedTimesBioentityQueryTermIRD();
			Set<String> foundBioentityTerms=bioentityTermCountForUserInput.keySet(); 
			for(String s:foundBioentityTerms){
				System.out.println("one bioentity term: "+s+"  number of appearance: "+bioentityTermCountForUserInput.get(s));
			}
			
			HashMap<String,Integer> freeTextTermCountForUserInput=rfbtr13.getMatchedTimesFreeTextQueryTermIRD();
			Set<String> foundFreeTextTerms=freeTextTermCountForUserInput.keySet(); 
			for(String s:foundFreeTextTerms){
				System.out.println("one free text term: "+s+"  number of appearance: "+freeTextTermCountForUserInput.get(s));
			}
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public RefineArticle1(String pmcid, HashMap<String,ArrayList<String>> queryOntologyToSetOfTerm) throws Exception{
		ontologyUserWant=queryOntologyToSetOfTerm.keySet();
		//		try{

		//pmc2206488 will make unresolved error,so skip it
		if(pmcid.equalsIgnoreCase("2206488")){
			return;
		}
		BufferedWriter bw=new BufferedWriter(new FileWriter("j:\\ForWork\\growNetwork\\taggedPmcid"+pmcid+".txt"));

		//			PmidToPmcid ptp=new PmidToPmcid(pmid);
		//			String pmcid=ptp.getPmcid();

		//			System.out.println("pmcid for "+pmid+" is:"+pmcid);

		//			if(!pmcid.equalsIgnoreCase("NotDefined")){

		EutilityPubMed5 eu3=new EutilityPubMed5();
		String eutilFullText=eu3.getFullTextByPost(pmcid);
		//			System.out.println("eutilfulltext:"+eutilFullText);
		PmcFullTextParserByMixedSchema8 pmcParser=new PmcFullTextParserByMixedSchema8(eutilFullText);


		String articleType=pmcParser.getArticleType();
		bw.write("article type of pmcid "+pmcid +" is:"+articleType+"\n");

		if(articleType.equalsIgnoreCase("research-article")||articleType.equalsIgnoreCase("announcement")){
			// if full text is accessible through PMC
			if(pmcParser.isFullTextAvailable()){

				String introduction_I=pmcParser.getIntroductionOrBackground();
				//					System.out.println("introduction retrieved from pmc:"+introduction_I);
				bw.write("introdcution/background from pmc:"+introduction_I+"\n");
				bw.flush();
				String result_R=pmcParser.getResult();
				bw.write("result from pmc:"+result_R+"\n");
				bw.flush();
				String discussion_D=pmcParser.getDiscussion();
				bw.write("discussion from pmc:"+discussion_D+"\n");
				bw.flush();
				
				String conclusion_C=pmcParser.getConclusion();
				bw.write("conclusion from pmc:"+conclusion_C);
				bw.flush();
//				WhatizitAgent whatAgentForIntroduction=new WhatizitAgent(introduction_I);
//				WhatizitAgent whatAgentForResult=new WhatizitAgent(result_R);
//				WhatizitAgent whatAgentForDiscussion=new WhatizitAgent(discussion_D);
				//						for(Object y:ontologyUserWant){
				//							System.out.println("ww one ontology user want to search is:"+(String)y);
				//						}
				if(ontologyUserWant.contains("chemical")){
					TerminologyAgent1 termAgent=new TerminologyAgent1(introduction_I,result_R,conclusion_C,discussion_D,"chemical",queryOntologyToSetOfTerm.get("chemical"),bw);
					//							
					matchedTimesBioentityTermIRCD=termAgent.getTermStatisticsOfUserDesire();

				}else if(ontologyUserWant.contains("protein")){
					TerminologyAgent1 termAgent=new TerminologyAgent1(introduction_I,result_R,conclusion_C,discussion_D,"protein",queryOntologyToSetOfTerm.get("protein"),bw);
					//							
					matchedTimesBioentityTermIRCD=termAgent.getTermStatisticsOfUserDesire();
				}

				if(ontologyUserWant.contains("freeText")){
					ArrayList freeTextTermUserWant=(ArrayList) queryOntologyToSetOfTerm.get("freeText");
					for(Object oneFreeTextTermUserWant:freeTextTermUserWant){
						matchedTimesFreeTextIRCD.put((String)oneFreeTextTermUserWant, 0);
						Pattern ptn=Pattern.compile(" "+(String)oneFreeTextTermUserWant+" ");
						Matcher mtchI=ptn.matcher(introduction_I);
						Matcher mtchR=ptn.matcher(result_R);
						Matcher mtchD=ptn.matcher(discussion_D);
						Matcher mtchC=ptn.matcher(conclusion_C);
						
						while(mtchI.find()){
							int oldCount=(Integer)(matchedTimesFreeTextIRCD.get(oneFreeTextTermUserWant));
							int newCount=oldCount+1;
							matchedTimesFreeTextIRCD.put((String)oneFreeTextTermUserWant, newCount);
						}

						while(mtchR.find()){
							int oldCount=(Integer)(matchedTimesFreeTextIRCD.get(oneFreeTextTermUserWant));
							int newCount=oldCount+1;
							matchedTimesFreeTextIRCD.put((String)oneFreeTextTermUserWant, newCount);
						}
						while(mtchD.find()){
							int oldCount=(Integer)(matchedTimesFreeTextIRCD.get(oneFreeTextTermUserWant));
							int newCount=oldCount+1;
							matchedTimesFreeTextIRCD.put((String)oneFreeTextTermUserWant, newCount);
						}
						
						while(mtchC.find()){
							int oldCount=(Integer)(matchedTimesFreeTextIRCD.get(oneFreeTextTermUserWant));
							int newCount=oldCount+1;
							matchedTimesFreeTextIRCD.put((String)oneFreeTextTermUserWant, newCount);
						}
					}
				}


			}else{  // if full text is not available from PMC, get abstract instead

				String abstractt=pmcParser.getAbstract();
//				WhatizitAgent whatAgentForAbstract=new WhatizitAgent(abstractt);

				if(ontologyUserWant.contains("chemical")){
					TerminologyAgent1 termAgent=new TerminologyAgent1(abstractt,"","","","chemical",queryOntologyToSetOfTerm.get("chemical"),bw);
					//							
					matchedTimesBioentityTermIRCD=termAgent.getTermStatisticsOfUserDesire();
				}else if(ontologyUserWant.contains("protein")){
					TerminologyAgent1 termAgent=new TerminologyAgent1(abstractt,"","","","protein",queryOntologyToSetOfTerm.get("protein"),bw);
					//							
					matchedTimesBioentityTermIRCD=termAgent.getTermStatisticsOfUserDesire();
				}

				if(ontologyUserWant.contains("freeText")){
					ArrayList freeTextTermUserWant=(ArrayList) queryOntologyToSetOfTerm.get("freeText");
					for(Object oneFreeTextTermUserWant:freeTextTermUserWant){
						matchedTimesFreeTextIRCD.put((String)oneFreeTextTermUserWant, 0);
						Pattern ptn=Pattern.compile(" "+(String)oneFreeTextTermUserWant+" ");
						Matcher mtch=ptn.matcher(abstractt);

						while(mtch.find()){
							int oldCount=(Integer)(matchedTimesFreeTextIRCD.get(oneFreeTextTermUserWant));
							int newCount=oldCount+1;
							matchedTimesFreeTextIRCD.put((String)oneFreeTextTermUserWant, newCount);
						}
					}
				}

			}


		}else if(articleType.equalsIgnoreCase("review-article")){
			if(pmcParser.isFullTextAvailable()){

				String bodyText=pmcParser.getBodyText();
				//					System.out.println("body text retrieved from pmc:"+bodyText);
				bw.write("body text from pmc:"+bodyText+"\n");

				if(ontologyUserWant.contains("chemical")){
					TerminologyAgent1 ta=new TerminologyAgent1(bodyText,"","","","chemical",(ArrayList) queryOntologyToSetOfTerm.get("chemical"),bw);

					matchedTimesBioentityTermIRCD=ta.getTermStatisticsOfUserDesire();

				}else if(ontologyUserWant.contains("protein")){
					TerminologyAgent1 ta=new TerminologyAgent1(bodyText,"","","","protein",(ArrayList) queryOntologyToSetOfTerm.get("protein"),bw);

					matchedTimesBioentityTermIRCD=ta.getTermStatisticsOfUserDesire();
				}

				if(ontologyUserWant.contains("freeText")){
					ArrayList freeTextTermUserWant=(ArrayList) queryOntologyToSetOfTerm.get("freeText");
					for(Object oneFreeTextTermUserWant:freeTextTermUserWant){
						matchedTimesFreeTextIRCD.put((String)oneFreeTextTermUserWant, 0);
						Pattern ptn=Pattern.compile(" "+(String)oneFreeTextTermUserWant+" ");
						Matcher mtchBodyText=ptn.matcher(bodyText);


						while(mtchBodyText.find()){
							int oldCount=(Integer)(matchedTimesFreeTextIRCD.get(oneFreeTextTermUserWant));
							int newCount=oldCount+1;
							matchedTimesFreeTextIRCD.put((String)oneFreeTextTermUserWant, newCount);
						}


					}
				}



			}else{  // if full text is not available from PMC, get abstract instead

				String abstractt=pmcParser.getAbstract();

				if(ontologyUserWant.contains("chemical")){
					TerminologyAgent1 ta=new TerminologyAgent1(abstractt,"","","","chemical",(ArrayList<String>) queryOntologyToSetOfTerm.get("chemical"),bw);

					matchedTimesBioentityTermIRCD=ta.getTermStatisticsOfUserDesire();
				}else if(ontologyUserWant.contains("protein")){
					TerminologyAgent1 ta=new TerminologyAgent1(abstractt,"","","","protein",(ArrayList<String>) queryOntologyToSetOfTerm.get("protein"),bw);

					matchedTimesBioentityTermIRCD=ta.getTermStatisticsOfUserDesire();
				}

				if(ontologyUserWant.contains("freeText")){
					ArrayList freeTextTermUserWant=(ArrayList) queryOntologyToSetOfTerm.get("freeText");
					for(Object oneFreeTextTermUserWant:freeTextTermUserWant){
						matchedTimesFreeTextIRCD.put((String)oneFreeTextTermUserWant, 0);
						Pattern ptn=Pattern.compile(" "+(String)oneFreeTextTermUserWant+" ");
						Matcher mtch=ptn.matcher(abstractt);

						while(mtch.find()){
							int oldCount=(Integer)(matchedTimesFreeTextIRCD.get(oneFreeTextTermUserWant));
							int newCount=oldCount+1;
							matchedTimesFreeTextIRCD.put((String)oneFreeTextTermUserWant, newCount);
						}
					}
				}

			}


		}else{

			if(ontologyUserWant.contains("chemical")){
				ArrayList chemicalTermUserWant=(ArrayList) queryOntologyToSetOfTerm.get("chemical");
				for(Object oneChemicalTermUserWant:chemicalTermUserWant){
					System.out.println("one chemical term user input:"+(String)oneChemicalTermUserWant);
					matchedTimesBioentityTermIRCD.put((String)oneChemicalTermUserWant, 0);
				}
			}else if(ontologyUserWant.contains("protein")){
				ArrayList proteinTermUserWant=(ArrayList) queryOntologyToSetOfTerm.get("protein");
				for(Object oneProteinTermUserWant:proteinTermUserWant){
					System.out.println("one chemical term user input:"+(String)oneProteinTermUserWant);
					matchedTimesBioentityTermIRCD.put((String)oneProteinTermUserWant, 0);
				}
			}

			if(ontologyUserWant.contains("freeText")){
				ArrayList freeTextTermUserWant=(ArrayList) queryOntologyToSetOfTerm.get("freeText");
				for(Object oneFreeTextTermUserWant:freeTextTermUserWant){
					matchedTimesFreeTextIRCD.put((String)oneFreeTextTermUserWant, 0);
				}
			}

		}
		//			}
		bw.close();

		//		}catch(IOException ioex){
		//			ioex.printStackTrace();
		//		}
	}

	//return the count of the terms which user wishes to find 
	public HashMap<String,Integer> getMatchedTimesBioentityQueryTermIRD(){
		HashMap<String,Integer> returnedHM=new HashMap<String,Integer>();
		if(ontologyUserWant.contains("chemical")||ontologyUserWant.contains("protein")){
			returnedHM=matchedTimesBioentityTermIRCD;
		}else{
			return null;
		}

		return returnedHM;
	}

	//return the count of free text term which user wishes to find
	public HashMap<String,Integer> getMatchedTimesFreeTextQueryTermIRD(){
		HashMap<String,Integer> returnedHM=new HashMap<String,Integer>();
		if(ontologyUserWant.contains("freeText")){
			returnedHM=matchedTimesFreeTextIRCD;
		}else{
			return null;
		}
		return returnedHM;
	}
}
