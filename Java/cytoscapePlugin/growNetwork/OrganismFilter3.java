package growNetwork;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

import webService.WrapperSimple;

//OrganismFilter--------------
//v0 20110310:check if one ec exists for certain organism
//v1 20110311: the algorithm of version 0 produced too many query to the server.
//v1 20110311: user can input id for ec,spacc.
//v1 20110524: user can input id for ec,spacc,spid.
//v1 20110527: change taxonomyList to be a global variable.Add method "getTaxonomyList()"
//v1 20110527: add new WrapperSimple() to if(idType.equals("spacc")){}
//v2 20110617: change the query string for using agbi around line 38
//v3 20110627: change the content in "if(idTyle.equals("ec")){.....}"

public class OrganismFilter3 {
	
	ArrayList taxonomyList=new ArrayList();
	Boolean belongToOrganism=false;
	
	public static void main(String[] args){
		String id=args[0];
		String idType=args[1];
		String taxoToTest=args[2];
		OrganismFilter3 i=new OrganismFilter3(id,idType,taxoToTest);
		ArrayList ii=i.getTaxonomyList();
		System.out.println(ii.get(0));
	}
	public OrganismFilter3(String id,String idType,String taxonomyToTest){
		WrapperSimple w=new WrapperSimple();
		
		if(idType.equals("ec")){
			String threeLetterToTest=(new OrganismTransformer()).taxonomyNumberTo3Letter(taxonomyToTest);
//			ArrayList latinNameStore=new ArrayList();
//			String query="select * from kegg_genes_enzyme where enzyme=? AND org=?"; //this query is used on dawis_md on tunicata
			String query="select * from kegg_enzyme_genes where entry=? AND org=?"; //this query is used on dawismd on agbi
			String[] seed={id,threeLetterToTest};
			

			Vector v=w.getResults(1, query, seed);

			Iterator it=v.iterator();

			while(it.hasNext()){
				String[] result=(String[])it.next();
				if(result[0]!=null && !result[0].equals("")){
					belongToOrganism=true;
					
				}
				
			}
			
			
		}else if(idType.equals("spacc")){
//			ArrayList taxonomyList=new ArrayList();
			WrapperSimple ww=new WrapperSimple();// if the call to this class is made from the button of "Get protein members", it doesn't work if using the WrapperSimple from the StartStaticVariable
			String query1="SELECT internal_index FROM iproclass_uniprotkb_accession i where accession_no=?";
			String query2="select ncbi_taxonomy from iproclass_ncbi_taxonomy where internal_index=?";  //this query returns the taxonomy number 
			
			String[] seed1={id};
			
			Vector v1=ww.getResults(2, query1, seed1);
			
			Iterator it1=v1.iterator();

			while(it1.hasNext()){
				String[] result1=(String[])it1.next();
				if(result1[0]!=null && !result1[0].equals("")){
					String internal_index=result1[0];
					System.out.println(internal_index);
					String[] seed2={internal_index};
					Vector v2=ww.getResults(2, query2, seed2);

					Iterator it2=v2.iterator();

					while(it2.hasNext()){
						String[] result2=(String[])it2.next();
						if(result2[0]!=null){
							String taxonomy=result2[0];
							System.out.println(taxonomy);
							taxonomyList.add(taxonomy);// add taxonomy number to organismList
						}
					}

				}
			}
			if(taxonomyList.size()>0){
				if(taxonomyList.contains(taxonomyToTest)){
					belongToOrganism= true;
				}else{
					belongToOrganism= false;
				}
			}
			
		}else if(idType.equals("spid")){
//			ArrayList taxonomyList=new ArrayList();
			String query1="SELECT internal_index FROM iproclass_uniprotkb_id i where uniprotkb_id=?";
			String query2="select ncbi_taxonomy from iproclass_ncbi_taxonomy where internal_index=?";  //this query returns the taxonomy number 
			
			String[] seed1={id};
			
			Vector v1=w.getResults(2, query1, seed1);
			
			Iterator it1=v1.iterator();

			while(it1.hasNext()){
				String[] result1=(String[])it1.next();
				if(result1[0]!=null && !result1[0].equals("")){
					String internal_index=result1[0];
					String[] seed2={internal_index};
					Vector v2=w.getResults(2, query2, seed2);

					Iterator it2=v2.iterator();

					while(it2.hasNext()){
						String[] result2=(String[])it2.next();
						if(result2[0]!=null){
							String taxonomy=result2[0];
							System.out.println("taxoNo:"+taxonomy+" "+this.getClass().getName()+" "+new Exception().getStackTrace()[0].getLineNumber());
							taxonomyList.add(taxonomy);// add taxonomy number to organismList
						}
					}

				}
			}
			if(taxonomyList.size()>0){
				if(taxonomyList.contains(taxonomyToTest)){
					belongToOrganism= true;
				}else{
					belongToOrganism= false;
				}
			}
		}
	}
	
	
	
	public boolean isOrganism(){
		return belongToOrganism;
	}
	public ArrayList getTaxonomyList(){
		return taxonomyList;
	}
//	public boolean isOrganism(String taxonomyNumber){ 
//		if(taxonomyList.size()>0){
//			if(taxonomyList.contains(taxonomyNumber)){
//				return true;
//			}else{
//				return false;
//			}
//		}else{
//			return true;
//		}
//	}

}
