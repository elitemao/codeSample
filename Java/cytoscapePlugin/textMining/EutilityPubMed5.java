package textMining;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpState;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;


// EutilityPubMed-----
//v0 20110131: Be careful!!! The string sent by HttpClient has to use Hex code.
//v1 20110131: add Cookie
//v2 20110215: add method- getFullTextByGet, getFullTextByPost
//v3 20110216: change the signature of getFullTextByPost method
//v3 20110217: remove <!DOCTYPE> tag in the received html.<!DOCTYPE> will interfere unmarshal
//v4 20110424: add one more method for using eSummary by get
//v5 20110715: add one more method: getAbstractForPmid

public class EutilityPubMed5 {
	String eSearchPrefix="http://eutils.ncbi.nlm.nih.gov/entrez/eutils/esearch.fcgi?";
	String eFetchPrefix="http://eutils.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi?";
	String eSummaryPrefix="http://eutils.ncbi.nlm.nih.gov/entrez/eutils/esummary.fcgi?";
	String returnedText;
	HttpClient client;
	Pattern ptn=Pattern.compile("\\<!DOCTYPE.*?\\>");
	boolean isDocTypeRemoved=false;
	
	public EutilityPubMed5(){
		HttpState initialState = new HttpState();
//		Cookie mycookie = new Cookie(".nig.gov", "mycookie", "stuff","d:\\cookie.txt", null, false);
//		initialState.addCookie(mycookie);

		client=new HttpClient();
		client.getHttpConnectionManager().getParams().setConnectionTimeout(30000);
		client.setState(initialState);
		client.getParams().setCookiePolicy(CookiePolicy.RFC_2109);
		try{
			String computername=InetAddress.getLocalHost().getHostName();
			System.out.println(computername);
			if(computername.equalsIgnoreCase("trevally")){
				client.getHostConfiguration().setProxy("www-proxy", 80);
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
		
	}
	
	//use esearch, by "get" method
	public String returnGetResult(String queryString){
		String returnedText=null;
		HttpMethod getMethod=new GetMethod(eSearchPrefix+queryString);
		
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
					ln=ln.replaceAll("\\<!DOCTYPE.*?\\>", ""); //<!DOCTYPE> tag will interfere the unmarshal process.
					if(lineCount==0){  // Be careful!!!This first line of the xml file can't be an empty line
						returnedText=ln;
						lineCount=1;
						continue;
					}
					returnedText=returnedText+ln;// if add "\n" between returnedText and line, there will be error when unmarshal the returnedText later
				}
//				while((ln=br.readLine())!=null && !(ln=br.readLine()).equalsIgnoreCase("")){
//					returnedText=returnedText+ln;
//					
//				}
				
			}
			
			
		}catch(IOException ioex){
			ioex.printStackTrace();
		}
		return returnedText;
	}
	
	//use esearch, by "post" method
	public String returnPostResult(HashMap keyValuePair){
		String returnedText="";
		BufferedReader returnHtmlInBufferedReader=DownLdUrlToBufferedReader.dnldUrlToBufferedReader(eSearchPrefix, keyValuePair);
		String line="";
		try{
			while((line=returnHtmlInBufferedReader.readLine())!=null){
				line=line.replaceAll("\\<!DOCTYPE.*?\\>", ""); //<!DOCTYPE> tag will interfere the unmarshal process.
				returnedText=returnedText+line; // if add "\n" between returnedText and line, there will be error when unmarshal the returnedText later

			}
		}catch(IOException ioex){
			ioex.printStackTrace();
		}
		return returnedText;
	}
	
	// use efetch, by "get" method
	public String getFullTextByGet(String pmcid){
		String returnedText=null;
		String queryString="db=pmc&id="+pmcid+"&retmode=xml";
		HttpMethod getMethod=new GetMethod(eFetchPrefix+queryString);
		
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
					ln=ln.replaceAll("\\<!DOCTYPE.*?\\>", ""); //<!DOCTYPE> tag will interfere the unmarshal process.
					if(lineCount==0){  // Be careful!!!This first line of the xml file can't be an empty line
						returnedText=ln;
						lineCount=1;
						continue;
					}
					returnedText=returnedText+ln; // if add "\n" between returnedText and line, there will be error when unmarshal the returnedText later
				}
//				while((ln=br.readLine())!=null && !(ln=br.readLine()).equalsIgnoreCase("")){
//					returnedText=returnedText+ln;
//					
//				}
				
			}
			
			
		}catch(IOException ioex){
			ioex.printStackTrace();
		}
		return returnedText;
	}
	
	//use efetch, by "post" method
	public String getFullTextByPost(String pmcid){
		HashMap keyValuePair=new HashMap();
		keyValuePair.put("db", "pmc");
		keyValuePair.put("id", pmcid);
		keyValuePair.put("retmode", "xml");
		String returnedText="";
		System.out.println(this.getClass().getName()+" line:"+new Exception().getStackTrace()[0].getLineNumber());
		BufferedReader returnHtmlInBufferedReader=DownLdUrlToBufferedReader.dnldUrlToBufferedReader(eFetchPrefix, keyValuePair);
		System.out.println(this.getClass().getName()+" line:"+new Exception().getStackTrace()[0].getLineNumber());
		String line="";
		
		try{
//			BufferedWriter bw=new BufferedWriter(new FileWriter("J:\\ForWork\\growNetwork\\xxxxxxx.xml"));
			while((line=returnHtmlInBufferedReader.readLine())!=null){
				line=line.replaceAll("\\<!DOCTYPE.*?\\>", ""); //<!DOCTYPE> tag will interfere the unmarshal process.
//				System.out.println(line);
//				bw.write(line+"\n");
				returnedText=returnedText+line; // if add "\n" between returnedText and line, there will be error when unmarshal the returnedText later

			}
//			bw.close();
		}catch(IOException ioex){
			ioex.printStackTrace();
		}
		return returnedText;
	}
	
	// for retrieving the meta data of pmid by http "get" method
	public String getESummaryResult(ArrayList pmidList){
		String returnedText=null;
		String pmidListString="";
		
		for(Object x:pmidList){
			pmidListString=pmidListString+(String)x+",";
		}
		String queryString="db=pubmed&id="+pmidListString+"&retmode=xml";
		HttpMethod getMethod=new GetMethod(eSummaryPrefix+queryString);
		
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
					ln=ln.replaceAll("\\<!DOCTYPE.*?\\>", ""); //<!DOCTYPE> tag will interfere the unmarshal process.
					if(lineCount==0){  // Be careful!!!This first line of the xml file can't be an empty line
						returnedText=ln;
						lineCount=1;
						continue;
					}
					returnedText=returnedText+ln; // if add "\n" between returnedText and line, there will be error when unmarshal the returnedText later
				}
//				while((ln=br.readLine())!=null && !(ln=br.readLine()).equalsIgnoreCase("")){
//					returnedText=returnedText+ln;
//					
//				}
				
			}
			
			
		}catch(IOException ioex){
			ioex.printStackTrace();
		}
		return returnedText;
	}
	
	public String getAbstractForPmid(String pmid){

		String returnedText=null;
		String queryString="db=pubmed&id="+pmid+"&retmode=xml&rettype=abstract";
		HttpMethod getMethod=new GetMethod(eFetchPrefix+queryString);
		
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
					ln=ln.replaceAll("\\<!DOCTYPE.*?\\>", ""); //<!DOCTYPE> tag will interfere the unmarshal process.
					if(lineCount==0){  // Be careful!!!This first line of the xml file can't be an empty line
						returnedText=ln;
						lineCount=1;
						continue;
					}
					returnedText=returnedText+ln; // if add "\n" between returnedText and line, there will be error when unmarshal the returnedText later
				}
//				while((ln=br.readLine())!=null && !(ln=br.readLine()).equalsIgnoreCase("")){
//					returnedText=returnedText+ln;
//					
//				}
				
			}
			
			
		}catch(IOException ioex){
			ioex.printStackTrace();
		}
		return returnedText;
	
	}
	
}
