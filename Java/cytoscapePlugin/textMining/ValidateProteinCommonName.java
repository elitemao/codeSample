package textMining;
//ValidateProteinCommonName--------------
//v0 20110203: the purpose is to transform the common name retrieved from my unip_protein_name table to a name suitable for text mining by e-utility
//v0 20110625: deal with the situation like: "Acetyl-CoA acetyltransferase, mitochondrial"

public class ValidateProteinCommonName {
	String originalName;
	
	public ValidateProteinCommonName(String originalName_){
		
		originalName=originalName_;
	}

	public String convert(){
		//v0 20110625: deal with the situation like: "Acetyl-CoA acetyltransferase, mitochondrial"
		String[] originalNameSplitted=originalName.split(",");
		String transferedName=originalNameSplitted[0].replaceAll("\\[", "");
		transferedName=transferedName.replaceAll("\\]", "");
		return transferedName;
		
		
	}
	
}
