package textMining;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
//WhatizitParser-----------------
//v0:
//v1 20110609: add generic type to HashMap,ArrayList
//v2 20110610: modify the regular expression in variable ptn due to the appearance of <a></a> tag.

public class WhatizitParser2 {
	HashMap<String,String> termAndOntology=new HashMap<String,String>();
	HashMap<String,ArrayList<String>> ontologyToTermSet=new HashMap<String,ArrayList<String>>();
	
	public WhatizitParser2(String taggedText){
		Pattern ptn=Pattern.compile("\\<span title=\"(.*?)\".*?\\>(\\<a .*?\\>)?(.*?)(\\<\\/a\\>)?\\<\\/span\\>");
//		Pattern ptn1=Pattern.compile("CHEBI%3A(\\d+)");
		Matcher mtch=ptn.matcher(taggedText);
		// search for tags in the whatizit-processed text
		while(mtch.find()){
			System.out.println("###############################################################################\n################################\n###########");
			String term=mtch.group(3);
			String ontology=mtch.group(1);
			System.out.println("term:"+term);
			System.out.println("ontology:"+ontology);
			termAndOntology.put(term, ontology);
			if(ontologyToTermSet.keySet().contains(ontology)){
				ArrayList<String> oldArray=(ArrayList<String>)ontologyToTermSet.get(ontology);
				oldArray.add(term);
				ontologyToTermSet.put(ontology, oldArray);
			}else{
				ArrayList<String> newArray=new ArrayList<String>();
				newArray.add(term);
				ontologyToTermSet.put(ontology, newArray);
			}
		}
		
	}

	public HashMap<String,String> getTaggedTerm(){
		return termAndOntology;
	}
	
	public HashMap<String,ArrayList<String>> getOntologyToTermSet(){
		return ontologyToTermSet;
	}
}
