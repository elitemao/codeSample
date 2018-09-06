package plugin;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;


import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;

// Use REST method to get the data from IntAct. Have ever tried its webservice,but doesn't work.

public class IntActAgent {
	BufferedReader restReturned;
	
    public IntActAgent(String id,String idType, boolean useSpoke){
//    	System.getProperties().put("http.proxyHost", "129.70.142.129"); //this method doesn't work
//    	System.out.println("jdssafljlasd");
//    	System.getProperties().put("http.proxyPort", "80");
//    	System.out.println("iiiiiiiiiiiiiiiiiiiiiiiiiii");
//    	  System.getProperties().put("proxySet",true);
//    	    System.getProperties().put("proxyPort","8080");
//    	    System.getProperties().put("proxyHost","host");
//    	    System.out.println("iiiiiiyuyiuyiouiooiyoiiiii");
    	
        // Use REST URL to find active PSICQUIC services in txt format.
        // More information on the registry available: http://code.google.com/p/psicquic/wiki/Registry
//        final URL url =
//                new URL( "http://www.ebi.ac.uk/Tools/webservices/psicquic/registry/registry?action=ACTIVE&format=txt" );
//    	String url="http://www.ebi.ac.uk/Tools/webservices/psicquic/registry/registry?action=ACTIVE&format=txt";

    	String url="";
    	if(useSpoke==true){
    		System.out.println("jdskjfksdjksjdkfj");
        	url="http://www.ebi.ac.uk/Tools/webservices/psicquic/intact/webservices/current/search/query/id:%22"+id+"%22";
        }else if(useSpoke==false){
        	System.out.println("oooooooooooooooooooooooooooooooooooooooo");
        	url="http://www.ebi.ac.uk/Tools/webservices/psicquic/intact/webservices/current/search/query/id:%22"+id+"%22%20AND%20NOT%20expansion:spoke";
        }
    	
    	HttpClient client = new HttpClient();
//        System.setProperty("java.net.useSystemProxies", "true");
    	try{
    		
			String computername=InetAddress.getLocalHost().getHostName();
			System.out.println(computername);
			if(computername.equalsIgnoreCase("trevally")){
				client.getHostConfiguration().setProxy("www-proxy", 80); //only this method for proxy setting works
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
		HttpMethod getMethod = new GetMethod(url);
		
	
		try{
			int returnCode = client.executeMethod(getMethod);

			if(returnCode != HttpStatus.SC_OK) {
				 System.err.println("Method failed: " + getMethod.getStatusLine());

				// still consume the response body
//				getMethod.getResponseBodyAsString();
			} else {

				InputStream returnedInputStream=getMethod.getResponseBodyAsStream();
				restReturned=new BufferedReader(new InputStreamReader(returnedInputStream));
//				System.out.println("gotten the html from IntAct");
//				String line;
//				while((line=(br.readLine()))!=null){
//					System.out.println("*****"+line+"&&&&&");
//				}
//				final Properties services = new Properties();
//		        services.load( returnedInputStream );
		       

		        // Print services
//		        System.out.println( "Found " + services.size() + " active service(s)."  );
//		        for ( Object o : services.keySet() ) {
//		            String key = (String ) o;
//		            System.out.println( key + " -> " + services.getProperty( key ));
//		        }
				
				
			}
		} catch (Exception e) {
			System.err.println(e);
			e.printStackTrace();
		} // end catch
     
      
        
    }
    
    public BufferedReader getResultInBufferedReader(){
    	return restReturned;
    }
}
