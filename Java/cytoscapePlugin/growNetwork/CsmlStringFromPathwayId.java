package growNetwork;

import java.util.Iterator;
import java.util.Vector;

import webService.WrapperSimple;

// CsmlStringFromPathwayId--------------------------
//v0 20110702: get csml string for ref pathway id. The input pathway id should start with "rn"

public class CsmlStringFromPathwayId {
	String pathwayId="";
	
	
	public static void main(String[] args){
		String refPathwayId=args[0];
		System.out.println(refPathwayId);
		String csml=new CsmlStringFromPathwayId(refPathwayId).getCsmlString();
		
		System.out.println("csml for "+refPathwayId+":\n"+csml);
	}
	
	
	public CsmlStringFromPathwayId(String pathwayId_){
		pathwayId=pathwayId_;
		System.out.println(pathwayId);
	}
	
	public String getCsmlString(){
		String csmlString="";
		

		WrapperSimple w=new WrapperSimple();
		

		String[] queryTerm={pathwayId};
		
		Vector<String[]> v=w.getResults(2, DawismdSqlQuery.refPathwayIdToCsmlString, queryTerm);
		
		Iterator<String[]> it=v.iterator();
		
		while(it.hasNext()){
			String[] result=it.next();
			
			if(result[0]!=null){
				csmlString=result[0];		
			}
		}
		
		return csmlString;
	}
}
