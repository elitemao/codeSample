package plugin;

public class InteractPair {

	String interactionType;
	String detectMethod;
	String idA;
	String idB;
	String taxoA;
	String taxoB;
	String refId;
	String sourceDb;
	
	public InteractPair(String idA_,String idB_,String taxoA_,String taxoB_){
		idA=idA_;
		idB=idB_;
		taxoA=taxoA_;
		taxoB=taxoB_;
	}
	
	public void setDetectMethod(String detectMethod_){
		detectMethod=detectMethod_;
	}
	
	public void setInteractionType(String interactionType_){
		interactionType=interactionType_;
	}
	
	public void setRefId(String refId_){
		refId=refId_;
	}
	
	public void setSourceDb(String sourceDb_){
		sourceDb=sourceDb_;
	}
	
	public String getIdA(){
		return idA;
	}
	
	public String getIdB(){
		return idB;
	}
	
	public String getTaxoA(){
		return taxoA;
	}
	
	public String getTaxoB(){
		return taxoB;
	}
	
	public String getRefId(){
		return refId;
	}
	
	public String getSourceDb(){
		return sourceDb;
	}
	public String getInteractionType(){
		return interactionType;
	}
	public String getDetectMethod(){
		return detectMethod;
	}
}
