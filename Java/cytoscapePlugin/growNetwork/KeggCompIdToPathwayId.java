package growNetwork;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import webService.WrapperSimple;

public class KeggCompIdToPathwayId {
	String keggCompId="";
	public KeggCompIdToPathwayId(String keggCompId_){
		keggCompId=keggCompId_;
	}
	
	public HashMap<String,String> getRelatedPathwayIdDigit(){

		HashMap<String,String> pathwayIdStore=new HashMap<String,String>();

		WrapperSimple w = new WrapperSimple();
		//		JOptionPane.showMessageDialog(new JFrame(), "in the constructor of "+this.getClass().getName()+"-1");
		String[] details = {keggCompId};  

		Vector<String[]> v = w.getResults(1, DawismdSqlQuery.keggCompIdToPathwayId,details);
		//		JOptionPane.showMessageDialog(new JFrame(), "in the constructor of "+this.getClass().getName()+"-2");
		Iterator<String[]> it= v.iterator();

		while(it.hasNext()){
			String[] result=(String[])it.next();
			if(result[0]!=null && !result[0].equalsIgnoreCase("")){
				String pathwayIdDigit=result[0];
				String pathwayIdPrefix=result[1];
				String pathwayName=result[2];
				pathwayIdStore.put(pathwayIdDigit, pathwayName);
			}
		}


		return pathwayIdStore;
	}
	
}
