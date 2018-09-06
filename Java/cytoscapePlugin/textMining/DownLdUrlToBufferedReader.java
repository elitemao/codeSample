package textMining;

//DownLdUrlToBufferedReader---------
//v0 20110216: take care of the proxy setting when executing on different machine.The computer name is "trevally" not "travelly"

import java.io.IOException;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;

public class DownLdUrlToBufferedReader {
	
	// this method is for "get" method
	public static BufferedReader dnldUrlToBufferedReader(String url) {

		HttpClient client = new HttpClient();
		try{
			String computername=InetAddress.getLocalHost().getHostName();
//			System.out.println(computername);
			if(computername.equalsIgnoreCase("trevally")){
				client.getHostConfiguration().setProxy("www-proxy", 80);
			}
		}catch (Exception e){
			System.out.println("Exception caught ="+e.getMessage());
		}
		GetMethod getMethod = new GetMethod(url);
		BufferedReader returnBr=null;
		try{
			int returnCode=client.executeMethod(getMethod);

			if(returnCode == HttpStatus.SC_NOT_IMPLEMENTED) {
				System.err.println("The Post method is not implemented by this URI");
				// still consume the response body
				getMethod.getResponseBodyAsString();
			} else {

				InputStream returnedInputStream=getMethod.getResponseBodyAsStream();
				BufferedReader   brUrl   =   new   BufferedReader(  
						new   InputStreamReader(returnedInputStream));  
				returnBr=brUrl;
//				String line="";
//				while((line=brUrl.readLine())!=null){
//					line=line.trim();
//					if(line.startsWith(">")){
//						returnString=returnString+line+"\n";
//					}else{
//						returnString=returnString+line;
//					}
//				}

			}
		}catch(IOException ioex){
			ioex.printStackTrace();
		}
		
		return returnBr;
	} 

	//this method is for post method
	public static BufferedReader dnldUrlToBufferedReader(String url,HashMap keyValuePair){

		HttpClient client = new HttpClient();
		
		try{
			String computername=InetAddress.getLocalHost().getHostName();
//			System.out.println(computername);
			if(computername.equalsIgnoreCase("trevally")){
				client.getHostConfiguration().setProxy("www-proxy", 80);
			}
		}catch (Exception e){
			System.out.println("Exception caught ="+e.getMessage());
		}
//		client.getHostConfiguration().setProxy("www-proxy", 80);
		PostMethod postMethod = new PostMethod(url);
		Set keySet=(keyValuePair.keySet());
		
		Part[] parts=new Part[keySet.size()];
		int indexForParts=0;
		for(Object s:keySet){
			String value=(String)keyValuePair.get(s);
			
			parts[indexForParts]=new StringPart((String)s,value);
			indexForParts++;
//			System.out.println((String)s+"="+value);
		}
		
//		Part[] parts={
//				new StringPart("from_fields",type1),
//
//				new StringPart("to_fields",type2),
//				new StringPart("ids",id1String),
////				new StringPart("format",);
//		};

		postMethod.setRequestEntity(new MultipartRequestEntity(parts,postMethod.getParams()));
		BufferedReader returnBr=null;
		try{
			int returnCode=client.executeMethod(postMethod);
			
			if(returnCode == HttpStatus.SC_NOT_IMPLEMENTED) {
				System.err.println("The Post method is not implemented by this URI");
				// still consume the response body
				postMethod.getResponseBodyAsString();
			} else {

				InputStream returnedInputStream=postMethod.getResponseBodyAsStream();
				System.out.println("get inputStream");
				BufferedReader   brUrl   =   new   BufferedReader(  
						new   InputStreamReader(returnedInputStream));  
				System.out.println("get bufferedReader");
				returnBr=brUrl;
				
//				String line="";
//				while((line=brUrl.readLine())!=null){
//					line=line.trim();
//					if(line.startsWith(">")){
//						returnString=returnString+line+"\n";
//					}else{
//						returnString=returnString+line;
//					}
//				}

			}
		}catch(IOException ioex){
			ioex.printStackTrace();
		}
		
		return returnBr;
		
	}
}
