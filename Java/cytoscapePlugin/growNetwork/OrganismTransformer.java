package growNetwork;

import java.util.Iterator;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import webService.WrapperSimple;


//OrganismTransformer-------------------
//v0 original date unknown: use dawis md on tunicata
//v0 20110627: use dawismd on agbi.The schema of kegg_genome_name table is changed.Add one more method "taxonomyNumberTo3Letter"

public class OrganismTransformer {
	WrapperSimple w;
	public OrganismTransformer(){
		w=new WrapperSimple();
	}

	public String latinToTaxonomyNumber(String latinName){
		
		String taxonomyNumber="";
		Pattern ptn=Pattern.compile("\\d+");
		String[] seed1={latinName};
		String query1="select entry from kegg_genome_name where name=?";
		String query2="select name from kegg_genome_name where entry=?";

		Vector v1=w.getResults(1, query1, seed1);

		Iterator it1=v1.iterator();

		while(it1.hasNext()){
			String[] result1=(String[])it1.next();
			if(result1[0]!=null && !result1[0].equals("")){
				String entryId=result1[0];
//				System.out.println("three letter abbreviation for "+latinName+" is:"+threeLetterOrg);
				String[] seed2={entryId};
				Vector v2=w.getResults(2, query2, seed2);

				Iterator it2=v2.iterator();
				while(it2.hasNext()){
					String[] result2=(String[])it2.next();
					if(result2[0]!=null && !result2[0].equals("")){
						Matcher mtch=ptn.matcher(result2[0]);
						if(mtch.matches()){
							taxonomyNumber=result2[0];
						}

					}
				}
			}
		}
		return taxonomyNumber;

	}

	public String taxonomyNumberToLatin(String taxonomyNumber){
		String latinName="";
		Pattern ptn=Pattern.compile("[a-zA-Z]*\\.[a-zA-Z]*");
		String[] seed1={taxonomyNumber};
		String query1="select entry from kegg_genome_name where name=?";
		String query2="select name from kegg_genome_name where entry=?";

		Vector v1=w.getResults(1, query1, seed1);

		Iterator it1=v1.iterator();

		while(it1.hasNext()){
			String[] result1=(String[])it1.next();
			if(result1[0]!=null && !result1[0].equals("")){
				String entryId=result1[0];
//				System.out.println("three letter abbreviation for "+taxonomyNumber+" is:"+threeLetterOrg);
				String[] seed2={entryId};
				Vector v2=w.getResults(1, query2, seed2);

				Iterator it2=v2.iterator();
				while(it2.hasNext()){
					String[] result2=(String[])it2.next();
					if(result2[0]!=null && !result2[0].equals("")){
						Matcher mtch=ptn.matcher(result2[0]);
						if(mtch.matches()){
							latinName=result2[0];
						}

					}
				}
			}
		}
		return latinName;
	}
	public String taxonomyNumberTo3Letter(String taxonomyNumber){
		String threeLetterName="";
		Pattern ptn=Pattern.compile("[\\w]{3}");
		String[] seed1={taxonomyNumber};
		String query1="select entry from kegg_genome_name where name=?";
		String query2="select name from kegg_genome_name where entry=?";

		Vector v1=w.getResults(1, query1, seed1);

		Iterator it1=v1.iterator();

		while(it1.hasNext()){
			String[] result1=(String[])it1.next();
			if(result1[0]!=null && !result1[0].equals("")){
				String entryId=result1[0];
//				System.out.println("three letter abbreviation for "+taxonomyNumber+" is:"+threeLetterOrg);
				String[] seed2={entryId};
				Vector v2=w.getResults(1, query2, seed2);

				Iterator it2=v2.iterator();
				while(it2.hasNext()){
					String[] result2=(String[])it2.next();
					if(result2[0]!=null && !result2[0].equals("")){
						Matcher mtch=ptn.matcher(result2[0]);
						if(mtch.matches()){
							threeLetterName=result2[0];
						}

					}
				}
			}
		}
		return threeLetterName;
	}
	public static void main(String[] args){
		String latinHuman=(new OrganismTransformer()).taxonomyNumberToLatin("9606");
		System.out.println("9606 is "+latinHuman);
	}
	
}
