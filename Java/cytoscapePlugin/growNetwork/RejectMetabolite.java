package growNetwork;
import java.util.*;
public class RejectMetabolite {

	private ArrayList rejectedKeggCompId=new ArrayList();
	
	public RejectMetabolite(){
		rejectedKeggCompId.add("C00001");
		rejectedKeggCompId.add("C00011");
		rejectedKeggCompId.add("C00002");
		rejectedKeggCompId.add("C00003");
		rejectedKeggCompId.add("C00004");
		rejectedKeggCompId.add("C00005");
		rejectedKeggCompId.add("C00006");
		rejectedKeggCompId.add("C00007");
		rejectedKeggCompId.add("C00008");
		rejectedKeggCompId.add("C00009");
		rejectedKeggCompId.add("C00014");
		rejectedKeggCompId.add("C00016");
		rejectedKeggCompId.add("C00020");
		rejectedKeggCompId.add("C00080");
		rejectedKeggCompId.add("C00282"); //H2
	}
	
	public ArrayList getRejectMetabolite(){
		return rejectedKeggCompId;
	}
	
	public boolean isRejectedMetabolite(String keggCompId){
		if(rejectedKeggCompId.contains(keggCompId)){
			return true;
		}else{
			return false;
		}
	}
}
