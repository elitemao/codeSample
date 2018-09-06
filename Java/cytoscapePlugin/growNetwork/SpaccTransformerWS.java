package growNetwork;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;


import webService.WrapperSimple;


//SpaccTransformerWS--------------
//v0 20110127: get related info for the input spacc. Use redoxnw to get protein name and dawismd for the gene symbol


public class SpaccTransformerWS {
	String spid;
	
	public SpaccTransformerWS(String spacc){
		spid=SpaccToSpidWS.doit(spacc);
	}
	
	public ArrayList getCommonName(){
		ArrayList commonNameStore=new ArrayList();
		
		String query="select protein_name from unip_protein_name where spid = ?";
		WrapperSimple w=new WrapperSimple();
		String[] details = {spid};  
		
		Vector v = w.getResults(2, query,details); // 2 is redoxnw, 3 is for hprd_db from Klaus

		Iterator it = v.iterator();
		while(it.hasNext()){
			String[] result=(String[])it.next();
			
			String proteinName= result[0].trim();
					
			
			if(proteinName!="" && !proteinName.equalsIgnoreCase("")){
				
				commonNameStore.add(proteinName);
			}
			
		}
		return commonNameStore;
	}
	
	public ArrayList getGeneSymbol(){
		ArrayList geneSymbolStore=new ArrayList();
		String query="select gene_name from uniprot_genenames where uniprot_id=?";
		WrapperSimple w=new WrapperSimple();
		String[] details = {spid};  
		
		Vector v = w.getResults(1, query,details); // 2 is redoxnw, 3 is for hprd_db from Klaus

		Iterator it = v.iterator();
		while(it.hasNext()){
			String[] result=(String[])it.next();
			
			String oneSymbol=result[0];
			if(!oneSymbol.equalsIgnoreCase("")&& oneSymbol!=""){
				geneSymbolStore.add(oneSymbol);
			}
		}
		
		return geneSymbolStore;
	}
}
