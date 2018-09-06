package growNetwork;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import webService.WrapperSimple;

// EcTransformerWS----------
//v0 20110703: get common name for ec node
//v0 20110712: change the name of the old method and add one new method
//v0 20110717: add method "getRelatedPathwayByKegg"

public class EcTransformerWS {
	String ec;
	
	public static void main(String[] args){
		String ec_=args[0];
		ArrayList<String> commonNames=new EcTransformerWS(ec_).getCommonNameByKegg();
		ArrayList<String> commonNamesBrenda=new EcTransformerWS(ec_).getCommonNameByBrenda();
		for(String oneName:commonNames){
			System.out.println("one name from kegg:"+oneName);
		}
		for(String oneName:commonNamesBrenda){
			System.out.println("one name from brenda:"+oneName);
		}
	}
	
	public EcTransformerWS(String ec_){
		ec=ec_;
	}
	
	public ArrayList<String> getCommonNameByKegg(){
		ArrayList<String> commonNameStore=new ArrayList<String>();
		WrapperSimple w=new WrapperSimple();
		String[] attributes={ec};
		Vector<String[]> v=w.getResults(1, DawismdSqlQuery.ecToCommonNameByKegg, attributes);
	
		Iterator<String[]> it=v.iterator();
		
		while(it.hasNext()){
			String[] result=(String[])it.next();
			if(!result[0].equalsIgnoreCase("")){
				commonNameStore.add(result[0]);
			}
			
		}
	
		return commonNameStore;
	}
	
	// this method is added on 2011/7/12
	public ArrayList<String> getCommonNameByBrenda(){
		ArrayList<String> commonNameStore=new ArrayList<String>();
		WrapperSimple w=new WrapperSimple();
		String[] attributes={ec};
		Vector<String[]> v=w.getResults(1, DawismdSqlQuery.ecToCommonNameByBrenda, attributes);
	
		Iterator<String[]> it=v.iterator();
		
		while(it.hasNext()){
			String[] result=(String[])it.next();
			if(!result[0].equalsIgnoreCase("")){
				commonNameStore.add(result[0]);
			}
			
		}
	
		return commonNameStore;
	}
	
	public HashMap<String,String> getRelatedPathwayByKegg(){
		HashMap<String,String> pathwayStore=new HashMap<String,String>();
		WrapperSimple w=new WrapperSimple();
		String[] attributes={ec};
		Vector<String[]> v=w.getResults(1, DawismdSqlQuery.ecToPathwayByKegg, attributes);
	
		Iterator<String[]> it=v.iterator();
		
		while(it.hasNext()){
			String[] result=(String[])it.next();
			if(result[0]!=null && result[1]!=null && !result[0].equals("") && !result[1].equals("")){
				String org=result[0];
				String number=result[1];
				String pathwayName=result[2];
				pathwayStore.put(number, pathwayName);		
			}
			
		}
	
		return pathwayStore;
	}
}
