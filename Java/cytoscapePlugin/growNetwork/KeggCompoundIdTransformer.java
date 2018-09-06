package growNetwork;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

public class KeggCompoundIdTransformer {
	String keggCompoundId;
	public KeggCompoundIdTransformer(String keggCompoundId_){
		keggCompoundId=keggCompoundId_;
	}
	
	public ArrayList getCommonName(){
		ArrayList nameStore=new ArrayList();
		String query="select distinct name from kegg_compound_name where entry=?";
		String[] details={keggCompoundId};
		
		Vector result=StartStaticVariable.wrapperSimple.getResults(1,query , details);
		
		Iterator it=result.iterator();
		
		while(it.hasNext()){
			String[] results=(String[])it.next();
			
			if(!results[0].equalsIgnoreCase("")&& results[0]!=""){
				nameStore.add(results[0]);
			}
			
		}
		return nameStore;
	}
}
