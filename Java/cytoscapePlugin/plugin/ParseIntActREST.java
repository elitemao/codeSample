package plugin;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;

public class ParseIntActREST {
	ArrayList interacterStore=new ArrayList();
	ArrayList preyStore=new ArrayList();
	
	public ParseIntActREST(BufferedReader intactBr, String baitId) {
		try{
			String line;

			while((line=intactBr.readLine())!=null){
				System.out.println("$$$$$$$$$$$"+line);
				String[] lineArray=line.split("\t");
				String[] p=lineArray[0].split("\\|");
//				System.out.println("lkkl"+p[0]);
				
				String interacterA=((lineArray[0].split("\\|"))[0].split(":"))[1].trim();//lineArray[0] might be like: "uniprotkb:P30044|intact:EBI-722161"
//				System.out.println("interacterA:"+interacterA);
				String interacterB=lineArray[1].split("\\|")[0].split(":")[1].trim();
//				System.out.println("interacterB:"+interacterB);
				String detectMethod=lineArray[6].trim(); //lineArray[6] is like--- "psi-mi:"MI:0006"(anti bait coimmunoprecipitation)"
//				System.out.println("detectMethod:"+detectMethod);
				String refId=lineArray[8].trim();
//				System.out.println("refId:"+refId);
				String speciesA=lineArray[9];
//				System.out.println("speciesA:"+speciesA);
				String speciesB=lineArray[10];
//				System.out.println("speciesB:"+speciesB);
				String interactionType=lineArray[11];
//				System.out.println("interactionType:"+interactionType);
				String sourceDb=lineArray[12];
//				System.out.println("sourceDb:"+sourceDb);

				InteractPair intP=new InteractPair(interacterA,interacterB,speciesA,speciesB);
				intP.setDetectMethod(detectMethod);
				intP.setInteractionType(interactionType);
				intP.setRefId(refId);
				intP.setSourceDb(sourceDb);
				interacterStore.add(intP);
				
				if(interacterA.equalsIgnoreCase(baitId)){
					preyStore.add(interacterB);
				}else if(interacterB.equalsIgnoreCase(baitId)){
					preyStore.add(interacterA);
				}
			}
			
			intactBr.close();
			
		}catch(IOException ioex){
			ioex.printStackTrace();
		}
	}
	
	public ArrayList getInteractPairs(){
		return interacterStore;
	}
	
	public ArrayList getPrey(){
		return preyStore;
	}
	
//	public static void main(){
//		
//	}
}
