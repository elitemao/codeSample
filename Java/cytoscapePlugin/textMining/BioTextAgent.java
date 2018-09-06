package textMining;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.util.ArrayList;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;

//BioTextAgent-------------------------------------
//v0 20110528: change 'catenatedTerm=(String)oneTerm";' to 'catenatedTerm="%22"+(String)oneTerm+"%22";'
//v0 20110528: replace space in each input term with "%2B"
//v0 20110528: change to 'catenatedTerm=catenatedTerm+"%22"+(String)oneTerm+"%22";'
public class BioTextAgent {

	String returnedText="";
	
	public BioTextAgent(ArrayList inputTerms){
		String bioTextHttpPrefix="http://biosearch.berkeley.edu/index.php?";
		HttpClient client=new HttpClient();
		try{
			String computername=InetAddress.getLocalHost().getHostName();
			System.out.println(computername);
			if(computername.equalsIgnoreCase("trevally")){
				client.getHostConfiguration().setProxy("www-proxy", 80);
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
		String catenatedTerm="";
		int termCount=0;
		for(Object oneTerm:inputTerms){
			oneTerm=((String)oneTerm).replaceAll(" ", "%2B");
			if(termCount==0){
				catenatedTerm="%22"+(String)oneTerm+"%22";
				termCount=1;
			}else{
				catenatedTerm=catenatedTerm+"%22"+(String)oneTerm+"%22";
			}
		}
		String queryString="q="+catenatedTerm+"&sumit=Search&view=abstract&sortedby=rel&r=1000&action=submit_search";
		
//		System.out.println(bioTextHttpPrefix+queryString);
		HttpMethod getMethod=new GetMethod(bioTextHttpPrefix+queryString);
		
		try{
			int returnCode=client.executeMethod(getMethod);
			
			if(returnCode != HttpStatus.SC_OK){
				System.err.println("Method failed: "+getMethod.getStatusLine());
			}else{
				InputStream returnInputStream=getMethod.getResponseBodyAsStream();
				BufferedReader br=new BufferedReader(new InputStreamReader(returnInputStream));
				
				String ln="";
				int lineCount=0;
				while((ln=br.readLine())!=null){
//					System.out.println(ln);
					if(lineCount==0){  // Be careful!!!This first line of the xml file can't be an empty line
						returnedText=ln;
						lineCount=1;
						continue;
					}
					returnedText=returnedText+"\n"+ln;
				}
				while((ln=br.readLine())!=null && !(ln=br.readLine()).equalsIgnoreCase("")){
					returnedText=returnedText+ln;
					
				}
				
			}
			
			
		}catch(IOException ioex){
			ioex.printStackTrace();
		}
		
		
//		PostMethod postMethod = new PostMethod("http://biosearch.berkeley.edu/index.php");
//		Part[] parts=new Part[5];
//	
//		parts[0]=new StringPart("q","%22redox%22%2B%22glutamate%20synthase%22");
//		parts[1]=new StringPart("submit","Search");	
//		parts[2]=new StringPart("view","abstract");	
//		parts[3]=new StringPart("sortedby","rel");	
//		parts[4]=new StringPart("action","submit");	
//			
//		postMethod.setRequestEntity(new MultipartRequestEntity(parts,postMethod.getParams()));
//		BufferedReader returnBr=null;
//		try{
//			int returnCode=client.executeMethod(postMethod);
//			
//			if(returnCode == HttpStatus.SC_NOT_IMPLEMENTED) {
//				System.err.println("The Post method is not implemented by this URI");
//				// still consume the response body
//				postMethod.getResponseBodyAsString();
//			} else {
//
//				InputStream returnedInputStream=postMethod.getResponseBodyAsStream();
//				System.out.println("get inputStream");
//				BufferedReader   brUrl   =   new   BufferedReader(  
//						new   InputStreamReader(returnedInputStream));  
//				System.out.println("get bufferedReader");
//				returnBr=brUrl;
//				
//				String line="";
//				while((line=brUrl.readLine())!=null){
//					line=line.trim();
//					if(line.startsWith(">")){
//						returnedText=returnedText+line+"\n";
//					}else{
//						returnedText=returnedText+line;
//					}
//				}
//
//			}
//		}catch(IOException ioex){
//			ioex.printStackTrace();
//		}
//		
//		
	}
	
	public String getHtml(){
		return returnedText;
	}
	
}
