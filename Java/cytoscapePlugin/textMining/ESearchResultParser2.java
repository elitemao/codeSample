package textMining;

import java.io.ByteArrayInputStream;

import java.io.InputStream;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import java.util.regex.Matcher;

import eSearchResultJaxb.ESearchResult;
import eSearchResultJaxb.IdList;
import eSearchResultJaxb.WarningList;

//v0 20110712: since the package eUtilitySchemaJaxb is renamed to eSearchResultJaxb, use eSearchResultJaxb for unmarshal
//v1 20110721: if there is ignored phrase when using ESearch, return ref count 0.
//v1 20110724: add try..catch in methods getRefCount(),getUID()
//v2 20110724: use regular expression to check if the returned xml string contains "PhraseNotFound" or "QuotedPhraseNotFound" 

public class ESearchResultParser2 {
	ESearchResult searchResult;
	String resultInXml="";
	public ESearchResultParser2(){
		
		
	}
	
	@SuppressWarnings("deprecation")
	public void parsePubMedResult(String resultInXml_){
		resultInXml=resultInXml_;
		try{
			System.out.println("the xml received in EutilityResultParser is:\n"+resultInXml);
//			PrintWriter out=new PrintWriter(new FileWriter(new File("d:\\ForWork\\growNetwork\\jkjkjkj.txt")));
//			out.print(resultInXml);
//			out.close();
//			InputStream is= new FileInputStream(new File("d:\\ForWork\\growNetwork\\jkjkjkj.txt"));
			InputStream is=new ByteArrayInputStream(resultInXml.getBytes());
//			StringBuffer xmlStr=new StringBuffer(resultInXml);
			JAXBContext jc=JAXBContext.newInstance("eSearchResultJaxb");
//			JOptionPane.showMessageDialog(new JFrame(),"third row in try");
			Unmarshaller u=jc.createUnmarshaller();

			searchResult=(ESearchResult)(u.unmarshal(is));
			
//			searchResult=(ESearchResult)(u.unmarshal(new StreamSource(new StringReader(xmlStr.toString()))));
			
//		}catch(UnsupportedEncodingException e){
//			e.printStackTrace();
//			
		}catch(JAXBException jaxbex){
			jaxbex.printStackTrace();
		}
//		catch(IOException ioex){
//			ioex.printStackTrace();
//		}
		
	}
	
	public boolean hasTermNotFound(){
		Pattern ptn=Pattern.compile(".*\\<QuotedPhraseNotFound\\>.*?\\<\\/QuotedPhraseNotFound\\>.*");
		Pattern ptn1=Pattern.compile(".*\\<PhraseNotFound\\>.*?\\<\\/PhraseNotFound\\>.*");
//		Pattern ptn2=Pattern.compile(".*\\<PhraseIgnored\\>.*?\\<\\/PhraseIgnored\\>.*");
		Matcher mtch=ptn.matcher(resultInXml);
		Matcher mtch1=ptn1.matcher(resultInXml);
//		Matcher mtch2=ptn2.matcher(resultInXml);
		if(mtch.matches()||mtch1.matches()){
			return true;
		}else{
			return false;
		}
		
	}
	public String getRefCount(){
		
//		WarningList warningList=searchResult.getWarningList();
//		List<String> ignoredPhraseList=null;
//		if(hasTermNotFound()){
//			ignoredPhraseList=warningList.getQuotedPhraseNotFound();
//		}
//		try{
//			ignoredPhraseList=warningList.getQuotedPhraseNotFound();
//		}catch(Exception exee){
//			exee.printStackTrace();
//			String refCount=searchResult.getCount();
//			return refCount;
//		}
		if(hasTermNotFound()){
			return "0";
		}else{
			String refCount=searchResult.getCount();
			return refCount;
		}
	}
	
	
	public ArrayList<String> getUID(){
		ArrayList<String> uidStore=new ArrayList<String>();
//		WarningList warningList=searchResult.getWarningList();
//		List<String> ignoredPhraseList=null;
//		try{
//			ignoredPhraseList=warningList.getQuotedPhraseNotFound();
//		}catch(Exception exee){
//			exee.printStackTrace();
//		}
		
		if(hasTermNotFound()){
			return uidStore;
		}else{
			IdList idList=searchResult.getIdList();
			uidStore=(ArrayList<String>)idList.getId();
			return uidStore;
		}
	}
}
