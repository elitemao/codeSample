package textMining;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpState;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.PostMethod;


//PmcidPmidConverter-----------------
//v0 20110714: There is cookie issue in this code. Not functional yet.
//v0 20110726: functional!!
//v1 20110726: allow the input to be a list of pmcid in method pmcidToPmid


public class PmcidPmidConverter1 {
	HashMap<String,String> pmcidToPmcMap=new HashMap<String,String>();
	public static void main(String[] args){
		ArrayList<String> inputPmcidList=new ArrayList<String>();
		for(String x:args[0].split(",")){
			inputPmcidList.add(x);
		}
		HashMap<String,String> transformedPmidMap=new PmcidPmidConverter1().pmcidToPmid(inputPmcidList);
		for(String pmcid:transformedPmidMap.keySet()){
			System.out.println("pmciddigit input:"+pmcid+"\t"+"transformed pmid:"+transformedPmidMap.get(pmcid));
		}
	}
	
	public PmcidPmidConverter1(){
		
	}
	
	public HashMap<String,String> pmcidToPmid(ArrayList<String> pmcidDigitListInput){
		String pmidToOutput="";
		String pmcidCatenated="";
		int pointer=0;
		for(String pmcidDigit:pmcidDigitListInput){
			if(pointer==0){
				pmcidCatenated=pmcidDigit;
				pointer=1;
			}else{
				pmcidCatenated=pmcidCatenated+","+pmcidDigit;
			}
		}
		Pattern ptn=Pattern.compile(".*\\<p class=\"sans125b\"\\>Converter Result\\<\\/p\\>.*PMID.*PMCID.*NIHMSID.*?\\<table.*?\\>\\<tr\\>\\<td.*?\\>(\\d+)\\<\\/td\\>\\<\\/tr\\>\\<\\/table\\>\\<\\/td\\>\\<td.*?\\>\\<table.*?\\>\\<tr\\>\\<td.*?\\>(PMC(\\d+))\\<\\/td\\>\\<\\/tr\\>\\<\\/table\\>\\<\\/td\\>.*");
		HttpClient client = new HttpClient();
		HttpState initialState = new HttpState();
//		Cookie mycookie = new Cookie("nih.gov", "mycookie", "stuff","C:\\Documents and Settings\\hang-mao\\Cookies\\", null, false);
//		initialState.addCookie(mycookie);
		
		
//		client.getParams().clear();
//		client.getHttpConnectionManager().getParams().clear();
		client.getHttpConnectionManager().getParams().setConnectionTimeout(30000);
		
		client.setState(initialState);
		
		client.getParams().setCookiePolicy(CookiePolicy.RFC_2109);
		client.getParams().setParameter("http.useragent", "firefox");
		
		HostConfiguration hostconfig=new HostConfiguration();
		hostconfig.setHost("www.uni-bielefeld.de");
				
		try{

			String computername=InetAddress.getLocalHost().getHostName();
			System.out.println(computername);
			if(computername.equalsIgnoreCase("trevally")){
				client.getHostConfiguration().setProxy("www-proxy", 80); //only this method for proxy setting works
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}

		BufferedReader brIdConverter=null;
				
		PostMethod postMethod = new PostMethod("http://www.ncbi.nlm.nih.gov/sites/pmctopmid/");

		NameValuePair[] parts={
				new NameValuePair("PMCRALayout.PMCIDS.PMCIDS_Portlet.Db","pmc"),
				new NameValuePair("PMCRALayout.PMCIDS.PMCIDS_Portlet.ToFile","file"),
				new NameValuePair("PMCRALayout.PMCIDS.PMCIDS_Portlet.Ids",pmcidCatenated),
				new NameValuePair("p$a","PMCRALayout.PMCIDS.PMCIDS_Portlet.Convert"),
				new NameValuePair("p$l","PMCRALayout"),
				new NameValuePair("p$st","pmctopmid")
				
		};

		postMethod.setRequestBody(parts);

		Cookie[] cookies = client.getState().getCookies();
		 
	      for (int i = 0; i < cookies.length; i++) {
	        Cookie cookie = cookies[i];
	        System.out.println(
	          "Cookie: " + cookie.getName() +
	          ", Value: " + cookie.getValue() +
	          ", IsPersistent?: " + cookie.isPersistent() +
	          ", Expiry Date: " + cookie.getExpiryDate() +
	          ", Comment: " + cookie.getComment());

	        
	      }
	      
	     
		try{
			int returnCode = client.executeMethod(postMethod);
//			System.out.println("returnCode is "+returnCode);
			
		      
			if(returnCode == HttpStatus.SC_NOT_IMPLEMENTED) {
				System.err.println("The Post method is not implemented by this URI");
				// still consume the response body
				postMethod.getResponseBodyAsString();
			} else {
				String pmidFound="";
				String pmcidDigitFound="";
				InputStream returnedInputStream=postMethod.getResponseBodyAsStream();
				BufferedReader   bf   =   new   BufferedReader(  
						new   InputStreamReader(returnedInputStream));  
				brIdConverter=bf;
				String   s   =   null;  
				while   ((s=brIdConverter.readLine())   !=   null)   {  
					System.out.println("**********"+s+"&&&&&&&&&&&&");
//					Matcher mtch=ptn.matcher(s);
//					if(mtch.matches()){
//						pmidFound=mtch.group(1);
//						pmcidDigitFound=mtch.group(3);
//						break;
//					}
					String[] lineArray=s.split(",");
					if(lineArray.length>=2 && pmcidDigitListInput.contains(lineArray[1].replaceAll("PMC", ""))){
						pmcidToPmcMap.put(lineArray[1].replaceAll("PMC", ""), lineArray[0]);
					}
					
				}
//				if(pmcidDigitFound.equalsIgnoreCase(pmcidDigitInput)){
//					pmidToOutput=pmidFound;
//				}
//				System.out.println("pmidFound:"+pmidFound);
//				System.out.println("pmcidDigitFound:"+pmcidDigitFound);
			}
		} catch (Exception e) {
			System.err.println(e);
			e.printStackTrace();
		} 
		
//		return pmidToOutput;
		return pmcidToPmcMap;
	}
}
