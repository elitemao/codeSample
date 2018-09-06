package textMining;

import java.io.*;
//import org.lobobrowser.html.*;
//import org.lobobrowser.html.gui.*;
//import org.lobobrowser.html.parser.*;
//import org.lobobrowser.html.test.*;
import org.w3c.dom.*;
import org.xml.sax.InputSource;
//import org.lobobrowser.html.domimpl.HTMLElementImpl;
import java.net.*;
import java.util.*;
import java.util.logging.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// PmidToPmcid---------
//v0 20110214: template code is from : http://www.benjysbrain.com/misc/cobra/. Can not use HttpClient to do it(ncbi doen't allow). Need to use web browser agent to do that.
//v0 20110222: add one more pattern- ptn2
//v0 20110712: there are useful information embedded in the error message. Add code in the catch{} statement to extract it.

public class PmidToPmcid {
	String pmid;
	String baseURL;
	String hostBase;
	String host;

	public static void main(String[] args) {
		String pmid=""; 
		if (args.length == 1)
			pmid = args[0];
		PmidToPmcid p = new PmidToPmcid(pmid);
		String pmcid=p.getPmcid();
		System.out.println("pmid:"+pmid+"to pmcid:"+pmcid);
	}
	
	public PmidToPmcid(String pmid) {
		this.pmid = pmid;
		String computerName;
		try {
			computerName = InetAddress.getLocalHost().getHostName();

			if(computerName.equalsIgnoreCase("trevally")){
				System.setProperty("http.proxyHost", "www-proxy");
				System.setProperty("http.proxyPort", "80");
			}
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String getPmcid() {
		String pmcid="NotDefined";
		Pattern ptn1=Pattern.compile(".*href=\"\\/pmc\\/articles\\/PMC(\\d+?)\\/.*"); // ptn1 and ptn2 are both possible response string from ncbi.
		Pattern ptn2=Pattern.compile(".*href=\"\\/sites\\/ppmc\\/articles\\/PMC(\\d+?)\\/.*");// the reason could be ptn1 is for normal time, and ptn2 is used when ncbi is updating data
		Pattern ptn3=Pattern.compile(".*http:\\/\\/www.ncbi.nlm.nih.gov\\/pmc\\/articles\\/PMC(\\d+)\\/\\?tool=pubmed.*");
		String url="http://www.ncbi.nlm.nih.gov/pmc/articles/pmid/"+pmid+"/?tool=pubmed";
		
		Logger.getLogger("").setLevel(Level.OFF);
		URI uri = null;
		URL urlObj = null;
		try {
			uri = new URI(url);
			urlObj = new URL(url);
		} catch (Exception e) {
			e.printStackTrace();
			
		}
		String path = uri.getPath();
		host = uri.getHost();
		String port = "";
		if (uri.getPort() != -1)
			port = Integer.toString(uri.getPort());
		if (!port.equals(""))
			port = ":" + port;
		baseURL = "http://" + uri.getHost() + port + path;
		System.out.println("baseURL:"+baseURL);
		hostBase = "http://" + uri.getHost() + port;
		System.out.println("hostBase:"+hostBase);
		
		try {
			URLConnection connection = urlObj.openConnection();
			InputStream in = connection.getInputStream();
			BufferedReader reader=new BufferedReader(new InputStreamReader(in));
			String line;
			int control=0;
			while((line=reader.readLine())!=null){
				if(control<50){
				System.out.println("html content:"+line);
				}
				
				control++;
				Matcher mtch1=ptn1.matcher(line);
				Matcher mtch2=ptn2.matcher(line);
				if(mtch1.matches()){
					pmcid=mtch1.group(1);
//					System.out.println("pmc:"+pmcid);
					break;
				}else if(mtch2.matches()){
					pmcid=mtch2.group(1);
					break;
				}
//				bw.write(line+"\n");
//				System.out.println(line);
			}
			
			
		} catch (Exception e) {
			String errorMessage=e.getMessage();
			Matcher mtch3=ptn3.matcher(errorMessage);
			if(mtch3.matches()){
				pmcid=mtch3.group(1);
			}
			return pmcid;
//			e.printStackTrace();
//			System.out.println("parsePage(" + url + "): " + e);
			
		}
		
		return pmcid;
	}

//	public void doTree(Node node) {
//		if (node instanceof Element) {
//			Element element = (Element) node;
//
//			doElement(element);
//			NodeList nl = element.getChildNodes();
//			if (nl == null)
//				return;
//			int num = nl.getLength();
//			for (int i = 0; i < num; i++)
//				doTree(nl.item(i));
//			doTagEnd(element);
//		}
//	}
//
//	public void doElement(Element element) {
//		System.out.println("<" + element.getTagName() + ">");
//	}
//
//	public void doTagEnd(Element element) {
//		System.out.println("</" + element.getTagName() + ">");
//	}

	
}