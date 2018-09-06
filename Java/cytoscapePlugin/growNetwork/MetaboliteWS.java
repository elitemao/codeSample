package growNetwork;


import java.io.Serializable;
import java.util.*;

//import javax.swing.JFrame;
//import javax.swing.JOptionPane;

//import webService.EstablishConnectionDawisMd;
import webService.WrapperSimple;
// MetaboliteWS-------
//v0: use webservice instead of bdbhost
//v0: use EstablishConnectionDawisMd
//v0: has to set the stoichiometry explicitly
//v0 20101124: disable EstablishConnectionDawisMd
//v0 20110503: if nameStore is empty, put keggcompid into it
/**
 * @author  hang-mao
 */
public class MetaboliteWS implements Serializable{
	@SuppressWarnings("unchecked,deprecated")
	
	private String moleType="metabolite";
	private ArrayList nameStore = new ArrayList();
	private String keggCompoundId = "";
	private String hmdbIdSingle="";
	private String chebiId = "";
	private ArrayList cellularLocationStore = new ArrayList();
	private ArrayList tissueStore=new ArrayList();
	private String casNumber = "";
	private String chemicalFormula = "";
	private double concentration;
	private double initialValue;
	private String unit="";
//	private String stoichiometry="1";
	private String stoichiometry;  //since there is no default value for stoichiometry, each Metabolite object has to be set stoichiometry explicitly.
	// private ArrayList associatedDisorder;
	private ArrayList omimStore = new ArrayList();
    private String entityLabel;
//	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
//	static final String DATABASE_URL = "jdbc:mysql://bdbhost/RedoxNW";

	// static final String
	// getHmdbIdFrKeggCompoundIdByHmdb_kegg_compound_id="select hmdb_id from
	// hmdb_kegg_compound_id where kegg_compound_id=?";

    public static void main(String[] args){
    	String keggCompId=args[0];
    	MetaboliteWS m=new MetaboliteWS(keggCompId);
    	ArrayList<String> nameStore=m.getName();
    	System.out.println("common name of "+keggCompId+" is "+nameStore.get(0));
    }
	public MetaboliteWS(){
		// this implicit constructor is used in gonParser.GonToCyNet
	}
	
	public MetaboliteWS(String keggCompoundIdOut) {
//		JOptionPane.showMessageDialog(new JFrame(), "in the constructor of "+this.getClass().getName());
		// ArrayList hmdbIdStore=new ArrayList();
		// String query1="select associated_disorder_name from
		// hmdb_associated_disorder where hmdb_id=?";
		System.out.println("in MetaboliteWS constructor line 49");
		String query0 = "select distinct name from kegg_compound_name where entry=?";
		String query1 = "select distinct hmdb_id from hmdb_kegg_compound_id where kegg_compound_id=?";
		String query2 = "select distinct name from hmdb_name where hmdb_id=?";
		String query3 = "select distinct chebi_id from hmdb_chebi_id where hmdb_id=?";
		String query4 = "select distinct cellular_location from hmdb_cellular_location where hmdb_id=?";
		String query5 = "select distinct cas_number from hmdb_cas_number where hmdb_id=?";
		String query6 = "select distinct chemical_formula from hmdb_chemical_formula where hmdb_id=?";
		String query7 = "select distinct omim_id from hmdb_omim_id where hmdb_id=?";
		String query8="select distinct tissue_location_case_name from hmdb_tissue_location_case_name where hmdb_id=?";
		try{
//		new EstablishConnectionDawisMd();
		WrapperSimple w1 = new WrapperSimple();
		String[] querySeed0 = { keggCompoundIdOut };
		Vector v0 = w1.getResults(1, query0, querySeed0);// 2 is redoxnw
		Iterator it0 = v0.iterator();
		
		while (it0.hasNext()) {
			String[] result0 = (String[]) it0.next();
			String name = result0[0];

//			System.out.println("metabolite name:"+name+"/"+this.getClass().getName()+" "+new Exception().getStackTrace()[0].getLineNumber());
//			JOptionPane.showMessageDialog(new JFrame(), "the name for "+keggCompoundIdOut+" is:"+name);
			nameStore.add(name);
		}
		// to prevent from empty nameStore 
		if(nameStore.size()==0){
			nameStore.add(keggCompoundIdOut);
		}
		keggCompoundId = keggCompoundIdOut;
		
		String[] querySeed1 = { keggCompoundIdOut };
		Vector v1 = w1.getResults(2, query1, querySeed1);
		Iterator it1=v1.iterator();
		while(it1.hasNext()){
			String[] result1 = (String[]) it1.next();
			if(result1[0]!=""){
				hmdbIdSingle=result1[0];
			}
		}
		if(hmdbIdSingle==""){
			System.out.println("there is no corresponding hmdb id for "+keggCompoundIdOut);
			
		}

			
			String[] querySeed2 = { hmdbIdSingle };

			
			Vector v2 = w1.getResults(2, query2, querySeed2);
			Iterator it2=v2.iterator();
			while(it2.hasNext()){
				String[] result2 = (String[]) it2.next();
				String name=result2[0];
				if(name!=""){
					nameStore.add(name);
				}
			}
			
			Vector v3 = w1.getResults(2, query3, querySeed2);
			Iterator it3=v3.iterator();
			while(it3.hasNext()){
				String[] result3 = (String[]) it3.next();
				chebiId=result3[0];
				
			}
			
			Vector v4 = w1.getResults(2, query4, querySeed2);
			Iterator it4=v4.iterator();
			while(it4.hasNext()){
				String[] result4 = (String[]) it4.next();
				String cellularLocationSingle=result4[0];
				if(cellularLocationSingle!=""){
					cellularLocationStore.add(cellularLocationSingle);
				}
			}
			
			Vector v5 = w1.getResults(2, query5, querySeed2);
			Iterator it5=v5.iterator();
			while(it5.hasNext()){
				String[] result5 = (String[]) it5.next();
				casNumber=result5[0];
			}
			
			Vector v6 = w1.getResults(2, query6, querySeed2);
			Iterator it6=v6.iterator();
			while(it6.hasNext()){
				String[] result6 = (String[]) it6.next();
				chemicalFormula=result6[0];
				
			}
			
			Vector v7 = w1.getResults(2, query7, querySeed2);
			Iterator it7=v7.iterator();
			while(it7.hasNext()){
				String[] result7 = (String[]) it7.next();
				String omimSingle=result7[0];
				if(omimSingle!=""){
					omimStore.add(omimSingle);
				}
			}
			
			Vector v8 = w1.getResults(2, query8, querySeed2);
			Iterator it8=v8.iterator();
			while(it8.hasNext()){
				String[] result8 = (String[]) it8.next();
				String tissueSingle=result8[0];
				if(tissueSingle!=""){
					tissueStore.add(tissueSingle);
				}
			}
	}catch(Exception ex){
		ex.printStackTrace();
	}
			

		

	}

	// public void setCommonName(String name){
	// commonName.add(name);
	//
	//
	// }
	// public void setKeggCompoundId(String keggCompId){
	// keggCompoundId=keggCompId;
	//
	// }
	// public void setChebiId(String chebi){
	// chebiId=chebi;
	//
	// }
	//
	// public void setCellularLocation(String goComponent){
	// cellularLocation=goComponent;
	//
	// }
	// public void setCasNumber(String casNo){
	// casNumber=casNo;
	//
	// }
	//
	// public void setChemicalFormula(String formula){
	// chemicalFormula=formula;
	//
	// }
	// public void setConcentration(double concen){
	// concentration=concen;
	//
	// }
	// public void setAssociatedDisorder(String disorder){
	// associatedDisorder.add(disorder);
	//
	// }
	//
	// public void setOmim(String omim){
	// omims.add(omim);
	//
	// }
	
	/**
	 * @param s
	 * @uml.property  name="stoichiometry"
	 */
	public void setStoichiometry(String s){
		stoichiometry=s;
	}
	
	public ArrayList getName() {

		return nameStore;
	}

	/**
	 * @return
	 * @uml.property  name="keggCompoundId"
	 */
	public String getKeggCompoundId() {

		return keggCompoundId;
	}

	public String getHmdbId(){
		
		return hmdbIdSingle;
	}
	
	/**
	 * @return
	 * @uml.property  name="chebiId"
	 */
	public String getChebiId() {

		return chebiId;
	}

	public ArrayList getCellularLocation() {

		return cellularLocationStore;
	}

	public ArrayList getTissue(){
		
		return tissueStore;
		
	}
	
	/**
	 * @return
	 * @uml.property  name="casNumber"
	 */
	public String getCasNumber() {

		return casNumber;
	}

	/**
	 * @return
	 * @uml.property  name="chemicalFormula"
	 */
	public String getChemicalFormula() {

		return chemicalFormula;
	}
	/**
	 * @return
	 * @uml.property  name="unit"
	 */
	public String getUnit(){
		return unit;
	}
	// public double getConcentration(){
	//
	// return concentration;
	// }
	// public ArrayList getAssociatedDisorder(){
	//
	// return associatedDisorder;
	// }
	public ArrayList getOmim() {

		return omimStore;
	}
	/**
	 * @return
	 * @uml.property  name="moleType"
	 */
	public String getMoleType(){
		
		return moleType;
	}
	/**
	 * @param entityLabel_
	 * @uml.property  name="entityLabel"
	 */
	public void setEntityLabel(String entityLabel_){
		entityLabel=entityLabel_;
	}
	/**
	 * @return
	 * @uml.property  name="entityLabel"
	 */
	public String getEntityLabel(){
		return entityLabel;
	}
	/**
	 * @return
	 * @uml.property  name="initialValue"
	 */
	public double getInitialValue(){
		return initialValue;
	}
	/**
	 * @return
	 * @uml.property  name="stoichiometry"
	 */
	public String getStoichiometry(){
		return stoichiometry;
	}
}
